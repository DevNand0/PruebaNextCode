package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.nextcode.conn.APIVolley;
import com.example.nextcode.db.UsuarioDataSource;
import com.example.nextcode.dialogs.AlertDialogs;
import com.example.nextcode.models.Usuario;
import com.example.nextcode.params.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class NuevoUsuarioActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_guardar, btn_borrar;
    private ImageButton btn_regresar;
    private EditText et_pasword, et_email, et_nombre, et_apellido, et_cedula;
    private UsuarioDataSource ds;
    private Usuario u;
    private RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        ds = new UsuarioDataSource(getApplicationContext());

        try{
            ds.open();
        }catch(android.database.SQLException e){
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        request= APIVolley.getInstance().getQueue();
        initializer();
        setListeners();
    }

    private void initializer(){

        btn_guardar = (Button)findViewById(R.id.btn_guardar);
        btn_borrar = (Button)findViewById(R.id.btn_borrar);
        btn_regresar = (ImageButton)findViewById(R.id.btn_regresar);
        et_pasword = (EditText)findViewById(R.id.et_password);
        et_email = (EditText)findViewById(R.id.et_email);
        et_nombre = (EditText)findViewById(R.id.et_nombre);
        et_apellido = (EditText)findViewById(R.id.et_apellido);
        et_cedula = (EditText)findViewById(R.id.et_cedula);

    }

    private void setListeners(){
        btn_guardar.setOnClickListener(this);
        btn_regresar.setOnClickListener(this);
        btn_borrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_guardar){
            saveAll();
        }else if(v == btn_borrar){
            CleanInputs();
        }else if(v == btn_regresar){
            back();
        }
    }

    private void CleanInputs(){
        btn_guardar.setText("");
        btn_borrar.setText("");
        et_pasword.setText("");
        et_email.setText("");
        et_nombre.setText("");
        et_apellido.setText("");
        et_cedula.setText("");
    }

    private void saveAll(){

        if( et_nombre.getText().toString().equals("")   ||
            et_apellido.getText().toString().equals("") ||
            et_email.getText().toString().equals("")    ||
            et_cedula.getText().toString().equals("")   ||
            et_pasword.getText().toString().equals("") ){
            Toast.makeText(getApplicationContext(),"Ingrese llenar todos los campos",Toast.LENGTH_SHORT).show();
        }else{
            if(et_cedula.getText().length()<10){
                Toast.makeText(getApplicationContext(),"La cedula no puede ser menor a 10 digitos",Toast.LENGTH_SHORT).show();
            }else{

                StringRequest SR = new StringRequest(Request.Method.POST, App.URL() + "usuarios", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try{
                            jsonObject = new JSONObject(response);
                            int id = jsonObject.getInt("id");

                            AlertDialogs alert = new AlertDialogs(NuevoUsuarioActivity.this);
                            boolean nuevoActivity = false;
                            if(id>0){
                                alert.setIntent(new Intent(NuevoUsuarioActivity.this,LoginActivity.class));
                                nuevoActivity=true;

                                u = new Usuario();
                                u.setId(id);
                                u.setNombres(et_nombre.getText().toString());
                                u.setApellidos(et_apellido.getText().toString());
                                u.setCorreo(et_email.getText().toString());
                                u.setContrasenia(et_pasword.getText().toString());
                                u.setCedula(et_cedula.getText().toString());
                                u.setStatus("A");

                                int insertado = ds.Insertar(u);
                                long resp = ds.closeUsers(id);

                            }
                            alert.MostrarSimple(nuevoActivity,"Respuesta","Usuario insertado");
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                        Toast.makeText(NuevoUsuarioActivity.this, "Falla en :"+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Access-Control-Allow-Origin", "*");

                        return params;
                    }
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json; charset=UTF-8");

                        params.put("cedula", et_cedula.getText().toString());
                        params.put("nombres", et_nombre.getText().toString());
                        params.put("apellidos", et_apellido.getText().toString());
                        params.put("correo", et_email.getText().toString());
                        params.put("contrasenia", et_pasword.getText().toString());

                        return params;
                    }
                };
                int socketTimeout = 15000;//10 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                SR.setRetryPolicy(policy);
                request.add(SR);
            }
        }


    }

    private void back(){
        Intent intent = new Intent(NuevoUsuarioActivity.this, LoginActivity.class);
        //Clear all activities and start new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}