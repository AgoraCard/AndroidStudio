package com.example.agoracard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Uebersicht extends AppCompatActivity {

    private TextView firstName_tv;
    private TextView lastName_tv;
    private TextView date_tv;
    private ListView listView;
    private String loginName = null;
    private Button logout_btn;
    private ProgressBar progressLoader;

    private Context context;

    //Adapter um die Daten zu speichern
    private ArrayList<String> list;
    private Training data;

    //Um Login Daten aus der Firebase Datenbank zu holen, Login
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebersicht);

        //Für die Tabelle
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, listView, false);

        context = this;

        data = new Training();
        listView = (ListView) findViewById(R.id.listViewID);
        firstName_tv = (TextView) findViewById(R.id.firstName_tv);
        lastName_tv = (TextView) findViewById(R.id.lastName_tv);
        date_tv = (TextView) findViewById(R.id.date_tv);
        list = new ArrayList<>();
        logout_btn = (Button) findViewById(R.id.btn_logout);
        progressLoader = (ProgressBar) findViewById(R.id.progress_loader);
        loginName = this.getIntent().getStringExtra("LoginName");


        //Um Daten aus der Firebase Datenbank zu holen, Login
        ref = FirebaseDatabase.getInstance().getReference().child("USER").child(loginName);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressLoader.setVisibility(View.GONE);

                //TODO: the keys should not be hardcoded
                firstName_tv.setText(dataSnapshot.child("Person").child("Vorname").getValue(String.class));
                lastName_tv.setText(dataSnapshot.child("Person").child("Nachname").getValue(String.class));
                date_tv.setText(dataSnapshot.child("Person").child("Geburtsdatum").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.addValueEventListener(listener);


        ref = FirebaseDatabase.getInstance().getReference().child("USER").child(loginName).child("Training");
        listView.addHeaderView(headerView);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String[]> items = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data = ds.getValue(Training.class);
                    String[] item = new String[]{
                            data.getName(), data.getDate()
                    };
                    items.add(item);
                }
                //sort
                sortData(items);

                ArrayList<String[]> result = new ArrayList<>(items.size());
                for (String[] item : items) {

                }

                if(items.size() == 0){
                    Toast.makeText(Uebersicht.this, "no data found", Toast.LENGTH_LONG).show();
                    return;
                }

                ListAdapter listadapter = new ListAdapter(context, R.layout.rowlayout, R.id.rowtraining, items);
                listView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    //Method sort Data
    public static void sortData(final ArrayList<String[]> list) {
        Collections.sort(list, new Comparator<String[]>() {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    return dateFormat.parse(o1[1]).compareTo(dateFormat.parse(o2[1]));
                } catch (ParseException e1) {
                    return 0;
                }
            }
        });
    }

}