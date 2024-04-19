package com.example.myapplication;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task_manager.db";
    private static final int DATABASE_VERSION = 1;

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY, title TEXT, description TEXT, due_date INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Method to retrieve all tasks from the database
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex("_id");
                    int titleIndex = cursor.getColumnIndex("title");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int dueDateIndex = cursor.getColumnIndex("due_date");

                    do {
                        int id = cursor.getInt(idIndex);
                        String title = cursor.getString(titleIndex);
                        String description = cursor.getString(descriptionIndex);
                        long dueDate = cursor.getLong(dueDateIndex);

                        // Create Task object and add it to the list
                        Task task = new Task(id, title, description, new Date(dueDate));
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        return taskList;
    }

    public Task getTask(int taskId) {
        Task task = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, "_id=?", new String[]{String.valueOf(taskId)}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int titleIndex = cursor.getColumnIndex("title");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int dueDateIndex = cursor.getColumnIndex("due_date");

                    if (titleIndex != -1 && descriptionIndex != -1 && dueDateIndex != -1) {
                        String title = cursor.getString(titleIndex);
                        String description = cursor.getString(descriptionIndex);
                        long dueDateMillis = cursor.getLong(dueDateIndex);

                        // Create a Task object with the retrieved data
                        task = new Task(taskId, title, description, new Date(dueDateMillis));
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return task;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("tasks", "_id=?", new String[]{String.valueOf(taskId)});
        db.close();
        return rowsDeleted > 0;
    }

}
