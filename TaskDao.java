package com.example.android.tsi.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY system")    //task is tableName in TaskEntry
    LiveData<List<TaskEntryRm>> loadAllTasks(); //t09b.07
    //@Query("SELECT * FROM task WHERE system = :system")
    //LiveData<TaskEntry> loadTaskBySystem(String system);    //get an array of rows that have the same system name
    @Insert
    void insertTask(TaskEntryRm taskEntry);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntryRm taskEntry);
    @Delete
    void deleteTask(TaskEntryRm taskEntry);
    @Query("DELETE FROM task")
    void deleteAll();
}
