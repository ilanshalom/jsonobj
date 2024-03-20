package com.example.objdentrodeobj;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog ;
    private int qualSolicitacao = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // android:onClick="lerJSON"
    }

    public void lerContatos(View view) {  //este método atende o evento de click no botão Países
        qualSolicitacao = 0;
        if (checkInternetConection()){
            progressDialog = ProgressDialog.show(this, "", "Obtendo dados");
            new DownloadJson().execute("http://mfpledon.com.br/contatosbck.json");
            // para aparelhos reais ou máquinas com mais memória, pode usar o endereço
            // http://mfpledon.com.br/contatosbck.json, que retorna mais dados
        } else{
            Toast.makeText(getApplicationContext(),"Sem conexão. Verifique.",Toast.LENGTH_LONG).show();
        }
    }


    public boolean checkInternetConection() {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
    }

    public void mostrarJSONContatos(String strjson){
        ((TextView)findViewById(R.id.dados)).setText(strjson);
        String data = "\n";
        try {
            JSONObject objRaiz = new JSONObject(strjson);
            JSONArray jsonArray = objRaiz.optJSONArray("contatos");
            JSONObject jsonObject = null;
            JSONObject jsonObjectphones = null;
            //percorre o vetor de funcionarios e pega o nome para imprimir
            for(int i=0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String address = jsonObject.getString("address");
                String gender = jsonObject.getString("gender");
                //os números de telefones são objetos dentro deste objeto:
                jsonObjectphones = jsonObject.getJSONObject("phone");
                String mobile = jsonObjectphones.getString("mobile");
                String home = jsonObjectphones.getString("home");
                data += " \n" + id + ", " + name + ", " + email
                        + ", address: " + address  + ", gender: " + gender
                        + ", mobile phone: " + mobile  + ", home phone: " + home + "\n";
                jsonObject = null;
                jsonObjectphones = null;

            }
            ((TextView)findViewById(R.id.dados)).setText(data);
        } catch (JSONException e) {
            ((TextView)findViewById(R.id.dados)).setText(e.getMessage() +"\n\n"+ data + "\n\n");
        }
        finally { progressDialog.dismiss(); }
    }

    private class DownloadJson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // params[0] é o URL.
            try {
                return downloadJSON(params[0]);
            } catch (IOException e) {
                return "URL inválido";
            }
        }

        // onPostExecute exibe o resultado do AsyncTask
        @Override
        protected void onPostExecute(String result) {
            if(qualSolicitacao == 0)mostrarJSONContatos(result);
            //... outras solicitações HTTP
        }

        private String downloadJSON(String myurl) throws IOException {
            InputStream is = null;
            String respostaHttp = "";
            HttpURLConnection conn = null;
            InputStream in = null;
            ByteArrayOutputStream bos = null;
            try {
                URL u = new URL(myurl);
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(7000); // 7 segundos de timeout
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                in = conn.getInputStream();
                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
                respostaHttp = bos.toString("UTF-8");
                return respostaHttp;
            } finally {
                if (in != null) in.close();
            }
        }

    }
}
