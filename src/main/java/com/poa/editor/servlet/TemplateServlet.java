package com.poa.editor.servlet;

import com.poa.editor.service.TemplateService;
import com.poa.editor.util.JsonUtil;
import com.poa.editor.util.ResponseUtil;
import com.poa.editor.vo.TemplateVo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class TemplateServlet extends HttpServlet {

    private final TemplateService service = new TemplateService();

    private String userId(HttpServletRequest req) {
        String uid = req.getHeader("Poa-User-Id");
        return (uid == null || uid.isBlank()) ? "anonymous" : uid.trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                ResponseUtil.success(res, service.list(uid));
            } else {
                Long id = parseId(path.substring(1));
                if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
                TemplateVo vo = service.get(id);
                if (vo == null) ResponseUtil.error(res, 404, "템플릿을 찾을 수 없습니다.");
                else ResponseUtil.success(res, vo);
            }
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        try {
            TemplateVo input = JsonUtil.parseBody(req, TemplateVo.class);
            TemplateVo vo = service.create(uid, input);
            ResponseUtil.created(res, vo);
        } catch (SecurityException e) {
            ResponseUtil.error(res, 403, e.getMessage());
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            ResponseUtil.error(res, 400, "ID가 필요합니다.");
            return;
        }
        try {
            String rest = path.substring(1);
            if (rest.endsWith("/move")) {
                Long id = parseId(rest.substring(0, rest.length() - "/move".length()));
                if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
                MoveBody mv = JsonUtil.parseBody(req, MoveBody.class);
                TemplateVo vo = service.move(id, mv.folderId, mv.orderIndex);
                if (vo == null) ResponseUtil.error(res, 404, "템플릿을 찾을 수 없습니다.");
                else ResponseUtil.success(res, vo);
                return;
            }
            Long id = parseId(rest);
            if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
            TemplateVo input = JsonUtil.parseBody(req, TemplateVo.class);
            TemplateVo vo = service.update(id, uid, input.name, input.content,
                                            input.folderId, input.isPublic, input.orderIndex);
            if (vo == null) ResponseUtil.error(res, 404, "템플릿을 찾을 수 없습니다.");
            else ResponseUtil.success(res, vo);
        } catch (SecurityException e) {
            ResponseUtil.error(res, 403, e.getMessage());
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            ResponseUtil.error(res, 400, "ID가 필요합니다.");
            return;
        }
        try {
            String rest = path.substring(1);
            if ("temp".equals(rest)) {
                int n = service.deleteTemp();
                ResponseUtil.success(res, n);
                return;
            }
            Long id = parseId(rest);
            if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
            boolean ok = service.delete(id, uid);
            if (!ok) ResponseUtil.error(res, 404, "템플릿을 찾을 수 없습니다.");
            else ResponseUtil.noContent(res);
        } catch (SecurityException e) {
            ResponseUtil.error(res, 403, e.getMessage());
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    private Long parseId(String s) {
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; }
    }

    static class MoveBody {
        public Long folderId;
        public int orderIndex;
    }
}
