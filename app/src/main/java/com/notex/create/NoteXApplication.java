package com.notex.create;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class NoteXApplication extends Application {
    private static final String TAG = "NoteXApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Set up global exception handler
        setupGlobalExceptionHandler();
        
        // Initialize crash reporting
        initializeCrashReporting();
    }
    
    private void setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception in thread " + thread.getName(), throwable);
                
                // Save crash information
                saveCrashInfo(thread, throwable);
                
                // Show crash dialog if possible
                showCrashDialog(throwable);
                
                // Give some time for the crash info to be saved
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore
                }
                
                // Call the default handler
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        });
    }
    
    private void saveCrashInfo(Thread thread, Throwable throwable) {
        try {
            SharedPreferences prefs = getSharedPreferences("debug_prefs", Context.MODE_PRIVATE);
            String crashInfo = "CRASH DETECTED!\n" +
                    "Thread: " + thread.getName() + "\n" +
                    "Exception: " + throwable.getClass().getSimpleName() + "\n" +
                    "Message: " + throwable.getMessage() + "\n" +
                    "Stack Trace:\n" + getStackTrace(throwable);
            
            prefs.edit()
                    .putString("last_crash", crashInfo)
                    .putLong("crash_timestamp", System.currentTimeMillis())
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving crash info", e);
        }
    }
    
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
    
    private void showCrashDialog(Throwable throwable) {
        try {
            // Try to show a toast message
            Toast.makeText(this, "App crashed: " + throwable.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing crash dialog", e);
        }
    }
    
    private void initializeCrashReporting() {
        try {
            // Initialize any crash reporting services here
            Log.i(TAG, "Crash reporting initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing crash reporting", e);
        }
    }
}