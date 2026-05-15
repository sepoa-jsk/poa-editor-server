package com.poa.editor.dao;

import com.poa.editor.util.DbUtil;
import com.poa.editor.vo.FolderVo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDao {

    /** 공용 + 본인 개인 폴더 + 비임시 템플릿 카운트 */
    public List<FolderVo> findAll(String userId) throws SQLException {
        String sql = "SELECT f.*, " +
                     "  (SELECT COUNT(*) FROM template t " +
                     "     WHERE t.folder_id = f.id AND t.is_temp = false) AS template_count " +
                     "FROM template_folder f " +
                     "WHERE f.is_public = true OR f.created_by = ? " +
                     "ORDER BY f.order_index ASC, f.name ASC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<FolderVo> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    public FolderVo findById(Long id) throws SQLException {
        String sql = "SELECT f.*, 0 AS template_count FROM template_folder f WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public FolderVo insert(FolderVo vo) throws SQLException {
        String sql = "INSERT INTO template_folder (parent_id, name, is_public, created_by, order_index) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (vo.parentId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, vo.parentId);
            ps.setString(2, vo.name);
            ps.setBoolean(3, vo.isPublic);
            if (vo.isPublic) ps.setNull(4, Types.VARCHAR); else ps.setString(4, vo.createdBy);
            ps.setInt(5, vo.orderIndex);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) vo.id = rs.getLong(1);
            }
        }
        return findById(vo.id);
    }

    public FolderVo update(Long id, Long parentId, String name, boolean isPublic, int orderIndex) throws SQLException {
        String sql = "UPDATE template_folder SET parent_id = ?, name = ?, is_public = ?, order_index = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (parentId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, parentId);
            ps.setString(2, name);
            ps.setBoolean(3, isPublic);
            ps.setInt(4, orderIndex);
            ps.setLong(5, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    /** 하위 폴더/템플릿 재귀 삭제 */
    public void deleteRecursive(Long id) throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                deleteRecursiveInternal(c, id);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void deleteRecursiveInternal(Connection c, Long id) throws SQLException {
        // 하위 폴더 먼저 재귀 삭제
        List<Long> childIds = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM template_folder WHERE parent_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) childIds.add(rs.getLong(1));
            }
        }
        for (Long cid : childIds) deleteRecursiveInternal(c, cid);

        // 하위 템플릿 삭제
        try (PreparedStatement ps = c.prepareStatement(
                "DELETE FROM template WHERE folder_id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }

        // 자기 자신 삭제
        try (PreparedStatement ps = c.prepareStatement(
                "DELETE FROM template_folder WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public int countTemplates(Long folderId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM template WHERE folder_id = ? AND is_temp = false";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, folderId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** 비기본 폴더 ID 목록 (cleanup용) */
    public List<Long> findNonDefaultIds() throws SQLException {
        String sql = "SELECT id FROM template_folder WHERE name NOT IN ('공용 템플릿', '내 템플릿')";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Long> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getLong(1));
            return ids;
        }
    }

    public int deleteNonDefault() throws SQLException {
        String sql = "DELETE FROM template_folder WHERE name NOT IN ('공용 템플릿', '내 템플릿')";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    public void ensureDefaultFolders() throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            ensureFolder(c, "공용 템플릿", true, 0);
            ensureFolder(c, "내 템플릿", false, 1);
        }
    }

    private void ensureFolder(Connection c, String name, boolean isPublic, int order) throws SQLException {
        try (PreparedStatement check = c.prepareStatement(
                "SELECT COUNT(*) FROM template_folder WHERE name = ?")) {
            check.setString(1, name);
            try (ResultSet rs = check.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) return;
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO template_folder (name, is_public, order_index) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setBoolean(2, isPublic);
            ps.setInt(3, order);
            ps.executeUpdate();
        }
    }

    private FolderVo map(ResultSet rs) throws SQLException {
        FolderVo vo = new FolderVo();
        vo.id = rs.getLong("id");
        long pid = rs.getLong("parent_id");
        vo.parentId = rs.wasNull() ? null : pid;
        vo.name = rs.getString("name");
        vo.isPublic = rs.getBoolean("is_public");
        vo.createdBy = rs.getString("created_by");
        vo.orderIndex = rs.getInt("order_index");
        vo.createdAt = String.valueOf(rs.getTimestamp("created_at"));
        vo.updatedAt = String.valueOf(rs.getTimestamp("updated_at"));
        try { vo.templateCount = rs.getInt("template_count"); } catch (SQLException ignore) {}
        return vo;
    }
}
