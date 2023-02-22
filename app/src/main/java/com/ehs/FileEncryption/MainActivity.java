package com.ehs.FileEncryption;

import static com.ehs.FileEncryption.EncryptionAlgorithm.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    TextInputEditText textInputEditText_ruta, textInputEditText_clave;

    TextInputLayout textInputLayout_CLAVE, textInputLayout_ruta;

    EditText text_area;

    RadioButton rEncriptar, rDesencriptar, rAES, rDES;
    RadioGroup radioGroup;

    Button btEncDes;

    String name = "";
    String txt_area = "";
    private static final int CREATE_FILE_REQUEST_CODE = 1; // código de solicitud para la actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // solicitar permiso de lectura/escritura
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        textInputEditText_ruta = findViewById(R.id.txtInputLayout_ruta);
        textInputEditText_clave = findViewById(R.id.txtInputLayout_clave);
        textInputLayout_ruta = findViewById(R.id.txtInputLayout);
        textInputLayout_CLAVE = findViewById(R.id.txtInputLayout2);
        text_area = findViewById(R.id.txt_area);

        //radio buttons
        rEncriptar = findViewById(R.id.rEncriptar);
        rDesencriptar = findViewById(R.id.rDesencriptar);
        rAES = findViewById(R.id.rAES);
        rDES = findViewById(R.id.rDES);
        radioGroup = findViewById(R.id.radioGroup);

        rEncriptar.setChecked(true);
        rAES.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                validarRB();
            }
        });

        //button for encrypt or decrypt
        btEncDes = findViewById(R.id.btEncDes);
        btEncDes.setOnClickListener(view -> encryptORdescrypt());

        textInputLayout_ruta.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodosArchivos.showFileChooser(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            File file = new File(path);
            name = file.getName();
            textInputEditText_ruta.setText(path);
            text_area.setText(MetodosArchivos.readFromFile(MainActivity.this, uri));
        }

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                MetodosArchivos.writeToFile(MainActivity.this, uri, txt_area);
                cleanRutaClave();
                text_area.setText(txt_area);
            }
        }
    }

    private void encryptORdescrypt() {
        if (validarClave_Ruta()) {
            String key = textInputEditText_clave.getText().toString();
            String m = text_area.getText().toString();

            if (rEncriptar.isChecked()) {
                if (rAES.isChecked()) {
                    byte[] b = encryptSMS(key, m, "AES");
                    txt_area = byteToString(b);
                    name = "enc_AES_" + name;
                    MetodosArchivos.createFile(MainActivity.this, name);
                } else {
                    byte[] b = encryptSMS(key, m, "DES");
                    txt_area = byteToString(b);
                    name = "enc_DES_" + name;
                    MetodosArchivos.createFile(MainActivity.this, name);
                }
            } else {
                if (rAES.isChecked()) {
                    byte[] b = stringtoBytes(m);
                    try {
                        b = decryptSMS(key, b, "AES");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (b != null) {
                        name = "des_AES_" + name;
                        MetodosArchivos.createFile(MainActivity.this, name);
                        txt_area = new String(b);
                    } else {
                        Toast.makeText(this, "Ocurrio un error al desencriptar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    byte[] b = stringtoBytes(m);
                    try {
                        b = decryptSMS(key, b, "DES");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (b != null) {
                        name = "des_DES_" + name;
                        MetodosArchivos.createFile(MainActivity.this, name);
                        txt_area = new String(b);
                    } else {
                        Toast.makeText(this, "Ocurrio un error al desencriptar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void validarRB() {
        if (rEncriptar.isChecked()) {
            btEncDes.setText("Encriptar");
        } else {
            btEncDes.setText("Desencriptar");
        }
    }

    private void cleanRutaClave() {
        textInputEditText_ruta.setText("");
        textInputEditText_clave.setText("");
    }

    private boolean validarClave_Ruta() {
        boolean band = true;
        if (textInputEditText_ruta.getText().toString().isEmpty()) {
//            textInputLayout_ruta.setError("Elegir un archivo txt");
            textInputEditText_ruta.requestFocus();
            Toast.makeText(this, "Debe seleccionar un archivo valido", Toast.LENGTH_SHORT).show();
            band = false;
        }

        if (rAES.isChecked()) {
            if (textInputEditText_clave.length() == 16) {
                textInputLayout_CLAVE.setErrorEnabled(false);
            } else {
                Toast.makeText(this, "La llave secreta debe contener 16 caracteres", Toast.LENGTH_SHORT).show();
                textInputLayout_CLAVE.setError("Solo 16 caracteres");
                band = false;
            }
        } else {
            if (textInputEditText_clave.length() == 8) {
                textInputLayout_CLAVE.setErrorEnabled(false);
            } else {
                Toast.makeText(this, "La llave secreta debe contener 8 caracteres", Toast.LENGTH_SHORT).show();
                textInputLayout_CLAVE.setError("Solo 8 caracteres");
                band = false;
            }
        }
        return band;
    }
}