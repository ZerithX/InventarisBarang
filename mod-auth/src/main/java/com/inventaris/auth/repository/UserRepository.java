package com.inventaris.auth.repository;

import com.inventaris.auth.domain.Role;
import com.inventaris.auth.domain.User;
import com.inventaris.core.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    /**
     * Mencari user berdasarkan nama (username). Sangat berguna untuk kebutuhan login.
     *
     * @param name Nama/Username user
     * @return Optional berisi User jika ditemukan, atau Optional.empty() jika tidak
     * @throws SQLException Jika terjadi error pada query database
     */
    public Optional<User> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, password, role FROM users WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("name"),
                            rs.getString("password"),
                            Role.valueOf(rs.getString("role"))
                    );
                    user.setId(rs.getString("id"));
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}
