package com.inventaris.inventory.domain;

import java.util.UUID;

public class Kategori {
    private String id;
    private String nama;

    // existing data (e.g. from database)
    public Kategori(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    // new data (generating UUID)
    public Kategori(String nama) {
        this.id = UUID.randomUUID().toString();
        this.nama = nama;
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
}
