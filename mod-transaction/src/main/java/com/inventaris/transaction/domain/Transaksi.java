package com.inventaris.transaction.domain;

import com.inventaris.auth.domain.User;
import com.inventaris.core.exception.StockException;
import com.inventaris.inventory.domain.Barang;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaksi {
    private String id;
    private Barang barang;
    private int jumlah;
    private TipeTransaksi tipeTransaksi;
    private User user;
    private LocalDateTime createdAt;
    private String keterangan;

    // Constructor for existing data (e.g. from database)
    public Transaksi(String id, Barang barang, int jumlah, TipeTransaksi tipeTransaksi, User user, LocalDateTime createdAt, String keterangan) {
        this.id = id;
        this.barang = barang;
        this.jumlah = jumlah;
        this.tipeTransaksi = tipeTransaksi;
        this.user = user;
        this.createdAt = createdAt;
        this.keterangan = keterangan;
    }

    // Constructor for new data (generating UUID and createdAt automatically)
    public Transaksi(Barang barang, int jumlah, TipeTransaksi tipeTransaksi, User user, String keterangan) {
        this.id = UUID.randomUUID().toString();
        this.barang = barang;
        this.jumlah = jumlah;
        this.tipeTransaksi = tipeTransaksi;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.keterangan = keterangan;
    }

    public abstract void prosesStok() throws StockException;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Barang getBarang() {
        return barang;
    }

    public void setBarang(Barang barang) {
        this.barang = barang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public TipeTransaksi getTipeTransaksi() {
        return tipeTransaksi;
    }

    public void setTipeTransaksi(TipeTransaksi tipeTransaksi) {
        this.tipeTransaksi = tipeTransaksi;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}