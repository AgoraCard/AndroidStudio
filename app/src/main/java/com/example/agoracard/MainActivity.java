package com.example.agoracard;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements Dialog.DialogListener {

    public static String resultQR;
    Button scan_btn;
    EditText rfideingabe = null;
    EditText passeingabe = null;
    Button login;
    NfcAdapter nfcAdapter;

    Button weiter;
    Button loginZwei;

    //Um Login zu machen
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rfideingabe = (EditText)findViewById(R.id.RFID_eingabe);
        passeingabe = (EditText)findViewById(R.id.Password_eingabe);
        login = (Button)findViewById(R.id.btn_login);
        scan_btn = (Button)findViewById(R.id.btn_scan);
        loginZwei = (Button)findViewById(R.id.loginZwei);

        firebaseAuth = FirebaseAuth.getInstance();

        //Zeigt an, ob NFC verfügbar
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show();
        }

        //Oeffnet den QR-Reader
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScanCodeActivity.class));
            if(rfideingabe == null){
                rfideingabe.setText(resultQR);
            }else{
                Toast.makeText(MainActivity.this, "RFID nicht gefunden", Toast.LENGTH_LONG).show();
            }
            }
        });


        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String password;
                final String userEingabe;
                final String email = "@agora.de";

                userEingabe = rfideingabe.getText().toString();
                password = passeingabe.getText().toString();

                if(userEingabe.toLowerCase().contains(email.toLowerCase())){
                }else {
                    rfideingabe.append(email);
                }


                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Proccessing...", true);
                (firebaseAuth.signInWithEmailAndPassword(rfideingabe.getText().toString(), password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                           //Uebergabe
                            SharedPreferences.Editor editor = getSharedPreferences("SHARED", MODE_PRIVATE).edit();
                            editor.putString("USER", userEingabe);
                            editor.apply();
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                           //next screen
                            Intent i = new Intent(MainActivity.this, Uebersicht.class);
                            i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                            startActivity(i);
                        }else{
                            Log.e("Error", task.getException().toString());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            rfideingabe.setText("");
                            passeingabe.setText("");
                        }
                    }
                });
            }
        });



        weiter = (Button)findViewById(R.id.btn_dialog);
        weiter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openDialog();
            }
        });

        //Geht zum zweiten LoginScreen
        loginZwei.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent auth = new Intent(MainActivity.this , Auth.class);
                startActivity(auth);
            }
        });
    }

    public void openDialog(){
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void applyText(String password) {
        passeingabe.setText(password);
    }
}




