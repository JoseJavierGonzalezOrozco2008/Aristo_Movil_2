package com.example.aristomovil2.facade;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.aristomovil2.modelos.Cuenta;
import com.example.aristomovil2.modelos.Detviaje;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.RenglonCalcula;
import com.example.aristomovil2.modelos.RenglonRepo;
import com.example.aristomovil2.modelos.Reposicion;
import com.example.aristomovil2.modelos.Asignacion;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.modelos.Caducidad;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.modelos.Documento;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Cobrados;
import com.example.aristomovil2.modelos.Contados;
import com.example.aristomovil2.modelos.DiferenciasCaducidades;
import com.example.aristomovil2.modelos.Estacion;
import com.example.aristomovil2.modelos.Lote;
import com.example.aristomovil2.modelos.MenuItem;
import com.example.aristomovil2.modelos.ProductoDI;
import com.example.aristomovil2.modelos.RenglonEnvio;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.ProductosUbicacion;
import com.example.aristomovil2.modelos.Proveedor;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.modelos.Subalmacen;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.modelos.Ubikprod;
import com.example.aristomovil2.modelos.UtilViaje;
import com.example.aristomovil2.modelos.Ventas;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Viaje;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.Libreria;

/**
 * Clase para interactuar con la base de datos
 */
public class Servicio {
    private final Abstract db;

    /**
     * Constructor de la clase
     * @param context Contexto de la aplicacion
     */
    public Servicio(Context context){
        db = new Abstract(context, Estatutos.NOMBRE_BD, null, Estatutos.VERSION_BD);
    }

    /**
     * Guarda un registro en la base de datos
     * @param insertBD Valores del registro
     * @param tablaBD Tabla donde se debe insertar
     */
    public void guardaBD(ContentValues insertBD, String tablaBD) {
        db.abreConexion();
        db.alta(tablaBD, insertBD, SQLiteDatabase.CONFLICT_REPLACE);
        db.cierraConexion();
    }

    /**
     * Elimina todos los registros de una tabla
     * @param pTabla Tabla a borrar
     */
    public void borraDatosTabla(String pTabla){
        db.abreConexion();
        try{
            db.borra(pTabla,"");
        }catch(Exception a){
            System.out.println(a);
        }        finally {
            db.cierraConexion();
        }
    }

    /**
     * Retorna todos los registros de la tabla estaciones
     * @return Lista de estaciones
     */
    @SuppressLint("Range")
    public List<Estacion> getEstaciones(){
        List<Estacion> estaciones = new ArrayList<>();

        db.abreConexion();
        try (Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_ESTACIONES, null)) {
            if(cursor.moveToFirst()){
                do{
                    int estaid = cursor.getInt(cursor.getColumnIndex("estaid"));
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
                    boolean asignada = cursor.getString(cursor.getColumnIndex("asignada")).equals("true");
                    Estacion obj = new Estacion(estaid, nombre, tipo, asignada);
                    estaciones.add(obj);
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }

        return estaciones;
    }

    /**
     * Retorna todos los registros de la tabla productos_ubicacion
     * @return Lista de productos por ubicacion
     */
    @SuppressLint("Range")
    public List<ProductosUbicacion> getProductosUbicacion() {
        List<ProductosUbicacion> productos = new ArrayList<>();

        db.abreConexion();
        try (Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_PRODUCTOS_UBICACION, null)) {
            if(cursor.moveToFirst()){
                do{
                    int productoID = cursor.getInt(cursor.getColumnIndex("prodid"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    boolean activo = cursor.getString(cursor.getColumnIndex("activo")).equals("true");
                    int cmax = cursor.getInt(cursor.getColumnIndex("cmax"));
                    int cmin = cursor.getInt(cursor.getColumnIndex("cmin"));
                    boolean lleno = cursor.getString(cursor.getColumnIndex("lleno")).equals("true");
                    ProductosUbicacion obj = new ProductosUbicacion(productoID, codigo, producto, activo, cmax, cmin, lleno);
                    productos.add(obj);
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }

        return productos;
    }

    /**
     * Retorna las opciones de menu principal guardadas en la base de datos
     * @return Lista de opciones
     */
    @SuppressLint("Range")
    public ArrayList<MenuItem> getItemsMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();

        db.abreConexion();
        try (Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_ITEMS_MENU + " ORDER BY orden", null)) {
            if(cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    int orden = cursor.getInt(cursor.getColumnIndex("orden"));
                    String texto = cursor.getString(cursor.getColumnIndex("texto"));
                    String imagen = cursor.getString(cursor.getColumnIndex("imagen"));
                    String grupo = cursor.getString(cursor.getColumnIndex("grupo"));
                    String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
                    MenuItem item = new MenuItem(id,orden,texto,imagen, grupo, titulo);
                    items.add(item);
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }

        return  items;
    }

    /**
     * Retorna las opciones de menu principal guardadas en la base de datos
     * @return Lista de opciones
     */
    @SuppressLint("Range")
    public boolean hayMenuUbicacion(){
        boolean retorno = false;

        db.abreConexion();
        try (Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_ITEMS_MENU + " WHERE id=1 ", null)) {
            retorno = cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }

        return  retorno;
    }

    @SuppressLint("Range")
    public ArrayList<MenuItem> getItemsMenu(String grup){
        ArrayList<MenuItem> items = new ArrayList<>();

        db.abreConexion();
        try (Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_ITEMS_MENU + " WHERE grupo = '" + grup + "' ORDER BY orden", null)) {
            if(cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    int orden = cursor.getInt(cursor.getColumnIndex("orden"));
                    String texto = cursor.getString(cursor.getColumnIndex("texto"));
                    String imagen = cursor.getString(cursor.getColumnIndex("imagen"));
                    String grupo = cursor.getString(cursor.getColumnIndex("grupo"));
                    String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
                    MenuItem item = new MenuItem(id,orden,texto,imagen, grupo, titulo);
                    items.add(item);
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }
        return  items;
    }

    @SuppressLint("Range")
    public ArrayList<String> getGruposName(){
        ArrayList<String> grupos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT DISTINCT grupo FROM " + Estatutos.TABLA_ITEMS_MENU, null)){
            if(cursor.moveToFirst()){
                do{
                    String grupo = cursor.getString(cursor.getColumnIndex("grupo"));
                    grupos.add(grupo);
                }while (cursor.moveToNext());
            }
        }

        return grupos;
    }

    /**
     * Retorna todos los registros de la tabla productos
     * @param context Contexto de la aplicacion
     * @return La lista de productos
     */
    @SuppressLint("Range")
    public ArrayList<Producto> getProductos(Context context){
        ArrayList<Producto> productos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_PRODUCTOS, null)){
            if(cursor.moveToFirst()){
                do{
                    int prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    String codigo  = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    float precio = cursor.getFloat(cursor.getColumnIndex("precio"));
                    int renglones = cursor.getInt(cursor.getColumnIndex("renglones"));
                    int preciomax = cursor.getInt(cursor.getColumnIndex("preciomax"));
                    int preciomin = cursor.getInt(cursor.getColumnIndex("preciomin"));
                    int disponible = cursor.getInt(cursor.getColumnIndex("disponible"));
                    String preciovolumen = cursor.getString(cursor.getColumnIndex("preciovolumen"));
                    String promo = cursor.getString(cursor.getColumnIndex("promo"));
                    String foto = cursor.getString(cursor.getColumnIndex("foto"));

                    Bitmap bm;
                    if (foto.startsWith("\\") || foto.equals("") || foto.isEmpty())
                        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.sinfoto);
                    else{
                        byte [] data = Base64.decode(foto, Base64.DEFAULT);
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inMutable = true;
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                    }
                    productos.add(new Producto(prodid, codigo, producto, precio, renglones, preciomax, preciomin, disponible, preciovolumen, promo, bm));
                }while (cursor.moveToNext());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return productos;
    }

