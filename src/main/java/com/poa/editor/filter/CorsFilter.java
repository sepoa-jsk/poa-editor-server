package com.poa.editor.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class CorsFilter implements Filter {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:5173",
            "http://192.168.50.120:5173",
            "https://sepoa-jsk.github.io"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 불필요
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;

        String origin = httpReq.getHeader("Origin");
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            httpRes.setHeader("Access-Control-Allow-Origin", origin);
            httpRes.setHeader("Access-Control-Allow-Credentials", "true");
        }
        httpRes.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpRes.setHeader("Access-Control-Allow-Headers", "Content-Type, Poa-User-Id");
        httpRes.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            httpRes.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // 정리 불필요
    }
}
