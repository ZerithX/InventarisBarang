package com.inventaris.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ActivityLogger {

    /**
     * Menyimpan log aktivitas user ke dalam tabel `aktivitas_log`.
     *
     * @param idUser    ID user yang melakukan aktivitas.
     * @param username  Nama user yang melakukan aktivitas.
     * @param role      Role user (ADMIN/STAFF).
     * @param aktivitas Jenis aktivitas (LOGIN, LOGOUT, TAMBAH_BARANG, EDIT_BARANG, dsb).
     * @param detail    Keterangan detil dari aktivitas tersebut.
     */
    public static void log(String idUser, String username, String role, String aktivitas, String detail) {
        String sql = "INSERT INTO aktivitas_log (id, id_user, username, role, aktivitas, detail) VALUES (CAST(? AS UUID), CAST(? AS UUID), ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, idUser);
            ps.setString(3, username);
            ps.setString(4, role);
            ps.setString(5, aktivitas);
            ps.setString(6, detail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Gagal menulis aktivitas log: " + e.getMessage());
        }
    }
}