    /**
     * Retorna todos los registros de la tabla ventas
     * @return La lista de ventas
     */
    @SuppressLint("Range")
    public ArrayList<Ventas> getVentas(){
        ArrayList<Ventas> ventas = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_VENTAS, null)){
            if(cursor.moveToFirst()){
                do{
                    String folio = cursor.getString(cursor.getColumnIndex("folio"));
                    String consumidor = cursor.getString(cursor.getColumnIndex("consumidor"));
                    String domicilio = cursor.getString(cursor.getColumnIndex("domicilio"));
                    String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    String nombrecliente = cursor.getString(cursor.getColumnIndex("nombrecliente"));
                    String tienecredito = cursor.getString(cursor.getColumnIndex("tienecredito"));
                    String credito = cursor.getString(cursor.getColumnIndex("credito"));
                    String titulo = cursor.getString(cursor.getColumnIndex("vntatitulo"));
                    int tipo = cursor.getInt(cursor.getColumnIndex("tipo"));
                    int usuario = cursor.getInt(cursor.getColumnIndex("usuario"));
                    int cliente = cursor.getInt(cursor.getColumnIndex("cliente"));
                    int estacion = cursor.getInt(cursor.getColumnIndex("estacion"));
                    int porcocinar = cursor.getInt(cursor.getColumnIndex("porcocinar"));
                    int telefono = cursor.getInt(cursor.getColumnIndex("telefono"));

                    ventas.add(new Ventas(folio, tipo, usuario, consumidor, domicilio, cliente, estacion, porcocinar, fecha, telefono, notas, nombrecliente, tienecredito, credito, titulo));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return ventas;
    }

    /**
     * Retorna todos los registros de la tabla ventas
     * @return La lista de ventas
     */
    @SuppressLint("Range")
    public Ventas traeVentaPorFolio(String pFolio){
        Ventas ventas = null;

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_VENTAS +" WHERE folio='"+pFolio+"' ", null)){
            if(cursor.moveToFirst()){
                do{
                    String folio = cursor.getString(cursor.getColumnIndex("folio"));
                    String consumidor = cursor.getString(cursor.getColumnIndex("consumidor"));
                    String domicilio = cursor.getString(cursor.getColumnIndex("domicilio"));
                    String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    String nombrecliente = cursor.getString(cursor.getColumnIndex("nombrecliente"));
                    String tienecredito = cursor.getString(cursor.getColumnIndex("tienecredito"));
                    String credito = cursor.getString(cursor.getColumnIndex("credito"));
                    String titulo = cursor.getString(cursor.getColumnIndex("vntatitulo"));
                    String regimen = cursor.getString(cursor.getColumnIndex("regimen"));
                    String rfc = cursor.getString(cursor.getColumnIndex("rfc"));
                    int tipo = cursor.getInt(cursor.getColumnIndex("tipo"));
                    int usuario = cursor.getInt(cursor.getColumnIndex("usuario"));
                    int cliente = cursor.getInt(cursor.getColumnIndex("cliente"));
                    int estacion = cursor.getInt(cursor.getColumnIndex("estacion"));
                    int porcocinar = cursor.getInt(cursor.getColumnIndex("porcocinar"));
                    int telefono = cursor.getInt(cursor.getColumnIndex("telefono"));
                    String pidefac = cursor.getString(cursor.getColumnIndex("solofac"));
                    Integer empr = cursor.getInt(cursor.getColumnIndex("empr"));

                    ventas = new Ventas(folio, tipo, usuario, consumidor, domicilio, cliente, estacion, porcocinar, fecha, telefono, notas, nombrecliente, tienecredito, credito, titulo);
                    ventas.setPideFac(Libreria.getBoolean(pidefac));
                    ventas.setRegimen(regimen);
                    ventas.setRfc(rfc);
                    ventas.setEmpr(empr);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return ventas;
    }

    /**
     * Retorna todos los registros de la tabla renglones
     * @param context Contexto de la aplicacion
     * @param folioVenta el folio de la venta
     * @return La lista de renglones
     */
    @SuppressLint("Range")
    public ArrayList<Renglon> getRenglones(Context context, String folioVenta){
        ArrayList<Renglon> renglones = new ArrayList<>();

        db.abreConexion();
        String[] params = {folioVenta};
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_RENGLONES + " WHERE folio=? ORDER BY dvtaid DESC", params)){
            if(cursor.moveToFirst()){
                do{
                    String folio = cursor.getString(cursor.getColumnIndex("folio"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String fraccionable = cursor.getString(cursor.getColumnIndex("fraccionable"));
                    String refer = cursor.getString(cursor.getColumnIndex("refer"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    int dvtaid = cursor.getInt(cursor.getColumnIndex("dvtaid"));
                    int prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    int estatus = cursor.getInt(cursor.getColumnIndex("estatus"));
                    float cant = cursor.getFloat(cursor.getColumnIndex("cant"));
                    float precio = cursor.getFloat(cursor.getColumnIndex("precio"));
                    float dscto = cursor.getFloat(cursor.getColumnIndex("dscto"));
                    float dsctoad = cursor.getFloat(cursor.getColumnIndex("dsctoad"));
                    float subtotal = cursor.getFloat(cursor.getColumnIndex("subtotal"));
                    float impuesto = cursor.getFloat(cursor.getColumnIndex("impuesto"));
                    float total = cursor.getFloat(cursor.getColumnIndex("total"));
                    float vntatotal = cursor.getFloat(cursor.getColumnIndex("vntatotal"));
                    float disponible = cursor.getFloat(cursor.getColumnIndex("disponible"));
                    float futura = cursor.getFloat(cursor.getColumnIndex("futura"));
                    float caduca = cursor.getFloat(cursor.getColumnIndex("caduca"));
                    String foto = cursor.getString(cursor.getColumnIndex("foto"));

                    Bitmap bm;
                    if (foto.startsWith("\\") || foto.equals("") || foto.isEmpty())
                        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.sinfoto);
                    else{
                        byte [] data = Base64.decode(foto, Base64.DEFAULT);
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inMutable = true;
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                    }
                    renglones.add(new Renglon(folio, codigo, producto, fraccionable, refer, notas, bm, dvtaid, prodid, estatus, cant, precio, dscto, dsctoad, subtotal, impuesto, total, vntatotal, disponible, futura, caduca));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return renglones;
    }

    /**
     * Retorna todos los registros de la tabla clientes
     * @return La lista de clientes
     */
    @SuppressLint("Range")
    public ArrayList<Cliente> getClientes(){
        ArrayList<Cliente> clientes = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_CLIENTES, null)){
            if(cursor.moveToFirst()){
                do{
                    int clteid = cursor.getInt(cursor.getColumnIndex("clteid"));
                    String cliente = cursor.getString(cursor.getColumnIndex("cliente"));
                    String razon = cursor.getString(cursor.getColumnIndex("razon"));
                    String domicilio = cursor.getString(cursor.getColumnIndex("domicilio"));
                    String rfc = cursor.getString(cursor.getColumnIndex("rfc"));
                    boolean credito = cursor.getString(cursor.getColumnIndex("credito")).equals("true");
                    boolean vntacredito = cursor.getString(cursor.getColumnIndex("vntacredito")).equals("true");

                    clientes.add(new Cliente(clteid, cliente, razon, credito, domicilio, rfc, vntacredito));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return clientes;
    }

    /**
     * Retorna todos los registros de la tabla clientes
     * @return La lista de clientes
     */
    @SuppressLint("Range")
    public ArrayList<String> traeDcatalogo(Integer pCatalogo){
        ArrayList<String> listaDcatalogos = new ArrayList<>();
        String abrevi;
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT abrevi FROM " + Estatutos.TABLA_DCATALOGO+" WHERE cata="+pCatalogo+" ORDER BY n2,n1", null)){
            if(cursor.moveToFirst()){
                do{
                    abrevi = cursor.getString(cursor.getColumnIndex("abrevi"));
                    listaDcatalogos.add(abrevi);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return listaDcatalogos;
    }

    @SuppressLint("Range")
    public ArrayList<String> traeMargenes(){
        ArrayList<String> listaDcatalogos = new ArrayList<>();
        String abrevi;
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT abrevi FROM " + Estatutos.TABLA_DCATALOGO+" WHERE cata=-1 AND id>1 ORDER BY n2,n1", null)){
            if(cursor.moveToFirst()){
                do{
                    abrevi = cursor.getString(cursor.getColumnIndex("abrevi"));
                    listaDcatalogos.add(abrevi);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return listaDcatalogos;
    }

    @SuppressLint("Range")
    public ArrayList<String> traeMargen(Integer pCatalogo){
        ArrayList<String> listaDcatalogos = new ArrayList<>();
        String abrevi;
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT abrevi FROM " + Estatutos.TABLA_DCATALOGO+" WHERE cata=-1 AND e1="+pCatalogo+" ORDER BY n2,n1", null)){
            if(cursor.moveToFirst()){
                do{
                    abrevi = cursor.getString(cursor.getColumnIndex("abrevi"));
                    listaDcatalogos.add(abrevi);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return listaDcatalogos;
    }

    @SuppressLint("Range")
    public Integer traePosicion(Integer pCatalogo,Integer pId){
        ArrayList<Integer> listaDcatalogos = new ArrayList<>();
        Integer abrevi = 0,retorno = 0;
        if(pId!=null){
            db.abreConexion();
            try(Cursor cursor = db.getDatabase().rawQuery("SELECT (SELECT COUNT()-1 ordenes FROM dcatalogo b WHERE a.cata=b.cata AND a.id>=b.id ORDER BY b.n2,b.n1 ) orden,id FROM " + Estatutos.TABLA_DCATALOGO+" a WHERE cata="+pCatalogo+" ORDER BY n2,n1", null)){
                if(cursor.moveToFirst()){
                    do{
                        abrevi = cursor.getInt(cursor.getColumnIndex("id"));
                        if(pId.compareTo(abrevi)==0){
                            retorno = cursor.getInt(cursor.getColumnIndex("orden"));
                            break;
                        }
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                db.cierraConexion();
            }
        }
        return retorno;
    }

    public ArrayList<Generica> traeDcatGenerica(Integer pCatalogo){
        ArrayList<Generica> listaDcatalogos = new ArrayList<>();
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT id,abrevi tex1,cata ent1,t1 tex2 FROM " + Estatutos.TABLA_DCATALOGO+" WHERE cata="+pCatalogo+" ORDER BY id DESC", null)){
            if(cursor.moveToFirst()){
                do{
                    listaDcatalogos.add(Generica.leerCursor2(cursor,"tex1,ent1,tex2"));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return listaDcatalogos;
    }

    @SuppressLint("Range")
    public Integer traeDcatIdporAbrevi(Integer pCatalogo,String pAbrevi){
        Integer grupos = 0;
        db.abreConexion();
        String[] where= {pCatalogo+"",pAbrevi};
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT id FROM dcatalogo  WHERE cata=? AND abrevi=?", where)){
            if(cursor.moveToFirst()){
                do{
                    grupos =cursor.getInt(cursor.getColumnIndex("id"));
                }while (cursor.moveToNext());
            }
        }
        return grupos;
    }

    @SuppressLint("Range")
    public String traeAbreviPorCata(Integer pCatalogo,Integer pDcatid){
        String grupos = "";
        db.abreConexion();
        String[] where= {pCatalogo+"",pDcatid+""};
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT abrevi FROM dcatalogo  WHERE cata=? AND id=?", where)){
            if(cursor.moveToFirst()){
                do{
                    grupos =cursor.getString(cursor.getColumnIndex("abrevi"));
                }while (cursor.moveToNext());
            }
        }
        return grupos;
    }

    /**
     * Retorna todos los registros de la tabla clientes
     * @return La lista de clientes
     */
    @SuppressLint("Range")
    public Cliente traeClientePorId(Integer pId){
        Cliente retorno=null;
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_CLIENTES+" WHERE clteid="+pId, null)){
            if(cursor.moveToFirst()){
                do{
                    int clteid = cursor.getInt(cursor.getColumnIndex("clteid"));
                    String cliente = cursor.getString(cursor.getColumnIndex("cliente"));
                    String razon = cursor.getString(cursor.getColumnIndex("razon"));
                    String domicilio = cursor.getString(cursor.getColumnIndex("domicilio"));
                    String rfc = cursor.getString(cursor.getColumnIndex("rfc"));
                    boolean credito = cursor.getString(cursor.getColumnIndex("credito")).equals("true");
                    boolean vntacredito = cursor.getString(cursor.getColumnIndex("vntacredito")).equals("true");
                    retorno = new Cliente(clteid, cliente, razon, credito, domicilio, rfc, vntacredito);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return retorno;
    }

    /**
     * Retorna todos los registros de la tabla cobrados
     * @return La lista de cobrados
     */
    @SuppressLint("Range")
    public ArrayList<Cobrados> getCobrados(){
        ArrayList<Cobrados> cobrados = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_PRODUCTOS_COBRADOS, null)){
            if(cursor.moveToFirst()){
                do{
                    int cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    float preciou = cursor.getFloat(cursor.getColumnIndex("preciou"));
                    float subtotal = cursor.getFloat(cursor.getColumnIndex("subtotal"));

                    cobrados.add(new Cobrados(producto, cantidad, preciou, subtotal));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return cobrados;
    }

    /**
     * Retorna todos los registros de la tabla catalogos
     * @return La lista de catalogos
     */
    @SuppressLint("Range")
    public ArrayList<Documento> getCatalogos(boolean captura){
        ArrayList<Documento> catalogos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * from catalogos where coalesce(foliodi, '') " + (captura?"=": "<>") + " '' ", null)){//ORDER BY prior DESC
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                String bulto="",foliodi,proveedor,factura,orcofe,estatusdi,operador,vntafolio,surtidor,prioridad,ruta,texto;
                int grupo,provid,pedido,rengsoc,surtid,diascred,prio;
                float importe,divisa;
                Documento doc;
                do{
                    foliodi = cursor.getString(cursor.getColumnIndex("foliodi"));
                    proveedor = cursor.getString(cursor.getColumnIndex("proveedor"));
                    factura = cursor.getString(cursor.getColumnIndex("factura"));
                    orcofe = cursor.getString(cursor.getColumnIndex("orcofe"));
                    estatusdi = cursor.getString(cursor.getColumnIndex("estatusdi"));
                    operador = cursor.getString(cursor.getColumnIndex("operador"));
                    vntafolio = cursor.getString(cursor.getColumnIndex("vntafolio"));
                    surtidor = cursor.getString(cursor.getColumnIndex("surtidor"));
                    prioridad = cursor.getString(cursor.getColumnIndex("prioridad"));
                    grupo = cursor.getInt(cursor.getColumnIndex("grupo"));
                    provid = cursor.getInt(cursor.getColumnIndex("provid"));
                    pedido = cursor.getInt(cursor.getColumnIndex("pedido"));
                    rengsoc = cursor.getInt(cursor.getColumnIndex("rengsoc"));
                    surtid = cursor.getInt(cursor.getColumnIndex("surtid"));
                    prio = cursor.getInt(cursor.getColumnIndex("prior"));
                    importe = cursor.getFloat(cursor.getColumnIndex("importe"));
                    divisa = cursor.getFloat(cursor.getColumnIndex("divisa"));
                    diascred = cursor.getInt(cursor.getColumnIndex("diascred"));
                    bulto = cursor.getString(cursor.getColumnIndex("bulto"));
                    ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                    texto = cursor.getString(cursor.getColumnIndex("texto"));
                    doc=new Documento(foliodi, proveedor, factura, orcofe, estatusdi, operador, vntafolio, surtidor, provid, pedido, importe, rengsoc, grupo, divisa, surtid,diascred,bulto,prioridad);
                    doc.setPrio(prio);
                    doc.setRuta(ruta);
                    doc.setRuta(texto);
                    catalogos.add(doc);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return catalogos;
    }

    @SuppressLint("Range")
    public Documento traeCatalogoPorFolioDI(String folioDI){
        Documento doc=null;

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * from catalogos where coalesce(foliodi, '') = '" + folioDI + "' ", null)){//ORDER BY prior DESC
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                String bulto="",foliodi,proveedor,factura,orcofe,estatusdi,operador,vntafolio,surtidor,prioridad,ruta,texto;
                int grupo,provid,pedido,rengsoc,surtid,diascred,prio;
                float importe,divisa;
                do{
                    foliodi = cursor.getString(cursor.getColumnIndex("foliodi"));
                    proveedor = cursor.getString(cursor.getColumnIndex("proveedor"));
                    factura = cursor.getString(cursor.getColumnIndex("factura"));
                    orcofe = cursor.getString(cursor.getColumnIndex("orcofe"));
                    estatusdi = cursor.getString(cursor.getColumnIndex("estatusdi"));
                    operador = cursor.getString(cursor.getColumnIndex("operador"));
                    vntafolio = cursor.getString(cursor.getColumnIndex("vntafolio"));
                    surtidor = cursor.getString(cursor.getColumnIndex("surtidor"));
                    prioridad = cursor.getString(cursor.getColumnIndex("prioridad"));
                    grupo = cursor.getInt(cursor.getColumnIndex("grupo"));
                    provid = cursor.getInt(cursor.getColumnIndex("provid"));
                    pedido = cursor.getInt(cursor.getColumnIndex("pedido"));
                    rengsoc = cursor.getInt(cursor.getColumnIndex("rengsoc"));
                    surtid = cursor.getInt(cursor.getColumnIndex("surtid"));
                    prio = cursor.getInt(cursor.getColumnIndex("prior"));
                    importe = cursor.getFloat(cursor.getColumnIndex("importe"));
                    divisa = cursor.getFloat(cursor.getColumnIndex("divisa"));
                    diascred = cursor.getInt(cursor.getColumnIndex("diascred"));
                    bulto = cursor.getString(cursor.getColumnIndex("bulto"));
                    ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                    texto = cursor.getString(cursor.getColumnIndex("texto"));
                    doc=new Documento(foliodi, proveedor, factura, orcofe, estatusdi, operador, vntafolio, surtidor, provid, pedido, importe, rengsoc, grupo, divisa, surtid,diascred,bulto,prioridad);
                    doc.setPrio(prio);
                    doc.setRuta(ruta);
                    doc.setRuta(texto);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return doc;
    }

    /**
     * Retorna todos los registros de la tabla proveedores
     * @return La lista de proveedores
     */
    @SuppressLint("Range")
    public  ArrayList<Proveedor> getProveedores(){
        ArrayList<Proveedor> proveedores = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_PROVEEDORES, null)){
            if(cursor.moveToFirst()){
                do{
                    String rfc = cursor.getString(cursor.getColumnIndex("rfc"));
                    String aliasp = cursor.getString(cursor.getColumnIndex("aliasp"));
                    String rsocial = cursor.getString(cursor.getColumnIndex("rsocial"));
                    int provid = cursor.getInt(cursor.getColumnIndex("provid"));
                    int diascred = cursor.getInt(cursor.getColumnIndex("diascred"));

                    proveedores.add(new Proveedor(rfc, aliasp, rsocial, provid,diascred));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return proveedores;
    }

    /**
     * Retorna todos los registros de la tabla subalmacenes
     * @return La lista de subalmacenes
     */
    @SuppressLint("Range")
    public ArrayList<Subalmacen> getSubalmacenes(){
        ArrayList<Subalmacen> subalmacenes = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_SUBALMACENES, null)){
            if(cursor.moveToFirst()){
                do{
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    int dcatid = cursor.getInt(cursor.getColumnIndex("dcatid"));

                    subalmacenes.add(new Subalmacen(nombre, dcatid));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return subalmacenes;
    }

    @SuppressLint("Range")
    public ArrayList<String> getDocumentosGrupo(){
        ArrayList<String> grupos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT DISTINCT pediid,substr(dcinfolio,1,1)||'*'||substr(dcinfolio,-4,3) dcinfolio,substr(vntafolio,1,1)||'*'||substr(vntafolio,-4,3) vntafolio FROM renglon r INNER JOIN catalogos c ON r.pediid=c.pedido", null)){
            if(cursor.moveToFirst()){
                do{
                    String venta=cursor.getString(cursor.getColumnIndex("vntafolio"));
                    String item = cursor.getString(cursor.getColumnIndex("pediid")) + ";" + cursor.getString(cursor.getColumnIndex("dcinfolio"))+ (!Libreria.tieneInformacion(venta)? "":(";" + venta));
                    grupos.add(item);
                }while (cursor.moveToNext());
            }
        }

        return grupos;
    }

    @SuppressLint("Range")
    public ArrayList<RenglonEnvio> getRenglones(String grupo){
        ArrayList<RenglonEnvio> renglones = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM renglon WHERE pediid = " + grupo + "  ORDER BY(numrenglon)", null)){
            if(cursor.moveToFirst()){
                do{
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                    String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    String pediid = cursor.getString(cursor.getColumnIndex("pediid"));
                    String dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                    int numrenglon = cursor.getInt(cursor.getColumnIndex("numrenglon"));
                    int numrengs = cursor.getInt(cursor.getColumnIndex("numrengs"));
                    int ddin = cursor.getInt(cursor.getColumnIndex("ddin"));
                    float cantpedida = cursor.getFloat(cursor.getColumnIndex("cantpedida"));
                    float ingresado = cursor.getFloat(cursor.getColumnIndex("ingresado"));
                    float dispo = cursor.getFloat(cursor.getColumnIndex("dispo"));
                    float anaquel = cursor.getFloat(cursor.getColumnIndex("anaquel"));
                    float futura = cursor.getFloat(cursor.getColumnIndex("futura"));

                    renglones.add(new RenglonEnvio(numrenglon, numrengs, ddin, codigo, producto, ubicacion, faltacadu, notas, pediid, dcinfolio, cantpedida, ingresado, dispo, anaquel, futura));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return renglones;
    }

    /**
     * Retorna todos los registros de la tabla renglon
     * @return La lista de renglon
     */
    @SuppressLint("Range")
    public ArrayList<RenglonEnvio> getRenglones(){
        ArrayList<RenglonEnvio> renglones = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM renglon", null)){
            if(cursor.moveToFirst()){
                do{
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                    String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    String pediid = cursor.getString(cursor.getColumnIndex("pediid"));
                    String dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                    int numrenglon = cursor.getInt(cursor.getColumnIndex("numrenglon"));
                    int numrengs = cursor.getInt(cursor.getColumnIndex("numrengs"));
                    int ddin = cursor.getInt(cursor.getColumnIndex("ddin"));
                    float cantpedida = cursor.getFloat(cursor.getColumnIndex("cantpedida"));
                    float ingresado = cursor.getFloat(cursor.getColumnIndex("ingresado"));
                    float dispo = cursor.getFloat(cursor.getColumnIndex("dispo"));
                    float anaquel = cursor.getFloat(cursor.getColumnIndex("anaquel"));
                    float futura = cursor.getFloat(cursor.getColumnIndex("futura"));

                    renglones.add(new RenglonEnvio(numrenglon, numrengs, ddin, codigo, producto, ubicacion, faltacadu, notas, pediid, dcinfolio, cantpedida, ingresado, dispo, anaquel, futura));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return renglones;
    }

    @SuppressLint("Range")
    public RenglonEnvio getRenglon(int renglon){
        RenglonEnvio renglonBD = new RenglonEnvio(0, 0, 0, "", "", "", "", "", "", "", 0, 0, 0, 0, 0);

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM renglon WHERE numrenglon = " + renglon, null)){
            if(cursor.moveToFirst()){
                String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                String producto = cursor.getString(cursor.getColumnIndex("producto"));
                String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));
                String notas = cursor.getString(cursor.getColumnIndex("notas"));
                String pediid = cursor.getString(cursor.getColumnIndex("pediid"));
                String dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                String codigos = cursor.getString(cursor.getColumnIndex("codigos"));
                String ubiks = cursor.getString(cursor.getColumnIndex("listaubiks"));
                int numrenglon = cursor.getInt(cursor.getColumnIndex("numrenglon"));
                int numrengs = cursor.getInt(cursor.getColumnIndex("numrengs"));
                int ddin = cursor.getInt(cursor.getColumnIndex("ddin"));
                float cantpedida = cursor.getFloat(cursor.getColumnIndex("cantpedida"));
                float ingresado = cursor.getFloat(cursor.getColumnIndex("ingresado"));
                float dispo = cursor.getFloat(cursor.getColumnIndex("dispo"));
                float anaquel = cursor.getFloat(cursor.getColumnIndex("anaquel"));
                float futura = cursor.getFloat(cursor.getColumnIndex("futura"));

                renglonBD = new RenglonEnvio(numrenglon, numrengs, ddin, codigo, producto, ubicacion, faltacadu, notas, pediid, dcinfolio, cantpedida, ingresado, dispo, anaquel, futura);
                renglonBD.setCodigos(codigos);
                renglonBD.setListaubiks(ubiks);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return renglonBD;
    }

    @SuppressLint("Range")
    public RenglonEnvio buscaRenglon(String codigoC){
        RenglonEnvio renglonBD = new RenglonEnvio(0, 0, 0, "", "", "", "", "", "", "", 0, 0, 0, 0, 0);

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM renglon WHERE codigo = '" + codigoC + "'", null)){
            if(cursor.moveToFirst()){
                String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                String producto = cursor.getString(cursor.getColumnIndex("producto"));
                String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));
                String notas = cursor.getString(cursor.getColumnIndex("notas"));
                String pediid = cursor.getString(cursor.getColumnIndex("pediid"));
                String dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                int numrenglon = cursor.getInt(cursor.getColumnIndex("numrenglon"));
                int numrengs = cursor.getInt(cursor.getColumnIndex("numrengs"));
                int ddin = cursor.getInt(cursor.getColumnIndex("ddin"));
                float cantpedida = cursor.getFloat(cursor.getColumnIndex("cantpedida"));
                float ingresado = cursor.getFloat(cursor.getColumnIndex("ingresado"));
                float dispo = cursor.getFloat(cursor.getColumnIndex("dispo"));
                float anaquel = cursor.getFloat(cursor.getColumnIndex("anaquel"));
                float futura = cursor.getFloat(cursor.getColumnIndex("futura"));

                renglonBD = new RenglonEnvio(numrenglon, numrengs, ddin, codigo, producto, ubicacion, faltacadu, notas, pediid, dcinfolio, cantpedida, ingresado, dispo, anaquel, futura);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return renglonBD;
    }

    @SuppressLint("Range")
    public String traeTitulo(String pDocumento,String pProvSuc,String pPedido){
        String titulo=pPedido+"Folio:I*"+pDocumento.substring(7,11)+" "+pProvSuc;
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT CASE WHEN COALESCE(pedido,0)=0 THEN '' ELSE '##1##' END||'Folio:'||(substr(foliodi,1,1)||'*'||substr(foliodi,-4,4))||' '||COALESCE(proveedor,'')|| " +
                "CASE WHEN surtidor<>'Sin surtidor' THEN ','||surtidor ELSE '' END  titulo FROM catalogos WHERE foliodi='"+pDocumento+"'", null)){
            if(cursor.moveToFirst()){
                do{
                    titulo = cursor.getString(cursor.getColumnIndex("titulo")).replace("##1##",pPedido);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return titulo;
    }

    @SuppressLint("Range")
    public Integer actualiza(String pCodigo,String pediid,float pPorSurtir){
        db.abreConexion();
        ContentValues contentValues=new ContentValues();
        contentValues.put("cantpedida",pPorSurtir);
        try{
            String where=" codigo = '" + pCodigo + "' AND pediid='"+pediid+"'" ;
            if(pPorSurtir==0){
                int numrenglon = 0;
                Integer eliminado=db.getDatabase().delete("renglon",where,null);
                if(eliminado>0){
                    return db.getDatabase().update("renglon",contentValues,where, null);
                }
            }else{
                return db.getDatabase().update("renglon",contentValues,where, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retorna todos los registros de la tabla de diferencias de caducidades
     * @return La lista de diferencias de caducidades
     */
    @SuppressLint("Range")
    public ArrayList<DiferenciasCaducidades> getDiferenciasCaducidades(){
        ArrayList<DiferenciasCaducidades> diferencias = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM diferencias_caducidades", null)){
            if(cursor.moveToFirst()){
                do{
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    float cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

                    diferencias.add(new DiferenciasCaducidades(codigo, producto, cantidad));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return diferencias;
    }

    /**
     * Retorna todos los registros de la tabla de caducidades de envio
     * @return La lista de caducidades de envio
     */
    @SuppressLint("Range")
    public ArrayList<Caducidad> getCaducidadesEnvio(){
        ArrayList<Caducidad> caducidades = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM caducidades_envio", null)){
            if(cursor.moveToFirst()){
                do{
                    String lote = cursor.getString(cursor.getColumnIndex("lote"));
                    String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    int dlot = cursor.getInt(cursor.getColumnIndex("dlot"));
                    float cantl = cursor.getFloat(cursor.getColumnIndex("cantl"));
                    float cantd = cursor.getFloat(cursor.getColumnIndex("cantd"));

                    caducidades.add(new Caducidad(dlot, lote, fecha, cantd, cantl, notas==null?"":notas));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return caducidades;
    }

    /**
     * Retorna todos los registros de la tabla caducidades
     * @return La lista de caducidades
     */
    @SuppressLint("Range")
    public ArrayList<Caducidad> getCaducidades(){
        ArrayList<Caducidad> caducidades = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM caducidades", null)){
            if(cursor.moveToFirst()){
                do{
                    String lote = cursor.getString(cursor.getColumnIndex("lote"));
                    String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    String notas = cursor.getString(cursor.getColumnIndex("notas"));
                    int dlot = cursor.getInt(cursor.getColumnIndex("dlot"));
                    float cantl = cursor.getFloat(cursor.getColumnIndex("cantl"));
                    float cant = cursor.getFloat(cursor.getColumnIndex("cant"));

                    caducidades.add(new Caducidad(dlot, lote, fecha, cant, cantl, notas==null?"":notas));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return caducidades;
    }

    /**
     * Retorna los lotes pendientes en una venta
     * @return La lista de lotes
     */
    @SuppressLint("Range")
    public ArrayList<Lote> getLotesVenta(){
        ArrayList<Lote> lotes = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM lotes", null)){
            if(cursor.moveToFirst()){
                do{
                    String folioventa = cursor.getString(cursor.getColumnIndex("folioventa"));
                    String descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    int prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    float cantidad = cursor.getFloat(cursor.getColumnIndex("cantidad"));

                    lotes.add(new Lote(folioventa, descripcion, codigo, prodid, cantidad));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return lotes;
    }

    /**
     * Retorna todos los registros de la tabla bultos
     * @return La lista de bultos
     */
    @SuppressLint("Range")
    public ArrayList<Bulto> getBultos(String dcinfolioC){
        ArrayList<Bulto> bultos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM bultos WHERE dcinfolio = '" + dcinfolioC + "'", null)){
            if(cursor.moveToFirst()){
                String contenedor,estatus,fecha,usuario,dcinfolio,pedidi,detalles;
                int rengs;float piezas;
                do{
                    contenedor = cursor.getString(cursor.getColumnIndex("contenedor"));
                    estatus = cursor.getString(cursor.getColumnIndex("estatus"));
                    fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                    pedidi  =cursor.getString(cursor.getColumnIndex("pediid"));
                    detalles  =cursor.getString(cursor.getColumnIndex("detalles"));
                    rengs = cursor.getInt(cursor.getColumnIndex("rengs"));
                    piezas = cursor.getFloat(cursor.getColumnIndex("piezas"));

                    bultos.add(new Bulto(contenedor, dcinfolio, estatus, pedidi, fecha, usuario, rengs, piezas,detalles));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return bultos;
    }

    @SuppressLint("Range")
    public ArrayList<String> traeBultos(){
        ArrayList<String> grupos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT DISTINCT pediid,dcinfolio,substr(vntafolio,1,1)||'*'||substr(vntafolio,-4,3) vntafolio FROM bultos r LEFT JOIN catalogos c ON r.pediid=c.pedido", null)){
            if(cursor.moveToFirst()){
                do{
                    String venta=cursor.getString(cursor.getColumnIndex("vntafolio"));
                    String grupo = cursor.getString(cursor.getColumnIndex("pediid")) + ";" + cursor.getString(cursor.getColumnIndex("dcinfolio"))+(!Libreria.tieneInformacion(venta) ? "":(";"+venta));
                    grupos.add(grupo);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }

        return grupos;
    }

    /**
     * Retorna todos los registros de la tabla productos_di
     * @return La lista de productos
     */
    @SuppressLint("Range")
    public ArrayList<String> getProductosDIGrupos(){
        ArrayList<String> grupos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT DISTINCT pediid, dcinfolio,substr(vntafolio,1,1)||'*'||substr(vntafolio,-4,4) vntafolio FROM productos_di r INNER JOIN catalogos c ON r.pediid=c.pedido", null)){
            if(cursor.moveToFirst()){
                do{
                    String venta=cursor.getString(cursor.getColumnIndex("vntafolio"));
                    String grupo = cursor.getString(cursor.getColumnIndex("pediid")) + ";" + cursor.getString(cursor.getColumnIndex("dcinfolio"))+(!Libreria.tieneInformacion(venta) ? "":(";"+venta));
                    grupos.add(grupo);
                }while(cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return grupos;
    }

    @SuppressLint("Range")
    public ArrayList<ProductoDI> getProductosDI(){
        ArrayList<ProductoDI> productos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM productos_di", null)){
            if(cursor.moveToFirst()){
                do{
                    int prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    int pedido = cursor.getInt(cursor.getColumnIndex("pedido"));
                    int orcoid = cursor.getInt(cursor.getColumnIndex("orcoid"));
                    int ddinid = cursor.getInt(cursor.getColumnIndex("ddinid"));
                    float registrado = cursor.getFloat(cursor.getColumnIndex("registrado"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));

                    productos.add(new ProductoDI(prodid, pedido, orcoid, ddinid, registrado, codigo, producto, usuario, faltacadu));
                }while(cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return productos;
    }

    @SuppressLint("Range")
    public ArrayList<ProductoDI> getProductosDI(String pediid){
        ArrayList<ProductoDI> productos = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM productos_di WHERE dcinfolio = '" + pediid + "'", null)){
            if(cursor.moveToFirst()){
                do{
                    int prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    int pedido = cursor.getInt(cursor.getColumnIndex("pedido"));
                    int orcoid = cursor.getInt(cursor.getColumnIndex("orcoid"));
                    int ddinid = cursor.getInt(cursor.getColumnIndex("ddinid"));
                    float registrado = cursor.getFloat(cursor.getColumnIndex("registrado"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    String faltacadu = cursor.getString(cursor.getColumnIndex("faltacadu"));

                    productos.add(new ProductoDI(prodid, pedido, orcoid, ddinid, registrado, codigo, producto, usuario, faltacadu));
                }while(cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return productos;
    }

    /**
     * Retorna todos los registros de la tabla ubicaciones
     * @return LA lista de ubicaciones
     */
    @SuppressLint("Range")
    public ArrayList<Ubicacion> traeUbicaciones(){
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM ubicaciones", null)){
            if(cursor.moveToFirst()){
                do{
                    int ubiid = cursor.getInt(cursor.getColumnIndex("ubiid"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String disponible = cursor.getString(cursor.getColumnIndex("disponible"));
                    String estatus = cursor.getString(cursor.getColumnIndex("estatus"));
                    String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                    String asifid = cursor.getString((cursor.getColumnIndex("asifid")));

                    ubicaciones.add(new Ubicacion(codigo, disponible, estatus, ubiid, ubicacion, asifid));
                }while(cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return ubicaciones;
    }

    /**
     * Retorna todos los registros de la tabla contados
     * @return La lista de productos contados
     */
    @SuppressLint("Range")
    public ArrayList<Contados> traeContados(){
        ArrayList<Contados> contados = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM contados", null)){
            if(cursor.moveToFirst()){
                do{
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String producto = cursor.getString(cursor.getColumnIndex("producto"));
                    String cant = cursor.getString(cursor.getColumnIndex("cant"));
                    int id = cursor.getInt(cursor.getColumnIndex("id"));

                    contados.add(new Contados(codigo, producto, cant, id));
                }while(cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return contados;
    }

    @SuppressLint("Range")
    public ArrayList<Asignacion> traeAsignaciones(){
        ArrayList<Asignacion> asignaciones = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM asignaciones", null)){
            if(cursor.moveToFirst()){
                do {
                    String ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                    String estatus = cursor.getString(cursor.getColumnIndex("estatus"));
                    String numconteo = cursor.getString(cursor.getColumnIndex("numconteo"));
                    int puedeabrir = cursor.getInt(cursor.getColumnIndex("puedeabrir"));
                    asignaciones.add(new Asignacion(ubicacion, estatus, numconteo, puedeabrir));
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return asignaciones;
    }

    @SuppressLint("Range")
    public ArrayList<Colonia> traeColonias(){
        ArrayList<Colonia> colonias = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM colonias", null)){
            if(cursor.moveToFirst()){
                do{
                    String clave = cursor.getString(cursor.getColumnIndex("clave"));
                    String colonia = cursor.getString(cursor.getColumnIndex("colonia"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String estado = cursor.getString(cursor.getColumnIndex("estado"));
                    String muni = cursor.getString(cursor.getColumnIndex("municipio"));
                    colonias.add(new Colonia(clave, colonia, codigo, id,muni,estado));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return colonias;
    }

    @SuppressLint("Range")
    public Cuenta traeCuenta(Integer pCuclid){
        Cuenta colonias =new Cuenta(0);
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT id,domiid,coloid,cuclid,info,tel,calle,exterior,interior,colonia,muni,estado,cp,clteid,cuenta,nombre FROM cuentas WHERE cuclid="+pCuclid, null)){
            if(cursor.moveToFirst()){
                colonias = Cuenta.leerCursor(cursor);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return colonias;
    }

    @SuppressLint("Range")
    public ArrayList<Cuenta> traeCuentas(){
        ArrayList<Cuenta> colonias = new ArrayList();
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT id,domiid,coloid,cuclid, info,tel,calle,exterior,interior,colonia,muni,estado,cp FROM cuentas", null)){
            if(cursor.moveToFirst()){
                do{
                    colonias.add(Cuenta.leerCursor(cursor));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return colonias;
    }

    /**
     * Retorna el folio de venta de una venta
     * @param f La orden de compra
     * @return El folio de la venta
     */
    @SuppressLint("Range")
    public String getVentaFolio(String f) {
        String vntaFolio = "";
        db.abreConexion();
        try {
            String [] params = {f};
            Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM catalogos WHERE pedido=?", params);
            if (cursor != null && cursor.moveToFirst()){
                vntaFolio = cursor.getString(cursor.getColumnIndex("vntafolio"));
                cursor.close();
            }

        } catch (Exception e ){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return vntaFolio;
    }

    @SuppressLint("Range")
    public ArrayList<Reposicion> traeOrdenesAcomodo(){
        ArrayList<Reposicion> colonias = new ArrayList<>();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT *,COALESCE(claveubicacion,'0')='0' cap FROM reposicion ORDER BY fecha DESC", null)){
            if(cursor.moveToFirst()){
                Reposicion rep;
                Integer id,rengs,cap,encarro;
                String dcinfolio,ubicacion,claveubicacion,usuario,dcinfefin,movi,dcin,prov;
                Double piezas;
                do{
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    dcinfolio = cursor.getString(cursor.getColumnIndex("dcinfolio"));
                    rengs = cursor.getInt(cursor.getColumnIndex("rengs"));
                    ubicacion = cursor.getString(cursor.getColumnIndex("ubicacion"));
                    claveubicacion = cursor.getString(cursor.getColumnIndex("claveubicacion"));
                    dcinfefin = cursor.getString(cursor.getColumnIndex("fecha"));
                    usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    cap = cursor.getInt(cursor.getColumnIndex("cap"));
                    encarro = cursor.getInt(cursor.getColumnIndex("encarro"));
                    piezas = cursor.getDouble(cursor.getColumnIndex("piezas"));
                    movi = cursor.getString(cursor.getColumnIndex("tipodi"));
                    dcin = cursor.getString(cursor.getColumnIndex("dcin"));
                    prov = cursor.getString(cursor.getColumnIndex("prov"));
                    rep = new Reposicion(id,rengs,dcinfolio,ubicacion,claveubicacion,dcinfefin,usuario,encarro);
                    rep.setPiezas(piezas!=null && piezas>0 ? new BigDecimal(piezas):BigDecimal.ZERO);
                    rep.setMovimiento(movi);
                    rep.setCaptura(cap==1);
                    rep.setDcin(dcin);
                    rep.setProv(prov);
                    colonias.add(rep);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return colonias;
    }

    @SuppressLint("Range")
    public List<RenglonRepo> traeRenglones(Integer pEntrada){
        ArrayList<RenglonRepo> reng_repo = new ArrayList<>();

        db.abreConexion();
        String estatuto="SELECT * FROM repofalta "+(pEntrada>0 ? (" WHERE id="+pEntrada) :"");
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            BigDecimal numerico=null;
            Integer prodid,rengs,id,daco;
            String faltan,codigo,producto,origen,destino;
            if(cursor.moveToFirst()){
                do{
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    faltan = cursor.getString(cursor.getColumnIndex("faltan"));
                    numerico=new BigDecimal(faltan);
                    rengs = cursor.getInt(cursor.getColumnIndex("rengs"));
                    codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    producto = cursor.getString(cursor.getColumnIndex("producto"));
                    origen = cursor.getString(cursor.getColumnIndex("origen"));
                    destino = cursor.getString(cursor.getColumnIndex("destino"));
                    daco = cursor.getInt(cursor.getColumnIndex("dacoid"));
                    reng_repo.add(new RenglonRepo(id,prodid,rengs,numerico,codigo,producto,origen,destino,daco));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    @SuppressLint("Range")
    public List<RenglonCalcula> traeRepoCalcula(){
        ArrayList<RenglonCalcula> reng_repo = new ArrayList<>();

        db.abreConexion();
        String estatuto="SELECT * FROM repocalcula ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            BigDecimal req=null,asig=null,dispo=null;
            Integer prodid,id;
            String codigo,producto,origen,destino,origena,destinoa,sreq,sasig,sdispo;
            if(cursor.moveToFirst()){
                do{
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    prodid = cursor.getInt(cursor.getColumnIndex("prodid"));
                    sreq = cursor.getString(cursor.getColumnIndex("requerido"));
                    req=new BigDecimal(sreq);
                    sasig = cursor.getString(cursor.getColumnIndex("asignado"));
                    asig=new BigDecimal(sasig);
                    sdispo = cursor.getString(cursor.getColumnIndex("disponible"));
                    dispo=new BigDecimal(sdispo);
                    codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    producto = cursor.getString(cursor.getColumnIndex("producto"));
                    origen = cursor.getString(cursor.getColumnIndex("origen"));
                    destino = cursor.getString(cursor.getColumnIndex("destino"));
                    origena = cursor.getString(cursor.getColumnIndex("origena"));
                    destinoa = cursor.getString(cursor.getColumnIndex("destinoa"));
                    reng_repo.add(new RenglonCalcula(id,prodid,req,dispo,asig,codigo,producto,origen,destino,origena,destinoa));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    public List<Ubikprod> traeUbicaProd(String pCodigo){
        ArrayList<Ubikprod> reng_repo = new ArrayList<>();

        db.abreConexion();
        String estatuto="SELECT * FROM ubikprod  WHERE codigo='"+pCodigo+"'";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    Ubikprod temp=Ubikprod.nuevoRegistro(cursor);
                    reng_repo.add(temp);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    public List<Generica> traeGenerica(){
        ArrayList<Generica> reng_repo = new ArrayList<>();
        db.abreConexion();
        String estatuto="SELECT * FROM generica  ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    Generica temp=Generica.leerCursor(cursor);
                    reng_repo.add(temp);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    public Generica traeGenReporteEnca(){
        Generica temp=null;
        db.abreConexion();
        String estatuto="SELECT * FROM generica WHERE id=0 ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                temp=Generica.leerCursor2(cursor,"tex1,tex2,tex3,tex4");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return temp;
    }

    public List<Generica> traeGenReporte(){
        ArrayList<Generica> reng_repo = new ArrayList<>();
        db.abreConexion();
        String estatuto="SELECT * FROM generica WHERE id>0 ORDER BY id ASC ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    Generica temp=Generica.leerCursor2(cursor,"tex1,tex2,tex3,tex4");
                    reng_repo.add(temp);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    public List<Generica> traeGenericaBqdaProd(){
        ArrayList<Generica> reng_repo = new ArrayList<>();
        db.abreConexion();
        String estatuto="SELECT id,enoferta log1,prod ent1,detalle tex1,estilo tex2,codigo tex3 FROM dcarro  ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    Generica temp=Generica.leerCursor2(cursor,"log1,ent1,tex1,tex2,tex3");
                    reng_repo.add(temp);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    public List<Generica> traeProductosVnta(){
        ArrayList<Generica> reng_repo = new ArrayList<>();
        db.abreConexion();
        String estatuto="SELECT id,prodid ent1,producto tex1,'estilo' tex2,codigo tex3 FROM productos_di  ";
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    Generica temp=Generica.leerCursor2(cursor,"ent1,tex1,tex2,tex3");
                    reng_repo.add(temp);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.cierraConexion();
        }

        return reng_repo;
    }

    @SuppressLint("Range")
    public String notiConsulta(String pUsuario){
        db.abreConexion();
        String retorno="";
        String estatuto="SELECT group_concat(tiempo||','||tipo||','||usuaid||'|') notificacion FROM notificacion WHERE usuaid="+pUsuario;
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    retorno=cursor.getString(cursor.getColumnIndex("notificacion"));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }
        return retorno;
    }

    @SuppressLint("Range")
    public List<Generica> traeNotificacion(String pUsuario){
        ArrayList<Generica> reng_repo = new ArrayList<>();
        db.abreConexion();
        String estatuto="SELECT * FROM notificacion WHERE usuaid="+pUsuario;
        Generica gen;
        int inicio=0;
        try(Cursor cursor = db.getDatabase().rawQuery(estatuto, null)){
            if(cursor.moveToFirst()){
                do{
                    gen=new Generica(cursor.getInt(inicio++));
                    gen.setTex1(cursor.getString(cursor.getColumnIndex("resultado")));
                    gen.setEnt1(cursor.getInt(cursor.getColumnIndex("tipo")));
                    reng_repo.add(gen);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.cierraConexion();
        }
        return reng_repo;
    }

    /**
     * Retorna los registros de viajes almacenados en el dispositivo
     * @return La lista de viajes
     */
    public List<Viaje> traeViajes(){
        List<Viaje> retorno = new ArrayList();

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_VIAJE , null)){
            if(cursor.moveToFirst()){
                do{
                    retorno.add(armaViaje(cursor));
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return retorno;
    }

    public Viaje traeViaje(String pFolio){
        Viaje retorno = null;

        db.abreConexion();
        String estatuto = "SELECT * FROM {0} WHERE viajfolio={1}";
        try(Cursor cursor = db.getDatabase().rawQuery(MessageFormat.format(estatuto,Estatutos.TABLA_VIAJE,"'"+pFolio+"'") , null)){
            if(cursor.moveToFirst()){
                do{
                    retorno = armaViaje(cursor);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return retorno;
    }

    @SuppressLint("Range")
    private Viaje armaViaje(Cursor cursor){
        String dato;
        Integer dato01;
        Double dato02;
        Viaje miViaje;
        miViaje = new Viaje();
        dato01 = cursor.getInt(cursor.getColumnIndex("id"));
        miViaje.setId(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("rengs"));
        miViaje.setRengs(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("pzas"));
        miViaje.setPzas(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("cdestinos"));
        miViaje.setCdestinos(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("patrid"));
        miViaje.setPatrid(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("choferid"));
        miViaje.setChoferid(dato01);
        dato01 = cursor.getInt(cursor.getColumnIndex("tipo"));
        miViaje.setTipo(dato01);
        dato = cursor.getString(cursor.getColumnIndex("folio"));
        miViaje.setFolio(dato);
        dato = cursor.getString(cursor.getColumnIndex("viajfolio"));
        miViaje.setViajfolio(dato);
        dato = cursor.getString(cursor.getColumnIndex("ruta"));
        miViaje.setRuta(dato);
        dato = cursor.getString(cursor.getColumnIndex("operador"));
        miViaje.setOperador(dato);
        dato = cursor.getString(cursor.getColumnIndex("feprog"));
        miViaje.setFeprog(dato);
        dato = cursor.getString(cursor.getColumnIndex("chofer"));
        miViaje.setChofer(dato);
        dato = cursor.getString(cursor.getColumnIndex("vehiculo"));
        miViaje.setVehiculo(dato);
        dato = cursor.getString(cursor.getColumnIndex("texto"));
        miViaje.setTexto(dato);
        dato = cursor.getString(cursor.getColumnIndex("estatus"));
        miViaje.setEstatus(dato);
        dato02 = cursor.getDouble(cursor.getColumnIndex("vol"));
        miViaje.setVol(BigDecimal.valueOf(dato02));
        return miViaje;
    }

    @SuppressLint("Range")
    public Detviaje traeDviaje(Integer pId){
        Detviaje retorno = null;

        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_DVIAJE +" WHERE renglon="+pId, null)){
            if(cursor.moveToFirst()){
                retorno = armaDetviaje(cursor);
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return retorno;
    }

    @SuppressLint("Range")
    public List<Detviaje> traeDviajes(){
        List<Detviaje> respuesta=new ArrayList();
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_DVIAJE , null)){
            if(cursor.moveToFirst()){
                do{
                    respuesta.add(armaDetviaje(cursor));
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return respuesta;
    }

    @SuppressLint("Range")
    private Detviaje armaDetviaje(Cursor pCursor){
        Detviaje retorno = new Detviaje();
        String dato;
        Integer dato01;
        Double dato02;
        dato01 = pCursor.getInt(pCursor.getColumnIndex("id"));
        retorno.setId(dato01);
        dato01 = pCursor.getInt(pCursor.getColumnIndex("rengs"));
        retorno.setRengs(dato01);
        dato01 = pCursor.getInt(pCursor.getColumnIndex("pzas"));
        retorno.setPzas(dato01);
        dato01 = pCursor.getInt(pCursor.getColumnIndex("totreng"));
        retorno.setTotreng(dato01);
        dato01 = pCursor.getInt(pCursor.getColumnIndex("renglon"));
        retorno.setRenglon(dato01);
        dato02 = pCursor.getDouble(pCursor.getColumnIndex("vol"));
        retorno.setVol(BigDecimal.valueOf(dato02));
        dato = pCursor.getString(pCursor.getColumnIndex("envio"));
        retorno.setEnvio(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("dcin"));
        retorno.setDcin(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("estatus"));
        retorno.setEstatus(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("texto"));
        retorno.setTexto(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("surtidor"));
        retorno.setSurtidor(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("cliente"));
        retorno.setCliente(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("ubica"));
        retorno.setUbica(dato);
        return retorno;
    }

    @SuppressLint("Range")
    public Integer actualizaViaje(String pEnvio){
        db.abreConexion();
        int retorno = -1;
        Integer pRenglon = 0;
        try{
            String sqlselect="SELECT renglon FROM {0} WHERE dcin={1}";
            String estatutoSel=MessageFormat.format(sqlselect,Estatutos.TABLA_DVIAJE,"'"+pEnvio+"'");
            Cursor cursor = db.getDatabase().rawQuery(estatutoSel,null);
            cursor.moveToFirst();
            pRenglon = cursor.getInt(cursor.getColumnIndex("renglon"));
            cursor.close();

            String sqldelete=" dcin={0} " ;
            String sqlUpdate="UPDATE {0} SET renglon=renglon-1  WHERE  renglon>={1}  ";//
            String estatutoUp=MessageFormat.format(sqlUpdate, Estatutos.TABLA_DVIAJE,pRenglon);
            String estatutoDel=MessageFormat.format(sqldelete,  "'"+pEnvio+"'");

            System.out.println("borra "+estatutoDel);

            Integer eliminado=db.getDatabase().delete(Estatutos.TABLA_DVIAJE,estatutoDel,null);
            if(eliminado>0){
                System.out.println("borra "+estatutoDel);
                cursor = db.getDatabase().rawQuery(estatutoUp,null);
                //cursor.moveToFirst();
                retorno = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retorno;
    }

    @SuppressLint("Range")
    public Integer disponiblesViaje(){
        db.abreConexion();
        int retorno = -1;
        try{
            String sqlUpdate="SELECT SUM(1) dispo FROM {0} ";
            String estatutoUp=MessageFormat.format(sqlUpdate, Estatutos.TABLA_DVIAJE);
            Cursor cursor = db.getDatabase().rawQuery(estatutoUp,null);
            cursor.moveToFirst();
            retorno = cursor.getInt(cursor.getColumnIndex("dispo"));
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return retorno;
    }

    @SuppressLint("Range")
    public List<UtilViaje> traeUtilViaje(){
        List<UtilViaje> respuesta=new ArrayList();
        db.abreConexion();
        try(Cursor cursor = db.getDatabase().rawQuery("SELECT * FROM " + Estatutos.TABLA_UTIVIAJE , null)){
            if(cursor.moveToFirst()){
                do{
                    respuesta.add(armaUtilViaje(cursor));
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.cierraConexion();
        }
        return respuesta;
    }

    @SuppressLint("Range")
    private UtilViaje armaUtilViaje(Cursor pCursor){
        UtilViaje retorno = new UtilViaje();
        String dato;
        Integer dato01;
        Double dato02;
        dato01 = pCursor.getInt(pCursor.getColumnIndex("id"));
        retorno.setId(dato01);
        dato01 = pCursor.getInt(pCursor.getColumnIndex("rid"));
        retorno.setRid(dato01);
        dato02 = pCursor.getDouble(pCursor.getColumnIndex("cant"));
        retorno.setCant(BigDecimal.valueOf(dato02));
        dato = pCursor.getString(pCursor.getColumnIndex("nombre"));
        retorno.setNombre(dato);
        dato = pCursor.getString(pCursor.getColumnIndex("nombre2"));
        retorno.setNombre2(dato);
        return retorno;
    }
}
