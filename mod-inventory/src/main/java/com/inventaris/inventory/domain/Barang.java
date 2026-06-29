package com.inventaris.inventory.domain;

import com.inventaris.core.exception.StockException;
import java.util.UUID;

public class Barang {
    private String id;
    private String nama;
    private Kategori kategori;
    private int stok;
    private String deskripsi;

    // existing data (e.g. from database)
    public Barang(String id, String nama, Kategori kategori, int stok, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.stok = stok;
        this.deskripsi = deskripsi;
    }

    //  new data (generating UUID)
    public Barang(String nama, Kategori kategori, int stok, String deskripsi) {
        this.id = UUID.randomUUID().toString();
        this.nama = nama;
        this.kategori = kategori;
        this.stok = stok;
        this.deskripsi = deskripsi;
    }

    public void tambahStok(int jumlah) throws StockException {
        if (jumlah <= 0) {
            throw new StockException("Jumlah penambahan stok harus lebih besar dari 0!");
        }
        this.stok += jumlah;
    }

    public void kurangiStok(int jumlah) throws StockException {
        if (jumlah <= 0) {
            throw new StockException("Jumlah pengurangan stok harus lebih besar dari 0!");
        }
        if (jumlah > this.stok) {
            throw new StockException("Stok tidak mencukupi! Stok saat ini: " + this.stok + ", yang diminta: " + jumlah);
        }
        this.stok -= jumlah;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public int getStok() {
        return stok;
    }

    private void setStok(int stok) {
        this.stok = stok;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
