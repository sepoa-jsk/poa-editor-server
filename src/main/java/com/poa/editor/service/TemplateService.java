package com.poa.editor.service;

import com.poa.editor.dao.TemplateDao;
import com.poa.editor.vo.TemplateVo;

import java.sql.SQLException;
import java.util.List;

public class TemplateService {

    private final TemplateDao dao = new TemplateDao();

    public static boolean isAdmin(String userId) {
        return "admin".equals(userId);
    }

    public List<TemplateVo> list(String userId) throws SQLException {
        return dao.findAll(userId);
    }

    public TemplateVo get(Long id) throws SQLException {
        return dao.findById(id);
    }

    public TemplateVo create(String userId, TemplateVo input) throws SQLException {
        if (input.isPublic && !isAdmin(userId)) {
            throw new SecurityException("공용 템플릿은 관리자만 생성할 수 있습니다.");
        }
        input.createdBy = userId;
        if (input.content == null) input.content = "";
        return dao.insert(input);
    }

    public TemplateVo update(Long id, String userId, String name, String content,
                              Long folderId, boolean isPublic, int orderIndex) throws SQLException {
        TemplateVo existing = dao.findById(id);
        if (existing == null) return null;
        if (existing.isPublic && !isAdmin(userId)) {
            throw new SecurityException("공용 템플릿은 관리자만 수정할 수 있습니다.");
        }
        if (!existing.isPublic && existing.createdBy != null && !existing.createdBy.equals(userId)) {
            throw new SecurityException("본인 템플릿만 수정할 수 있습니다.");
        }
        if (isPublic && !isAdmin(userId)) isPublic = false;
        return dao.update(id, name, content == null ? existing.content : content,
                          folderId, isPublic, orderIndex);
    }

    public boolean delete(Long id, String userId) throws SQLException {
        TemplateVo existing = dao.findById(id);
        if (existing == null) return false;
        if (existing.isPublic && !isAdmin(userId)) {
            throw new SecurityException("공용 템플릿은 관리자만 삭제할 수 있습니다.");
        }
        if (!existing.isPublic && existing.createdBy != null && !existing.createdBy.equals(userId)) {
            throw new SecurityException("본인 템플릿만 삭제할 수 있습니다.");
        }
        return dao.delete(id, userId, isAdmin(userId));
    }

    public TemplateVo move(Long id, Long folderId, int orderIndex) throws SQLException {
        return dao.move(id, folderId, orderIndex);
    }

    public int deleteTemp() throws SQLException {
        return dao.deleteTemp();
    }
}
