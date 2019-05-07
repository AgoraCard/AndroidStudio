package com.example.agoracard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Auth  extends AppCompatActivity {

    EditText usereingabe = null;
    EditText passeingabe = null;
    Button loginRegister;

    //Um Login zu machen
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        usereingabe = (EditText)findViewById(R.id.USEReingabe);
        passeingabe = (EditText)findViewById(R.id.Passwordeingabe);

        firebaseAuth = FirebaseAuth.getInstance();

        loginRegister = (Button)findViewById(R.id.btnlogReg);
        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //erweitere String User
                usereingabe.append("@agora.de");
                //Legt User an
                (firebaseAuth.createUserWithEmailAndPassword(usereingabe.getText().toString(),
                        passeingabe.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Auth.this, "Registration successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Auth.this, Auth.class);
                            startActivity(i);
                        } else {
                            Log.e("Error", task.getException().toString());
                            Toast.makeText(Auth.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        usereingabe.setText("");
                        passeingabe.setText("");
                        }
                    }
                });
            }
        });



    }
    }
