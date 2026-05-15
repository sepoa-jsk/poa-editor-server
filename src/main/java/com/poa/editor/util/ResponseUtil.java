package com.poa.editor.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtil {

    public static void success(HttpServletResponse res, Object data) throws IOException {
        write(res, HttpServletResponse.SC_OK, true, data, null);
    }

    public static void created(HttpServletResponse res, Object data) throws IOException {
        write(res, HttpServletResponse.SC_CREATED, true, data, null);
    }

    public static void noContent(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public static void error(HttpServletResponse res, int status, String message) throws IOException {
        write(res, status, false, null, message);
    }

    private static void write(HttpServletResponse res, int status, boolean success,
                              Object data, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", success);
        body.put("data", data);
        body.put("message", message);

        try (PrintWriter w = res.getWriter()) {
            w.write(JsonUtil.toJson(body));
        }
    }

    private ResponseUtil() {}
}
