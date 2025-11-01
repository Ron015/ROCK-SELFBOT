package com.dev.ron;

import okhttp3.OkHttpClient;
public class NetworkClient {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient(); // No DoH, just basic client
        }
        return client;
    }
}