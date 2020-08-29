package com.example.nextcode.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


public class AlertDialogs {
    Intent intent;
    Context c;
    public AlertDialogs(Context c){
        this.c=c;
    }
    public void setIntent(Intent intent){
        this.intent=intent;
    }

    public void MostrarSimple(final boolean sigActivity, String titulo, String contenido){
        AlertDialog alertDialog = new AlertDialog.Builder(this.c).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(contenido);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(sigActivity){
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            c.startActivity(intent);
                        }

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
