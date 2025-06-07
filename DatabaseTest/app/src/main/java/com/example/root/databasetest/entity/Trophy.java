package com.example.root.databasetest.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trophy",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )},
        indices = { @Index(value = "id")}
)
public class Trophy {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public String description;

    public Trophy(long userId, String description) {
        this.userId = userId;
        this.description = description;
    }
}
