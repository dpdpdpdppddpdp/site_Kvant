package com.kvant.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@Order(1)
public class DatabaseMigration implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);
    private final DataSource dataSource;

    public DatabaseMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Migration completed — DB recreated with correct nullable schema
    }

    private void migrateUsersTable(Connection conn, Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
        boolean phoneNullable = true;
        boolean hasPhone = false;

        while (rs.next()) {
            String colName = rs.getString("name");
            if ("phone".equals(colName)) {
                hasPhone = true;
                int notNull = rs.getInt("notnull");
                phoneNullable = (notNull == 0);
            }
        }
        rs.close();

        if (!hasPhone || phoneNullable) {
            logger.info("DB migration: users.phone already nullable or missing, skipping.");
            return;
        }

        logger.info("DB migration: making users.phone, first_name, last_name nullable...");

        conn.setAutoCommit(false);
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS users_new (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "first_name TEXT," +
                "last_name TEXT," +
                "phone TEXT," +
                "role TEXT NOT NULL DEFAULT 'USER'," +
                "enabled INTEGER NOT NULL DEFAULT 1," +
                "account_non_expired INTEGER NOT NULL DEFAULT 1," +
                "account_non_locked INTEGER NOT NULL DEFAULT 1," +
                "credentials_non_expired INTEGER NOT NULL DEFAULT 1," +
                "created_at TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime'))," +
                "updated_at TIMESTAMP" +
            ")");

            stmt.execute("INSERT INTO users_new " +
                "(id, username, password, email, first_name, last_name, phone, role, enabled, " +
                "account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at) " +
                "SELECT id, username, password, email, first_name, last_name, phone, role, enabled, " +
                "account_non_expired, account_non_locked, credentials_non_expired, " +
                "COALESCE(strftime('%Y-%m-%dT%H:%M:%S', created_at), strftime('%Y-%m-%dT%H:%M:%S', 'now','localtime')), " +
                "CASE WHEN updated_at IS NOT NULL THEN strftime('%Y-%m-%dT%H:%M:%S', updated_at) ELSE NULL END " +
                "FROM users");

            stmt.execute("DROP TABLE users");
            stmt.execute("ALTER TABLE users_new RENAME TO users");

            conn.commit();
            logger.info("DB migration: users table migrated successfully.");
        } catch (Exception e) {
            conn.rollback();
            logger.error("DB migration failed: {}", e.getMessage(), e);
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
