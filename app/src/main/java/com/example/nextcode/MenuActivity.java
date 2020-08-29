package com.example.nextcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.nextcode.db.UsuarioDataSource;
import com.example.nextcode.models.Usuario;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private UsuarioDataSource ds;
    private TextView tv_usuario;
    private Usuario usuario;
    private CardView cv_usuarios, cv_facturas, cv_planes, cv_salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ds = new UsuarioDataSource(getApplicationContext());

        try{
            ds.open();
        }catch(android.database.SQLException e){
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        initializer();
        setListener();

        usuario=ds.usuarioActivo();
        tv_usuario.setText(usuario.getNombres()+" "+usuario.getApellidos());
    }

    private void initializer(){
        tv_usuario=(TextView)findViewById(R.id.tv_usuario);
        cv_usuarios=(CardView)findViewById(R.id.cv_usuarios);
        cv_facturas=(CardView)findViewById(R.id.cv_facturas);
        cv_planes=(CardView)findViewById(R.id.cv_planes);
        cv_salir=(CardView)findViewById(R.id.cv_salir);
    }

    private void setListener(){
        cv_usuarios.setOnClickListener(this);
        cv_facturas.setOnClickListener(this);
        cv_planes.setOnClickListener(this);
        cv_salir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==cv_usuarios){
            Intent intent = new Intent(MenuActivity.this, UsuarioActivity.class);
            //Clear all activities and start new task
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(v==cv_facturas){

        }else if(v==cv_planes){

        }else if(v==cv_salir){
            if(ds.sessionClose(usuario.getId())>=0){
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                //Clear all activities and start new task
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }
}