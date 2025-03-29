package com.kita.organizer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.kita.organizer.data.db.Converters;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(tableName = "tasks")
@TypeConverters({Converters.class})  // Tell Room to use custom converters
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private LocalDate date;
    private LocalTime time;
    private RepeatOption repeatOption;
    private String listName;

    public Task(String text, LocalDate date, LocalTime time, RepeatOption repeatOption, String listName) {
        this.text = text;
        this.date = date;
        this.time = time;
        this.repeatOption = repeatOption;
        this.listName = listName;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters (no need for setters if using constructor)
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public RepeatOption getRepeatOption() {
        return repeatOption;
    }

    public String getListName() {
        return listName;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", repeatOption=" + repeatOption +
                ", listName='" + listName + '\'' +
                '}';
    }
}
