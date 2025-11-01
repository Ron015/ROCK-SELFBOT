package com.dev.ron;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DeKfhDjjjddh {

    private static final String ENCRYPTION_KEY = SekdJdhdJd.getKdEhdJdjeDjj(); // Server wali key
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    

    private static byte[] getFixedKey(String key, Context context) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] fixed = md.digest(key.getBytes("UTF-8"));
        return fixed;
    }

    public static String decrypt(String encrypted, Context context) {
        try {
            
            if (!encrypted.contains(":")) {
                return null;
            }

            String[] parts = encrypted.split(":");
            if (parts.length != 2) {
                return null;
            }

            byte[] iv = hexStringToByteArray(parts[0]);
            byte[] encryptedBytes = hexStringToByteArray(parts[1]);

            SecretKeySpec keySpec = new SecretKeySpec(getFixedKey(ENCRYPTION_KEY, context), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] original = cipher.doFinal(encryptedBytes);
            String decryptedText = new String(original, "UTF-8");

            return decryptedText;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}