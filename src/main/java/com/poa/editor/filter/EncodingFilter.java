package com.poa.editor.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 불필요
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        // 요청 바디 파싱용 인코딩만 설정.
        // 응답 인코딩은 각 Servlet (ResponseUtil) 에서 명시적으로 설정한다.
        // 여기서 res.setCharacterEncoding 을 호출하면 Tomcat DefaultServlet 이
        // 정적 파일(JS/CSS)을 character 모드로 서빙하면서 플랫폼 기본 인코딩
        // (Windows: MS949) 으로 읽어 UTF-8 로 재인코딩 → 한글 문자 깨짐.
        req.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // 정리 불필요
    }
}
