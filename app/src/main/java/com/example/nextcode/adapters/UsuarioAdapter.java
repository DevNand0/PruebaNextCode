package com.example.nextcode.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nextcode.R;
import com.example.nextcode.models.Usuario;

import java.util.ArrayList;

public class UsuarioAdapter extends BaseAdapter {
    private Context c;
    private LayoutInflater layoutInflater;
    private TextView tv_email, tv_cedula, tv_nombre;
    private ArrayList<Usuario> lista;

    public UsuarioAdapter(Context c, ArrayList<Usuario> lista){
        this.c=c;
        this.layoutInflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.lista=lista;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view  = convertView;
        if(convertView==null){
            view = layoutInflater.inflate(R.layout.usuario_item,null,true);
        }

        tv_email = (TextView)view.findViewById(R.id.tv_email);
        tv_cedula= (TextView)view.findViewById(R.id.tv_cedula);
        tv_nombre= (TextView)view.findViewById(R.id.tv_nombre);

        Usuario usuario = lista.get(position);

        tv_email.setText(usuario.getCorreo());
        tv_nombre.setText(usuario.getNombres()+" "+usuario.getApellidos());
        tv_cedula.setText(usuario.getCedula());

        return view;
    }


}
