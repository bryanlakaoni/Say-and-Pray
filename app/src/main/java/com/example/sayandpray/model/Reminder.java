package com.example.sayandpray.model;

public class Reminder {
    private int idReminder;
    private int idDoa;
    private int jamReminder;
    private int menitReminder;
    //private boolean isActiveReminder;

    public Reminder(int idReminder, int idDoa, int jamReminder, int menitReminder) {
        this.idReminder = idReminder;
        this.idDoa = idDoa;
        this.jamReminder = jamReminder;
        this.menitReminder = menitReminder;
        //this.isActiveReminder = isActiveReminder;
    }

    public int getIdReminder() {
        return idReminder;
    }

    public void setIdReminder(int idReminder) {
        this.idReminder = idReminder;
    }

    public int getIdDoa() {
        return idDoa;
    }

    public void setIdDoa(int idDoa) {
        this.idDoa = idDoa;
    }

    public int getJamReminder() {
        return jamReminder;
    }

    public void setJamReminder(int jamReminder) {
        this.jamReminder = jamReminder;
    }

    public int getMenitReminder() {
        return menitReminder;
    }

    public void setMenitReminder(int menitReminder) {
        this.menitReminder = menitReminder;
    }

//    public boolean isActiveReminder() {
//        return isActiveReminder;
//    }
//
//    public void setActiveReminder(boolean activeReminder) {
//        isActiveReminder = activeReminder;
//    }
}
