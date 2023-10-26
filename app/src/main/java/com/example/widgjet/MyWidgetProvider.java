package com.example.widgjet;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MyWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private Boolean[] convertStringArrayToBooleanArray(String[] strArray) {
        Boolean[] boolArray = new Boolean[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            boolArray[i] = Boolean.parseBoolean(strArray[i]);
        }
        return boolArray;
    }

    private static String GetRandomTask(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences("WidgetPrefs_" + appWidgetId, Context.MODE_PRIVATE);
        String[] taskList = preferences.getString("taskList", "").split(";");
        String[] taskStatusList = preferences.getString("taskStatusList", "").split(";");



        String[] availableTasks = filterStrings(taskList,taskStatusList,false);
        if (availableTasks.length==0)
        {
            return  "Задач нет";
        }
        else {
            return getRandomString(availableTasks);
        }

    }

    public static String[] filterStrings(String[] strings, String[] booleans, boolean filterValue) {
        if (strings.length != booleans.length) {
            System.out.println("Массивы имеют разную длину.");
            return new String[0];
        }

        List<String> filteredStrings = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) {
            boolean bool = Boolean.parseBoolean(booleans[i]);
            if (bool == filterValue) {
                filteredStrings.add(strings[i]);
            }
        }

        return filteredStrings.toArray(new String[0]);
    }

    public static String getRandomString(String[] strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(strings.length);
        return strings[randomIndex];
    }
    static void updateWidget(Context context, AppWidgetManager appWidgetManager,int widgetID) {
        String RandomTask = GetRandomTask(context, widgetID);
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        widgetView.setTextViewText(R.id.taskText, RandomTask);
        Intent clickIntent = new Intent(context, MyWidgetProvider.class);
        clickIntent.setAction("android.appwidget.action.APPWIDGET_CLICK");
        clickIntent.putExtra("WidgetId",widgetID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetID, clickIntent,PendingIntent.FLAG_IMMUTABLE);
        widgetView.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("a","recive");
        Log.i("a",String.valueOf( intent.getIntExtra("WidgetId",-1)));
        Log.i("a",intent.getAction());


        if ("android.appwidget.action.APPWIDGET_CLICK".equals(intent.getAction())) {

           updateWidget(context,AppWidgetManager.getInstance(context),intent.getIntExtra("WidgetId",-1));
        }
    }





}