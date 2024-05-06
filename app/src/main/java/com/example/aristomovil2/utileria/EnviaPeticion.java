package com.example.aristomovil2.utileria;

public class EnviaPeticion {

    /**
     * Clase realizada para contener los datos con los que se realizarón una petición al servidor y
     * ademas almacena los datos de respuesta de la petición
     */
    private Enumeradores.Valores tarea;
    private String origen;
    private String clave;
    private String dato1;
    private String dato2;
    private String dato3;
    private String usuario;

    private Boolean exito;
    private String mensaje;
    private Object extra1;
    private Object extra2;

    /**
     * Inicializacion de variables con la tarea que se realizar la peticion.
     * @param tarea (int) Servicio que se solicitara
     */
    public EnviaPeticion(Enumeradores.Valores tarea) {
        this.tarea = tarea;
        this.exito = false;
        origen = "";
        dato1 = "";
        dato2 = "";
        dato3 = "";
        clave = "";
        usuario = "";
    }

    /**
     * Regresa la tarea que se solicitara.
     */
    public Enumeradores.Valores getTarea() {
        return tarea;
    }

    /**
     * Ingresa una nueva tarea que se realizara peticion.
     * @param tarea (int) nuevo servicio
     */
    public void setTarea(Enumeradores.Valores tarea) {
        this.tarea = tarea;
    }

    /**
     * Regresa el Origen.
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Ingresa un nuevo origen que se realizara peticion.
     * @param origen (String) nuevo origen.
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * obtiene la clave para utilizar un servicio
     * @return regresa la clave.
     */
    public String getClave() {
        return clave;
    }

    /**
     * Ingresa una nueva clave que se realizará petición.
     * @param clave (String) nueva clave.
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * regresa el primer dato utilizado en la peticion
     * @return (String)
     */
    public String getDato1() {
        return dato1;
    }

    /**
     * Ingresa dato1 que se realizara para poder procesar la peticion.
     * @param dato1 (String) ingresar el primer dato.
     */
    public void setDato1(String dato1) {
        this.dato1 = dato1;
    }

    /**
     * regresa el segundo dato utilizado en la peticion
     * @return (String)
     */
    public String getDato2() {
        return dato2;
    }

    /**
     * Ingresa dato2 que se realizara para poder procesar la peticion.
     * @param dato2 (String) ingresar el segundo dato.
     */
    public void setDato2(String dato2) {
        this.dato2 = dato2;
    }

    /**
     * regresa el tercer dato utilizado en la petición.
     * @return (String)
     */
    public String getDato3() {
        return dato3;
    }

    /**
     * Ingresa dato3 que se realizara para poder procesar la peticion.
     * @param dato3 (String) ingresar el tercer dato.
     */
    public void setDato3(String dato3) {
        this.dato3 = dato3;
    }

    /**
     * Muestra si la peticion fue exito o no.
     * @return (boolean)
     */
    public Boolean getExito() {
        return exito;
    }

    /**
     * Ingresa el estatus de respuesta de la peticion
     * @param exito (boolean) ingresa true o false dependiendo de la respuesta.
     */
    public void setExito(Boolean exito) {
        this.exito = exito;
    }

    /**
     * Regresa el mensaje informativo obtenido.
     * @return (String)
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Ingresa la respuesta del servidor.
     * @param mensaje (String) mensaje informatico correspondiente a la respuesta del servicio
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Regresa el contenido extra1
     * @return (Object)
     */
    public Object getExtra1() {
        return extra1;
    }

    /**
     * Ingresa toda la informacion regresa por el servidor.
     * @param extra1 (Object) se almacena toda la informacion regresada por el servidor (Normalmente, se trata como ContentValues).
     */
    public void setExtra1(Object extra1) {
        this.extra1 = extra1;
    }

    /**
     * Regresa el contenido extra2
     * @return (Object)
     */
    public Object getExtra2() {
        return extra2;
    }

    /**
     * Almacena informacion extra
     * @param extra2 (Object) se utiliza para almacenar informacione extra en caso de necesitar.
     */
    public void setExtra2(Object extra2) {
        this.extra2 = extra2;
    }

    /**
     * Regresa el ususario
     * @return El usuario
     */
    public String getUsuario() { return usuario; }

    /**
     * Ingresa el usuario
     * @param usuario El usuario
     */
    public void setUsuario(String usuario) { this.usuario = usuario;}
}
