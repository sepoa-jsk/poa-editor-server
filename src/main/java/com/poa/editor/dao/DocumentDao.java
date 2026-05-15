package com.poa.editor.dao;

import com.poa.editor.util.DbUtil;
import com.poa.editor.vo.DocumentVo;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDao {

    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 본인 문서 목록 (content 제외) */
    public List<DocumentVo> findAll(String userId) throws SQLException {
        String sql = "SELECT id, doc_key, title, created_by, created_at, updated_at, is_deleted " +
                     "FROM document WHERE created_by = ? AND is_deleted = false " +
                     "ORDER BY updated_at DESC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<DocumentVo> list = new ArrayList<>();
                while (rs.next()) list.add(mapSummary(rs));
                return list;
            }
        }
    }

    /** 단건 조회 (content 포함) */
    public DocumentVo findByDocKey(String docKey, String userId) throws SQLException {
        String sql = "SELECT * FROM document WHERE doc_key = ? AND is_deleted = false";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, docKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                DocumentVo vo = mapFull(rs);
                if (!vo.createdBy.equals(userId)) return null;
                return vo;
            }
        }
    }

    public DocumentVo insert(DocumentVo vo) throws SQLException {
        String docKey = generateDocKey();
        String sql = "INSERT INTO document (doc_key, title, content, created_by) VALUES (?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, docKey);
            ps.setString(2, vo.title);
            ps.setString(3, vo.content);
            ps.setString(4, vo.createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) vo.id = rs.getLong(1);
            }
        }
        vo.docKey = docKey;
        return findByDocKey(docKey, vo.createdBy);
    }

    public DocumentVo update(String docKey, String userId, String title, String content) throws SQLException {
        String sql = "UPDATE document SET title = ?, content = ? " +
                     "WHERE doc_key = ? AND created_by = ? AND is_deleted = false";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, docKey);
            ps.setString(4, userId);
            int rows = ps.executeUpdate();
            if (rows == 0) return null;
        }
        return findByDocKey(docKey, userId);
    }

    public boolean softDelete(String docKey, String userId) throws SQLException {
        String sql = "UPDATE document SET is_deleted = true WHERE doc_key = ? AND created_by = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, docKey);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private DocumentVo mapSummary(ResultSet rs) throws SQLException {
        DocumentVo vo = new DocumentVo();
        vo.id = rs.getLong("id");
        vo.docKey = rs.getString("doc_key");
        vo.title = rs.getString("title");
        vo.createdBy = rs.getString("created_by");
        vo.createdAt = String.valueOf(rs.getTimestamp("created_at"));
        vo.updatedAt = String.valueOf(rs.getTimestamp("updated_at"));
        vo.isDeleted = rs.getBoolean("is_deleted");
        return vo;
    }

    private DocumentVo mapFull(ResultSet rs) throws SQLException {
        DocumentVo vo = mapSummary(rs);
        vo.content = rs.getString("content");
        return vo;
    }

    private static String generateDocKey() {
        StringBuilder sb = new StringBuilder(21);
        for (int i = 0; i < 21; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
