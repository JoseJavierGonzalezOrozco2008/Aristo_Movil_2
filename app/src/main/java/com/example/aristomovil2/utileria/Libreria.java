package com.example.aristomovil2.utileria;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.google.zxing.BarcodeFormat;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * Clase que contiene funciones genericas para la utilización desde cualquier clase e incluso proyecto.
 */
public final class Libreria {
    /**
     *Verifica si un cadena es un numero entero
     * @param number (String) cadena que comprobara si es numero entero o no.
     * @return regresa verdadero si la cadena es un numero entero y en caso contrario regresa un falso.
     */
    public static Boolean isNumberInt(String number){
        try {
            Integer.parseInt(number);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Metodo que se encarga de comprobar si una variable (String) contiene informacion
     * @param pCadena (String) cadena que se confirmara si contiene algun caracter
     * @return regresa verdadero si contiene informacion y falso si no.
     */
    public static boolean tieneInformacion(String pCadena){
        return pCadena!=null && !pCadena.trim().equals("null") && !pCadena.trim().equals("");
    }

    /**
     * Método que ajusta la altura de un ListView a partir de un límite
     * @param list ListView a configurar
     * @param limite cantidad de items a mostrar (límite)
     */
     public static void ajustaAltoListView(ListView list, int limite){

         ListAdapter adapter = list.getAdapter();
         if (adapter == null) {
             return;
         }
         int totalItems = list.getCount();

         if (totalItems < limite) {
            int totalHeight = 0;

            for (int i = 0; i < totalItems; i++) {
                View listItem = adapter.getView(i, null, list);
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = list.getLayoutParams();
            params.height = totalHeight + (list.getDividerHeight() * (totalItems - 1));
            list.setLayoutParams(params);
         } else {
            View listItem = adapter.getView(0, null, list);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            ViewGroup.LayoutParams params = list.getLayoutParams();
            params.height = listItem.getMeasuredHeight() * limite + (list.getDividerHeight() * (limite - 1));
            list.setLayoutParams(params);
         }
     }


    /**
     * Evalua si un String tiene informacion ademas de que sea numero
     * @param pCadena (String) Cadena a Evaluar
     * @return  regresa el entero encontrado, en caso de que no sea numero o no hay informacion regresa 0
     */
    public static Integer tieneInformacionEntero(String pCadena){
        return tieneInformacionEntero(pCadena,0);
    }

    /**
     * Evalua si un String tiene informacion ademas de que sea numero
     * @param pCadena (String) Cadena a Evaluar
     * @param pDefault (Integer) Valor que se regresará en caso de ser vacio o no sea un numero
     * @return regresa el valor de la cadena en entero si fue correcto, si no el valor pDefault
     */
    public static Integer tieneInformacionEntero(String pCadena,Integer pDefault){
        return tieneInformacion(pCadena) && isNumberInt(pCadena) ? Integer.parseInt(pCadena):pDefault;
    }

    public static float tieneInformacionFloat(String pCadena,float pDefault){
        return tieneInformacion(pCadena) && (isNumberInt(pCadena) || isNumeric(pCadena)) ? Float.parseFloat(pCadena):pDefault;
    }
    public static String traeInfo(String pCadena,String pDefault){
        return tieneInformacion(pCadena) ? pCadena:pDefault;
    }

    public static String traeInfo(String pCadena){
        return traeInfo(pCadena,"");
    }

    /**
     * Metodo utilizado para comprobar que todas las casillas de un vector de String contiene solo digitos
     * @param spl (String []) variable que contiene informacion y se verificara si son puros digitos su contenido.
     * @return .
     */
    public static boolean allDigit(String [] spl){
        try{
            for (String s : spl) {
                Integer.parseInt(s);
            }
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }


    /**
     * Metodo utilizada para eliminar la parte decimal de un numero interpretado como cadena, en caso de que cuente con esta.
     * @param num (String) numero que se le quitara la parte decimal
     * @return regresa la parte entera del numero y en caso de que no haya numero retorna el 0
     */
    public static String quitaDecimal(String num){
        if(!num.equals("") && num.replace(".", "-").split("-").length>0)
            return num.replace(".", "-").split("-")[0];
        else
            return "0";
    }

    /**
     * Metodo que verifica que una cadena no tenga mas de 6 digitos y almenso uno
     * @param data (String) cadena que se verificara que tenga almenos 1 digito y no mas que 4.
     * @return regresa verdadero si cumple con las condiciones de 0<data<4 de lo contrario 0.
     */
    public static boolean countQuantity(String data){
        return data.length() <= 6 && data.length() >= 1 /*&& !data.equals("0")*/;
    }

    /**
     * Verifica si una direccion ip es valida
     * @param ip La ip
     * @return Retorna true si la ip es valida
     */
    public static boolean isIp(String ip){
        String [] spl = ip.replace("." , ":").split(":");
        return allDigit(spl) && spl.length == 4;
    }

    /**
     * Genera el XML correspondiente para insertar un renglon en una venta
     * @param estaid El id de la estacion
     * @param usuaid El id del usuario
     * @param cadena La cadena d isnercion
     * @param clteid El id del cliente
     * @param folio El folio de la venta
     * @param lastVenta El id del ultimo producto insertado
     * @param tipoventa El tipo de venta
     * @param notas Las notas de la venta
     * @param metpago El metodo de pago
     * @return El xml
     */
    public static String xmlInsertVenta(int estaid, String usuaid, String cadena, int clteid,
                                        String folio, int lastVenta, int tipoventa, String notas, boolean metpago){
        return "<linea>" +
                "<estacion>" + estaid + "</estacion> " +
                "<usuario>" + usuaid +"</usuario>" +
                "<cadena>" + cadena + "</cadena>" +
                "<metpago>" + (metpago? "99":"98") + "</metpago>" +
                "<cliente>" + clteid + "</cliente> " +
                "<venta>" + ((null != folio && !folio.equals("null") && !folio.equals("0"))? folio:"") + "</venta>" +
                "<dventa>" + lastVenta + "</dventa> " +
                "<traeauto>true</traeauto> " +
                "<supervisor>0</supervisor> " +
                "<notaventa>" + notas + "</notaventa>" +
                "<notadetalle>a</notadetalle>" +
                "<referencia>a</referencia>" +
                "<tipoventa>"+tipoventa+"</tipoventa> " +
                "<intentoret>1</intentoret> " +
                /*"<foliocons></foliocons> " +
                "<foliodomi></foliodomi> " +*/
                "<comensales>0</comensales> " +
                "</linea>";
    }

    /**
     * Convierte un string a boolean
     * @param cadena El string a convertir
     * @return El valor en booleano
     */
    public static boolean getBoolean(String cadena){
        return tieneInformacion(cadena) && (cadena.equals("1") || cadena.equals("true"));
    }

    /**
     *Valida si una fecha es valida
     * @param formato El formato de la fecha
     * @param fecha la fecha
     * @return Retorna true si la fecha es valida para el formato especificado
     */
    public static boolean validaFecha(String formato, String fecha){
        if((formato).equalsIgnoreCase("MMAA") )
            return fecha.length() == 4 && Integer.parseInt(fecha.substring(2,4)) <= 12 && Integer.parseInt(fecha.substring(2,4)) > 0;
        else if(formato.equalsIgnoreCase("DDMMAA") && fecha.length() == 6){
            if(Integer.parseInt(fecha.substring(2,4)) <= 12){
                switch (Integer.parseInt(fecha.substring(2,4))){
                    case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                        return Integer.parseInt(fecha.substring(4,6)) <= 31 && Integer.parseInt(fecha.substring(4,6)) > 0;
                    case 4: case 6: case 9: case 11:
                        return Integer.parseInt(fecha.substring(4,6)) <= 30 && Integer.parseInt(fecha.substring(4,6)) > 0;
                    case 2:
                        return Integer.parseInt(fecha.substring(4,6)) <= 28 && Integer.parseInt(fecha.substring(4,6)) > 0;
                }
            }
            else return false;
        }
        return false;
    }

    /**
     * Checa si la cadena es un numero
     * @param cadena Texto a comparar
     * @return true si es nuemero,false si no lo es o esta vacio
     */
    public static boolean isNumeric(String cadena){
        char[] chars = cadena.toCharArray();
        if(!tieneInformacion(cadena)){
            return false;
        }
        for(char c:chars){
            if(!Character.isDigit(c) && c!='-' && c!='.')
                return false;
        }

        return true;
    }

    /**
     * Formatea un texto de una fecha y regresa la misma fecha con un formato distinto
     * @param cadena    Cadena con la fecha a transformar
     * @param pOrigen   Formato de origen
     * @param pFinal    Formato de salida
     * @return  fecha formateada con pFinal si solo si cadena es distinto vacio y tiene el formato de pOrigen
     */
    public static String fecha_to_fecha(String cadena,String pOrigen,String pFinal){
        String salida="";
        try {
            SimpleDateFormat parser = new SimpleDateFormat(pOrigen);//"yyyy-MM-dd'T'HH:mm:ss"
            SimpleDateFormat formatter = new SimpleDateFormat(pFinal);//"dd-MM-yyyy"
            salida=formatter.format(Objects.requireNonNull(parser.parse(cadena)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return salida;
    }

    /**
     * Formatea una texto a fecha con el formato deseado
     * @param cadena    Cadena contenedora de la fecha
     * @param pFinal    Formato en que esta cadena
     * @return  Date formateada, null si no se pudo formatear o cadena es null
     */
    public static Date texto_to_fecha(String cadena, String pFinal){
        try {
            SimpleDateFormat parser = new SimpleDateFormat(pFinal);//"yyyy-MM-dd'T'HH:mm:ss"
            return Objects.requireNonNull(parser.parse(cadena));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Transforma un Date por un texto con un formato
     * @param fecha     Objeto a transformar
     * @param format    Formato de salida para el string
     * @return  regresa una cadena de texto con ek formato deseado, vacio o null si hubo un error en la transformacion
     */
    public static String dateToString(Date fecha, String format) {
        String fechaRet = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            fechaRet = sdf.format(fecha);
        } catch (Exception ex) {
            return "";
        }
        return fechaRet;
    }

    // Método para ajustar la altura del GridView
    /**
     * Ajusta la altura del GridView conforme sus elementos
     * @param gridView     Objeto tipo GridView
     * @param columnas    Número de columnas
     */
    public static void setGridViewHeightBasedOnChildren(GridView gridView, int columnas) {
        ListAdapter adapter = gridView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        int items = adapter.getCount();
        int rows = (int) Math.ceil((double) items / columnas);

        for (int i = 0; i < rows; i++) {
            View listItem = adapter.getView(i * columnas, null, gridView);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight += (gridView.getVerticalSpacing() * (rows - 1));

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    /**
     * Genera un xml apartir de un mapa,si un valor es "" o null se omite el tag del xml
     * @param lineCap   Contenido de los tag y sus valores,tag->key,value->value
     * @param pPadre    Tag raiz del xml
     * @return  String con el xml formado
     */
    public static String xmlLineaCapturaSV(ContentValues lineCap, String pPadre) {
        String salida = "";

        for (String llave : lineCap.keySet()) {
            if(tieneInformacion(String.valueOf(lineCap.getAsString(llave)))){
                salida += meteRenglon(String.valueOf(llave), lineCap.get(llave));
            }
        }
        salida = meteRenglon(pPadre, salida);
        return salida;
    }

    /**
     * genera un tag xml
     * @param pLlave    Nombre del tag
     * @param pInfo     El valor del tag
     * @return  tag del xml
     */
    public static String meteRenglon(String pLlave, Object pInfo) {
        String pcampo = "<##1##>" + (tieneInformacion(String.valueOf(pInfo)) ? pInfo : "") + "</##1##>";
        return pcampo.replace("##1##", pLlave);
    }

    public static boolean menorLimite(String pCadena,Integer pLimite){
        return tieneInformacion(pCadena) && isNumeric(pCadena) ? Float.parseFloat(pCadena) <= pLimite : false;
    }

    public static String RPAD(String pCadena,String pSec,Integer pLongitud){
        String retorno=pCadena;
        if(pLongitud>pCadena.length()){
            Integer pdif=pLongitud-pCadena.length();
            String cad="";
            for(int i=0;i<pdif;i++){
                cad += pSec;
            }
            retorno=pCadena+cad;
        }
        return retorno;
    }

    public static String LPAD(String pCadena,String pSec,Integer pLongitud){
        String retorno=pCadena;
        if(pLongitud>pCadena.length()){
            Integer pdif=pLongitud-pCadena.length();
            String cad="";
            for(int i=0;i<pdif;i++){
                cad += pSec;
            }
            retorno=cad+pCadena;
        }
        return retorno;
    }

    public static ServicioImpresora imprimeBulto(Bulto pBulto, ServicioImpresora pServicio,String ptitulo,String pUsuario,Boolean pCodbarras,Boolean pDetalles,int pEspacios){
        pServicio.addLine("Contenedor");
        ptitulo = ptitulo.length()>15 ? ptitulo.substring(0,15) : ptitulo;
        pServicio.addTitle(upper(ptitulo));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date="";
        try {
            if(tieneInformacion(date))
                date = formatter.format(Objects.requireNonNull(parser.parse(pBulto.getFecha())));
        } catch (ParseException e) {
            date = "Sin fecha";
            e.printStackTrace();
        }
        //impresora.addLine(date);
        String v_usuario=pUsuario;
        if(pUsuario.length()>=15){
            v_usuario=pUsuario.substring(0,14);
        }
        v_usuario=Libreria.RPAD(v_usuario," ",16);
        pServicio.addLine( v_usuario + date);
        pServicio.addLine("Rengs: " + pBulto.getRengs() + " Piezas: " + pBulto.getPiezas());
        //impresora.addEndLine();
        //impresora.addLine(bulto.getContenedor(), ServicioImpresora.CENTER, ServicioImpresora.TEXT_BIG, true, false);
        //impresora.addEndLine();
        pServicio.addLine("Folio: " + pBulto.getDcinfolio());
        pServicio.addLine(".");

        if (pCodbarras){
            pServicio.addBarcodeImage(pBulto.getContenedor(), 400, 100, BarcodeFormat.CODE_128);
        }
        pServicio.addTitle(pBulto.getContenedor());

        if(pDetalles && Libreria.tieneInformacion(pBulto.getDetalles())){
            pServicio.addLine("--------------------------------" );
            pServicio.addLine("Codigo       Producto       Cant" );
            pServicio.addLine("--------------------------------" );
            String detall[]=pBulto.getDetalles().split(",");
            String renglon_det;
            for(String detalle:detall){
                renglon_det=detalle.substring(0,32);
                pServicio.addLine(renglon_det);
            }
        }
        pServicio.addEndLine(pEspacios);

        return pServicio;
    }

    public static ServicioImpresora imprimeDI(Bulto pBulto, ServicioImpresora pServicio,String ptitulo,String pUsuario,Boolean pCodbarras,Boolean pDetalles,Integer pEspacios,boolean pMuestaImporte,ContentValues pFin){
        String pProv = pFin.getAsString("prov");
        if(pBulto!=null){
            pServicio=imprimeBulto(pBulto,pServicio,upper(pProv),pUsuario,pCodbarras,pDetalles,pEspacios);
        }
        String imprime = pFin.getAsString("imprime");
        if(Libreria.getBoolean(imprime)) {
            String pEmpresa = pFin.getAsString("empresa");
            String foliodi = pFin.getAsString("foliodi");
            String piezasdi = pFin.getAsString("piezasdi");
            String rengsdi = pFin.getAsString("rengsdi");
            String totaldi = pFin.getAsString("totaldi");
            String factura = pFin.getAsString("factura");
            String fechaFolio = pFin.getAsString("fechaFolio");
            pServicio.addLine(ptitulo);
            pServicio.addTitle(upper(pEmpresa));
            pServicio.addTitle(upper(pProv));
            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            pServicio.addLine("Folio: " + foliodi + "\n" + "Fech mov: " + fechaHoy);
            //pServicio.addLine(foliodi);
            if (tieneInformacion(factura) && tieneInformacion(fechaFolio)) {
                /*Elemetos recuperados de la pantalla de Compra*/
                pServicio.addLine("Fac: " + factura + " " + fechaFolio);
            }
            pServicio.addLine(pUsuario);
            pServicio.addLine("Rengs: " + rengsdi + " Piezas: " + piezasdi);
            if (pMuestaImporte) {
                pServicio.addLine("Total: " + totaldi);
            }
            if (pCodbarras)
                pServicio.addBarcodeImage(foliodi, 400, 100, BarcodeFormat.CODE_128);
            else
                pServicio.addBarcode(foliodi);
            for (int i = 0; i < pEspacios; i++)
                pServicio.addLine(".");
            pServicio.addEndLine(pEspacios);
        }
        return pServicio;
    }

    public static ServicioImpresora imprimeDISurt(ServicioImpresora pServicio,String ptitulo,String pUsuario,Boolean pCodbarras,Boolean pDetalles,Integer pEspacios,boolean pMuestaImporte,ContentValues pFin){
        String pProv = pFin.getAsString("prov");

        String imprime = pFin.getAsString("imprime");
        if(Libreria.getBoolean(imprime)) {
            String pEmpresa = pFin.getAsString("empresa");
            String foliodi = pFin.getAsString("foliodi");
            String piezasdi = pFin.getAsString("piezasdi");
            String rengsdi = pFin.getAsString("rengsdi");
            String totaldi = pFin.getAsString("totaldi");
            String factura = pFin.getAsString("factura");
            String fechaFolio = pFin.getAsString("fechaFolio");
            pServicio.addLine(ptitulo);
            pServicio.addTitle(upper(pEmpresa));
            pServicio.addTitle(upper(pProv));
            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            pServicio.addLine("Folio: " + foliodi + "\n" + "Fech mov: " + fechaHoy);
            //pServicio.addLine(foliodi);
            if (tieneInformacion(factura) && tieneInformacion(fechaFolio)) {
                /*Elemetos recuperados de la pantalla de Compra*/
                pServicio.addLine("Fac: " + factura + " " + fechaFolio);
            }
            pServicio.addLine(pUsuario);
            pServicio.addLine("Rengs: " + rengsdi + " Piezas: " + piezasdi);
            if (pMuestaImporte) {
                pServicio.addLine("Total: " + totaldi);
            }
            if (pCodbarras)
                pServicio.addBarcodeImage(foliodi, 400, 100, BarcodeFormat.CODE_128);
            else
                pServicio.addBarcode(foliodi);
            for (int i = 0; i < pEspacios; i++)
                pServicio.addLine(".");
            pServicio.addEndLine(pEspacios);
        }
        return pServicio;
    }

    public static ServicioImpresora imprimeSol(ServicioImpresora pServicio,String pDetalles,Integer pEspacios){
        if(Libreria.tieneInformacion(pDetalles)){
            String detall[]=pDetalles.split(Pattern.quote("|")),lexico[];
            String texto;
            for(String linea:detall){
                lexico=linea.split(",");
                if(lexico.length>1){
                    texto=lexico[0].replace(";",",");
                    if(Libreria.tieneInformacion(texto)){
                        switch (lexico[1]){
                            case "T1":pServicio.addLine(texto);break;
                            case "T2":pServicio.addTitle(texto);break;
                            case "**":pServicio.addBarcodeImage(texto, 400, 100, BarcodeFormat.CODE_128);break;
                            case "T1n":pServicio.addLine(texto,ServicioImpresora.CENTER,ServicioImpresora.TEXT_NORMAL,true,false);break;
                            case "T2n":pServicio.addTitle(texto,true);break;
                            case "T1w":pServicio.addLine(texto,ServicioImpresora.CENTER,ServicioImpresora.TEXT_WIDE);break;
                        }
                    }
                }
            }
        }
        pServicio.addEndLine(pEspacios);
        return pServicio;
    }

    public static String upper(String pCadena){
        return tieneInformacion(pCadena) ? pCadena.toUpperCase() : "";
    }

    public static Bitmap recuperaFoto(String pData){
        Bitmap bm=null;
        try{
            if(tieneInformacion(pData)){
                byte [] data = Base64.decode(pData, Base64.DEFAULT);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            }
        }   catch(Exception e){
            System.out.println(e);
        }
        return bm;
    }

    public static TextView columnH(String pCadena, Integer pColor, Context pContext){
        TextView header = new TextView(pContext);
        header.setText(pCadena);
        header.setTextColor(pColor);
        header.setTextSize(22);
        header.setPadding(10, 10, 10, 10);
        return header;
    }

    public static TextView columnInfo(String pCadena, Integer pColor, Context pContext){
        TextView header = new TextView(pContext);
        header.setText(pCadena);
        header.setTextColor(pColor);
        header.setTextSize(20);
        header.setPadding(10, 5, 10, 5);
        return header;
    }

    public static String remplazaXml(String pCadena){
        String retorno = pCadena;
        String[] cadenas=new String[]{"&amp;","&lt;","&gt;","&apos;","&quot;"};
        String[] alias=new String[]{"&","<",">","'","\""};
        for(int i=0;i<cadenas.length;i++){
            retorno = retorno.replace(alias[i],cadenas[i]);
        }
        return retorno;
    }

    public static ContentValues traeDatosURL(String pURL){
        ContentValues mapa =new ContentValues();
        if(tieneInformacion(pURL) && pURL.toUpperCase().contains("HTTP")){
            mapa.put("url",pURL.substring(0,pURL.indexOf("?")));
            String cadena1 = pURL.substring(pURL.indexOf("?")+1);
            String[] datos,cadenas=cadena1.split("&");
            for(int i=0;i<cadenas.length;i++){
                datos = cadenas[i].split("=");
                mapa.put(datos[0],datos.length>1?datos[1]:"");
            }
        }
        return mapa;
    }

    public static String toHtml(String pCadena){
        String sIT="";
        if(tieneInformacion(pCadena)){
            String detall[]=pCadena.split(Pattern.quote("|")),lexico[];
            String texto;
            for(String linea:detall){
                lexico=linea.split(",");
                if(lexico.length>1){
                    texto=lexico[0].replace(";",",");
                    if(tieneInformacion(texto)){
                        switch (lexico[1]){
                            case "T1":sIT+= MessageFormat.format("<div  align=\"center\">{0}</div><br/>",lexico[0]);break;
                            case "T2":sIT+= MessageFormat.format("<center><h1>{0}</h1></center><br/>",lexico[0]);break;
                            case "T1n":sIT+= MessageFormat.format("<left><span>{0}</span></left><br/>",lexico[0]);break;
                            case "T2n":sIT+= MessageFormat.format("<center><h1><b>{0}</b></h1></center><br/>",lexico[0]);break;
                            case "T1w":sIT+= MessageFormat.format("<right><p>{0}</p></right><br/>",lexico[0]);break;
                            case "**":
                            case "QRC":
                            case "QRM":
                            case "QRG":
                            case "CT":sIT+= MessageFormat.format("",lexico[0]);break;
                        }
                    }
                }
            }
        }
        return sIT;
    }
}
