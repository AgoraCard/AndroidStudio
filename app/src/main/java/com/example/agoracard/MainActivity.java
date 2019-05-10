package com.example.agoracard;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static String resultQR = "";
    Button scan_btn;
    FloatingActionButton fab;
    EditText rfideingabe = null;
    EditText passeingabe = null;
    Button login;
    NfcAdapter nfcAdapter;

    Button weiter;
    Button btn_create;

    //Um Login zu machen
    private FirebaseAuth firebaseAuth;

    // list of NFC technologies detected:
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rfideingabe = (EditText) findViewById(R.id.RFID_eingabe);
        passeingabe = (EditText) findViewById(R.id.Password_eingabe);
        login = (Button) findViewById(R.id.btn_login);
        scan_btn = (Button) findViewById(R.id.btn_scan);
        btn_create = (Button) findViewById(R.id.btn_create);

        firebaseAuth = FirebaseAuth.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        //nfc active check
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled() == false) {

            Toast.makeText(getApplicationContext(), "Click the Button in the right corner to activate NFC", Toast.LENGTH_LONG).show();
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            }
        });

        //Oeffnet den QR-Reader
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password;
                final String userEingabe;
                final String email = "@agora.de";

                password = passeingabe.getText().toString();
                userEingabe = rfideingabe.getText().toString();

                //validating data
                //matches("\\.|#\\$|\\[|\\]") only worked when the regex char was the first in the string
                if (userEingabe.contains(".") ||
                        userEingabe.contains("#") ||
                        userEingabe.contains("$") ||
                        userEingabe.contains("[") ||
                        userEingabe.contains("]")) {
                    showDialog("Error", "You can not use ., #, $, [ or ] in your Name");
                    return;
                }

                if(password.length() == 0 || userEingabe.length() == 0){
                    showDialog("Error", "Name and Password can not be empty");
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Proccessing...", true);
                (firebaseAuth.signInWithEmailAndPassword(userEingabe + email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            //open detail screen
                            Intent intent = new Intent(MainActivity.this, Uebersicht.class);
                            intent.putExtra("LoginName", firebaseAuth.getCurrentUser().getEmail().replaceAll(email, ""));
                            startActivity(intent);
                        } else {
                            Log.e("Error", task.getException().toString());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //erweitere String User

                final String password;
                final String userEingabe;
                final String email = "@agora.de";

                if (rfideingabe.getText().toString().toLowerCase().contains(email.toLowerCase()) == false) {
                    rfideingabe.append(email);
                }

                userEingabe = rfideingabe.getText().toString();
                password = passeingabe.getText().toString();


                //Legt User an
                (firebaseAuth.createUserWithEmailAndPassword(userEingabe,
                        password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, Auth.class);
                            startActivity(i);
                        } else {
                            Log.e("Error", task.getException().toString());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void showDialog(String title, String message) {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //region nfc stuff
    @Override
    public void onResume() {
        super.onResume();

        if (resultQR != "") {
            rfideingabe.setText((resultQR));
        }

        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("onNewIntent", "1");

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Log.d("onNewIntent", "2");
            rfideingabe.setText(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

            //if(getIntent().hasExtra(NfcAdapter.EXTRA_TAG)){

            Parcelable tagN = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tagN != null) {
                Log.d("nfc", "Parcelable OK");
                NdefMessage[] msgs;
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                byte[] payload = dumpTagData(tagN).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};

                //Log.d(TAG, msgs[0].toString());


            } else {
                Log.d("nfc", "Parcelable NULL");
            }


            Parcelable[] messages1 = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages1 != null) {
                Log.d("nfc", "Found " + messages1.length + " NDEF messages");
            } else {
                Log.d("nfc", "Not EXTRA_NDEF_MESSAGES");
            }

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {

                Log.d("onNewIntent:", "NfcAdapter.EXTRA_TAG");

                Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (messages != null) {
                    Log.d("nfc", "Found " + messages.length + " NDEF messages");
                }
            } else {
                Log.d("nfc", "Write to an unformatted tag not implemented");
            }


            //mTextView.setText( "NFC Tag\n" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_TAG)));
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();

        sb.append(getReversed(id));
        rfideingabe.setText(sb.toString());

//        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
//        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
//        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");
//
//
//
//        String prefix = "android.nfc.tech.";
//        sb.append("Technologies: ");
//        for (String tech : tag.getTechList()) {
//            sb.append(tech.substring(prefix.length()));
//            sb.append(", ");
//        }
//        sb.delete(sb.length() - 2, sb.length());
//        for (String tech : tag.getTechList()) {
//            if (tech.equals(MifareClassic.class.getName())) {
//                sb.append('\n');
//                MifareClassic mifareTag = MifareClassic.get(tag);
//                String type = "Unknown";
//                switch (mifareTag.getType()) {
//                    case MifareClassic.TYPE_CLASSIC:
//                        type = "Classic";
//                        break;
//                    case MifareClassic.TYPE_PLUS:
//                        type = "Plus";
//                        break;
//                    case MifareClassic.TYPE_PRO:
//                        type = "Pro";
//                        break;
//                }
//                sb.append("Mifare Classic type: ");
//                sb.append(type);
//                sb.append('\n');
//
//                sb.append("Mifare size: ");
//                sb.append(mifareTag.getSize() + " bytes");
//                sb.append('\n');
//
//                sb.append("Mifare sectors: ");
//                sb.append(mifareTag.getSectorCount());
//                sb.append('\n');
//
//                sb.append("Mifare blocks: ");
//                sb.append(mifareTag.getBlockCount());
//            }
//
//            if (tech.equals(MifareUltralight.class.getName())) {
//                sb.append('\n');
//                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
//                String type = "Unknown";
//                switch (mifareUlTag.getType()) {
//                    case MifareUltralight.TYPE_ULTRALIGHT:
//                        type = "Ultralight";
//                        break;
//                    case MifareUltralight.TYPE_ULTRALIGHT_C:
//                        type = "Ultralight C";
//                        break;
//                }
//                sb.append("Mifare Ultralight type: ");
//                sb.append(type);
//            }
//        }
//        Log.d("Datos: ", sb.toString());
//
//        DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
//        Date now = new Date();
//
//        rfideingabe.setText(TIME_FORMAT.format(now) + '\n' + sb.toString());
        return sb.toString();
    }


    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private String ByteArrayToHexString(byte[] inarray) {

        Log.d("ByteArrayToHexString", inarray.toString());

        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
//CE7AEED4
//EE7BEED4
        Log.d("ByteArrayToHexString", String.format("%0" + (inarray.length * 2) + "X", new BigInteger(1, inarray)));


        return out;
    }
//endregion
}




