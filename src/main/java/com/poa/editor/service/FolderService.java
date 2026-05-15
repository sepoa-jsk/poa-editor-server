package com.poa.editor.service;

import com.poa.editor.dao.FolderDao;
import com.poa.editor.dao.TemplateDao;
import com.poa.editor.vo.FolderVo;

import java.sql.SQLException;
import java.util.List;

public class FolderService {

    private final FolderDao dao = new FolderDao();
    private final TemplateDao templateDao = new TemplateDao();

    public List<FolderVo> list(String userId) throws SQLException {
        return dao.findAll(userId);
    }

    public FolderVo create(String userId, FolderVo input) throws SQLException {
        if (input.isPublic && !TemplateService.isAdmin(userId)) {
            throw new SecurityException("공용 폴더는 관리자만 생성할 수 있습니다.");
        }
        input.createdBy = userId;
        return dao.insert(input);
    }

    public FolderVo update(Long id, String userId, Long parentId, String name,
                           boolean isPublic, int orderIndex) throws SQLException {
        FolderVo existing = dao.findById(id);
        if (existing == null) return null;
        if (existing.isPublic && !TemplateService.isAdmin(userId)) {
            throw new SecurityException("공용 폴더는 관리자만 수정할 수 있습니다.");
        }
        if (!existing.isPublic && existing.createdBy != null && !existing.createdBy.equals(userId)) {
            throw new SecurityException("본인 폴더만 수정할 수 있습니다.");
        }
        if (isPublic && !TemplateService.isAdmin(userId)) isPublic = false;
        return dao.update(id, parentId, name, isPublic, orderIndex);
    }

    public boolean delete(Long id, String userId) throws SQLException {
        FolderVo existing = dao.findById(id);
        if (existing == null) return false;
        if (existing.isPublic && !TemplateService.isAdmin(userId)) {
            throw new SecurityException("공용 폴더는 관리자만 삭제할 수 있습니다.");
        }
        if (!existing.isPublic && existing.createdBy != null && !existing.createdBy.equals(userId)) {
            throw new SecurityException("본인 폴더만 삭제할 수 있습니다.");
        }
        dao.deleteRecursive(id);
        return true;
    }

    /** Admin cleanup: 비기본 폴더 전체 삭제 + 기본 폴더 보장 */
    public void cleanup() throws SQLException {
        List<Long> nonDefault = dao.findNonDefaultIds();
        for (Long id : nonDefault) {
            templateDao.deleteByFolderId(id);
        }
        templateDao.deleteTemp();
        dao.deleteNonDefault();
        dao.ensureDefaultFolders();
    }
}
