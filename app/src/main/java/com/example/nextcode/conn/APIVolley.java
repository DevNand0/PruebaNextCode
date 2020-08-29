package com.example.nextcode.conn;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class APIVolley {

    private RequestQueue queue;
    private static APIVolley singleton= null;
    private APIVolley(){
        this.queue = Volley.newRequestQueue(ApplicationConn.getInstance().getApplicationContext());
    }

    public static APIVolley getInstance(){
        if(singleton==null){
            singleton=new APIVolley();
        }
        return singleton;
    }

    public RequestQueue getQueue(){
        return this.queue;
    }
}
