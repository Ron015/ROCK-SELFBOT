package com.dev.ron;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AdnhdEjdjsS {

    private static String getAESKey(Context context) {
        try {
            String key = SekdJdhdJd.getKEK();
            if (key.length() != 16) {
                return null;
            }
            return key;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(String plainText, Context context) {
        try {
            String aesKey = getAESKey(context);
            if (aesKey == null) return null;

            byte[] keyBytes = aesKey.getBytes();
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(plainText.getBytes());

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
            mac.update(iv);
            mac.update(ciphertext);
            byte[] hmac = mac.doFinal();

            byte[] finalData = new byte[iv.length + ciphertext.length + hmac.length];
            System.arraycopy(iv, 0, finalData, 0, iv.length);
            System.arraycopy(ciphertext, 0, finalData, iv.length, ciphertext.length);
            System.arraycopy(hmac, 0, finalData, iv.length + ciphertext.length, hmac.length);

            return Base64.encodeToString(finalData, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String encryptedText, Context context) {
        try {
            String aesKey = getAESKey(context);
            if (aesKey == null) return null;

            byte[] keyBytes = aesKey.getBytes();
            byte[] allBytes = Base64.decode(encryptedText, Base64.NO_WRAP);

            byte[] iv = Arrays.copyOfRange(allBytes, 0, 16);
            byte[] hmac = Arrays.copyOfRange(allBytes, allBytes.length - 32, allBytes.length);
            byte[] ciphertext = Arrays.copyOfRange(allBytes, 16, allBytes.length - 32);

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
            mac.update(iv);
            mac.update(ciphertext);
            byte[] expectedHmac = mac.doFinal();

            if (!Arrays.equals(hmac, expectedHmac)) {
                return null;
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(iv));

            return new String(cipher.doFinal(ciphertext));
        } catch (Exception e) {
            return null;
        }
    }

}