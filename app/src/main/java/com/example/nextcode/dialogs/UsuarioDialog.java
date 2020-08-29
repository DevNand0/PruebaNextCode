package com.example.nextcode.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

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
import com.example.nextcode.R;
import com.example.nextcode.UsuarioActivity;
import com.example.nextcode.adapters.UsuarioAdapter;
import com.example.nextcode.conn.APIVolley;
import com.example.nextcode.models.Usuario;
import com.example.nextcode.params.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDialog extends DialogFragment {
    private Context context;
    private Usuario u;
    private EditText et_nombre, et_apellido, et_email, et_password,et_cedula;
    private Button btn_cerrar, btn_actualizar;

    private RequestQueue request;

    public void setContexto(Context context){
        this.context = context;
    }

    public void setUsuario(Usuario u){
        this.u=u;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_usuario, null);

        et_nombre = (EditText) view.findViewById(R.id.et_nombre);
        et_apellido = (EditText) view.findViewById(R.id.et_apellido);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_cedula = (EditText) view.findViewById(R.id.et_cedula);
        btn_cerrar = (Button)view.findViewById(R.id.btn_cerrar);
        btn_actualizar = (Button) view.findViewById(R.id.btn_actualizar);

        request= APIVolley.getInstance().getQueue();

        et_nombre.setText(u.getNombres());
        et_apellido.setText(u.getApellidos());
        et_cedula.setText(u.getCedula());
        et_email.setText(u.getCorreo());
        et_password.setText(u.getContrasenia());



        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });



        return view;
    }

    private void update(){
        StringRequest SR = new StringRequest(Request.Method.PUT, App.URL() + "usuarios/"+u.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj = null;
                try{
                    jsonObj = new JSONObject(response);
                    Usuario usuario = new Usuario();
                    usuario.setId(jsonObj.getInt("id"));
                    usuario.setCedula(jsonObj.getString("cedula"));
                    usuario.setNombres(jsonObj.getString("nombres"));
                    usuario.setApellidos(jsonObj.getString("apellidos"));
                    usuario.setCorreo(jsonObj.getString("correo"));
                    usuario.setStatus(jsonObj.getString("status"));

                    if(usuario.getId()>0){
                        Toast.makeText(getActivity(),"Datos Actualizados",Toast.LENGTH_LONG).show();
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
                params.put("contrasenia", et_password.getText().toString());
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
