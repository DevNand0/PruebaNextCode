package com.example.nextcode.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextcode.models.Usuario;

import java.sql.SQLException;

public class UsuarioDataSource {

    private SQLiteHelper dbHelper;
    private Usuario usuario;
    private SQLiteDatabase database;
    private String allColumns[]={
            SQLiteHelper.ID_USUARIO,
            SQLiteHelper.NOMBRES,
            SQLiteHelper.APELLIDOS,
            SQLiteHelper.CEDULA,
            SQLiteHelper.EMAIL,
            SQLiteHelper.PASSWORD,
            SQLiteHelper.ESTADO
    };

    public UsuarioDataSource(Context c) {
        dbHelper = new SQLiteHelper(c);
        usuario = new Usuario();
    }

    public void open()throws SQLException {
        database=dbHelper.getReadableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public long closeUsers(int id){
        ContentValues v = new ContentValues();
        v.put(SQLiteHelper.ESTADO,"I");
        long resp = database.update(SQLiteHelper.TABLA_USUARIO, v, SQLiteHelper.ID_USUARIO+"<>"+id,null);
        return resp;
    }

    public long sessionUser(int id){
        ContentValues v = new ContentValues();
        v.put(SQLiteHelper.ESTADO,"A");
        long resp = database.update(SQLiteHelper.TABLA_USUARIO, v, SQLiteHelper.ID_USUARIO+"="+id,null);
        return resp;
    }

    public long sessionClose(int id){
        ContentValues v = new ContentValues();
        v.put(SQLiteHelper.ESTADO,"I");
        long resp = database.update(SQLiteHelper.TABLA_USUARIO, v, SQLiteHelper.ID_USUARIO+"="+id,null);
        return resp;
    }


    public int  Insertar(Usuario u){
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.ID_USUARIO,u.getId());
        values.put(SQLiteHelper.PASSWORD,u.getContrasenia());
        values.put(SQLiteHelper.NOMBRES,u.getNombres());
        values.put(SQLiteHelper.APELLIDOS,u.getApellidos());
        values.put(SQLiteHelper.EMAIL,u.getCorreo());
        values.put(SQLiteHelper.ESTADO,u.getStatus());
        values.put(SQLiteHelper.CEDULA,u.getCedula());
        long insert_id=database.insert(SQLiteHelper.TABLA_USUARIO,null,values);
        //u.setId((int)insert_id);
        return (int)insert_id;

    }

    public Usuario usuarioActivo(){
        Usuario user =  null;

        Cursor cursor = null;
        try {
            cursor = database.query(SQLiteHelper.TABLA_USUARIO,allColumns,SQLiteHelper.ESTADO+"='A'",null,null,null,null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                user =new Usuario();
                user.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.ID_USUARIO)));
                user.setNombres(cursor.getString(cursor.getColumnIndex(SQLiteHelper.NOMBRES)));
                user.setApellidos(cursor.getString(cursor.getColumnIndex(SQLiteHelper.APELLIDOS)));
                user.setCorreo(cursor.getString(cursor.getColumnIndex(SQLiteHelper.EMAIL)));
                user.setStatus(cursor.getString(cursor.getColumnIndex(SQLiteHelper.ESTADO)));
                user.setContrasenia(cursor.getString(cursor.getColumnIndex(SQLiteHelper.PASSWORD)));
                user.setCedula(cursor.getString(cursor.getColumnIndex(SQLiteHelper.CEDULA)));
                //countUsers=cursor.getCount();
            }
        }finally {
            cursor.close();
        }

        return user;
    }



    public Usuario getUsuario(String email){
        Usuario user =  null;

        Cursor cursor = null;
        try {
            cursor = database.query(SQLiteHelper.TABLA_USUARIO,allColumns,SQLiteHelper.EMAIL+"='"+email+"'",null,null,null,null);
            if(cursor!=null) {
                cursor.moveToFirst();
                user =new Usuario();
                user.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.ID_USUARIO)));
                user.setNombres(cursor.getString(cursor.getColumnIndex(SQLiteHelper.NOMBRES)));
                user.setApellidos(cursor.getString(cursor.getColumnIndex(SQLiteHelper.APELLIDOS)));
                user.setCorreo(cursor.getString(cursor.getColumnIndex(SQLiteHelper.EMAIL)));
                user.setStatus(cursor.getString(cursor.getColumnIndex(SQLiteHelper.ESTADO)));
                user.setContrasenia(cursor.getString(cursor.getColumnIndex(SQLiteHelper.PASSWORD)));
                user.setCedula(cursor.getString(cursor.getColumnIndex(SQLiteHelper.CEDULA)));
                //countUsers=cursor.getCount();
            }
        }finally {
            cursor.close();
        }

        return user;
    }

    public int eliminaUsuarios(int id){
        String sql =SQLiteHelper.ID_USUARIO+"<>"+id;
        int delete_usuarios = database.delete(SQLiteHelper.TABLA_USUARIO,"1",null);
        return delete_usuarios;
    }

}
