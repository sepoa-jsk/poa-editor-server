package com.poa.editor.servlet;

import com.poa.editor.service.FolderService;
import com.poa.editor.util.JsonUtil;
import com.poa.editor.util.ResponseUtil;
import com.poa.editor.vo.FolderVo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class FolderServlet extends HttpServlet {

    private final FolderService service = new FolderService();

    private String userId(HttpServletRequest req) {
        String uid = req.getHeader("Poa-User-Id");
        return (uid == null || uid.isBlank()) ? "anonymous" : uid.trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        try {
            ResponseUtil.success(res, service.list(uid));
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uid = userId(req);
        try {
            FolderVo input = JsonUtil.parseBody(req, FolderVo.class);
            FolderVo vo = service.create(uid, input);
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
            Long id = parseId(path.substring(1));
            if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
            FolderVo input = JsonUtil.parseBody(req, FolderVo.class);
            FolderVo vo = service.update(id, uid, input.parentId, input.name,
                                          input.isPublic, input.orderIndex);
            if (vo == null) ResponseUtil.error(res, 404, "폴더를 찾을 수 없습니다.");
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
            Long id = parseId(path.substring(1));
            if (id == null) { ResponseUtil.error(res, 400, "잘못된 ID"); return; }
            boolean ok = service.delete(id, uid);
            if (!ok) ResponseUtil.error(res, 404, "폴더를 찾을 수 없습니다.");
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
}
