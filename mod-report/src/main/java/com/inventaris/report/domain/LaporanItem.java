package com.inventaris.report.domain;

public class LaporanItem {
    private String idBarang;
    private String namaBarang;
    private String namaKategori;
    private int stokAwal;
    private int masuk;
    private int keluar;
    private int stokAkhir;

    public LaporanItem(String idBarang, String namaBarang, String namaKategori, int stokAwal, int masuk, int keluar) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.namaKategori = namaKategori;
        this.stokAwal = stokAwal;
        this.masuk = masuk;
        this.keluar = keluar;
        this.stokAkhir = stokAwal + masuk - keluar;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public int getStokAwal() {
        return stokAwal;
    }

    public int getMasuk() {
        return masuk;
    }

    public int getKeluar() {
        return keluar;
    }

    public int getStokAkhir() {
        return stokAkhir;
    }
}
