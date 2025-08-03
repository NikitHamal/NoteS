package com.notex.create;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DebugActivity extends AppCompatActivity {
    private static final String TAG = "DebugActivity";
    private static final List<String> logBuffer = new ArrayList<>();
    private static final int MAX_LOG_ENTRIES = 1000;
    
    private MaterialTextView crashInfoText;
    private MaterialTextView systemInfoText;
    private MaterialTextView logText;
    private MaterialButton copyLogsButton;
    private MaterialButton restartAppButton;
    private MaterialButton clearLogsButton;
    private MaterialButton exportLogsButton;
    
    private Handler handler;
    private boolean isLogging = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        
        // Initialize crash handler
        setupCrashHandler();
        
        // Initialize UI
        setupUI();
        
        // Start log collection
        startLogCollection();
        
        // Display system info
        displaySystemInfo();
        
        // Check for crash info from intent
        checkCrashInfo();
    }
    
    private void setupUI() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Debug Console");
        }
        
        crashInfoText = findViewById(R.id.crash_info_text);
        systemInfoText = findViewById(R.id.system_info_text);
        logText = findViewById(R.id.log_text);
        copyLogsButton = findViewById(R.id.copy_logs_button);
        restartAppButton = findViewById(R.id.restart_app_button);
        clearLogsButton = findViewById(R.id.clear_logs_button);
        exportLogsButton = findViewById(R.id.export_logs_button);
        
        // Setup button click listeners
        copyLogsButton.setOnClickListener(v -> copyLogsToClipboard());
        restartAppButton.setOnClickListener(v -> restartApp());
        clearLogsButton.setOnClickListener(v -> clearLogs());
        exportLogsButton.setOnClickListener(v -> exportLogs());
        
        // Setup log display
        updateLogDisplay();
    }
    
    private void setupCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                String crashInfo = "CRASH DETECTED!\n" +
                        "Thread: " + thread.getName() + "\n" +
                        "Exception: " + throwable.getClass().getSimpleName() + "\n" +
                        "Message: " + throwable.getMessage() + "\n" +
                        "Stack Trace:\n" + getStackTrace(throwable);
                
                addLogEntry("CRASH", crashInfo);
                
                // Save crash info to shared preferences for next launch
                getSharedPreferences("debug_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("last_crash", crashInfo)
                        .putLong("crash_timestamp", System.currentTimeMillis())
                        .apply();
                
                // Show crash dialog
                showCrashDialog(crashInfo);
            }
        });
    }
    
    private void startLogCollection() {
        handler = new Handler(Looper.getMainLooper());
        isLogging = true;
        
        // Start logcat collection in background
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("logcat -v time");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                
                while (isLogging && (line = reader.readLine()) != null) {
                    if (line.contains("com.notex.create") || 
                        line.contains("AndroidRuntime") || 
                        line.contains("FATAL") ||
                        line.contains("ERROR")) {
                        
                        final String logEntry = line;
                        handler.post(() -> {
                            addLogEntry("LOGCAT", logEntry);
                            updateLogDisplay();
                        });
                    }
                }
            } catch (IOException e) {
                addLogEntry("ERROR", "Failed to read logcat: " + e.getMessage());
            }
        }).start();
    }
    
    private void addLogEntry(String type, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String entry = "[" + timestamp + "] " + type + ": " + message;
        
        synchronized (logBuffer) {
            logBuffer.add(entry);
            if (logBuffer.size() > MAX_LOG_ENTRIES) {
                logBuffer.remove(0);
            }
        }
    }
    
    private void updateLogDisplay() {
        StringBuilder sb = new StringBuilder();
        synchronized (logBuffer) {
            for (String entry : logBuffer) {
                sb.append(entry).append("\n");
            }
        }
        logText.setText(sb.toString());
    }
    
    private void displaySystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Device: ").append(android.os.Build.MANUFACTURER)
            .append(" ").append(android.os.Build.MODEL).append("\n");
        info.append("Android Version: ").append(android.os.Build.VERSION.RELEASE)
            .append(" (API ").append(android.os.Build.VERSION.SDK_INT).append(")\n");
        info.append("App Version: ").append(getAppVersion()).append("\n");
        info.append("Available Memory: ").append(getAvailableMemory()).append(" MB\n");
        info.append("Total Memory: ").append(getTotalMemory()).append(" MB\n");
        info.append("Storage Available: ").append(getAvailableStorage()).append(" MB\n");
        
        systemInfoText.setText(info.toString());
    }
    
    private void checkCrashInfo() {
        String lastCrash = getSharedPreferences("debug_prefs", MODE_PRIVATE)
                .getString("last_crash", null);
        
        if (lastCrash != null) {
            crashInfoText.setVisibility(View.VISIBLE);
            crashInfoText.setText("Last Crash:\n" + lastCrash);
            
            // Clear the crash info after displaying
            getSharedPreferences("debug_prefs", MODE_PRIVATE)
                    .edit()
                    .remove("last_crash")
                    .apply();
        } else {
            crashInfoText.setVisibility(View.GONE);
        }
    }
    
    private void copyLogsToClipboard() {
        StringBuilder logs = new StringBuilder();
        logs.append("=== NoteX Debug Logs ===\n");
        logs.append("Timestamp: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");
        
        synchronized (logBuffer) {
            for (String entry : logBuffer) {
                logs.append(entry).append("\n");
            }
        }
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("NoteX Debug Logs", logs.toString());
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(this, "Logs copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    
    private void restartApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    
    private void clearLogs() {
        synchronized (logBuffer) {
            logBuffer.clear();
        }
        updateLogDisplay();
        Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show();
    }
    
    private void exportLogs() {
        // For now, just copy to clipboard with more detailed format
        copyLogsToClipboard();
    }
    
    private void showCrashDialog(String crashInfo) {
        runOnUiThread(() -> {
            crashInfoText.setVisibility(View.VISIBLE);
            crashInfoText.setText("CRASH DETECTED!\n" + crashInfo);
            Toast.makeText(this, "Crash detected! Check debug console.", Toast.LENGTH_LONG).show();
        });
    }
    
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
    
    private String getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String getAvailableMemory() {
        Runtime runtime = Runtime.getRuntime();
        long availableMemory = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
        return String.valueOf(availableMemory / (1024 * 1024));
    }
    
    private String getTotalMemory() {
        Runtime runtime = Runtime.getRuntime();
        return String.valueOf(runtime.maxMemory() / (1024 * 1024));
    }
    
    private String getAvailableStorage() {
        try {
            android.os.StatFs stat = new android.os.StatFs(getFilesDir().getPath());
            long availableBytes = stat.getAvailableBytes();
            return String.valueOf(availableBytes / (1024 * 1024));
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLogging = false;
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}