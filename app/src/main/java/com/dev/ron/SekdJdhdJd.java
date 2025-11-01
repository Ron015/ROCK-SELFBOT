package com.dev.ron;

import android.util.Base64;
import java.nio.charset.StandardCharsets;

public class SekdJdhdJd {
    static {
        System.loadLibrary("native-lib");
    }

    // Native methods (return Base64 encoded strings)
    public static native String UmeDhjdh();        
    public static native String fJdhJejehsh();     
    public static native String kdEhdJdjeDjj();
    public static native String AjdhKsH();  
    public static native String AksjsKsjdjJshdjs();
    public static native String getKEK();
    

    // Wrapper methods to get decoded values
    public static String getUmeDhjdh() {
        return UmeDhjdh();
    }

    public static String getFJdhJejehsh() {
        return fJdhJejehsh();
    }

    public static String getKdEhdJdjeDjj() {
        return kdEhdJdjeDjj();
    }

    public static String getAjdhKsH() {
        return AjdhKsH();
    }
}