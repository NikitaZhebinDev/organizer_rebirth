package com.kita.organizer.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.kita.organizer.data.db.Converters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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
public class TaskEntity implements Parcelable {
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

    protected TaskEntity(Parcel in) {
        id   = in.readInt();
        text = in.readString();

        // Read back with null‐check
        String dateStr = in.readString();
        date = (dateStr != null)
                ? LocalDate.parse(dateStr)
                : null;

        String timeStr = in.readString();
        time = (timeStr != null)
                ? LocalTime.parse(timeStr)
                : null;

        int repeatVal = in.readInt();
        repeatOption = RepeatOption.fromValue(repeatVal);
        listId       = in.readInt();
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

    @Override
    public boolean equals(Object o) {
        // 1. same reference
        if (this == o) return true;
        // 2. null or different class
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        // 3. once both ids are set, they are authoritative
        if (this.id != 0 && that.id != 0) {
            return this.id == that.id;
        }

        // 4. otherwise compare the "meaningful" columns
        return listId == that.listId &&
                Objects.equals(text,  that.text) &&
                Objects.equals(date,  that.date) &&
                Objects.equals(time,  that.time) &&
                repeatOption == that.repeatOption;
    }

    @Override
    public int hashCode() {
        // mirror equals(): id when present, else all business fields
        return id != 0
                ? Integer.hashCode(id)
                : Objects.hash(text, date, time, repeatOption, listId);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        // Null-safe writes:
        dest.writeString(date  != null ? date.toString() : null);
        dest.writeString(time  != null ? time.toString() : null);
        dest.writeInt(repeatOption != null
                ? repeatOption.getValue()
                : RepeatOption.NO_REPEAT.getValue());
        dest.writeInt(listId);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Parcelable.Creator<TaskEntity> CREATOR = new Parcelable.Creator<TaskEntity>() {
        @Override
        public TaskEntity createFromParcel(Parcel in) {
            return new TaskEntity(in);
        }
        @Override
        public TaskEntity[] newArray(int size) {
            return new TaskEntity[size];
        }
    };

}
