package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.facade.Estatutos.TABLA_GENERICA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;

public class Corte extends ActividadBase {
    private String v_cortFolio;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corte);
        inicializarActividad2("","");
        Bundle extras=getIntent().getExtras();
        v_cortFolio = extras.getString("cortfolio","");
        String fecha = extras.getString("fecha", Libreria.dateToString(new Date(),"YY-MM-DD HH:mm"));
        String menAviso = extras.getString("mensaje", "");
        actualizaToolbar2(v_cortFolio+" "+fecha,usuario+" "+v_nombreestacion);

        tvResultado = findViewById(R.id.cortResultado);
        TextView avisos = findViewById(R.id.cortAviso);
        tvResultado.setText("");
        avisos.setText(menAviso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, Menu.NONE, "Haz corte");
        menu.add(1, 2, Menu.NONE, "Reporte Corte");
        menu.add(1, 3, Menu.NONE, "Reporte Arqueos");
        menu.add(2, 4, Menu.NONE, "Indicadores de venta");
        if(false){
            menu.add(3, 5, Menu.NONE, "Factura");
            menu.add(3, 6, Menu.NONE, "Reporte Factura");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            menu.setGroupDividerEnabled(true);
        }
        return true;
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 1:
                hazCorte();
                break;
            case 2:
                reporte(Enumeradores.Valores.TAREA_REPCORTEMOVIL);
                break;
            case 3:
                reporte(Enumeradores.Valores.TAREA_REPORTEARQE);
                break;
            case 4:
                indicadores();
                break;
            default:
                dlgMensajeError("Menu en construccion",R.drawable.mensaje_warning);
        }
        return false;
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        ContentValues obj = (ContentValues)output.getExtra1();
        try{
            switch (output.getTarea()){
                case TAREA_REPCORTEMOVIL:
                    dlgReporte(9);
                    break;
                case TAREA_REPORTEARQE:
                    dlgReporte(3);
                    break;
                case TAREA_REPVENTAMOVIL:
                    String cadena = obj.getAsString("anexo");
                    dlgIndicadores(cadena);
                    break;
                case TAREA_TIKCORTEMOVIL:
                case TAREA_IMPRIMEARQE:
                    if(output.getExito()){
                        String ticket = obj.getAsString("anexo");
                        doImprime(ticket,false);
                    }else{
                        dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error);
                    }
                    break;
                case TAREA_HAZCORTEMOVIL:
                    if(output.getExito()){
                        tvResultado.setText("");
                        wsImprime(Enumeradores.Valores.TAREA_TIKCORTEMOVIL,v_cortFolio);
                    }else{
                        String cadena1 = obj.getAsString("anexo");
                        //tvResultado.setText(cadena1);
                        dlgMensajeError(cadena1,R.drawable.mensaje_error2);
                    }
                    break;
                default:
                    super.Finish(output);
            }
        }catch(UnsupportedEncodingException e){
            System.out.println(e);
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public void dlgIndicadores(String pCadena){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        String html = MessageFormat.format("<html>{0}</html>",pCadena);
        webview.loadData(html, "text/html; charset=utf-8", "UTF-8");

        alert.setView(webview);
        alert.setNegativeButton("Cerrar", (dialog, id) -> dialog.dismiss());
        alert.show();
    }

    private void reporte(Enumeradores.Valores pTarea){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(pTarea, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    private void indicadores(){
        peticionWS(Enumeradores.Valores.TAREA_REPVENTAMOVIL, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    private void hazCorte(){
        peticionWS(Enumeradores.Valores.TAREA_HAZCORTEMOVIL, "SQL", "SQL", usuarioID+"", v_cortFolio, "");
    }
}