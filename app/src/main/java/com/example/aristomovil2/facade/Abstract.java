package com.example.aristomovil2.facade;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase que administra la conexion y operaciones a la base de datos
 */
public class Abstract extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    /**
     * Constructor de la clase
     * @param context Contexto de la aplicacion
     * @param name Nombre
     * @param factory Factory
     * @param version Version de la base de datos
     */
    public Abstract(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Evento onCreate
     * @param sqLiteDatabase Base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        creaBD(sqLiteDatabase);
    }

    /**
     * Actualiza la base de datos si la version ha cambiado
     * @param sqLiteDatabase Base de datos
     * @param oldVersion Version actual
     * @param newVersion Nueva Version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            borraBD(sqLiteDatabase);
            creaBD(sqLiteDatabase);
        }
    }

    /**
     * Crea la base de datos
     * @param db Conexion a la bse de datos
     */
    private void creaBD(SQLiteDatabase db){
        for (String query: Estatutos.CREA_BD){
            db.execSQL(query);
        }
    }

    /**
     * Elimina la base de datos
     * @param db Conexion a la base de datos
     */
    public void borraBD(SQLiteDatabase db) {
        for (String query : Estatutos.BORRA_BD) {
            db.execSQL(query);
        }
    }

    /**
     * Guarda un registro en la base de datos
     * @param pTabla Tabla donde se debe insertar el registro
     * @param pContenido Contenido del registro
     * @param pConflicto Conflicto
     */
    public void alta(String pTabla, ContentValues pContenido, Integer pConflicto){
        database.insertWithOnConflict(pTabla, null, pContenido, pConflicto);
    }

    /**
     * ELimina un registro de la base de datos
     * @param pTabla Tabla de donde se va a eliminar
     * @param pWhere Condicion WHERE para eleiminar
     */
    public void borra(String pTabla, String pWhere){
        database.delete(pTabla, pWhere, null);
    }

    /**
     * CRea una conexion a la base de datos
     */
    public void abreConexion(){
        database = getWritableDatabase();
    }

    /**
     * Cierra la conexion de la base de datos
     */
    public void cierraConexion() {
        if(database!=null){
            database.close();
        }
    }

    /**
     * Indica si se tiene conexion
     * @return .
     */
    public boolean tieneConexion(){
        return database!=null;
    }

    /**
     * Retorna la base de datos
     * @return La base de datos
     */
    public SQLiteDatabase getDatabase() {
        return database;
    }
}
