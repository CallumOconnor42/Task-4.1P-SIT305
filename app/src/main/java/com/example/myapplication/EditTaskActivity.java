package com.example.myapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dueDateEditText;
    private Button saveButton;
    private TaskDatabaseHelper databaseHelper;
    private Task taskToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        dueDateEditText = findViewById(R.id.due_date_edit_text);
        saveButton = findViewById(R.id.save_button);
        databaseHelper = new TaskDatabaseHelper(this);

        // Retrieve the task ID from the intent
        int taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            // If task ID is not provided, finish the activity
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve the task details from the database
        taskToUpdate = databaseHelper.getTask(taskId);
        if (taskToUpdate == null) {
            // If task is not found in the database, finish the activity
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the EditText fields with the task details
        titleEditText.setText(taskToUpdate.getTitle());
        descriptionEditText.setText(taskToUpdate.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(taskToUpdate.getDueDate());
        dueDateEditText.setText(formattedDate);

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });
    }

    private void updateTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String dueDateString = dueDateEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the due date string
        long dueDateMillis;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dueDate = sdf.parse(dueDateString);
            dueDateMillis = dueDate.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid due date format. Please use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("due_date", dueDateMillis);

        int rowsAffected = db.update("tasks", values, "_id=?", new String[]{String.valueOf(taskToUpdate.getId())});
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }
}