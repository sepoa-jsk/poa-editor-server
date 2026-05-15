package com.poa.editor.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    private static final String URL =
            "jdbc:mariadb://devmariadb.sepoa.co.kr:3306/poasrm_restd?characterEncoding=UTF-8&serverTimezone=Asia/Seoul";
    private static final String USER = "poasrm_restd";
    private static final String PASS = "poasrm_restd01";

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MariaDB JDBC Driver 로드 실패", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private DbUtil() {}
}
