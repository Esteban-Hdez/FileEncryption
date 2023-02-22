package com.ehs.FileEncryption;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class MetodosArchivos {

    private static final int CREATE_FILE_REQUEST_CODE = 1; // código de solicitud para la actividad

    // Metodo para crear un archivo
    public static void createFile(Activity activity, String name) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, name);
        activity.startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    // Metodo que muestra un cuadro de diálogo para seleccionar un archivo de texto plano desde la memoria del dispositivo
    public static void showFileChooser(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar Archivo"), 100);
        } catch (Exception e) {
            Toast.makeText(activity, "Please install a file manager", Toast.LENGTH_SHORT).show();
        }
    }
    // Metodo para leer un archivo txt
    public static String readFromFile(Activity activity, Uri uri) {
        String result = null;
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String receiveString = "";
                int i = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (i == 0) stringBuilder.append(receiveString);
                    else stringBuilder.append("\n").append(receiveString);
                    i++;
                }
                inputStream.close();
                result = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Metodo para escribir un String en un archivo txt
    public static void writeToFile(Activity activity, Uri uri, String content) {
        try {
            OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
