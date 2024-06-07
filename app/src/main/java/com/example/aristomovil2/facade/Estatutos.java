package com.example.aristomovil2.facade;

public class Estatutos {
    public static Integer VERSION_BD = 67;
    public static String NOMBRE_BD = "A01Movil2";
    public static final String TABLA_ESTACIONES = "estaciones";
    public static final String TABLA_PRODUCTOS_UBICACION = "productos_ubicacion";
    public static final String TABLA_ITEMS_MENU = "items_menu";
    public static final String TABLA_PRODUCTOS = "productos";
    public static final String TABLA_VENTAS = "ventas";
    public static final String TABLA_RENGLONES = "renglones";
    public static final String TABLA_CLIENTES = "clientes";
    public static final String TABLA_LOTES_PENDIENTES = "lotes";
    public static final String TABLA_PRODUCTOS_COBRADOS = "cobrados";
    public static final String TABLA_CATALOGOS = "catalogos";
    public static final String TABLA_PROVEEDORES = "proveedores";
    public static final String TABLA_SUBALMACENES = "subalmacenes";
    public static final String TABLA_RENGLON = "renglon";
    public static final String TABLA_DIFERENCIAS_CADUCIDADES = "diferencias_caducidades";
    public static final String TABLA_CADUCIDADES_ENVIO = "caducidades_envio";
    public static final String TABLA_CADUCIDADES = "caducidades";
    public static final String TABLA_BULTOS = "bultos";
    public static final String TABLA_PRODUCTOS_DI = "productos_di";
    public static final String TABLA_UBICACIONES = "ubicaciones";
    public static final String TABLA_CONTADOS = "contados";
    public static final String TABLA_ASIGNCION = "asignaciones";
    public static final String TABLA_COLONIAS = "colonias";
    public static final String TABLA_REPOSICION = "reposicion";
    public static final String TABLA_REPOFALTA = "repofalta";
    public static final String TABLA_REPOCALCULA = "repocalcula";
    public static final String TABLA_UBIKPROD = "ubikprod";
    public static final String TABLA_GENERICA = "generica";
    public static final String TABLA_NOTIFICACION = "notificacion";
    public static final String TABLA_DCATALOGO = "dcatalogo";
    public static final String TABLA_VIAJE = "viaje";
    public static final String TABLA_DVIAJE = "dviaje";
    public static final String TABLA_UTIVIAJE = "utiviaje";
    public static final String TABLA_DCARRO = "dcarro";
    public static final String TABLA_CUENTAS = "cuentas";

