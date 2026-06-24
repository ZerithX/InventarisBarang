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
     * Menyimpan data user baru ke database.
     *
     * @param user Objek user yang akan disimpan
     * @throws SQLException Jika terjadi error pada query database
     */
//    public void save(User user) throws SQLException {
//        String sql = "INSERT INTO user (id, name, password, role) VALUES (?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, user.getId());
//            ps.setString(2, user.getName());
//            ps.setString(3, user.getPassword());
//            ps.setString(4, user.getRole().name()); // Mengambil nama enum (ADMIN / STAFF)
//
//            ps.executeUpdate();
//        }
//    }

    /**
     * Mencari user berdasarkan ID.
     *
     * @param id ID unik user (UUID)
     * @return Optional berisi User jika ditemukan, atau Optional.empty() jika tidak
     * @throws SQLException Jika terjadi error pada query database
     */
//    public Optional<User> findById(String id) throws SQLException {
//        String sql = "SELECT id, name, password, role FROM user WHERE id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, id);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    User user = new User(
//                            rs.getString("name"),
//                            rs.getString("password"),
//                            Role.valueOf(rs.getString("role")) // Konversi String ke Enum Role
//                    );
//                    user.setId(rs.getString("id"));
//                    return Optional.of(user);
//                }
//            }
//        }
//        return Optional.empty();
//    }

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

    /**
     * Mengambil semua daftar user dari database.
     *
     * @return List berisi semua objek User
     * @throws SQLException Jika terjadi error pada query database
     */
//    public List<User> findAll() throws SQLException {
//        List<User> users = new ArrayList<>();
//        String sql = "SELECT id, name, password, role FROM user";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                User user = new User(
//                        rs.getString("name"),
//                        rs.getString("password"),
//                        Role.valueOf(rs.getString("role"))
//                );
//                user.setId(rs.getString("id"));
//                users.add(user);
//            }
//        }
//        return users;
//    }

    /**
     * Memperbarui data user yang sudah ada.
     *
     * @param user Objek user berisi data baru beserta ID-nya
     * @throws SQLException Jika terjadi error pada query database
     */
//    public void update(User user) throws SQLException {
//        String sql = "UPDATE user SET name = ?, password = ?, role = ? WHERE id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, user.getName());
//            ps.setString(2, user.getPassword());
//            ps.setString(3, user.getRole().name());
//            ps.setString(4, user.getId());
//
//            ps.executeUpdate();
//        }
//    }

    /**
     * Menghapus user berdasarkan ID.
     *
     * @param id ID user yang ingin dihapus
     * @throws SQLException Jika terjadi error pada query database
     */
//    public void delete(String id) throws SQLException {
//        String sql = "DELETE FROM user WHERE id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, id);
//            ps.executeUpdate();
//        }
//    }
}
