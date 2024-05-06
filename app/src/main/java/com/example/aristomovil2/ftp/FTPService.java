package com.example.aristomovil2.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FTPService {
    private final String servidor, usuario, password;
    private final int port;

    public FTPService(String servidor, String usuario, String password, int port){
        this.servidor = servidor;
        this.usuario = usuario;
        this.password = password;
        this.port = port;
    }

    /**
     * Retorna el path donde se guarda un archivo traido desde el FTP
     * @param destino El destino del archivo
     * @param origen El origen del archivo
     * @param nombre El nombre del archivo
     * @return La ubicacion del archivo
     */
    public String traeArchivo(String destino, String origen, String nombre){
        FTPClient client = new FTPClient();
        FileOutputStream output;
        String fileName = "";

        try{
            File destinoFile = new File(destino);

            if(!destinoFile.exists())
                destinoFile.mkdir();

            client.setDataTimeout(10);
            client.connect(servidor, port);
            client.login(usuario, password);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();

            if(client.isConnected()){
                FTPFile []files = client.listFiles(origen);

                if(files.length >= 1){
                    String path = destinoFile.getAbsolutePath() + "/" + nombre;
                    output = new FileOutputStream(path);
                    client.retrieveFile(origen, output);
                    fileName = path;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if(client.isConnected())
                    client.disconnect();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        return fileName;
    }
}
