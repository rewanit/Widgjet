package com.example.widgjet;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button buttonAddTask;
    private ListView listViewTasks;
    private Button buttonSaveTasks;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config);
        RemoteViews widgetView = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
        int appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        listViewTasks = findViewById(R.id.listViewTasks);
        buttonSaveTasks = findViewById(R.id.buttonSaveTasks);

        final List<String> taskList = new ArrayList<String>();
        final List<Boolean> taskStatusList = new ArrayList<Boolean>();

        final ArrayAdapter<String> taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice);
        listViewTasks.setAdapter(taskAdapter);
        listViewTasks.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        SharedPreferences preferences = getSharedPreferences("WidgetPrefs_" + appWidgetId, Context.MODE_PRIVATE);                SharedPreferences.Editor editor = preferences.edit();
        String taskListString = preferences.getString("taskList", "");
        String statusListString = preferences.getString("taskStatusList", "");

        if (!taskListString.isEmpty() && !statusListString.isEmpty()) {
            String[] tasks = taskListString.split(";");
            String[] statuses = statusListString.split(";");

            for (int i = 0; i < tasks.length; i++) {
                taskList.add(tasks[i]);
                boolean isCompleted = Boolean.parseBoolean(statuses[i]);
                taskStatusList.add(isCompleted);
                taskAdapter.add(tasks[i]);
            }
        }

        for (int i = 0; i < taskStatusList.size(); i++) {
            listViewTasks.setItemChecked(i, taskStatusList.get(i));
        }

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isChecked = listViewTasks.isItemChecked(position);
                taskStatusList.set(position, isChecked);

            }
        });
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextTask.getText().toString();
                if (!description.isEmpty()) {
                    taskList.add(description);
                    taskStatusList.add(false);
                    taskAdapter.add(description);
                    taskAdapter.notifyDataSetChanged();
                    editTextTask.setText("");
                }
            }
        });

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Intent updateIntent = new Intent(this, MyWidgetProvider.class);

        buttonSaveTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Сохранение задач и их статусов в SharedPreferences
                SharedPreferences preferences = getSharedPreferences("WidgetPrefs_" + appWidgetId, Context.MODE_PRIVATE);                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("taskList", TextUtils.join(";", taskList));
                editor.putString("taskStatusList", TextUtils.join(";", convertBooleanArrayToStringArray(taskStatusList.toArray(new Boolean[0]))));
                editor.apply();
                appWidgetManager.updateAppWidget(appWidgetId,widgetView);
                Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);

                updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
                sendBroadcast(updateIntent);
                MyWidgetProvider.updateWidget( getApplicationContext(), appWidgetManager, appWidgetId);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.taskText);
                finish();
            }
        });





    }
    private String[] convertBooleanArrayToStringArray(Boolean[] boolArray) {
        String[] strArray = new String[boolArray.length];
        for (int i = 0; i < boolArray.length; i++) {
            strArray[i] = String.valueOf(boolArray[i]);
        }
        return strArray;
    }
}
