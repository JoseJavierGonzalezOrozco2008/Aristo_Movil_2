package com.example.aristomovil2.ftp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.example.aristomovil2.BuildConfig;
import com.example.aristomovil2.Login;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConexionFTP extends AsyncTask<Void, Void, EnviaPeticion> {
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private String versionFTP, apk;
    private boolean conexion, actualizacion;
    private String url="/A01Movil/";

    public ConexionFTP(Context context) {
        this.context = context;
        this.actualizacion = false;
    }

    @Override
    protected EnviaPeticion doInBackground(Void... voids) {
        if(true){
            conexion = false;
            return null;
        }
        String versionActual = BuildConfig.VERSION_NAME;
        System.out.println(versionActual);
        File destino = context.getExternalFilesDir("AristoMovil");

        if(!destino.exists())
            destino.mkdirs();

        String archivotxt="cambios.txt",archivoapk="";
        FTPService service = new FTPService("maxse.com.mx", "versiones@maxse.com.mx", "1@@Primaveras", 21);
        versionFTP = service.traeArchivo(destino.getAbsolutePath(), "/aristoMovil/versiones.txt", "versiones.txt");
        String line = null;

        try{
            FileInputStream input = new FileInputStream(versionFTP);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();

            while ((line = buffer.readLine()) != null)
                builder.append(line).append(System.getProperty("line.separator"));

            buffer.close();
            input.close();
            line = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String []lines;

        if(Libreria.tieneInformacion(line)){
            conexion = true;
            lines = line.split("\\|");
            String versionNueva=lines[1];
            /*String[] verActual = versionActual.split("\\.");
            String[] verNueva = lines[1].split("\\.");*/

            /*if((Integer.parseInt(verActual[0]) < Integer.parseInt(verNueva[0]))
                || (Integer.parseInt(verActual[0]) <= Integer.parseInt(verNueva[0]) && Integer.parseInt(verActual[1]) < Integer.parseInt(verNueva[1]))
                || (Integer.parseInt(verActual[0]) <= Integer.parseInt(verNueva[0]) && Integer.parseInt(verActual[1]) <= Integer.parseInt(verNueva[1]) && Integer.parseInt(verActual[2]) < Integer.parseInt(verNueva[2]))){*/
            if(this.evaluaVersion(versionActual,versionNueva)){
                actualizacion = true;
                File fileApk = new File(destino.getAbsolutePath() + "/AristoMovil.apk");

                if(!fileApk.exists()){
                    //System.out.println("/aristoMovil/Aristo Movil 2 " + lines[1] + ".apk");
                    apk = service.traeArchivo(destino.getAbsolutePath(),"/aristoMovil/Aristo Movil 2 " + versionNueva + ".apk","AristoMovil.apk");
                }else{
                    apk = fileApk.getAbsolutePath();
                }
            }else{
                File f = new File(destino.getAbsolutePath() + "/PuntoVenta.apk");
                if(f.exists())
                    f.delete();
            }
        }else
            conexion = false;

        return null;
    }

    @Override
    protected void onPostExecute(EnviaPeticion enviaPeticion) {
        super.onPostExecute(enviaPeticion);

        if(conexion){
            String line = null;
            try {
                FileInputStream input = new FileInputStream(versionFTP);
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader buffer = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();

                while ((line = buffer.readLine()) != null)
                    builder.append(line).append(System.getProperty("line.separator"));

                buffer.close();
                input.close();
                line = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] lines;
            if (null != line) {
                lines = line.split("\\|");

                if (actualizacion && lines[0].equals("0"))
                    dialogoAviso();
                else if (actualizacion && lines[0].equals("1")) {
                    Toast.makeText(context, "Notas de Actualización: \n- " + lines[2], Toast.LENGTH_SHORT).show();
                    dialogoActualizar();
                } else
                    irLogin();
            }
            else
                irLogin();
        }
        else {
            Toast.makeText(context, "Error al comprobar actualizacion", Toast.LENGTH_LONG).show();
            irLogin();
        }
    }

    /**
     * Muestra un dialogo donde se avisa que exite un nueva actualizacion
     */
    public void dialogoAviso() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Alerta de Actualizacion");
        dialog.setMessage("Existe una actualización, favor de ponerse en contacto con el administrador en aristo@maxse.com.mx");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Aceptar", (dialogInterface, i) -> irLogin());
        dialog.show();
    }

    /**
     * Muestra el dialogo para instaalar una nueva actualizacion
     */
    public void dialogoActualizar(){
        if(Libreria.tieneInformacion(apk)){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Actualización Disponible");
            dialog.setMessage("¿Desea instalar la actualización?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Instalar", (dialogInterface, i) -> instala());
            dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> irLogin());
            dialog.show();
        }
        else
            irLogin();
    }

    /**
     * Instala una nueva actualizacion
     */
    public void instala(){
        Intent intent;
        File archivo = new File(apk);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", archivo);
            intent = new Intent().setType("*/*").setAction(Intent.ACTION_INSTALL_PACKAGE).setData(apkUri).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else
            intent = new Intent(Intent.ACTION_VIEW).setAction(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(archivo),
                    "application/vnd.android.package-archive").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    /**
     * Inicia el activity de login
     */
    private void irLogin(){
        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private boolean evaluaVersion(String pVActual,String pVNueva){
        String[] verActual = pVActual.split("\\.");
        String[] verNueva = pVNueva.split("\\.");

        return ((Integer.parseInt(verActual[0]) < Integer.parseInt(verNueva[0]))
                || (Integer.parseInt(verActual[0]) <= Integer.parseInt(verNueva[0]) && Integer.parseInt(verActual[1]) < Integer.parseInt(verNueva[1]))
                || (Integer.parseInt(verActual[0]) <= Integer.parseInt(verNueva[0]) && Integer.parseInt(verActual[1]) <= Integer.parseInt(verNueva[1]) && Integer.parseInt(verActual[2]) < Integer.parseInt(verNueva[2])));

    }
}
