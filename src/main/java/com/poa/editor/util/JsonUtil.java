package com.poa.editor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String readBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static <T> T parseBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return fromJson(readBody(req), clazz);
    }

    private JsonUtil() {}
}
