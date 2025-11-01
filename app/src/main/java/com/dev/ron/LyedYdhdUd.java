package com.dev.ron;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class LyedYdhdUd {

    private static final String CMD_LOG = "cmd_logs.json";
    private static final String CON_LOG = "console_logs.txt";

    // --- COMMAND LOG SYSTEM ---

    public static void addLog(Context context, String command, String username, String server, String channel, String datetime) {
        try {
            JSONArray oldArray = getLogs(context);
            JSONArray newArray = new JSONArray();

            JSONObject newLog = new JSONObject();
            newLog.put("command", command);
            newLog.put("username", username);
            newLog.put("server", server);
            newLog.put("channel", channel);
            newLog.put("datetime", datetime);

            newArray.put(newLog);

            for (int i = 0; i < oldArray.length(); i++) {
                newArray.put(oldArray.getJSONObject(i));
            }

            FileOutputStream fos = context.openFileOutput(CMD_LOG, Context.MODE_PRIVATE);
            fos.write(newArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteLog(Context context, int index) {
        try {
            JSONArray logArray = getLogs(context);
            JSONArray updatedArray = new JSONArray();

            for (int i = 0; i < logArray.length(); i++) {
                if (i != index) {
                    updatedArray.put(logArray.getJSONObject(i));
                }
            }

            FileOutputStream fos = context.openFileOutput(CMD_LOG, Context.MODE_PRIVATE);
            fos.write(updatedArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearLogs(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(CMD_LOG, Context.MODE_PRIVATE);
            fos.write("[]".getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getLogs(Context context) {
        try {
            FileInputStream fis = context.openFileInput(CMD_LOG);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            return new JSONArray(json.toString());
        } catch (Exception e) {
            return new JSONArray(); // Default empty
        }
    }

    // --- CONSOLE LOG SYSTEM ---

    public static void addConsoleLog(Context context, String message) {
        try {
            FileOutputStream fos = context.openFileOutput(CON_LOG, Context.MODE_APPEND);
            fos.write((message + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConsoleLogs(String logs, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(CON_LOG, Context.MODE_PRIVATE);
            fos.write(logs.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void trimConsoleLogFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(CON_LOG);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            LinkedList<String> lines = new LinkedList<>();
            String line;
    
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
    
            reader.close();
            fis.close();
    
            // Only trim if more than 500 lines
            if (lines.size() <= 500) {
                return; // Do nothing if 500 or less
            }
    
            // Keep only last 500
            while (lines.size() > 500) {
                lines.removeFirst();
            }
    
            // Now overwrite with last 500 lines
            FileOutputStream fos = context.openFileOutput(CON_LOG, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
    
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
    
            writer.flush();
            writer.close();
            fos.close();
    
        } catch (Exception e) {
            // Silent fail, as per your preference
        }
    }

    public static String getConsoleLogs(Context context) {
        try {
            FileInputStream fis = context.openFileInput(CON_LOG);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder logBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                logBuilder.append(line).append("\n");
            }
            reader.close();
            return logBuilder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static void clearConsoleLogs(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(CON_LOG, Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}