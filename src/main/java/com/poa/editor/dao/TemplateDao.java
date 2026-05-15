package com.poa.editor.dao;

import com.poa.editor.util.DbUtil;
import com.poa.editor.vo.TemplateVo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateDao {

    /** 공용 + 본인 개인 템플릿 목록 (content 제외) */
    public List<TemplateVo> findAll(String userId) throws SQLException {
        String sql = "SELECT id, folder_id, name, is_public, is_temp, created_by, order_index, " +
                     "created_at, updated_at " +
                     "FROM template " +
                     "WHERE (is_public = true OR created_by = ?) " +
                     "ORDER BY order_index ASC, name ASC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<TemplateVo> list = new ArrayList<>();
                while (rs.next()) list.add(mapSummary(rs));
                return list;
            }
        }
    }

    /** 단건 조회 (content 포함) */
    public TemplateVo findById(Long id) throws SQLException {
        String sql = "SELECT * FROM template WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFull(rs) : null;
            }
        }
    }

    public TemplateVo insert(TemplateVo vo) throws SQLException {
        String sql = "INSERT INTO template (folder_id, name, content, is_public, is_temp, created_by, order_index) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (vo.folderId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, vo.folderId);
            ps.setString(2, vo.name);
            ps.setString(3, vo.content);
            ps.setBoolean(4, vo.isPublic);
            ps.setBoolean(5, vo.isTemp);
            if (vo.isPublic) ps.setNull(6, Types.VARCHAR); else ps.setString(6, vo.createdBy);
            ps.setInt(7, vo.orderIndex);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) vo.id = rs.getLong(1);
            }
        }
        return findById(vo.id);
    }

    public TemplateVo update(Long id, String name, String content, Long folderId, boolean isPublic, int orderIndex) throws SQLException {
        String sql = "UPDATE template SET name = ?, content = ?, folder_id = ?, is_public = ?, order_index = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, content);
            if (folderId == null) ps.setNull(3, Types.BIGINT); else ps.setLong(3, folderId);
            ps.setBoolean(4, isPublic);
            ps.setInt(5, orderIndex);
            ps.setLong(6, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean delete(Long id, String userId, boolean isAdmin) throws SQLException {
        String sql = isAdmin
                ? "DELETE FROM template WHERE id = ?"
                : "DELETE FROM template WHERE id = ? AND (created_by = ? OR is_public = false AND created_by = ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     isAdmin ? sql : "DELETE FROM template WHERE id = ? AND created_by = ? AND is_public = false")) {
            ps.setLong(1, id);
            if (!isAdmin) {
                ps.setString(2, userId);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public TemplateVo move(Long id, Long folderId, int orderIndex) throws SQLException {
        String sql = "UPDATE template SET folder_id = ?, order_index = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (folderId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, folderId);
            ps.setInt(2, orderIndex);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    /** 임시 데이터 삭제 + 비기본 폴더 소속 템플릿 삭제 */
    public int deleteTemp() throws SQLException {
        String sql = "DELETE FROM template " +
                     "WHERE is_temp = true " +
                     "   OR name LIKE 'preview_%' " +
                     "   OR name LIKE '임시_%' " +
                     "   OR name LIKE '__%' " +
                     "   OR folder_id IN (" +
                     "       SELECT id FROM (" +
                     "           SELECT id FROM template_folder WHERE name NOT IN ('공용 템플릿', '내 템플릿')" +
                     "       ) AS x" +
                     "   )";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    public int deleteByFolderId(Long folderId) throws SQLException {
        String sql = "DELETE FROM template WHERE folder_id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, folderId);
            return ps.executeUpdate();
        }
    }

    private TemplateVo mapSummary(ResultSet rs) throws SQLException {
        TemplateVo vo = new TemplateVo();
        vo.id = rs.getLong("id");
        long fid = rs.getLong("folder_id");
        vo.folderId = rs.wasNull() ? null : fid;
        vo.name = rs.getString("name");
        vo.isPublic = rs.getBoolean("is_public");
        vo.isTemp = rs.getBoolean("is_temp");
        vo.createdBy = rs.getString("created_by");
        vo.orderIndex = rs.getInt("order_index");
        vo.createdAt = String.valueOf(rs.getTimestamp("created_at"));
        vo.updatedAt = String.valueOf(rs.getTimestamp("updated_at"));
        return vo;
    }

    private TemplateVo mapFull(ResultSet rs) throws SQLException {
        TemplateVo vo = mapSummary(rs);
        vo.content = rs.getString("content");
        return vo;
    }
}
