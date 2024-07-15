package com.example.aristomovil2.utileria;

import static com.example.aristomovil2.facade.Estatutos.*;

import com.example.aristomovil2.facade.Estatutos;

public class Enumeradores {
    public enum Valores{
        HOLA_MUNDO(0,"", "", false, false),
        TAREA_PRUEBA_CONEXION(-2,"validacion", "", false, false),
        TAREA_GUARDA_CLIENTE(18, "validacion", "", false, false),
        TAREA_COBRAR(233, "validacion", "", false, false),
        TAREA_COBRAR_WS(21, "validacion", "", false, false),
        TAREA_INSERTA_RENGLON(245, "salida", "", false, false),
        TAREA_TRAE_RENGLONES(246, "venta", TABLA_RENGLONES, true, true),
        TAREA_LOGIN(249,"raiz", "", false, false),
        TAREA_INSERTA_RENGLON_CONTEO(307, "validacion", "", false, false),
        TAREA_CIERRA_CONTEO(308, "validacion", "", false, false),
        TAREA_NUEVO_INVENTARIO(309, "validacion", "", false, false),
        TAREA_RENGLONES_CONTADOS(313, "validacion", TABLA_CONTADOS, true, true),
        TAREA_PRODUCTO_CATALOGO(314, "validacion", "", false, false),
        TAREA_CUENTA_UBICACION(315, "validacion", "", false, false),
        TAREA_UBICACIONES_ASIGNADAS(327, "validacion", TABLA_ASIGNCION, true, true),
        TAREA_GUARDA_DETALLE_DI(354, "validacion", "", false, false),
        TAREA_ABRE_BULTO(357, "validacion", "", false, false),
        TAREA_ENVIA_A_ESPERA(358, "validacion", "", false, false),
        TAREA_BAJA_CAPTURA(359, "validacion", "", false, false),
        TAREA_BORRAR_RENGLON(360, "", "", false, false),
        TAREA_FINALIZA_DI(20, "validacion", "", false, false),
        TAREA_PRODUCTOS_BUSQUEDA(406, "raiz", TABLA_PRODUCTOS, true, true),
        TAREA_ESTACIONES(408, "linea", TABLA_ESTACIONES, true, true),
        TAREA_ASIGNA_ESTACION(409, "raiz", "", false, false),
        TAREA_GUARDA_COMPRA(414, "validacion", "", false, false),
        TAREA_SUBALMACENES(415, "linea", TABLA_SUBALMACENES, true, true),
        TAREA_PROVEEDORES(417, "linea", TABLA_PROVEEDORES, true, true),
        TAREA_TRAE_BULTO_CAPTURA(442, "raiz", "", false, false),
        TAREA_CIERRA_BULTO(443, "raiz", "", false, false),
        TAREA_GUARDA_LOTE(446, "validacion", TABLA_CADUCIDADES, true, true),
        TAREA_LISTA_LOTE(447, "validacion", TABLA_CADUCIDADES, true, true),
        TAREA_BORRA_LOTE(448, "validacion", "", false, false),
        TAREA_LISTA_LOTE_ENVIO(451, "validacion", TABLA_CADUCIDADES_ENVIO, true, true),
        TAREA_GUARDA_LOTE_VENTA(459, "validacion", TABLA_CADUCIDADES, true, true),
        TAREA_LISTA_DIF_CADUCIDADES(464, "validacion", TABLA_DIFERENCIAS_CADUCIDADES, true, true),
        TAREA_BUSCAR_CLIENTE(467, "validacion", TABLA_CLIENTES, true, true),
        TAREA_BUSCA_UBICACION(475, "validacion", "", false, false),
        TAREA_UBICA_PRODUCTO(476,"validacion","",false,false),
        TAREA_PRODS_UBICACION(477,"validacion", TABLA_PRODUCTOS_UBICACION, true, true),
        TAREA_UBICACION_DE_PROD(478, "validacion", TABLA_UBIKPROD, true, true),
        TAREA_SURTE_VENTA(503, "validacion", "", false, false),
        TAREA_CANCELA_VENTA(514, "validacion", "", false, false),
        TAREA_LOTES_VENTA(518, "validacion", TABLA_CADUCIDADES, true, true),
        TAREA_LOTES_PENDIENTES(520, "validacion", TABLA_LOTES_PENDIENTES, true, true),
        TAREA_TRAE_UBIC_X_NIVEL(551, "", "", false, false),
        TAREA_REPO_ORDEN(552, "", "", false, false),
        TAREA_REPO_GUARDA(553, "validacion", "", false, false),
        TAREA_REPO_DET_ORDEN(554, "", "", false, false),
        TAREA_REPO_CREA(695, "validacion", "", false, false),
        TAREA_REPO_FALTA_SUBIR(556, "validacion", TABLA_REPOFALTA, true, true),
        TAREA_REPO_FALTA_BAJAR(557, "", "", false, false),
        TAREA_REPO_SUBIDOS(558, "", "", false, false),
        TAREA_REPO_BAJADOS(559, "validacion", TABLA_REPOFALTA, true, true),
        TAREA_REPO_SUBE_RENGLON(560, "validacion", "", false, false),
        TAREA_REPO_BAJA_RENGLON(561, "", "", false, false),
        TAREA_REPO_BUSCA_PRROD_UBIC(562, "validacion", "", false, false),
        TAREA_REPO_BUSCA_PROD_BAJAR(563, "validacion", "", false, false),
        TAREA_ITEMS_MENU(567, "linea", TABLA_ITEMS_MENU, true, true),
        TAREA_ESPACIO_ETIQUETA(569, "validacion", "", false, false),
        TAREA_APLICA_CIERRA(583, "validacion", "", false, false),
        TAREA_INFO_INVENTARIO(592, "validacion", "", false, false),
        TAREA_INFO_DOCUMENTO(593, "validacion", "", false, false),
        TAREA_CONSULTA_INVENTARIO(597, "validacion", "", false, false),
        TAREA_ASIGNA_INVENTARIO(598, "validacion", "", false, false),
        TAREA_TRAE_UBICACIONES(599, "raiz", TABLA_UBICACIONES, true,true),
        TAREA_DESASIGNA_INVENTARIO(600, "validacion", "", false, false),
        TAREA_ABRE_UBICACION(602, "validacion", "", false, false),
        TAREA_ELIMINAR_CONTADOS(603, "validacion", "", false, false),
        TAREA_CARGA_PEDIDO(605, "validacion", TABLA_RENGLON, true, true),
        TAREA_GUARDA_TAREA(606, "validacion", "", false, false),
        TAREA_SURTE_TAREA(607, "validacion", "", false, false),
        TAREA_CATALOGOS(608, "lineas", TABLA_CATALOGOS, true, true),
        TAREA_PRODUCTOS_DI(610, "productos", TABLA_PRODUCTOS_DI, true, true),
        TAREA_LISTA_BULTOS(611, "raiz", TABLA_BULTOS, true, true),
        TAREA_TRAE_ESTACIONES(613, "linea", TABLA_ESTACIONES, true, true),
        TAREA_ASIGNA_ESTACION_MOVIL(614, "raiz", "", false, false),
        TAREA_LOGIN_NUMERICO(615, "raiz", "", false, false),
        TAREA_VENTAS(616,"raiz", TABLA_VENTAS, true, true),
        TAREA_ACTUALIZA_TITULO_NOTAS(617, "validacion", "", false, false),
        TAREA_PEDIDOS_FOLIOS(620, "validacion", "", false, false),
        TAREA_COLONIAS_CP(636, "validacion", TABLA_COLONIAS, true, true),
        TAREA_ORDENES_REPO(696, "validacion", TABLA_REPOSICION, true, true),
        TAREA_BUSCA_UBIC(704, "validacion", "", false, false),
        TAREA_TRAE_ANDENES(705, "validacion", "", false, false),
        TAREA_CAPTURA_REPO(706, "validacion", TABLA_REPOSICION, true, true),
        TAREA_REPO_CALCULA(677, "validacion", TABLA_REPOCALCULA, true, true),
        TAREA_REPO_NUEVO_LIBRE(716, "validacion", "", false, false),
        TAREA_REPO_DACOCREA(717, "validacion", "", false, false),
        TAREA_REPO_ACOM_CIERRA(718, "validacion", "", false, false),
        TAREA_REPO_DACO_BORRA(719, "validacion", "", false, false),
        TAREA_CONT_GUARDA(723, "validacion", "", false, false),
        TAREA_CONT_ASIGNA(724, "validacion", "", false, false),
        TAREA_CONT_ESTATUS(726, "validacion", "", false, false),
        TAREA_CONT_BORRA_RENGLON(727, "validacion", "", false, false),
        TAREA_CONT_GUARDA_RENGLON(731, "validacion", "", false, false),
        TAREA_CONT_TRAECONTEO(733, "validacion", "", false, false),
        TAREA_CONT_VALIDA_ASIG(734, "validacion", "", false, false),
        TAREA_CONT_BUSCA_PROD(737, "validacion", "", false, false),
        TAREA_CONT_LISTA_CONTADO(739, "validacion", TABLA_CONTADOS, true, true),
        TAREA_CONT_CANCELA(741, "validacion", "", false, false),
        TAREA_CONT_APLICA(310, "validacion", "", false, false),
        TAREA_INFO_PROD(892, "validacion", "", false, false),
        TAREA_CONT_MIS_ASIG(743, "validacion", TABLA_GENERICA, true, true),
        TAREA_TRAE_PROD_DDIN(794, "validacion", "", false, false),
        TAREA_AJUSTA_OC(797, "validacion", "", false, false),
        TAREA_DVTA_SINEXIST(875, "texto", "", false, false),
        TAREA_ESDO_CUENTA(903, "validacion", "", false, false),
        TAREA_MOVIL_TOTAL(923, "validacion", "", false, false),
        TAREA_IMPORTESDI(925, "validacion", "", false, false),
        TAREA_NOTIHH(929, "validacion", TABLA_NOTIFICACION, true, true),
        REST_VERSION(0, "html", "servicio/Descargas/recibirjson?json=", false, false),
        TAREA_TRAE_ANDENES_SURT(944, "validacion", "", false, false),
        TAREA_GUARDA_ANDEN_SURT(943, "validacion", "", false, false),
        TAREA_ACOBRAR(840, "validacion", "", false, false),
        TAREA_VNTACLTE(492, "validacion", "", false, false),
        TAREA_FACT_GUARDA(472, "validacion", "", false, false),
        TAREA_VIAJ_LISTA(971, "validacion", TABLA_VIAJE, true, true),
        TAREA_DVIAJ_LISTA(972, "validacion", TABLA_DVIAJE, true, true),
        TAREA_DVIAJ_IND(151, "validacion", "", false, false),
        TAREA_VIAJ_INFO(974, "validacion", "", false, false),
        TAREA_VIAJ_SUBE(975, "validacion", "", false, false),
        TAREA_VIAJ_BAJA(976, "validacion", "", false, false),
        TAREA_VIAJ_CIERRA(977, "validacion", "", false, false),
        TAREA_TRAE_CHOFER(980, "validacion", TABLA_UTIVIAJE, true, true),
        TAREA_TRAE_VEHICULO(979, "validacion", TABLA_UTIVIAJE, true, true),
        TAREA_ASIGNA_VCHOFER(997, "validacion", "", false, false),
        TAREA_ASIGNA_VVEHICULO(998, "validacion", "", false, false),
        TAREA_VIAJE_DETALLES(973, "validacion", TABLA_UTIVIAJE, true, true),
        TAREA_VNTATRAEULTIMA(1050, "validacion", "", false, false),
        TAREA_VNTABQDPRODUCTO(1049, "lineas", TABLA_DCARRO, true, true),
        TAREA_VNTARETIRO(534, "validacion", "", false, false),
        TAREA_VNTAULTIMAVNTA(1051, "validacion", "", false, false),
        TAREA_ARQEINICIO(1052, "validacion", "", false, false),
        TAREA_ARQEGUARDA(1073, "validacion", "", false, false),
        TAREA_BQDACLTE(1061, "lineas", TABLA_GENERICA, true, true),
        TAREA_VNTACONCREDITO(1063, "validacion", "", false, false),
        TAREA_TRAECLTE(1068, "validacion", "", false, false),
        TAREA_VNTAMASPROD(1069, "lineas", TABLA_PRODUCTOS_DI,true, true),
        TAREA_TRAECUENTA(1070, "lineas", TABLA_CUENTAS, true, true),
        TAREA_VNTACANCELA(1071, "validacion", "", false, false),
        TAREA_GUARDA_CUENTA(22, "validacion", "", false, false),
        TAREA_REPORTEVNTA(1074, "lineas", TABLA_GENERICA, true, true),
        TAREA_REPORTEARQE(1075, "lineas", TABLA_GENERICA, true, true),
        TAREA_IMPRIMEARQE(1076, "validacion", "", false, false),
        TAREA_REPODOCUMENTO(1077, "lineas", TABLA_GENERICA, true, true),
        TAREA_IMPRIMEDOCS(1078, "validacion", "", false, false),
        TAREA_VNTAREGISTRAR(1087, "validacion", "", false, false),
        TAREA_REPOVNTAESPERA(1086, "lineas", TABLA_GENERICA, true, true),
        TAREA_REPORETIROS(1088, "lineas", TABLA_GENERICA, true, true),
        TAREA_IMPRIMERETIRO(1089, "validacion", "", false, false),
        TAREA_REPORTEEXIS(1091, "lineas", TABLA_GENERICA, true, true),
        TAREA_IMPRIMEEDOCTA(1090, "validacion", "", false, false),
        TAREA_PRODGUARDA(1092, "validacion", "", false, false),
        TAREA_PRECGUARDA(1093, "validacion", "", false, false),
        TAREA_MGNFAMIGUARDA(1094, "validacion", "", false, false),
        TAREA_TRAEPRODMOVIL(1095, "linea", "", false, false),
        TAREA_IMPUGUARDA(1096, "validacion", "", false, false),
        TAREA_TRAEMARGEN(1097, "lineas", TABLA_GENERICA, true, true),
        TAREA_PERMISOSPROD(1098, "linea", "", false, false),
        TAREA_REPCORTEMOVIL(1099, "lineas", TABLA_GENERICA, true, true),
        TAREA_TIKCORTEMOVIL(1100, "validacion", "", false, false),
        TAREA_HAZCORTEMOVIL(1101, "validacion", "", false, false),
        TAREA_REPVENTAMOVIL(1102, "validacion", "", false, false),
        TAREA_TRAECORTMOVIL(1103, "validacion", "", false, false),
        TAREA_BUSCAFOLIODEV(1104, "validacion", "", false, false),
        TAREA_DEVOLPRODS(1105, "lineas", TABLA_GENERICA, true, true),
        TAREA_DEVOLTOTAL(1106, "validacion", "", false, false),
        TAREA_DEVOLAPLICA(1107, "validacion", "", false, false),
        TAREA_CAMBIAPASS(1108, "validacion", "", false, false),
        TAREA_TRAEORCOINFO(1109, "validacion", "", false, false),
        TAREA_LEEDATOSSUCURSAL(1111, "validacion", "", false, false),
        TAREA_LEEDATOSPRODUCTO(1112, "validacion", "", false, false),
        TAREA_LEEDATOSTIAI(1113, "validacion", "", false, false),
        TAREA_GUARDADATOSSUCURSAL(1114, "validacion", "", false, false),
        TAREA_GUARDADATOSPRODUCTO(1115, "validacion", "", false, false),
        TAREA_GUARDADATOSEXTRA(1116, "validacion", "", false, false),
        TAREA_CONSULTASALDOTA(23, "validacion", "", false, false),
        TAREA_CONTMODELOS(1118, "validacion", TABLA_GENERICA, true, true),
        TAREA_REPFALTCONTAR(1119, "lineas", TABLA_GENERICA, true, true),
        TAREA_VNTAINSERTARENGLON(1121, "validacion", TABLA_RENGLONES, true, true),
        TAREA_CONTCHEKFINMODELO(1122, "validacion", "", false, false),
        TAREA_GUARDAF10(1125, "validacion", "", false, false),
        TAREA_FINCOTIZA(1126, "validacion", "", false, false),
        TAREA_TRAECUCL(1127, "validacion", "", false, false),
        TAREA_DATAINFO(-3, "validacion", "", false, false)
        ;


        private final int tareaId;
        private final String tipoRespuesta;
        private final String tablaBD;
        private final boolean multipleRespuesta;
        private final boolean directoBD;

        Valores(int tareaId, String tipoRespuesta, String tablaBD, boolean multipleRespuesta, boolean directoBD){
            this.tareaId = tareaId;
            this.tipoRespuesta = tipoRespuesta;
            this.tablaBD = tablaBD;
            this.multipleRespuesta = multipleRespuesta;
            this.directoBD = directoBD;
        }

        public int getTareaId() {
            return tareaId;
        }

        public String getTipoRespuesta() {
            return tipoRespuesta;
        }

        public String getTablaBD() {
            return tablaBD;
        }

        public boolean isMultipleRespuesta() {
            return multipleRespuesta;
        }

        public boolean isDirectoBD() {
            return directoBD;
        }
    }
}
