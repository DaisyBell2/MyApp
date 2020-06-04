package com.daisybell.myapp.check_list;

import android.net.Uri;

import java.io.Serializable;

public class SaveResultCheckList implements Serializable {
    public String idUser, fullNameUser, nameCheckList, photoUri, date, time, doneCheckList, notDoneCheckList;

    public SaveResultCheckList() {
    }

    public SaveResultCheckList(String idUser, String fullNameUser, String nameCheckList, String photoUri,
                               String date, String time, String doneCheckList, String notDoneCheckList) {
        this.idUser = idUser;
        this.fullNameUser = fullNameUser;
        this.nameCheckList = nameCheckList;
        this.photoUri = photoUri;
        this.date = date;
        this.time = time;
        this.doneCheckList = doneCheckList;
        this.notDoneCheckList = notDoneCheckList;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFullNameUser() {
        return fullNameUser;
    }

    public void setFullNameUser(String fullNameUser) {
        this.fullNameUser = fullNameUser;
    }

    public String getNameCheckList() {
        return nameCheckList;
    }

    public void setNameCheckList(String nameCheckList) {
        this.nameCheckList = nameCheckList;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoneCheckList() {
        return doneCheckList;
    }

    public void setDoneCheckList(String doneCheckList) {
        this.doneCheckList = doneCheckList;
    }

    public String getNotDoneCheckList() {
        return notDoneCheckList;
    }

    public void setNotDoneCheckList(String notDoneCheckList) {
        this.notDoneCheckList = notDoneCheckList;
    }
}
