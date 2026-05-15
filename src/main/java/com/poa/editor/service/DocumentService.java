package com.poa.editor.service;

import com.poa.editor.dao.DocumentDao;
import com.poa.editor.vo.DocumentVo;

import java.sql.SQLException;
import java.util.List;

public class DocumentService {

    private final DocumentDao dao = new DocumentDao();

    public List<DocumentVo> list(String userId) throws SQLException {
        return dao.findAll(userId);
    }

    public DocumentVo get(String docKey, String userId) throws SQLException {
        return dao.findByDocKey(docKey, userId);
    }

    public DocumentVo create(String userId, String title, String content) throws SQLException {
        String safeTitle = (title == null || title.isBlank()) ? "새 문서" : title.trim();
        DocumentVo vo = new DocumentVo();
        vo.title = safeTitle;
        vo.content = content == null ? "" : content;
        vo.createdBy = userId;
        return dao.insert(vo);
    }

    public DocumentVo update(String docKey, String userId, String title, String content) throws SQLException {
        String safeTitle = (title == null || title.isBlank()) ? "새 문서" : title.trim();
        return dao.update(docKey, userId, safeTitle, content == null ? "" : content);
    }

    public boolean delete(String docKey, String userId) throws SQLException {
        return dao.softDelete(docKey, userId);
    }
}
