package com.example.agoracard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.content.Context.MODE_PRIVATE;

public class Dialog extends AppCompatDialogFragment {

    private EditText editTextPassword;

    //Listener um Daten an die MainActivity zu senden
    private DialogListener listener;

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Password")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            String pass = editTextPassword.getText().toString();
                            listener.applyText(pass);
                    }
                });

        editTextPassword = view.findViewById(R.id.edit_password);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "implement ExampleDialogListener");
        }
    }

    public interface DialogListener{
        void applyText(String password);
    }
}
