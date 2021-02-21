package com.example.hlc_03;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.hlc_03.databinding.ActivityMain1Binding;

public class Main1 extends AppCompatActivity implements View.OnClickListener {
    private ActivityMain1Binding binding;
    private static final int REQUEST_CONNECT = 1;
    long inicio, fin;
    TareaAsincrona tareaAsincrona;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        binding = ActivityMain1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.button4.setOnClickListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            url = new URL(binding.editTextTextUrl.getText().toString());
            descarga(url);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            mostrarError(e.getMessage());
        }
    }

    private void descarga(URL url)
    {
        inicio = System.currentTimeMillis();
        tareaAsincrona = new TareaAsincrona(this);
        tareaAsincrona.execute(url);
        binding.textView.setText("Descargando la página . . .");
    }

    private void mostrarRespuesta(String s)
    {
        fin = System.currentTimeMillis();
        Main1.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                binding.webView.loadDataWithBaseURL(String.valueOf(url), s, "text/html", "UTF-8", null);
                binding.textViewTiempoDescarga.setText("Duración: " + String.valueOf(fin - inicio) + "ms");
            }
        });
    }

    private void mostrarError(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public  class TareaAsincrona extends AsyncTask<URL, Void, Resultado> {
        private ProgressDialog progreso;
        private Context context;

        public TareaAsincrona(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(context);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Conectando . . .");
            progreso.setCancelable(true);
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TareaAsincrona.this.cancel(true);
                }
            });
            progreso.show();
        }

        @Override
        protected Resultado doInBackground(URL... urls) {
            Resultado resultado;

            try {
                resultado = Conexion.conectarJava(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error de conexión: ", e.getMessage());
                resultado = new Resultado();
                resultado.setCodigo(500);
                resultado.setMensaje("Error de conexión: " + e.getMessage());
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Resultado resultado) {
            super.onPostExecute(resultado);
            progreso.dismiss();
            fin = System.currentTimeMillis();
            if (resultado.getCodigo() == HttpURLConnection.HTTP_OK) {
                binding.webView.loadDataWithBaseURL(String.valueOf(url), resultado.getContenido(), "text/html", "UTF-8", null);
            } else {
                mostrarError(resultado.getMensaje());
                binding.webView.loadDataWithBaseURL(String.valueOf(url), resultado.getMensaje(), "text/html", "UTF-8", null);
            }
            binding.textView.setText("Duración: " + String.valueOf(fin - inicio) + "ms");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progreso.dismiss();
            mostrarError("Cancelado");
        }
    }
}