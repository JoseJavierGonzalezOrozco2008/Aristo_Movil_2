package com.example.aristomovil2.utileria;

import android.content.ContentValues;
import com.example.aristomovil2.facade.Servicio;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Xml {

    /**
     *Funcion encargada de leer e interpretar el XML recibido como String
     * @param peticion (EnviaPeticion) recibe la informacion de petición, que incluye tarea, datos, etc.
     * @param xml (String) es la respuesta de la peticion realizada, y lista para interpretar
     * @param servicio (servicio) funge para realiza conexiones a la base de datos
     */
    public static void obtenerInfoWS(EnviaPeticion peticion, String xml, Servicio servicio){
        if(peticion.getTarea().isDirectoBD())
            servicio.borraDatosTabla(peticion.getTarea().getTablaBD());

        if (xml.equals("") && !peticion.getTarea().getTipoRespuesta().equals("texto")){
            peticion.setExito(false);
            peticion.setMensaje("SIN INFORMACION");
            ContentValues contentValues = new ContentValues();
            peticion.setExtra1(contentValues);
        } else if(peticion.getTarea() == Enumeradores.Valores.TAREA_BORRA_LOTE){
            peticion.setExito(!xml.equals("0"));
            peticion.setMensaje(peticion.getExito()? "Exito":"");
            ContentValues contentValues = new ContentValues();
            contentValues.put("exito", peticion.getExito()!=null ? peticion.getExito():false);
            contentValues.put("anexo",xml);
            peticion.setExtra1(contentValues);
        } else if(peticion.getTarea().getTipoRespuesta().equals("texto")){
            peticion.setExito(Libreria.tieneInformacion(xml));
            peticion.setMensaje(peticion.getExito()? "Exito":"");
            ContentValues contentValues = new ContentValues();
            contentValues.put("exito", peticion.getExito());
            contentValues.put("anexo",xml);
            peticion.setExtra1(contentValues);
        }else {
            ContentValues contentValues;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor;

            try{
                constructor = factory.newDocumentBuilder();
                Document documento = constructor.parse(new InputSource( new StringReader(xml)));
                documento.getDocumentElement().normalize();
                NodeList nList = documento.getElementsByTagName(getTag(peticion.getTarea()));
                /*Obtener varios registros*/
                if (peticion.getTarea().isMultipleRespuesta()) {
                    contentValues = obtenerTag(nList, servicio, peticion);
                }
                else {  /*Esto es para obtener informacion de un solo registro*/
                    contentValues = obtenerTag(nList);
                }
                Boolean exito=contentValues.getAsBoolean("exito");
                peticion.setExito(exito!=null ? exito:false);
                peticion.setMensaje(contentValues.getAsString("mensaje"));
                peticion.setExtra1(contentValues);
            } catch (ParserConfigurationException e){
                System.out.println("Parser exception");
                e.printStackTrace();
            } catch (SAXException e){
                System.out.println("SAXE exception");
                e.printStackTrace();
            } catch (IOException e){
                System.out.println("IO exception");
                e.printStackTrace();
            }
        }
    }

    /**Obtener un solo registro*/
    private static ContentValues obtenerTag (NodeList nList){
        ContentValues contentValues = new ContentValues();
        if(true/*nList.item(0)!=null*/){
        nList = nList.item(0).getChildNodes();
        for (int i = 0; i<nList.getLength(); i++){
            switch (nList.item(i).getNodeName()) {
                case "venta":
                case "detalle": {
                    NodeList list = nList.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        contentValues.put(list.item(j).getNodeName(), list.item(j).getTextContent());
                    }
                    break;
                }
                case "anexo": {
                    contentValues.put(nList.item(i).getNodeName(), nList.item(i).getTextContent());
                    NodeList list = nList.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        if (list.item(j).getNodeName().equals("linea")) {
                            NodeList list1 = list.item(j).getChildNodes();
                            for (int k = 0; k < list1.getLength(); k++) {
                                contentValues.put(list1.item(k).getNodeName(), list1.item(k).getTextContent());
                            }
                        } else
                            contentValues.put(list.item(j).getNodeName(), list.item(j).getTextContent());
                    }
                    break;
                }
                case "linea": {
                    NodeList list = nList.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        contentValues.put(list.item(j).getNodeName(), list.item(j).getTextContent());
                    }
                    contentValues.put(nList.item(i).getNodeName(), nList.item(i).getTextContent());
                    break;
                }
                case "Validacion": {
                    NodeList list = nList.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        switch (list.item(j).getNodeName()) {
                            case "Valido":
                                contentValues.put("exito", list.item(j).getTextContent());
                                break;
                            case "Mensaje":
                                contentValues.put("mensaje", list.item(j).getTextContent());
                                break;
                            case "Anexo":
                                contentValues.put("xml", list.item(j).getTextContent());
                                break;
                        }
                    }
                    break;
                }
                default:
                    contentValues.put(nList.item(i).getNodeName(), nList.item(i).getTextContent());
                    break;
            }
        }}
        return contentValues;
    }

    /**Obtener multiples registros desde la base de datos*/
    private static ContentValues obtenerTag(NodeList nList, Servicio servicio, EnviaPeticion Tarea){
        ContentValues contentValues = new ContentValues();
        nList = nList.item(0).getChildNodes();
        for (int i = 0; i<nList.getLength(); i++){
            switch (nList.item(i).getNodeName()) {
                case "venta": case "estacion": case "detalle": case "catalogo": case "marca":
                case "proveedor": case "linea": case "renglon": case "menu": case "ubicaciones":{
                    NodeList list = nList.item(i).getChildNodes();
                    ContentValues insertDB = new ContentValues();
                    for (int j = 0; j < list.getLength(); j++) {
                        insertDB.put(list.item(j).getNodeName(), list.item(j).getTextContent());
                    }
                    if (insertDB.size() > 0)
                        insertarDatos(Tarea.getTarea(), insertDB, servicio);
                    break;
                }
                case "carta": {
                    NodeList list = nList.item(i).getChildNodes();
                    ContentValues insertDB = new ContentValues();
                    for (int j = 0; j < list.getLength(); j++)
                        insertDB.put(list.item(j).getNodeName(), list.item(j).getTextContent());

                    if (insertDB.size() > 0) {
                        insertarDatos(Tarea.getTarea(), insertDB, servicio);
                        insertDB.clear();
                    }
                    break;
                }
                case "articulos": {
                    NodeList list = nList.item(i).getChildNodes();
                    ContentValues insertDB = new ContentValues();
                    for (int j = 0; j < list.getLength(); j++) {
                        if (list.item(j).getNodeName().equals("articulo")) {
                            NodeList innerList = list.item(j).getChildNodes();
                            for (int k = 0; k < innerList.getLength(); k++)
                                insertDB.put(innerList.item(k).getNodeName(), innerList.item(k).getTextContent());

                            if (insertDB.size() > 0) {
                                insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                insertDB.clear();
                            }
                        }
                    }
                    break;
                }
                case "anexo": {
                    ContentValues insertDB = new ContentValues();
                    NodeList list = nList.item(i).getChildNodes();

                    for (int j = 0; j < list.getLength(); j++) {
                        switch (list.item(j).getNodeName()) {
                            case "producto":
                            case "linea": {
                                NodeList list2 = list.item(j).getChildNodes();
                                for (int k = 0; k < list2.getLength(); k++) {
                                    insertDB.put(list2.item(k).getNodeName(), list2.item(k).getTextContent());
                                }
                                if (insertDB.size() > 0)
                                    insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                break;
                            }
                            case "lineas": {
                                NodeList list2 = list.item(j).getChildNodes();
                                for (int k = 0; k < list2.getLength(); k++) {
                                    if (list2.item(k).getNodeName().equals("linea")) {
                                        NodeList list3 = list2.item(k).getChildNodes();
                                        for (int l = 0; l < list3.getLength(); l++) {
                                            insertDB.put(list3.item(l).getNodeName(), list3.item(l).getTextContent());
                                        }
                                        if (insertDB.size() > 0) {
                                            insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                            insertDB.clear();
                                        }
                                    }
                                }
                                break;
                            }
                            case "detalles": {
                                NodeList list2 = list.item(j).getChildNodes();
                                for (int k = 0; k < list2.getLength(); k++) {
                                    if (list2.item(k).getNodeName().equals("venta")) {
                                        NodeList list3 = list2.item(k).getChildNodes();
                                        if (list3.getLength() != 0 && list3.item(k).getNodeName().equals("detalle")) {
                                            NodeList list4 = list3.item(k).getChildNodes();
                                            for (int l = 0; l < list4.getLength(); l++) {
                                                insertDB.put(list4.item(l).getNodeName(), list4.item(l).getTextContent());
                                            }
                                            if (insertDB.size() > 0) {
                                                //System.out.println("INSERTDB: " + insertDB);
                                                insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                                insertDB.clear();
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case "encabezado": {
                                NodeList list2 = list.item(j).getChildNodes();
                                for (int k = 0; k < list2.getLength(); k++) {
                                    contentValues.put(list2.item(k).getNodeName(), list2.item(k).getTextContent());
                                }
                                break;
                            }
                            case "clientes": {
                                NodeList list2 = list.item(j).getChildNodes();
                                for (int k = 0; k < list2.getLength(); k++) {
                                    if (list2.item(k).getNodeName().equals("cliente")) {
                                        NodeList list3 = list2.item(k).getChildNodes();
                                        for (int l = 0; l < list3.getLength(); l++)
                                            insertDB.put(list3.item(l).getNodeName(), list3.item(l).getTextContent());

                                        if (insertDB.size() > 0) {
                                            insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                            insertDB.clear();
                                        }
                                    }
                                }
                                break;
                            }
                            default:
                                contentValues.put(list.item(j).getNodeName(), list.item(j).getTextContent());
                                break;
                        }
                    }
                    break;
                }
                case "detalles": {
                    NodeList list = nList.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        if (list.item(j).getNodeName().equals("detalle")) {
                            NodeList list2 = list.item(j).getChildNodes();
                            ContentValues insertDB = new ContentValues();
                            for (int k = 0; k < list2.getLength(); k++)
                                insertDB.put(list2.item(k).getNodeName(), list2.item(k).getTextContent());

                            if (insertDB.size() > 0) {
                                insertarDatos(Tarea.getTarea(), insertDB, servicio);
                                insertDB.clear();
                            }
                        }
                    }
                    break;
                }
                default:
                    contentValues.put(nList.item(i).getNodeName(), nList.item(i).getTextContent());
                    break;
            }
        }
        return contentValues;
    }

    /**
     * Realiza una inserción en una tabla de la Base de Datos A01.
     * @param tarea (int) recibe la tarea que se solicito, para conocer a que tabla se ingresaran los datos.
     * @param insertBD (ContentValues) contiene los datos a insertar en la base de datos
     * @param servicio (Servicio) sirve para realizar la conexión a la Base de Datos.
     */
    private static void insertarDatos(Enumeradores.Valores tarea, ContentValues insertBD, Servicio servicio){
        servicio.guardaBD(insertBD, tarea.getTablaBD());
    }

    /**
     * Retorna el tag correspondiente a una tarea
     * @param tarea La tarea
     * @return El tag
     */
    private static String getTag(Enumeradores.Valores tarea){
        return tarea.getTipoRespuesta();
    }
}
