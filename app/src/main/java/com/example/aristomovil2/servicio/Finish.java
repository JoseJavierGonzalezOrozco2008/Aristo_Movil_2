package com.example.aristomovil2.servicio;

import com.example.aristomovil2.utileria.EnviaPeticion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface Finish {
    default void Finish(EnviaPeticion output) throws Exception { }
}
