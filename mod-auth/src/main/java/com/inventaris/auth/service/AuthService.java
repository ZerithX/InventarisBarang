package com.inventaris.auth.service;

import com.inventaris.auth.domain.User;
import com.inventaris.auth.repository.UserRepository;
import com.inventaris.core.exception.AuthException;

import java.sql.SQLException;
import java.util.Optional;

/**
 * AuthService bertanggung jawab atas logika bisnis autentikasi,
 * seperti melakukan validasi kredensial login user.
 */
public class AuthService {
    private final UserRepository userRepository;

    /**
     * Constructor dengan Dependency Injection UserRepository.
     *
     * @param userRepository Instance dari UserRepository
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Melakukan proses verifikasi login pengguna.
     *
     * @param username Username yang diinput di form login
     * @param password Password yang diinput di form login
     * @return Objek User yang berhasil login jika kredensial benar
     * @throws AuthException Jika username tidak ditemukan atau password salah
     * @throws SQLException Jika terjadi masalah koneksi atau query pada database
     */
    public User login(String username, String password) throws AuthException, SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthException("Username tidak boleh kosong!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthException("Password tidak boleh kosong!");
        }

        Optional<User> userOptional = userRepository.findByName(username);

        if (userOptional.isEmpty()) {
            throw new AuthException("Username tidak ditemukan!");
        }

        User user = userOptional.get();

        // cek password
        if (!user.getPassword().equals(password)) {
            throw new AuthException("Password salah!");
        }

        // login sukses
        return user;
    }
}
