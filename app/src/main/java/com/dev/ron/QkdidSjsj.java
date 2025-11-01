package com.dev.ron;

import android.content.Context;
import android.util.Log;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileOutputStream;

public class QkdidSjsj {

    private static Thread pythonThread;
    private static volatile boolean isRunning = false;
    private static PyObject pyModule;
    private static final Object lock = new Object();

    public static void StartS(Context context) {
        synchronized (lock) {
            if (isRunning) return;
            isRunning = true;
        }

        pythonThread = new Thread(() -> {
            try {
                synchronized (lock) {
                    if (!Python.isStarted()) {
                        Python.start(new AndroidPlatform(context));
                    }
                }

                Python py = Python.getInstance();
                pyModule = py.getModule("ron");
                pyModule.callAttr("main");

            } catch (Exception e) {
                LyedYdhdUd.addConsoleLog(context, "ROCK Error: " + Log.getStackTraceString(e));
            } finally {
                synchronized (lock) {
                    isRunning = false;
                    pyModule = null;
                }
            }
        });
        pythonThread.start();
    }

    public static void StopS(Context context) {
        synchronized (lock) {
            if (!isRunning) return;
            isRunning = false;
        }
    
        new Thread(() -> {
            try {
                // Create empty stop flag file at correct location
                try {
                    context.openFileOutput("bot_stop.flag", Context.MODE_PRIVATE).close();
                    LyedYdhdUd.addConsoleLog(context, "🔄 ROCK Stopping....");
                } catch (Exception e) {
                    LyedYdhdUd.addConsoleLog(context, "ROCK Stopping failed: " + Log.getStackTraceString(e));
                }
    
                // Wait up to 5 sec for Python thread to stop
                if (pythonThread != null) {
                    pythonThread.join(5000);
                    if (pythonThread.isAlive()) {
                        pythonThread.interrupt();
                        LyedYdhdUd.addConsoleLog(context, "Server Stopped.");
                    } else {
                        LyedYdhdUd.addConsoleLog(context, "Server stopped cleanly.");
                    }
                }
    
            } catch (Exception e) {
                LyedYdhdUd.addConsoleLog(context, "Stop failed: " + Log.getStackTraceString(e));
            }
        }).start();
    }

    public static boolean isRunning() {
        synchronized (lock) {
            return isRunning;
        }
    }
}