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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ListEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        // 1. quick exit for same reference
        if (this == o) return true;
        // 2. null or different runtime class
        if (o == null || getClass() != o.getClass()) return false;

        ListEntity that = (ListEntity) o;

        /*
         * 3.  When Room hasn’t assigned an id yet (id == 0), we fall back to name.
         *     Once id is non-zero, it uniquely identifies the row, so we only
         *     compare ids.  This makes the method work both pre- and post-insert.
         */
        if (this.id != 0 && that.id != 0) {
            return this.id == that.id;
        }
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        /*
         * Match the logic in equals(): use id when it’s set, otherwise hash the
         * name.  This guarantees that equal objects share the same hash code.
         */
        return id != 0 ? Integer.hashCode(id) : name.hashCode();
    }

}
