package com.inventaris.transaction.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaksi {
    private String id;
    private String idBarang;
    private int jumlah;
    private TipeTransaksi tipeTransaksi;
    private String createdBy;
    private LocalDateTime createdAt;
    private String keterangan;

    // Constructor for existing data (e.g. from database)
    public Transaksi(String id, String idBarang, int jumlah, TipeTransaksi tipeTransaksi, String createdBy, LocalDateTime createdAt, String keterangan) {
        this.id = id;
        this.idBarang = idBarang;
        this.jumlah = jumlah;
        this.tipeTransaksi = tipeTransaksi;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.keterangan = keterangan;
    }

    // Constructor for new data (generating UUID and createdAt automatically)
    public Transaksi(String idBarang, int jumlah, TipeTransaksi tipeTransaksi, String createdBy, String keterangan) {
        this.id = UUID.randomUUID().toString();
        this.idBarang = idBarang;
        this.jumlah = jumlah;
        this.tipeTransaksi = tipeTransaksi;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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