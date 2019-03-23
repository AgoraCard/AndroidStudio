package com.example.agoracard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Uebersicht extends AppCompatActivity {

    TextView vorname;
    TextView nachname;
    TextView date;
    ListView listView;
    String sharedP = null;
    Button logout;

    //Adapter um die Daten zu speichern
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Training data;

    //Um Login Daten aus der Firebase Datenbank zu holen, Login
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebersicht);

        data = new Training();
        listView = (ListView)findViewById(R.id.listViewID);
        vorname = (TextView)findViewById(R.id.vorname_ausgabe);
        nachname = (TextView)findViewById(R.id.nachname_ausgabe);
        date = (TextView)findViewById(R.id.gebut_ausgabe);
        list = new ArrayList<>();
        logout = (Button)findViewById(R.id.btn_logout);

        final SharedPreferences prefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        if (prefs != null) {
            String shared = prefs.getString("RFID", "No name defined");
            //vorname.setText(shared);
            sharedP = shared;
        }


        //Um Daten aus der Firebase Datenbank zu holen, Login
        ref = FirebaseDatabase.getInstance().getReference().child("RFID").child(sharedP);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vorname.setText(dataSnapshot.child("Person").child("Vorname").getValue(String.class));
                nachname.setText(dataSnapshot.child("Person").child("Nachname").getValue(String.class));
                date.setText(dataSnapshot.child("Person").child("Geburtsdatum").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.addValueEventListener(listener);




        ref = FirebaseDatabase.getInstance().getReference().child("RFID").child(sharedP).child("Training");
        adapter = new ArrayAdapter<String>(this, R.layout.training_info, R.id.trainingInfo, list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    data = ds.getValue(Training.class);
                    list.add("Die Schulung im Bereich " + data.getName() + " ist am " + data.getDate() + " fällig");
                }
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().remove("RFID").apply();
                startActivity(new Intent(Uebersicht.this, MainActivity.class));
            }
        });
    }

}