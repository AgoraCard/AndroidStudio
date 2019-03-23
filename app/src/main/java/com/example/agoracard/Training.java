package com.example.agoracard;


public class Training {

   private String Name;
   private String Date;


    public Training(String name, String date) {
        this.Name = name;
        this.Date = date;
    }

    public Training() {
    }



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }
}
