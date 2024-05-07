package com.example.aristomovil2;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.async.AsyncEscPosPrinter;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.aristomovil2.modelos.Estacion;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class Configuracion extends ActividadBase {
    SettingsFragment settings;
    String androidId;
    Boolean esLogin, hayimpresora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        inicializarActividad("Configuración");

        SharedPreferences preferences;
        preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        SettingsFragment fragment = new SettingsFragment(preferences, this, this);
        androidId = Objects.requireNonNull(getIntent().getExtras()).getString("androidID");
        esLogin = getIntent().getExtras().getBoolean("login", true);
        hayimpresora = getIntent().getExtras().getBoolean("impresora", false);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, fragment).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Listener para la seleccion de un item del menu en el Toolbar
     * @param item Item seleccionado
     * @return Retorna true para indicar extio
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        finish();
        return true;
    }

    /**
     * Retorna una instanica de impresion (AsyncEscPosPrinter) para una prueba de impresora
     * @param printerConnection (DeviceConection) La impresora a la que se va a conectar
     * @param generaCodigo Indica si el codigo de barras se genera por imagen(true) o por xml(false)
     * @return Retorna la instancia de impresion
     */
    private AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, boolean generaCodigo) {
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);
        String strCode = "C0100000029";
        Bitmap bmp = null;

        if (generaCodigo) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(strCode, BarcodeFormat.CODE_128, 400, 100);
                bmp = Bitmap.createBitmap(400, 100, Bitmap.Config.RGB_565);
                for (int i = 0; i < 400; i++) {
                    for (int j = 0; j < 100; j++) {
                        bmp.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        return printer.setTextToPrint(
                "[L]\n" +
                        "[C]<b><font size='big'>Venta</font></b>\n" +
                        "[L]\n" +
                        "[C]Vendedor: administrador\n" +
                        "[C]Cliente: Público general\n" +
                        "[C]conocido #S/N, Conocido C.P. 00000\n" +
                        "[C]<b><font size = 'big'>C0100000029</font></b>\n" +
                        "[C]Prod | Can | Precio | Subtotal\n" +
                        "[C]---------------------\n" +
                        "[C]Leche 1 x $20.0 $20.0\n" +
                        "[C]---------------------\n" +
                        (generaCodigo ?
                                "<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bmp) + "</img>" :
                                "[C]<barcode type='128' height='10'>" + strCode + "</barcode>\n")
        );
    }

    /**
     * Trae la lista de estaciones
     */
    public void traeEstaciones() {
        peticionWS(Enumeradores.Valores.TAREA_TRAE_ESTACIONES, "SQL", "SQL", "a", "", "");
    }

    /**
     * Asigna la estacion al dispositivo
     * @param estacion Id de la estacion
     */
    public void asignaEstacion(String estacion) {
        peticionWS(Enumeradores.Valores.TAREA_ASIGNA_ESTACION_MOVIL, "SQL", "SQL", estacion, androidId, "a");
    }

    /**
     * Procesa la repsuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        ContentValues obj = (ContentValues) output.getExtra1();
        if (obj != null) {
            if (output.getTarea() == Enumeradores.Valores.TAREA_TRAE_ESTACIONES) {
                settings.setEstaciones(servicio.getEstaciones());
                settings.setListPreferenceData(settings.getListEstaciones());
            } else if (output.getTarea() == Enumeradores.Valores.TAREA_ASIGNA_ESTACION_MOVIL) {
                if (output.getExito()) {
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_exito);
                    SharedPreferences sharedPreferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    addPreferenceInt(editor, obj, "anexo", "estaid", 0);
                    editor.apply();
                } else
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
            }
        } else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final SharedPreferences args;
        private final Configuracion conf;
        private ListPreference listEstaciones;
        private List<Estacion> estaciones;
        private BluetoothConnection selectedDevice;
        private String impresora;
        private boolean generaCodigo;
        private Context contexto;

        /**
         * Crea un PreferenceFragment
         * Obtiene las configuraciones de la aplicacion
         * Inicializa los elementos de configuracion
         * @param savedInstanceState Bundle
         * @param rootKey Key
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            String ip = args.getString("ip", "");
            impresora = args.getString("impresora", "");
            generaCodigo = args.getBoolean(impresora + "CodigoBarras", true);


            EditTextPreference editIp = findPreference("configuracion_edit_ip");
            EditTextPreference editIpImp = findPreference("configuracion_edit_ip_impresora");
            EditTextPreference editPuertoImp = findPreference("configuracion_edit_puerto_impresora");
            Preference btnPrueba = findPreference("configuracion_edit_prueba");
            Preference btnPruebaRed = findPreference("configuracion_edit_pruebaRed");
            SwitchPreference swCodigo = findPreference("configuracion_switch_codigo");
            ListPreference listaImpresoras = findPreference("configuracion_list_impresoras");
            ListPreference listaTipoImpresion = findPreference("tipoImpresion");
            listaTipoImpresion.setValue(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp",""));
            editIpImp.setText(conf.getSharedPreferences("configuracion_edit_ip_impresora",Context.MODE_PRIVATE).getString("ipImpRed",""));
            editPuertoImp.setText(conf.getSharedPreferences("configuracion_edit_puerto_impresora",Context.MODE_PRIVATE).getString("puertoImpRed",""));


            if (editIpImp != null) {
                editIpImp.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (text == null || TextUtils.isEmpty(text)){
                            return "IP sin definir";
                        }
                        return text;
                    }
                });
            }
            if (editPuertoImp != null) {
                editPuertoImp.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (text == null || TextUtils.isEmpty(text)){
                            return "Puerto para impresora sin definir";
                        }
                        return text;
                    }
                });
            }
            if (btnPruebaRed != null) {
                btnPruebaRed.setSummaryProvider(new Preference.SummaryProvider<Preference>() {
                    @Override
                    public CharSequence provideSummary(Preference preference) {
                        return "";
                    }
                });
            }



            System.out.println("Valor para tImmp "+ conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp",""));

            if(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","") == null || conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","").equals("")){
                System.out.println("Entre a vacio");
                editIpImp.setEnabled(false);
                editPuertoImp.setEnabled(false);
                btnPruebaRed.setEnabled(false);
                listaImpresoras.setEnabled(false);
                btnPrueba.setEnabled(false);
                swCodigo.setEnabled(false);
            }
            if(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","") != null && conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","").equals("Red")){
                editIpImp.setEnabled(true);
                editPuertoImp.setEnabled(true);
                listaImpresoras.setEnabled(false);
                btnPrueba.setEnabled(false);
                swCodigo.setEnabled(false);
                if((conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "") != null && !conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "").equals(""))  && ((conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", "")) != null)){
                    btnPruebaRed.setEnabled(true);
                } else{
                    btnPruebaRed.setEnabled(false);
                }
            }else if(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","") != null && conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","").equals("Bluetooth")){
                editIpImp.setEnabled(false);
                editPuertoImp.setEnabled(false);
                listaImpresoras.setEnabled(true);
                btnPruebaRed.setEnabled(false);
                btnPrueba.setEnabled(conf.hayimpresora);
                swCodigo.setEnabled(conf.hayimpresora);

                assert editIp != null;
                editIp.setText(ip);
                assert btnPrueba != null;
                btnPrueba.setSummary(impresora.equals("") ? "Predeterminada" : impresora);
                assert swCodigo != null;
                swCodigo.setEnabled(!impresora.equals("") && !impresora.equals("Predeterminada"));
            }

            listaTipoImpresion.setOnPreferenceChangeListener((preference, newValue) -> {
                String modoImp = newValue.toString();
                SharedPreferences x = conf.getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = x.edit();
                editor.putString("tImp",modoImp);
                editor.apply();
                editor.commit();
                String valor = x.getString("tImp","");
                listaTipoImpresion.setValue(valor);
                if(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","") != null && conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","").equals("Red")){
                    editIpImp.setEnabled(true);
                    editPuertoImp.setEnabled(true);
                    listaImpresoras.setEnabled(false);
                    btnPrueba.setEnabled(false);
                    swCodigo.setEnabled(false);
                    if((conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "") != null && !conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "").equals(""))  && ((conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", "")) != null)){
                        btnPruebaRed.setEnabled(true);
                    } else{
                        btnPruebaRed.setEnabled(false);
                    }
                }else if(conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","") != null && conf.getSharedPreferences("tipoImpresion",Context.MODE_PRIVATE).getString("tImp","").equals("Bluetooth")){
                    editIpImp.setEnabled(false);
                    editPuertoImp.setEnabled(false);
                    listaImpresoras.setEnabled(true);
                    btnPruebaRed.setEnabled(false);
                    btnPrueba.setEnabled(conf.hayimpresora);
                    swCodigo.setEnabled(conf.hayimpresora);

                    assert editIp != null;
                    editIp.setText(ip);
                    assert btnPrueba != null;
                    btnPrueba.setSummary(impresora.equals("") ? "Predeterminada" : impresora);
                    assert swCodigo != null;
                    swCodigo.setEnabled(!impresora.equals("") && !impresora.equals("Predeterminada"));
                }
                return false;
            });

            editIpImp.setOnPreferenceChangeListener((preference, newValue) -> {
                String ipImpRed = newValue.toString();
                SharedPreferences x = conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = x.edit();
                editor.putString("ipImpRed",ipImpRed);
                editor.apply();
                editor.commit();
                String valor = x.getString("ipImpRed","");
                editIpImp.setText(valor);
                if((conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "") != null && !conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "").equals(""))  && ((conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", "")) != null)){
                    btnPruebaRed.setEnabled(true);
                } else{
                    btnPruebaRed.setEnabled(false);
                }
                return false;
            });

            editPuertoImp.setOnPreferenceChangeListener((preference, newValue) -> {
                String puertoImpRed = newValue.toString();
                SharedPreferences x = conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = x.edit();
                editor.putString("puertoImpRed",puertoImpRed);
                editor.apply();
                editor.commit();
                String valor = x.getString("puertoImpRed","");
                editPuertoImp.setText(valor);
                if((conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "") != null && !conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "").equals(""))  && ((conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", "")) != null)){
                    btnPruebaRed.setEnabled(true);
                } else{
                    btnPruebaRed.setEnabled(false);
                }
                return false;
            });
            String prueba = "Hola es una prueba,T1w|otro ;renglon,T2|75001476,**";
            btnPruebaRed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String ip = conf.getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                    int puerto = Integer.parseInt(conf.getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));

                    new Impresora(ip, prueba, puerto,conf.getSharedPreferences("renglones",Context.MODE_PRIVATE).getInt("espacios",3)).execute();

                    return true;
                }
            });
            listEstaciones = findPreference("configuracion_list_estaciones");
            //listEstaciones.setEnabled(conf.esLogin);
            //editIp.setEnabled(conf.esLogin);

            //listaImpresoras.setEnabled(true);
            //listaImpresoras.setEnabled(conf.hayimpresora);


            CharSequence[] impresoras = new CharSequence[1];
            impresoras[0] = "Predeterminada";
            final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
            if (bluetoothDevicesList != null && bluetoothDevicesList.length > 0) {
                impresoras = new CharSequence[bluetoothDevicesList.length + 1];
                impresoras[0] = "Predeterminada";
                BluetoothConnection blue, diente;
                selectedDevice = bluetoothDevicesList[0];
                for (int i = 1; i <= bluetoothDevicesList.length; i++) {
                    blue = bluetoothDevicesList[i - 1];
                    if(Build.VERSION.SDK_INT >= 31){
                        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    impresoras[i] = blue.getDevice().getName();
                    if(blue.getDevice().getName().equals(impresora))
                        selectedDevice = blue;
                }
            }
            assert listaImpresoras != null ;
            listaImpresoras.setEntries(impresoras);
            listaImpresoras.setEntryValues(impresoras);
            listaImpresoras.setValue(impresora);
            if(!impresora.equals(""))
                listaImpresoras.setSummary(impresora);

            //Listener para cambiar la IP
            editIp.setOnPreferenceChangeListener((preference, newValue) -> {
                String ipNueva = newValue.toString();
                if(Libreria.isIp(ipNueva)){
                    ((EditTextPreference)preference).setText(ipNueva);
                    SharedPreferences.Editor editor = args.edit();
                    editor.putString("ip", ipNueva);
                    editor.apply();
                    conf.traeEstaciones();
                }
                else {
                    ((EditTextPreference)preference).setText(ip);
                    conf.muestraMensaje("Ip inválida", R.drawable.mensaje_error);
                }

                return false;
            });

            //Listener para probrar impresora
            btnPrueba.setOnPreferenceClickListener((preference -> {
                if (ContextCompat.checkSelfPermission(conf, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(conf, new String[]{Manifest.permission.BLUETOOTH}, 1);
                } else {
                    new AsyncBluetoothEscPosPrint(conf,false).execute(conf.getAsyncEscPosPrinter(selectedDevice, generaCodigo));
                }
                return true;
            }));

            //Listener para asignar estacion
            listEstaciones.setVisible(false);
            listEstaciones.setOnPreferenceChangeListener((preference, estacionID) -> {
                conf.asignaEstacion((String) estacionID);
                return false;
            });

            //Listener para asignar impresora
            listaImpresoras.setOnPreferenceChangeListener(((preference, newValue) -> {
                SharedPreferences.Editor editor = args.edit();
                editor.putString("impresora", (String) newValue);
                editor.apply();
                impresora = (String) newValue;
                listaImpresoras.setValue((String) newValue);
                listaImpresoras.setSummary(listaImpresoras.getEntry());
                btnPrueba.setSummary(listaImpresoras.getEntry());
                swCodigo.setEnabled(!newValue.equals("") && !newValue.equals("Predeterminada"));

                if (bluetoothDevicesList != null){
                    BluetoothConnection blue;
                    for (int i = 1; i<= bluetoothDevicesList.length; i++){
                        blue=bluetoothDevicesList[i-1];
                        if(blue.getDevice().getName().equals(impresora))
                            selectedDevice = blue;
                    }
                }

                return false;
            }));

            //Listener para configurar la generacion de codigo de barras por imagen
            swCodigo.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editor = args.edit();
                editor.putBoolean(impresora+"CodigoBarras", (boolean) newValue);
                editor.apply();
                generaCodigo = (boolean)newValue;
                return true;
            });

            conf.traeEstaciones();

        }

        /**
         * Constructor del SettingsFragment
         * @param args SharedPreferences para guardar los cambios en las configuraciones
         * @param conf Referencia del activity de Configuracion para acceder a sus funciones
         */
        public SettingsFragment(SharedPreferences args, Configuracion conf,Context contexto){
            this.args = args;
            this.conf = conf;
            this.conf.settings = this;
            this.estaciones = new ArrayList<>();
            this.contexto = contexto;
        }

        /**
         * Llena la lista de estaciones
         * @param lp ListPreference donde se van a mostrar las estaciones
         */
        public void setListPreferenceData(ListPreference lp) {
            CharSequence[] entries = new CharSequence[estaciones.size()];
            CharSequence[] entryValues = new CharSequence[estaciones.size()];
            String id = "";

            for(int i=0; i<estaciones.size(); i++){
               entries[i] = estaciones.get(i).getNombre();
               entryValues[i] = String.valueOf(estaciones.get(i).getEstaid());

               if(estaciones.get(i).isAsignada())
                   id = String.valueOf(estaciones.get(i).getEstaid());
            }

            lp.setEntries(entries);
            lp.setEntryValues(entryValues);
            lp.setValue(id);
        }

        /**
         * Retorna el ListPreference correspondiente a las estaciones
         * @return El ListPreference
         */
        public ListPreference getListEstaciones() {
            return listEstaciones;
        }

        /**
         * Establece el valor de la lista de estaciones
         * @param estaciones El nuevo valor
         */
        public void setEstaciones(List<Estacion> estaciones) {
            this.estaciones = estaciones;
        }
    }
}