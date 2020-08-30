package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.nextcode.db.UsuarioDataSource;
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

public class PlanesActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btn_regresar, btn_nuevo;
    private ListView lv_planes;
    private Usuario usuario;
    private UsuarioDataSource ds;
    private RequestQueue request;
    private ArrayList<Plan> lista;
    private ArrayAdapter<String> adapater_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planes);

        ds = new UsuarioDataSource(getApplicationContext());

        try{
            ds.open();
        }catch(android.database.SQLException e){
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        usuario=ds.usuarioActivo();
        request= APIVolley.getInstance().getQueue();
        initializer();
        setListeners();
    }

    private void initializer(){
        btn_nuevo = (ImageButton)findViewById(R.id.btn_nuevo);
        btn_regresar =(ImageButton)findViewById(R.id.btn_regresar);
        lv_planes = (ListView)findViewById(R.id.lv_planes);
        lista = new ArrayList<>();
        setList();
    }

    private void setListeners(){
        btn_nuevo.setOnClickListener(this);
        btn_regresar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_regresar){
            back();
        }else if(v==btn_nuevo){
            cargaNuevoPlan();
        }
    }

    public void setList(){
        StringRequest SR = new StringRequest(Request.Method.GET, App.URL() + "usuarios/"+usuario.getId()+"?embed=planes?embed=plan", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try{

                    jsonObject = new JSONObject(response);
                    JSONArray collection = jsonObject.getJSONArray("planes");

                    //Toast.makeText(PlanesActivity.this, "size :"+collection.length(), Toast.LENGTH_SHORT).show();
                    if(collection.length()>0){
                        JSONObject jsonObj = null;
                        for(int i=0;i<collection.length();i++){
                            Plan plan = new Plan();
                            jsonObj = collection.getJSONObject(i);
                            JSONObject json = jsonObj.getJSONObject("plan");
                            plan.setId(json.getInt("id"));
                            plan.setNombre(json.getString("nombre"));
                            plan.setTipo(json.getString("tipo"));
                            plan.setSubtotal(json.getString("subtotal"));
                            plan.setIva(json.getString("iva"));
                            plan.setTotal(json.getString("total"));
                            lista.add(plan);
                        }
                    }

                    adapater_plan = new ArrayAdapter(PlanesActivity.this,android.R.layout.simple_list_item_1,lista);
                    lv_planes.setAdapter(adapater_plan);
                    lv_planes.setLongClickable(true);
                    lv_planes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //UsuarioDialog dialog = new UsuarioDialog();
                            //dialog.setContexto(UsuarioActivity.this);
                            //dialog.setUsuario( lista.get(position) );
                            //dialog.show(getSupportFragmentManager(),"Aplicacion");
                        }
                    });
                    lv_planes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                            // TODO Auto-generated method stub
                            /*
                            final Usuario u = lista.get(pos);
                            SimpleConfirmDialog confirm =  new SimpleConfirmDialog(UsuarioActivity.this,"Info","Deseas eliminar al usuario "+lista.get(pos).getNombres()+" "+lista.get(pos).getApellidos());
                            confirm.setPositive("Si",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //deleteUser(u.getId());
                                }
                            });
                            confirm.setNegative("No",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            confirm.make();
                            */
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
                Toast.makeText(PlanesActivity.this, "Falla en :"+error.toString(), Toast.LENGTH_SHORT).show();
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

    private void back(){
        Intent intent = new Intent(PlanesActivity.this, MenuActivity.class);
        //Clear all activities and start new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void cargaNuevoPlan(){
        Intent intent = new Intent(PlanesActivity.this, NuevoPlanActivity.class);
        //Clear all activities and start new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}