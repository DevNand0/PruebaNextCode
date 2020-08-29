package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.nextcode.db.UsuarioDataSource;
import com.example.nextcode.dialogs.AlertDialogs;
import com.example.nextcode.models.Usuario;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_registrar;
    private EditText et_email, et_passsword;
    private Button btn_login;

    private Usuario u;
    private UsuarioDataSource ds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ds = new UsuarioDataSource(getApplicationContext());

        try{
            ds.open();
        }catch(android.database.SQLException e){
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        initializer();
        setListeners();

    }


    private void initializer(){

        tv_registrar = (TextView)findViewById(R.id.tv_registrar);
        et_email = (EditText)findViewById(R.id.et_email);
        et_passsword = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.btn_login);

    }

    private void setListeners(){
        tv_registrar.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==tv_registrar){
            Intent intent = new Intent(LoginActivity.this, NuevoUsuarioActivity.class);
            //Clear all activities and start new task
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(v==btn_login){
            login();
        }
    }

    private void login(){

        u=ds.getUsuario(et_email.getText().toString());
        if(u!=null){
            if(et_passsword.getText().toString().equals(u.getContrasenia())){
                ds.sessionUser(u.getId());
                ds.closeUsers(u.getId());
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                //Clear all activities and start new task
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                AlertDialogs alert = new AlertDialogs(LoginActivity.this);
                alert.MostrarSimple(false,"Error","Ese password es incorrecto");
            }
        }else{
            AlertDialogs alert = new AlertDialogs(LoginActivity.this);
            alert.MostrarSimple(false,"Error","Ese usuario no existe");
        }

    }
}