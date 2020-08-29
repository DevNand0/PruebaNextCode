package com.example.nextcode.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "test.db";
    public static final int DATABASE_VERSION=1;

    public static final String  TABLA_USUARIO="usuario";
    public static final String  ID_USUARIO="id";
    public static final String  PASSWORD="contrasenia";
    public static final String  NOMBRES="nombres";
    public static final String  APELLIDOS="apellidos";
    public static final String  CEDULA="cedula";
    public static final String  EMAIL="correo";
    public static final String  ESTADO="status";


    public static final String CREATE_TABLE_USUARIO = "CREATE TABLE "+TABLA_USUARIO+" ("+
            ID_USUARIO+" INTEGER NOT NULL, "+
            NOMBRES+" TEXT NOT NULL, "+
            APELLIDOS+" TEXT NOT NULL, "+
            CEDULA+" VACHAR(13) NOT NULL, "+
            PASSWORD+" TEXT NOT NULL, "+
            ESTADO+" VARCHAR(1) DEFAULT 'A', "+
            EMAIL+" TEXT NOT NULL);";


    public SQLiteHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLA_USUARIO);

        onCreate(db);
    }

}
