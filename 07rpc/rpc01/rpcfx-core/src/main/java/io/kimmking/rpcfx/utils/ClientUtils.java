package io.kimmking.rpcfx.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ClientUtils {

    private static final OkHttpClient client = new OkHttpClient();

    public static Response execute(Request request) throws IOException {
        return client.newCall(request).execute();
    }

}
