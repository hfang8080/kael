package com.internet.common.lock.services;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * // TODO update to connection pool.
 * @author Kael He
 */
public class MysqlService {

    static MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        dataSource.setUser("tester");
        dataSource.setPassword("tester");
        dataSource.setServerName("127.0.0.1");
        dataSource.setDatabaseName("lock");
    }

    public int update(String sql) {
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            count = stmt.executeUpdate(sql);
//            conn.commit();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }

    public ResultSet query(String sql) {
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rs;
    }

}