    public static String[] CREA_BD = {
            "CREATE TABLE estaciones(id integer primary key autoincrement, estaid integer, nombre text, tipo text, asignada text);",
            "CREATE TABLE productos_ubicacion(id integer primary key autoincrement, prodid integer, codigo text, producto text, activo text, cmax integer, cmin integer, lleno text);",
            "CREATE TABLE items_menu(id integer primary key, orden integer, texto text, imagen text, grupo text, titulo text);",
            "CREATE TABLE productos(id integer primary key autoincrement, prodid integer, codigo text, producto text, precio numeric, renglones integer, foto text, preciomax integer, preciomin integer, disponible integer, preciovolumen text, promo text);",
            "CREATE TABLE ventas(id integer primary key autoincrement, folio text, tipo integer, usuario integer, consumidor text, domicilio text, cliente integer, estacion integer, porcocinar integer, fecha text, telefono integer, notas text, nombrecliente text, tienecredito text, credito text, vntatitulo text,regimen TEXT,solofac integer,rfc TEXT,empr integer);",
            "CREATE TABLE renglones(id integer primary key autoincrement, folio text, dvtaid integer, prodid integer, codigo text, producto text, fraccionable text, estatus integer, refer text, cant numeric, precio  numeric, dscto numeric, dsctoad numeric, subtotal numeric, impuesto numeric, total numeric, vntatotal numeric, notas text, foto text, disponible numeric, futura numeric, caduca numeric);",
            "CREATE TABLE clientes (id integer primary key autoincrement, clteid integer, cliente text, razon text, credito text, domicilio text, rfc text, vntacredito text);",
            "CREATE TABLE lotes(id integer primary key autoincrement, folioventa text, prodid integer, descripcion text, codigo text, cantidad numeric)",
            "CREATE TABLE cobrados(id integer primary key autoincrement,codigo text, producto text, cantidad integer, preciou numeric, subtotal numeric, total numeric, dscto numeric, dsctoad numeric, impuesto);",
            "CREATE TABLE catalogos(id integer primary key autoincrement, foliodi text, proveedor text, provid integer, factura text, pedido integer, orcofe text, rengsoc integer, estatusdi text, importe numeric,operador text, vntafolio text, surtidor text, grupo integer, divisa number, surtid integer, diascred integer,tipopedido integer,bulto text,prioridad text,prior integer,feviaje text,fecha2 numeric,ruta text,llevar integer,texto text,piezas numeric);",
            "CREATE TABLE proveedores(id integer primary key autoincrement, provid integer, rfc text, aliasp text, rsocial text,diascred integer);",
            "CREATE TABLE subalmacenes(id integer primary key autoincrement, dcatid integer, nombre text);",
            "CREATE TABLE renglon(id integer primary key autoincrement, numrenglon integer, codigo text, producto text, cantpedida numeric, ingresado numeric, ubicacion text, dispo numeric, anaquel numeric, futura numeric, numrengs integer, faltacadu text, ddin integer, notas text, pediid text, dcinfolio text,codigos text,listaubiks text);",
            "CREATE TABLE diferencias_caducidades(id integer primary key autoincrement, codigo text, producto text, cantidad numeric);",
            "CREATE TABLE caducidades_envio(id integer primary key autoincrement, dlot integer, lote text, fecha string, cantl numeric, cantd numeric, notas string);",
            "CREATE TABLE caducidades(id integer primary key autoincrement, dlot integer, lote text, fecha string, cant numeric, cantl integer, notas string);",
            "CREATE TABLE bultos(id integer primary key autoincrement, contenedor text, estatus text, fecha text, usuario text, rengs integer, piezas numeric, dcinfolio text, pediid text,detalles text);",
            "CREATE TABLE productos_di(id integer primary key autoincrement,prodid integer, pedido integer, registrado numeric, codigo text, producto text, usuario text, faltacadu text, orcoid integer, ddinid integer, dcinfolio text, pediid text);",
            "CREATE TABLE ubicaciones(id integer primary key autoincrement, codigo text, disponible text, ubiid integer, estatus text, ubicacion text, asifid text)",
            "CREATE TABLE contados(id integer primary key autoincrement, codigo text, producto text, cant text);",
            "CREATE TABLE asignaciones(id integer primary key autoincrement, ubicacion text, estatus text, numconteo text,puedeabrir integer);",
            "CREATE TABLE colonias(id integer primary key autoincrement, clave text, colonia text, codigo text,estado text,municipio text)",
            "CREATE TABLE reposicion(id integer primary key autoincrement,dcinfolio text,rengs integer,fecha text,ubicacion text,claveubicacion text,usuario text ,encarro integer,tipodi text,piezas numeric,prov text,dcin text)",
            "CREATE TABLE repofalta(id integer primary key autoincrement,producto text,destino text,codigo text,faltan numeric,origen text,rengs integer,prodid integer,dacoid)",
            "CREATE TABLE repocalcula(id integer primary key autoincrement,producto text,destino text,origen text,destinoa text,origena text,codigo text,prodid integer,requerido numeric,disponible numeric,asignado numeric)",
            "CREATE TABLE ubikprod(id integer primary key autoincrement,codigo text,cant numeric,ubicacion text,tipo text,producto text,ubikid integer)",
            "CREATE TABLE generica(id integer primary key autoincrement,tex1 text,tex2 text,tex3 text,tex4 text,tex5 text,dec1 numeric,dec2 numeric,dec3 numeric,dec4 numeric,dec5 numeric" +
                    ",ent1 entero,ent2 entero,ent3 entero,ent4 entero,ent5 entero,log1 entero,log2 entero,log3 entero,log4 entero,log5 entero)",
            "CREATE TABLE notificacion(usuaid integer NOT NULL,tipo integer NOT NULL,tiempo real,resultado text ,PRIMARY KEY (usuaid, tipo))",
            "CREATE TABLE dcatalogo(id integer primary key autoincrement,cata integer NOT NULL,abrevi text,t1 text,t2 text ,n1 numeric,n2 numeric,l1 integer,l2 integer,e1 integer,e2 integer)",
            "CREATE TABLE viaje(id integer primary key autoincrement,viajfolio TEXT,folio TEXT,ruta TEXT,operador TEXT,feprog TEXT,rengs INTEGER,pzas INTEGER,vol NUMERIC,cdestinos INTEGER,patrid INTEGER,choferid INTEGER,chofer TEXT,vehiculo TEXT,texto TEXT,estatus TEXT,tipo INTEGER)",
            "CREATE TABLE dviaje(id integer primary key autoincrement,envio TEXT,dcin TEXT,estatus TEXT,texto TEXT,surtidor TEXT,cliente TEXT,ubica TEXT,clteid INTEGER,rengs INTEGER,pzas INTEGER,totreng INTEGER,renglon INTEGER,vol NUMERIC)",
            "CREATE TABLE utiviaje(id integer primary key autoincrement,rid INTEGER, nombre TEXT,  cant NUMERIC, nombre2 TEXT)",
            "CREATE TABLE dcarro(id integer primary key autoincrement,prod INTEGER, codigo TEXT, estilo TEXT,  enoferta INTEGER, detalle TEXT)",
            "CREATE TABLE cuentas(id integer primary key autoincrement,info TEXT, cuenta TEXT, nombre TEXT,  domiid INTEGER, clteid INTEGER,calle TEXT,exterior TEXT,interior TEXT,cp TEXT,colonia TEXT,coloid INTEGER,estado TEXT,muni TEXT,tel TEXT,cuclid Integer)",

            "CREATE UNIQUE INDEX estaciones_data ON estaciones(estaid)",
            "CREATE UNIQUE INDEX productos_ubicacion_data ON productos_ubicacion(prodid)",
            "CREATE UNIQUE INDEX items_menu_data ON items_menu(id)",
            "CREATE UNIQUE INDEX productos_data ON productos(id)",
            "CREATE UNIQUE INDEX ventas_data ON ventas(id)",
            "CREATE UNIQUE INDEX renglones_data ON renglones(id)",
            "CREATE UNIQUE INDEX clientes_data ON clientes(id)",
            "CREATE UNIQUE INDEX lotes_pendientes_data ON lotes(id)",
            "CREATE UNIQUE INDEX cobrados_data ON cobrados(id)",
            "CREATE UNIQUE INDEX catalogos_data ON catalogos(id)",
            "CREATE UNIQUE INDEX proveedores_data ON proveedores(id)",
            "CREATE UNIQUE INDEX subalmacenes_data ON subalmacenes(id)",
            "CREATE UNIQUE INDEX renglon_data ON renglon(id)",
            "CREATE UNIQUE INDEX diferencias_caducidades_data ON diferencias_caducidades(id)",
            "CREATE UNIQUE INDEX caducidades_envio_data ON caducidades_envio(id)",
            "CREATE UNIQUE INDEX caducidades_data ON caducidades(id)",
            "CREATE UNIQUE INDEX bultos_data ON bultos(id)",
            "CREATE UNIQUE INDEX productos_di_data ON productos_di(id)",
            "CREATE UNIQUE INDEX ubicaciones_data ON ubicaciones(id)",
            "CREATE UNIQUE INDEX contados_data ON contados(id)",
            "CREATE UNIQUE INDEX asignaciones_data ON asignaciones(id)",
            "CREATE UNIQUE INDEX colonias_data ON colonias(id)",
            "CREATE UNIQUE INDEX reposicion_data ON reposicion(id)",
            "CREATE UNIQUE INDEX repofalta_data ON repofalta(id)",
            "CREATE UNIQUE INDEX repocalcula_data ON repocalcula(id)",
            "CREATE UNIQUE INDEX ubikprod_data ON ubikprod(id)",
            "CREATE UNIQUE INDEX generica_data ON generica(id)",
            "CREATE UNIQUE INDEX notificacion_data ON notificacion(usuaid, tipo)",
            "CREATE UNIQUE INDEX dcatalogo_data ON dcatalogo(id)",
            "CREATE UNIQUE INDEX viaje_data ON viaje(id)",
            "CREATE UNIQUE INDEX dviaje_data ON dviaje(id)",
            "CREATE UNIQUE INDEX utiviaje_data ON utiviaje(id)",
            "CREATE UNIQUE INDEX dcarro_data ON dcarro(id)",
            "CREATE UNIQUE INDEX cuenta_data ON cuentas(id)"
    };

