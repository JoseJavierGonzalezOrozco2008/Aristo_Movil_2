package com.example.aristomovil2.modelos;

public class ProductoDI {
    private final int prodid, pedido, orcoid, ddinid;
    private final float registrado;
    private final String codigo, producto, usuario, faltacadu;

    public ProductoDI(int prodid, int pedido, int orcoid, int ddinid, float registrado, String codigo, String producto, String usuario, String faltacadu) {
        this.prodid = prodid;
        this.pedido = pedido;
        this.orcoid = orcoid;
        this.ddinid = ddinid;
        this.registrado = registrado;
        this.codigo = codigo;
        this.producto = producto;
        this.usuario = usuario;
        this.faltacadu = faltacadu;
    }

    public int getProdid() {
        return prodid;
    }

    public int getPedido() {
        return pedido;
    }

    public int getOrcoid() {
        return orcoid;
    }

    public int getDdinid() {
        return ddinid;
    }

    public float getRegistrado() {
        return registrado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getFaltacadu() {
        return faltacadu;
    }
}
