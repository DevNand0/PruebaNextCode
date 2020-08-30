package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.nextcode.adapters.UsuarioAdapter;
import com.example.nextcode.conn.APIVolley;
import com.example.nextcode.db.UsuarioDataSource;
import com.example.nextcode.dialogs.AlertDialogs;
import com.example.nextcode.dialogs.SimpleConfirmDialog;
import com.example.nextcode.dialogs.UsuarioDialog;
import com.example.nextcode.models.Plan;
import com.example.nextcode.models.Usuario;
import com.example.nextcode.params.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NuevoPlanActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_guardar;
    private ImageButton btn_regresar;
    private RequestQueue request;
    private UsuarioDataSource ds;
    private Usuario usuario;
    private Plan planSelected;
    private Spinner sp_planes;
    private ArrayList<Plan> lista;
    private ArrayList<String> items;

    private EditText et_subtotal, et_total, et_iva, et_tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_plan);
        request= APIVolley.getInstance().getQueue();
        ds = new UsuarioDataSource(getApplicationContext());

        try{
            ds.open();
        }catch(android.database.SQLException e){
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        usuario=ds.usuarioActivo();
        initializer();
        setListeners();
    }

    private void initializer(){
        btn_regresar=(ImageButton)findViewById(R.id.btn_regresar);
        btn_guardar = (Button)findViewById(R.id.btn_guardar);
        sp_planes = (Spinner)findViewById(R.id.sp_planes);
        et_subtotal = (EditText)findViewById(R.id.et_subtotal);
        et_total = (EditText)findViewById(R.id.et_total);
        et_iva = (EditText)findViewById(R.id.et_iva);
        et_tipo = (EditText)findViewById(R.id.et_tipo);
        lista = new ArrayList<>();
        items = new ArrayList<>();
        loadSpinner();
    }

    private void setListeners(){
        btn_regresar.setOnClickListener(this);
        btn_guardar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_regresar){
            back();
        }else if(v == btn_guardar){
            comprarPlan();
        }
    }

    private void back(){
        Intent intent = new Intent(NuevoPlanActivity.this, PlanesActivity.class);
        //Clear all activities and start new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadSpinner(){
        StringRequest SR = new StringRequest(Request.Method.GET, App.URL() + "planes", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try{

                    jsonObject = new JSONObject(response);
                    JSONArray collection = jsonObject.getJSONArray("data");


                    if(collection.length()>0){
                        JSONObject jsonObj = null;
                        for(int i=0;i<collection.length();i++){
                            Plan plan = new Plan();
                            jsonObj = collection.getJSONObject(i);
                            plan.setId(jsonObj.getInt("id"));
                            plan.setNombre(jsonObj.getString("nombre"));
                            plan.setSubtotal(jsonObj.getString("subtotal"));
                            plan.setIva(jsonObj.getString("iva"));
                            plan.setTotal(jsonObj.getString("total"));
                            plan.setTipo(jsonObj.getString("tipo"));
                            lista.add(plan);
                            items.add(plan.getNombre());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(NuevoPlanActivity.this,R.layout.support_simple_spinner_dropdown_item,items);
                    sp_planes.setAdapter(adapter);

                    sp_planes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            planSelected = lista.get(position);
                            et_subtotal.setText(planSelected.getSubtotal());
                            et_total.setText(planSelected.getTotal());
                            et_iva.setText(planSelected.getIva());
                            et_tipo.setText(planSelected.getTipo());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
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
                Toast.makeText(NuevoPlanActivity.this, "Falla en :"+error.toString(), Toast.LENGTH_SHORT).show();
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


    public void comprarPlan(){

        StringRequest SR = new StringRequest(Request.Method.POST, App.URL() + "planesusuario", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try{
                    jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("id");
                    if(id>0){
                        AlertDialogs alert = new AlertDialogs(NuevoPlanActivity.this);
                        alert.setIntent(new Intent(NuevoPlanActivity.this,PlanesActivity.class));
                        alert.MostrarSimple(true,"Respuesta","Plan Adquirido");
                    }

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
                Toast.makeText(NuevoPlanActivity.this, "Falla en :"+error.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("usuario_id", String.valueOf(usuario.getId()));
                params.put("plan_id", String.valueOf(planSelected.getId()));

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