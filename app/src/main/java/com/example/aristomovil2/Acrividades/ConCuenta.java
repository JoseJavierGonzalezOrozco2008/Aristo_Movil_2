package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.VentasAdapter;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ConCuenta extends ActividadBase {
    private TableLayout tablaClientes;
    private  EditText capcliente;
    private Dialog dialogoClt;
    private TextView infoCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_cuenta);
        inicializarActividad("Estado de cuenta cliente");

        Button bntcliente=findViewById(R.id.btnCliente);
        ImageButton bntbusca=findViewById(R.id.btnBuscaClte);
        capcliente=findViewById(R.id.infoEdit);
        infoCuenta=findViewById(R.id.infoCuenta);

        bntbusca.setOnClickListener(view -> wsEstadoCuenta());
        bntcliente.setOnClickListener(view -> dlgCliente(capcliente.getText().toString()));
        capcliente.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                bntbusca.callOnClick();
            }
            return false;
        });
    }

    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_BUSCAR_CLIENTE:
                    if(output.getExito()){
                        llenarTablaClientes();
                    }else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    break;
                case TAREA_ESDO_CUENTA:
                    if(output.getExito()){
                        String anexo=obj.getAsString("anexo");
                        infoCuenta.setText(anexo);
                    }else{
                        infoCuenta.setText("");
                        capcliente.setText("");
                    }
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito : R.drawable.mensaje_error);
                    break;
            }
        } else {
            cierraDialogo();
            muestraMensaje("Sin respuesta del servidor", R.drawable.mensaje_error);
        }

        cierraDialogo();
    }

    public void wsBuscaCliente(String cliente){
        if(Libreria.tieneInformacion(cliente)){
            peticionWS(Enumeradores.Valores.TAREA_BUSCAR_CLIENTE, "SQL", "SQL", cliente, "", "");
        }else{
            muestraMensaje("Se requiere un nombre de cliente para buscar", R.drawable.mensaje_error);
        }
    }

    public void wsEstadoCuenta(){
        String cliente=capcliente.getText().toString();
        if(Libreria.tieneInformacion(cliente)){
            ContentValues mapa=new ContentValues();
            mapa.put("cliente",cliente);
            mapa.put("usuario",usuarioID);
            String xmledo=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_ESDO_CUENTA, "SQL", "SQL", xmledo, "-2", "");
        }else{
            muestraMensaje("Se requiere un nombre de cliente para buscar", R.drawable.mensaje_error);
        }
    }

    private void dlgCliente(String cliente){
        dialogoClt=new Dialog(this);
        dialogoClt.setContentView(R.layout.dialogo_busca_cliente);
        dialogoClt.setCancelable(true);
        tablaClientes = dialogoClt.findViewById(R.id.tablaCliente);
        EditText edita= dialogoClt.findViewById(R.id.editCliente);
        ImageButton busca=dialogoClt.findViewById(R.id.btnBuscaClte);

        busca.setOnClickListener(view -> wsBuscaCliente(edita.getText().toString()));
        edita.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                busca.callOnClick();
            }
            return false;
        });
        edita.requestFocus();
        if(Libreria.tieneInformacion(cliente)){
            edita.setText(cliente);
            busca.callOnClick();
        }
        dialogoClt.show();
    }

    private void llenarTablaClientes(){
        if(tablaClientes!=null){
        tablaClientes.removeAllViews();

        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.GRAY);
        row.setGravity(Gravity.CENTER);

        /*Acción*/
        TextView textView = new TextView(this);
        textView.setText(" ");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(0, 10, 10, 10);
        row.addView(textView);

        textView = new TextView(this);
        textView.setText(" ");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        //row.addView(textView);

        /*Cliente*/
        textView = new TextView(this);
        textView.setText("Cliente");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        row.addView(textView);

        /*Credito*/
        textView = new TextView(this);
        textView.setText("Credito");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        //row.addView(textView);

        tablaClientes.addView(row);
        ArrayList<Cliente> clientes = servicio.getClientes();
        /*Llenamos los datos de la tabla con los resultados de la búsqueda de clientes*/
        for (Cliente c:clientes) {
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            //Acción
            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 5, 10, 5);

            btnSeleccionar.setOnClickListener(view -> {
                capcliente.setText(c.getCliente());
                wsEstadoCuenta();
                dialogoClt.dismiss();
            });

            /*//Ver detalles
            ImageButton btnVerDetalle = new ImageButton(this);
            btnVerDetalle.setImageResource(R.drawable.eye);
            btnVerDetalle.setBackgroundColor(Color.TRANSPARENT);
            btnVerDetalle.setPadding(10, 10, 10, 10);
            btnVerDetalle.setOnClickListener(view -> muestraDetalleCliente(c));*/

            row.addView(btnSeleccionar);
           // row.addView(btnVerDetalle);

            //Cliente
            textView = new TextView(this);
            textView.setText(MessageFormat.format("{0}", c.getCliente()));
            textView.setTextColor(Color.BLACK);
            textView.setPadding(10, 5, 10, 5);
            row.addView(textView);

            //Credito
            textView = new TextView(this);
            textView.setText(c.getCredito()? "Si":"No");
            textView.setTextColor(Color.BLACK);
            textView.setPadding(10, 10, 10, 10);
            //row.addView(textView);

            tablaClientes.addView(row);
        }
        }
    }
}