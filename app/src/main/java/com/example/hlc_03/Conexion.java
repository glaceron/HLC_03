package com.example.hlc_03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Conexion
{
    public static Resultado conectarJava(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        int respuesta = 500;
        Resultado resultado = new Resultado();

        urlConnection = (HttpURLConnection) url.openConnection();
        respuesta = urlConnection.getResponseCode();
        resultado.setCodigo(respuesta);
        if (respuesta == HttpURLConnection.HTTP_OK) {
            resultado.setContenido( leer(urlConnection.getInputStream()));
        } else {
            resultado.setMensaje("Error en el acceso a la web: " + String.valueOf(respuesta));
        }
        urlConnection.disconnect();

        return resultado;
    }

    private static String leer(InputStream inputStream) throws IOException
    {
        BufferedReader in;
        String linea;
        StringBuilder miCadena = new StringBuilder();

        in = new BufferedReader((new InputStreamReader(inputStream)), 32000);
        while ( (linea = in.readLine()) != null) {
            miCadena.append(linea);
        }
        in.close();

        return  miCadena.toString();
    }
}