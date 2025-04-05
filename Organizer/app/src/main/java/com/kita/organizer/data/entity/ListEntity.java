package com.kita.organizer.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "list")
public class ListEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    public ListEntity(@NonNull String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "ListEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
