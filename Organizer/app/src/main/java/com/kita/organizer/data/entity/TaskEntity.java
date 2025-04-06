package com.kita.organizer.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.kita.organizer.data.db.Converters;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(
        tableName = "task",
        foreignKeys = @ForeignKey(
                entity = ListEntity.class,
                parentColumns = "id",
                childColumns = "listId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "listId")} // Create an index for listId
)
@TypeConverters({Converters.class})  // Tell Room to use custom converters
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private LocalDate date;
    private LocalTime time;
    private RepeatOption repeatOption;
    private int listId;  // foreign key reference

    public TaskEntity(String text, LocalDate date, LocalTime time, RepeatOption repeatOption, int listId) {
        this.text = text;
        this.date = date;
        this.time = time;
        this.repeatOption = repeatOption;
        this.listId = listId;
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

    public int getListId() {
        return listId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", repeatOption=" + repeatOption +
                ", listId=" + listId +
                '}';
    }
}
