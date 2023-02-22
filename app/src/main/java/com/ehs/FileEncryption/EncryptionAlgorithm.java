package com.ehs.FileEncryption;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Base64;
import android.util.Log;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionAlgorithm {
//String algorithm
    private static Key generateKey(String secretKeyString, String algorithm) throws Exception {
        // generate AES secret key from a String
        Key key = new SecretKeySpec(secretKeyString.getBytes(), algorithm);

        return key;
    }

    // encryption function
    public static byte[] encryptSMS(String secretKeyString,
                                    String msgContentString, String algorithm) {

        try {
            byte[] returnArray;

            // generate AES secret key from user input
            Key key = generateKey(secretKeyString, algorithm);

            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance(algorithm);

            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);

            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());

            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }
    }

    // decryption function
    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg, String algorithm)
            throws Exception {
        // generate AES secret key from the user input secret key
        Key key = generateKey(secretKeyString, algorithm);
        byte[] decValue = null;

        try {
            // get the cipher algorithm for AES
            Cipher c = Cipher.getInstance(algorithm);

            // specify the decryption mode
            c.init(Cipher.DECRYPT_MODE, key);

            // decrypt the message
             decValue = c.doFinal(encryptedMsg);
        } catch (Exception e){
            Log.e(TAG, "AES decryption error");
        }


        return decValue;
    }

    public static String byteToString(byte[] b){
        String result = Base64.encodeToString(b, Base64.DEFAULT);

        return result;
    }

    public static byte[] stringtoBytes(String m){
        byte[] result = Base64.decode(m, Base64.DEFAULT);

        return  result;
    }
}
