package com.poa.editor.servlet;

import com.poa.editor.service.FolderService;
import com.poa.editor.util.ResponseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AdminServlet extends HttpServlet {

    private final FolderService folderService = new FolderService();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        if (path == null || !"/cleanup".equals(path)) {
            ResponseUtil.error(res, 404, "지원하지 않는 경로입니다.");
            return;
        }
        try {
            folderService.cleanup();
            ResponseUtil.noContent(res);
        } catch (SQLException e) {
            ResponseUtil.error(res, 500, "DB 오류: " + e.getMessage());
        }
    }
}
