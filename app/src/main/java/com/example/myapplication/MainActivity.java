package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView recyclerView;
    private Button addTaskButton;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.task_recycler_view);
        addTaskButton = findViewById(R.id.add_task_button);
        databaseHelper = new TaskDatabaseHelper(this);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddTaskActivity
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this); // Pass the listener
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(databaseHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    // Handle edit action
    @Override
    public void onEditClick(int position) {
        // Retrieve the task from the taskList
        Task task = taskList.get(position);
        // Open EditTaskActivity with the task details
        Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    // Handle delete action
    @Override
    public void onDeleteClick(int position) {
        // Retrieve the task from the taskList
        Task task = taskList.get(position);
        // Delete the task from the database
        boolean deleted = databaseHelper.deleteTask(task.getId());
        if (deleted) {
            // Remove the task from the taskList
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}