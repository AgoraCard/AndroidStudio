package com.example.agoracard;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements Dialog.DialogListener {

    public static String resultQR;
    Button scan_btn;
    EditText rfideingabe = null;
    EditText passeingabe = null;
    Button login;
    NfcAdapter nfcAdapter;

    Button weiter;

    //Um Login Daten aus der Firebase Datenbank zu holen, Login
    private DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rfideingabe = (EditText)findViewById(R.id.RFID_eingabe);
        passeingabe = (EditText)findViewById(R.id.Password_eingabe);
        login = (Button)findViewById(R.id.btn_login);
        scan_btn = (Button)findViewById(R.id.btn_scan);

        //Um Daten aus der Firebase Datenbank zu holen, Login
        ref = FirebaseDatabase.getInstance().getReference().child("RFID");

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
            if(ref.child(resultQR) != null){
                rfideingabe.setText(resultQR);
            }else{
                Toast.makeText(MainActivity.this, "RFID nicht gefunden", Toast.LENGTH_LONG).show();
            }
            }
        });



        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String PA;
                String mtID = rfideingabe.getText().toString();
                PA = passeingabe.getText().toString();

                if(ref.child(mtID) != null){
                    ref.child(mtID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            RFID rfid = dataSnapshot.getValue(RFID.class);
                            if(PA.equals(rfid.getPassword()))
                            {
                                Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("SHARED", MODE_PRIVATE).edit();
                                editor.putString("RFID", rfid.getRFID());
                                editor.apply();
                                Intent ueber = new Intent(MainActivity.this , Uebersicht.class);
                                startActivity(ueber);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Enter Correct Password", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else{
                    Toast.makeText(MainActivity.this, "Mitarbeiter Does not Exist", Toast.LENGTH_LONG).show();
                }
            }
        });



        weiter = (Button)findViewById(R.id.btn_dialog);
        weiter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openDialog();
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
