package com.poa.editor.servlet;

import com.poa.editor.service.DocumentService;
import com.poa.editor.util.JsonUtil;
import com.poa.editor.util.ResponseUtil;
import com.poa.editor.vo.DocumentVo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class DocumentServlet extends HttpServlet {

    private final DocumentService service = new DocumentService();

    private String userId(HttpServletRequest req) {
        String uid = req.getHeader("Poa-User-Id");
        return (uid == null || uid.isBlank()) ? null : uid.trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        if (uid == null) { ResponseUtil.error(res, 401, "인증이 필요합니다."); return; }
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                ResponseUtil.success(res, service.list(uid));
            } else {
                String docKey = path.substring(1);
                DocumentVo vo = service.get(docKey, uid);
                if (vo == null) ResponseUtil.error(res, 404, "문서를 찾을 수 없습니다.");
                else ResponseUtil.success(res, vo);
            }
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        if (uid == null) { ResponseUtil.error(res, 401, "인증이 필요합니다."); return; }
        try {
            DocBody body = JsonUtil.parseBody(req, DocBody.class);
            DocumentVo vo = service.create(uid, body.title, body.content);
            ResponseUtil.created(res, vo);
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        if (uid == null) { ResponseUtil.error(res, 401, "인증이 필요합니다."); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            ResponseUtil.error(res, 400, "docKey가 필요합니다.");
            return;
        }
        try {
            String docKey = path.substring(1);
            DocBody body = JsonUtil.parseBody(req, DocBody.class);
            DocumentVo vo = service.update(docKey, uid, body.title, body.content);
            if (vo == null) ResponseUtil.error(res, 404, "문서를 찾을 수 없습니다.");
            else ResponseUtil.success(res, vo);
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        if (uid == null) { ResponseUtil.error(res, 401, "인증이 필요합니다."); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            ResponseUtil.error(res, 400, "docKey가 필요합니다.");
            return;
        }
        try {
            String docKey = path.substring(1);
            boolean ok = service.delete(docKey, uid);
            if (!ok) ResponseUtil.error(res, 404, "문서를 찾을 수 없습니다.");
            else ResponseUtil.noContent(res);
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    static class DocBody {
        public String title;
        public String content;
    }
}
