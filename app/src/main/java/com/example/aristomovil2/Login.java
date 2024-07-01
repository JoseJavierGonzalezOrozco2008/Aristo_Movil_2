package com.example.aristomovil2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aristomovil2.facade.Estatutos;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

public class Login extends ActividadBase implements EasyPermissions.PermissionCallbacks{
    private String androidId;
    private EditText editIP;
    Dialog dialPermis;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarActividad("Aristo Movil");
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        dialPermis = new Dialog(this);
        Button btnlogin = findViewById(R.id.btnLoginLogin);
        EditText txtUsuario = findViewById(R.id.editLoginUsuario);
        EditText txtPassword = findViewById(R.id.editLoginPassword);

        ((TextView)findViewById(R.id.txtLoginVersion)).setText(String.format("Versión: %s", BuildConfig.VERSION_NAME));

        btnlogin.setOnClickListener(view -> {
            hideKeyboard(view);
            if (Libreria.isNumeric(txtPassword.getText().toString()))
                login(txtUsuario.getText().toString().trim(), txtPassword.getText().toString());
            else
                muestraMensaje("La contraseña debe ser numérica", R.drawable.mensaje_warning);
        });

        txtPassword.setOnKeyListener(((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                btnlogin.performClick();
                return true;
            }
            return false;
        }));

        checkBluetoothPermissions();
    }

    private void mostrarMensajeExplicativo() {

        dialPermis.setContentView(R.layout.dialog_permisos);
        dialPermis.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageViewAcept = dialPermis.findViewById(R.id.imageView);
        Button btnAceptPermiso = dialPermis.findViewById(R.id.btnAceptarPermis);
        dialPermis.show();
        btnAceptPermiso.setOnClickListener(v -> {
            dialPermis.dismiss();
            //checkBluetoothPermissions();
        });
        /*new AlertDialog.Builder(this,R.style.AlertDialog_Style)
                .setTitle("Permisos de Bluetooth")
                .setMessage("Esta aplicación requiere de algunos permisos para funcionar correctamente.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkBluetoothPermissions();
                    }
                })
                /*.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Permiso concedido, realiza las operaciones de Bluetooth aquí
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // El usuario denegó el permiso, puedes mostrar un mensaje o tomar alguna acción
        mostrarMensajeExplicativo();
    }

    private void checkBluetoothPermissions() {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        String[] permBluConn = {Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

        if(Build.VERSION.SDK_INT < 31){
            if (EasyPermissions.hasPermissions(this, perms)) {

            } else {
                EasyPermissions.requestPermissions(this, "Se requieren algunos permisos.",
                        123, perms);
            }
        } else if(Build.VERSION.SDK_INT >= 31){
            if (EasyPermissions.hasPermissions(this, permBluConn)) {
            }else{
                EasyPermissions.requestPermissions(this, "Se requieren algunos permisos.",123, permBluConn);
            }
        }
    }

    /**
     * Listener de boton de retorceso
     * Muestra un dialogo pra indicar si se desea salir de la aplicacion
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmacion")
                .setMessage("¿Estas seguro que desas salir de la aplicacion?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    finish();
                    System.exit(0);
                })
                .setNegativeButton(android.R.string.no, null).setCancelable(false).show();
    }

    /**
     * Crea las opciones de menu del Toolbar
     *
     * @param menu Menu
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener para la seleccion de un item en el menu del Toolbar
     *
     * @param item Item seleccionado
     * @return Retorna true para indicar exito
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLoginConfiguracion:
                //configuracion();
                break;
            case R.id.menuLoginConectarse:
                dialogoIp();
                break;
            case R.id.menuLoginAcercade:
                dialogoAcercaDe();
                break;
        }
        return true;
    }

    public void dialogoAcercaDe(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_acerda_de);

        TextView txtVersion = dialog.findViewById(R.id.txtVersion);
        TextView txtDispositvo = dialog.findViewById(R.id.txtDispositivo);
        //ImageButton btnClose = dialog.findViewById(R.id.btnCloseAcercaDe);

        txtDispositvo.setText(MessageFormat.format("Dispositivo: {0}", androidId));
        txtVersion.setText(String.format("Versión: %s", BuildConfig.VERSION_NAME));

        //btnClose.setOnClickListener(view -> { dialog.dismiss(); });

        dialog.show();
    }

    /**
     * LLamaa al servicio de login
     *
     * @param usuario  El nobmre de usuario
     * @param password Lacotnraseña
     */
    private void login(String usuario, String password) {
        peticionWS(Enumeradores.Valores.TAREA_LOGIN_NUMERICO, "SQL", "SQL", usuario, password, "i"/*androidId*/);
    }

    @SuppressLint("SetTextI18n")
    public void dialogoIp() {
        final Dialog dialogoConecta = new Dialog(Login.this);
        dialogoConecta.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogoConecta.setCancelable(false);
        Objects.requireNonNull(dialogoConecta.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogoConecta.setContentView(R.layout.dialogo_ip);

        String miIp = getValuePreferences("ip");
        TextView textView = dialogoConecta.findViewById(R.id.tituloIp);
        textView.setText("Direccion IP\nIp actual: " + (!Libreria.tieneInformacion(miIp) ? "192.168.1.81" : miIp));
        editIP = dialogoConecta.findViewById(R.id.newIp);
        Button aceptar = dialogoConecta.findViewById(R.id.btnAceptar);
        Button cancelar = dialogoConecta.findViewById(R.id.btnCancelar);
        Button probar = dialogoConecta.findViewById(R.id.btnProbar);
        ImageButton escaner = dialogoConecta.findViewById(R.id.btnEscaner);
        editIP.setText(miIp);
        editIP.requestFocus();

        aceptar.setOnClickListener(v -> {
            String nuevaIp = editIP.getText().toString();
            if (Libreria.isIp(nuevaIp)) {
                SharedPreferences preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ip", nuevaIp);
                editor.apply();
                muestraMensaje("Guardado", R.drawable.mensaje_exito);
                dialogoConecta.dismiss();
            } else {
                muestraMensaje("IP invalida", R.drawable.mensaje_error);
                editIP.requestFocus();
            }
        });

        probar.setOnClickListener(view -> wsPrueba(editIP.getText().toString()));

        editIP.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                aceptar.performClick();
                return true;
            }
            return false;
        });

        escaner.setOnClickListener(view -> barcodeEscaner());

        cancelar.setOnClickListener(v -> dialogoConecta.dismiss());

        dialogoConecta.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult.getContents() != null && editIP != null)
            editIP.setText(intentResult.getContents());
        else
            muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
    }

    /**
     * Muestra un dialogo donde se debe pedir contraseña para ingresar a las configuraciones de la app
     */
    private void configuracion() {
        final Dialog dialogo = new Dialog(this);
        dialogo.setCancelable(false);
        Objects.requireNonNull(dialogo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogo.setContentView(R.layout.dialogo_password);

        final EditText contraseña = dialogo.findViewById(R.id.dPasswordContraseña);
        Button aceptar = dialogo.findViewById(R.id.dPasswordbtnAceptar);
        Button cancelar = dialogo.findViewById(R.id.dPasswordbtnCancelar);

        aceptar.setOnClickListener(v -> {
            hideKeyboard(contraseña);
            if (contraseña.getText().toString().equals("Aristo")) {
                Intent intent = new Intent(this, Configuracion.class);
                intent.putExtra("androidID", androidId);
                intent.putExtra("login", true);
                intent.putExtra("impresora", false);
                startActivity(intent);
                dialogo.dismiss();

            } else {
                muestraMensaje("Contraseña de Configuracion incorrecta", R.drawable.mensaje_error);
            }
        });

        cancelar.setOnClickListener(view -> dialogo.dismiss());
        dialogo.show();
    }

    /**
     * Procesa la respuesta de una peticion
     *
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        hideKeyboard(this);
        ContentValues obj = (ContentValues) output.getExtra1();
        if ( output.getTarea() == Enumeradores.Valores.TAREA_LOGIN_NUMERICO) {
            if(!output.getExito()){
                muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
            }else if(obj == null){
                muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
                return ;
            }
        }
        if (output.getTarea() == Enumeradores.Valores.TAREA_LOGIN_NUMERICO) {
            if (output.getExito()) {
                SharedPreferences sharedPreferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("androidID", androidId);
                addPreferenceString(editor, obj, "usuaid", "usuarioID", "");
                addPreferenceString(editor, obj, "mensaje", "user", "");
                addPreferenceString(editor, obj, "usuaid", "usuarioID", "");
                addPreferenceInt(editor, obj, "estaid", "estaid", 0);
                addPreferenceString(editor, obj, "estacion", "estacion", "");
                addPreferenceInt(editor, obj, "tipoventa", "tipoVenta", 0);
                addPreferenceBoolean(editor, obj, "puedecobrar", "puedecobrar", true);
                addPreferenceBoolean(editor, obj, "verfoto", "muestrafoto", true);
                addPreferenceString(editor, obj, "fechacad", "fechacad", "");
                addPreferenceBoolean(editor, obj, "vendesinexist", "existencias", true);
                addPreferenceBoolean(editor, obj, "mandaimprimir", "mandaimprimir", true);
                addPreferenceBoolean(editor, obj, "capcredito", "cambiacredito", true);
                addPreferenceInt(editor, obj, "clientedefault", "clientedefault", 0);
                addPreferenceString(editor, obj, "nomCliente", "nomCliente", "");
                addPreferenceBoolean(editor, obj, "capcredito", "cambiacredito", true);
                addPreferenceString(editor, obj, "cuentabanco", "cuentabanco", "");
                addPreferenceInt(editor, obj, "cuentaid", "cuentaid", 0);
                addPreferenceBoolean(editor, obj, "directovnta", "directoVenta", true);
                addPreferenceBoolean(editor, obj, "mispedidos", "surtidorChecked", false);
                addPreferenceString(editor, obj, "sucuid", "sucuID", "");
                addPreferenceString(editor, obj, "empresa", "empresa", "");
                addPreferenceInt(editor, obj, "digitos", "digitos", 0);
                addPreferenceBoolean(editor, obj, "concaducidad", "concaducidad", true);
                addPreferenceBoolean(editor, obj, "muestraimportes", "muestraimportes", true);
                addPreferenceBoolean(editor, obj, "imprimedetalle", "imprimedetalle", true);
                addPreferenceBoolean(editor, obj, "tipobus", "tecladocodigo", true);
                addPreferenceInt(editor, obj, "espacios", "espacios", 0);
                addPreferenceInt(editor, obj, "tiporecibo", "tiporecibo", 0);
                addPreferenceInt(editor, obj, "cantidadusual", "cantidadusual", 10);
                addPreferenceBoolean(editor, obj, "mandaimprimir", "mandaimprimir", true);
                addPreferenceBoolean(editor, obj, "permif", "permif", false);
                addPreferenceBoolean(editor, obj, "capdivisa", "capdivisa", false);
                addPreferenceBoolean(editor, obj, "doc14soli", "doc14soli", true);
                addPreferenceInt(editor, obj, "estadoprod", "estadoprod", 84);
                addPreferenceBoolean(editor, obj, "muestrabultos", "muestrabultos", false);
                addPreferenceInt(editor, obj, "divisadefault", "divisadefault", 212);
                addPreferenceBoolean(editor, obj, "puedeasignar", "puedeasignar", false);
                addPreferenceBoolean(editor, obj, "confirmacierraubic", "confirmacierraubic", false);
                addPreferenceBoolean(editor, obj, "editacompra", "editacompra", false);
                addPreferenceInt(editor, obj, "tiempoconsulta", "tiempoconsulta", 120);
                addPreferenceBoolean(editor, obj, "pidefac", "pidefac", false);
                addPreferenceBoolean(editor, obj, "solifac", "solifac", false);
                addPreferenceBoolean(editor, obj, "pideanden", "pideanden", false);
                addPreferenceBoolean(editor, obj, "vertouch", "vertouch", false);
                addPreferenceBoolean(editor, obj, "rompe", "rompe", true);
                addPreferenceBoolean(editor, obj, "mono", "mono", false);
                addPreferenceInt(editor, obj, "muestraf", "muestraf", 3);

                agregaDcatalogos(obj.getAsString("dcatalogo"));
                agregaMargenes(obj.getAsString("tipomargen"));
                agregaImpuestos(obj.getAsString("impuesots"));
                Intent intent = new Intent(this, MainMenu.class);
                editor.apply();
                startActivity(intent);
                finish();
            } else
                muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
        }else if(output.getTarea() == Enumeradores.Valores.TAREA_PRUEBA_CONEXION){
            if(obj==null){
                muestraMensaje("Error en la conexion",R.drawable.mensaje_error);
            }
            dlgPruebaConexion(obj);
        }

    }

    private void agregaDcatalogos(String pCadena){
        if(!Libreria.tieneInformacion(pCadena)){
            return;
        }
        servicio.borraDatosTabla(Estatutos.TABLA_DCATALOGO);
        String dcatalogos[] = pCadena.split(Pattern.quote("|"));
        if(dcatalogos.length==0)
            return;
        String campos[];
        ContentValues obj;
        for(String dcat:dcatalogos){
            campos = dcat.split(",");
            if(campos.length>0){
                obj=new ContentValues();
                obj.put("id",campos[1]);
                obj.put("cata",campos[0]);
                obj.put("abrevi",campos[2]);
                obj.put("n2",campos[3]);
                if(campos.length>4){
                    obj.put("t1",campos[4]);
                }
                servicio.guardaBD(obj, Estatutos.TABLA_DCATALOGO);
            }
        }
    }

    private void agregaImpuestos(String pCadena){
        if(!Libreria.tieneInformacion(pCadena)){
            return;
        }
        String dcatalogos[] = pCadena.split(Pattern.quote("|"));
        if(dcatalogos.length==0)
            return;
        String campos[];
        ContentValues obj;
        for(String dcat:dcatalogos){
            campos = dcat.split(",");
            if(campos.length>0){
                obj=new ContentValues();
                obj.put("cata",-2);
                obj.put("id",campos[0]);
                obj.put("abrevi",campos[1]);
                obj.put("l1",campos[2]);
                servicio.guardaBD(obj, Estatutos.TABLA_DCATALOGO);
            }
        }
    }

    private void agregaMargenes(String pCadena){
        if(!Libreria.tieneInformacion(pCadena)){
            return;
        }
        String dcatalogos[] = pCadena.split(Pattern.quote("|"));
        if(dcatalogos.length==0)
            return;
        String campos[];
        ContentValues obj;
        for(String dcat:dcatalogos){
            campos = dcat.split(",");
            if(campos.length>0){
                obj=new ContentValues();
                obj.put("cata",-1);
                obj.put("id",campos[0]);
                obj.put("abrevi",campos[1]);
                obj.put("e1",campos[2]);
                servicio.guardaBD(obj, Estatutos.TABLA_DCATALOGO);
            }
        }
    }

    public void dlgPruebaConexion(ContentValues pContenido){
        String mensaje="No se pudo establecer la conexion con el servidor";

        final Dialog dialogo = new Dialog(this);
        //dialogo.setCancelable(false);
        dialogo.setContentView(R.layout.dialogo_acerda_de);
        TextView acerca=dialogo.findViewById(R.id.acercarde);
        TextView version=dialogo.findViewById(R.id.txtVersion);
        TextView dispositivo=dialogo.findViewById(R.id.txtDispositivo);
        dispositivo.setVisibility(View.GONE);
        version.setVisibility(View.GONE);
        if(pContenido!=null){
            mensaje=pContenido.getAsString("mensaje");
        }else{
            acerca.setBackgroundColor(getResources().getColor(R.color.fondoError));
            acerca.setTextColor(getResources().getColor(R.color.white));
        }
        acerca.setText(mensaje);
        acerca.setTypeface(Typeface.MONOSPACE);

        dialogo.show();
    }
}