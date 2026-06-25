package com.inventaris.inventory.domain;

import java.util.UUID;

public class Barang {
    private String id;
    private String nama;
    private String idKategori;
    private int stok;

    // existing data (e.g. from database)
    public Barang(String id, String nama, String idKategori, int stok) {
        this.id = id;
        this.nama = nama;
        this.idKategori = idKategori;
        this.stok = stok;
    }

    //  new data (generating UUID)
    public Barang(String nama, String idKategori, int stok) {
        this.id = UUID.randomUUID().toString();
        this.nama = nama;
        this.idKategori = idKategori;
        this.stok = stok;
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

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}
