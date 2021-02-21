package com.example.hlc_03;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.hlc_03.databinding.ActivityMain2Binding;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;



import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Main2 extends AppCompatActivity implements View.OnClickListener
{
    private ActivityMain2Binding binding;
    private static final int REQUEST_CONNECT = 1;
    long inicio, fin;
    Main2.TareaAsincrona tareaAsincrona;
    URL url;
    double constante;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try
        {
            url = new  URL("https://dam.org.es/ficheros/cambio.txt");
            descargaOkHTTP(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        binding.botonConvertir.setOnClickListener(this);
        binding.radioButtonDolaresAeuros.setChecked(true);
        binding.editTextEuros.setEnabled(false);
        binding.radioButtonDolaresAeuros.setOnClickListener(this);
        binding.radioButtonEurosADolares.setOnClickListener(this);

    Double euro;
}

    private void descargaOkHTTP(URL web) {
        inicio = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(web)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                                mostrarRespuesta(responseData);
                    } else {
                               mostrarRespuesta("Unexpected code: " + response);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Error", e.getMessage());
                //      mostrarRespuesta("Fallo: " + e.getMessage());
            }
        });
    }

    private void mostrarRespuesta(String s) {
        fin = System.currentTimeMillis();
        Main2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("S es: " + s);
                constante = Double.parseDouble(s.replace(",","."));
                System.out.println(constante);

            }
        });
    }

    @Override
    public void onClick(View view)
    {
        if(view == binding.radioButtonDolaresAeuros)
        {
            binding.editTextEuros.setEnabled(false);
            binding.editTextDolares.setEnabled(true);
        }
        if(view == binding.radioButtonEurosADolares)
        {
            binding.editTextEuros.setEnabled(true);
            binding.editTextDolares.setEnabled(false);
        }
        if(view == binding.botonConvertir)
        {
            if(binding.radioButtonDolaresAeuros.isChecked())
            {
                try
                {
                    Double dolar = Double.parseDouble(String.valueOf(binding.editTextDolares.getText()));
                    System.out.println(constante);
                    Double euro = dolar * constante;
                    binding.editTextEuros.setText(euro.toString());
                }
                catch (Exception e)
                {
                    Toast.makeText(Main2.this, "Introduce un numero valido",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                try
                {
                    Double euro = Double.parseDouble(String.valueOf(binding.editTextEuros.getText()));
                    Double dolar= euro / constante;
                    binding.editTextDolares.setText(dolar.toString());
                }
                catch (Exception e)
                {
                    Toast.makeText(Main2.this, "Introduce un numero valido",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void mostrarError(String message) {
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
                // constante = Float.parseFloat(resultado.getContenido());
                System.out.println("La constante es: " + constante);
            } else {
                mostrarError(resultado.getMensaje());
            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            progreso.dismiss();
            mostrarError("Cancelado");
        }
    }
}