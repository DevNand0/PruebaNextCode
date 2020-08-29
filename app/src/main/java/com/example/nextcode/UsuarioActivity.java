package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.example.nextcode.adapters.UsuarioAdapter;
import com.example.nextcode.conn.APIVolley;
import com.example.nextcode.dialogs.AlertDialogs;
import com.example.nextcode.dialogs.SimpleConfirmDialog;
import com.example.nextcode.dialogs.UsuarioDialog;
import com.example.nextcode.models.Usuario;
import com.example.nextcode.params.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsuarioActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView lv_usuarios;
    private ArrayList<Usuario> lista;
    private UsuarioAdapter adapter;
    private ImageButton btn_regresar;

    private RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        request= APIVolley.getInstance().getQueue();
        initializer();
        setListeners();
        setList();

    }


    private void initializer(){
        lv_usuarios=(ListView)findViewById(R.id.lv_usuarios);
        btn_regresar=(ImageButton)findViewById(R.id.btn_regresar);
        lista = new ArrayList<>();
    }

    private void setListeners(){
        btn_regresar.setOnClickListener(this);
    }


    private void back(){
        Intent intent = new Intent(UsuarioActivity.this, MenuActivity.class);
        //Clear all activities and start new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_regresar){
            back();
        }
    }

    int id_del;
    private void setList(){

        StringRequest SR = new StringRequest(Request.Method.GET, App.URL() + "usuarios", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try{

                    jsonObject = new JSONObject(response);
                    JSONArray collection = jsonObject.getJSONArray("data");


                    if(collection.length()>0){
                        JSONObject jsonObj = null;
                        for(int i=0;i<collection.length();i++){
                            Usuario usuario = new Usuario();
                            jsonObj = collection.getJSONObject(i);
                            usuario.setId(jsonObj.getInt("id"));
                            usuario.setCedula(jsonObj.getString("cedula"));
                            usuario.setNombres(jsonObj.getString("nombres"));
                            usuario.setApellidos(jsonObj.getString("apellidos"));
                            usuario.setCorreo(jsonObj.getString("correo"));
                            usuario.setStatus(jsonObj.getString("status"));
                            lista.add(usuario);
                        }
                    }

                    adapter = new UsuarioAdapter(UsuarioActivity.this,lista);
                    lv_usuarios.setAdapter(adapter);
                    lv_usuarios.setLongClickable(true);
                    lv_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UsuarioDialog dialog = new UsuarioDialog();
                            dialog.setContexto(UsuarioActivity.this);
                            dialog.setUsuario( lista.get(position) );
                            dialog.show(getSupportFragmentManager(),"Aplicacion");
                        }
                    });
                    lv_usuarios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                            // TODO Auto-generated method stub
                            final Usuario u = lista.get(pos);
                            SimpleConfirmDialog confirm =  new SimpleConfirmDialog(UsuarioActivity.this,"Info","Deseas eliminar al usuario "+lista.get(pos).getNombres()+" "+lista.get(pos).getApellidos());
                            confirm.setPositive("Si",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteUser(u.getId());
                                }
                            });
                            confirm.setNegative("No",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            confirm.make();

                            return true;
                        }
                    });
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
                Toast.makeText(UsuarioActivity.this, "Falla en :"+error.toString(), Toast.LENGTH_SHORT).show();
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

    private void deleteUser(int id){





    }
}