package com.example.agoracard;

public class RFID {

    private String RFID;
    private String Password;

    public RFID(String RFID, String Password) {
        this.RFID = RFID;
        this.Password = Password;
    }

    public RFID() {
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
