package com.mas.smartmirror.models;

/*Kayıtlı olan faaliyetleri Note sınıfında
bulunan aşağıdaki parametrelere ayrıştıracağız.*/
public class Note {
    String key;
    String time;
    String date;
    String title;
    String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
