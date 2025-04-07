package com.kita.organizer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.kita.organizer.data.db.Converters;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(tableName = "completed_task")
@TypeConverters({Converters.class})
public class CompletedTaskEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // These fields are essentially the same as TaskEntity's
    private String text;
    private LocalDate date;
    private LocalTime time;
    private RepeatOption repeatOption;
    private int listId; // the original foreign key reference; kept for potential restoration

    // Additional fields for the completed task
    private String listName;         // storing the list name so it can be restored later
    private LocalDate completionDate;  // capturing the completion date

    public CompletedTaskEntity(String text,
                               LocalDate date,
                               LocalTime time,
                               RepeatOption repeatOption,
                               int listId,
                               String listName,
                               LocalDate completionDate) {
        this.text = text;
        this.date = date;
        this.time = time;
        this.repeatOption = repeatOption;
        this.listId = listId;
        this.listName = listName;
        this.completionDate = completionDate;
    }

    // Setter for auto-generated id
    public void setId(int id) {
        this.id = id;
    }

    // Getters
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

    public String getListName() {
        return listName;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    @Override
    public String toString() {
        return "CompletedTaskEntity{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", repeatOption=" + repeatOption +
                ", listId=" + listId +
                ", listName='" + listName + '\'' +
                ", completionDate=" + completionDate +
                '}';
    }
}