    public static  String[] BORRA_BD = {
            "DROP TABLE IF EXISTS estaciones",
            "DROP TABLE IF EXISTS productos_ubicacion",
            "DROP TABLE IF EXISTS items_menu",
            "DROP TABLE IF EXISTS productos",
            "DROP TABLE IF EXISTS ventas",
            "DROP TABLE IF EXISTS renglones",
            "DROP TABLE IF EXISTS clientes",
            "DROP TABLE IF EXISTS lotes",
            "DROP TABLE IF EXISTS cobrados",
            "DROP TABLE IF EXISTS catalogos",
            "DROP TABLE IF EXISTS proveedores",
            "DROP TABLE IF EXISTS subalmacenes",
            "DROP TABLE IF EXISTS renglon",
            "DROP TABLE IF EXISTS diferencias_caducidades",
            "DROP TABLE IF EXISTS caducidades_envio",
            "DROP TABLE IF EXISTS caducidades",
            "DROP TABLE IF EXISTS bultos",
            "DROP TABLE IF EXISTS productos_di",
            "DROP TABLE IF EXISTS ubicaciones",
            "DROP TABLE IF EXISTS contados",
            "DROP TABLE IF EXISTS asignaciones",
            "DROP TABLE IF EXISTS colonias",
            "DROP TABLE IF EXISTS reposicion",
            "DROP TABLE IF EXISTS repofalta",
            "DROP TABLE IF EXISTS repocalcula",
            "DROP TABLE IF EXISTS ubikprod",
            "DROP TABLE IF EXISTS generica",
            "DROP TABLE IF EXISTS notificacion",
            "DROP TABLE IF EXISTS dcatalogo",
            "DROP TABLE IF EXISTS viaje",
            "DROP TABLE IF EXISTS dviaje",
            "DROP TABLE IF EXISTS utiviaje",
            "DROP TABLE IF EXISTS dcarro",
            "DROP TABLE IF EXISTS cuentas"
    };
}
