# Sistem Informasi Manajemen Inventaris Barang

Sistem Informasi Manajemen Inventaris Barang ini dirancang untuk mengelola dan memonitor stok barang secara efisien. Aplikasi ini menggantikan pencatatan manual dengan alur kerja yang terstruktur untuk menghindari kesalahan (*human error*), ketidakakuratan data, dan keterlambatan dalam pembaruan informasi stok.

## Fitur Utama
*   **Otomatisasi Pembaruan Stok:** Memperbarui stok secara otomatis setiap kali terjadi transaksi barang masuk maupun barang keluar.
*   **Manajemen Master Data:** Mengelola data kategori barang dan data barang secara terpusat (Tambah, Edit, Hapus, Cari).
*   **Role-Based Access Control:** Pembagian hak akses antara Admin (Akses Penuh) dan Staff (Akses Operasional).
*   **Pelaporan & Cetak PDF:** Fitur laporan stok komprehensif yang dapat diakses secara real-time dan dicetak ke dalam format PDF.
*   **Validasi Real-time:** Mencegah terjadinya input stok negatif maupun transaksi keluar yang melebihi batas stok tersedia.

## Tech Stack
*   **Bahasa Pemrograman:** Java
*   **Arsitektur:** Object-Oriented Programming (OOP) & Multi-module Architecture
*   **Komponen Modul:** `app-launcher`, `core-shared`, `mod-auth`, `mod-inventory`, `mod-report`, `mod-transaction`

## Cara Menjalankan Project di Lokal

Jika kamu ingin menjalankan aplikasi ini di komputer lokalmu, ikuti langkah-langkah berikut:

### 1. Kloning Repositori
Buka terminal/command prompt dan jalankan perintah berikut:

```bash
git clone https://github.com/ZerithX/InventarisBarang.git
cd InventarisBarang
```

### 2. Buka di IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Buka IDE pilihan Anda (disarankan IntelliJ IDEA).
2. Pilih "Open" dan arahkan ke folder proyek hasil kloning (`InventarisBarang`).
3. Tunggu proses sinkronisasi dan indexing selesai sepenuhnya.

### 3. Setup Library Dependensi (Manual)
Karena proyek ini tidak menggunakan build tool otomatis (seperti Maven/Gradle) dan file konfigurasi IDE diabaikan oleh `.gitignore`, Anda perlu mengunduh dan menyetel library eksternal secara manual agar proyek dapat dikompilasi tanpa error:

> [!IMPORTANT]
> **Tautan Resmi Unduhan Library (.jar):**
> * **FlatLaf (UI Theme):** [Unduh FlatLaf di FormDev](https://www.formdev.com/flatlaf/#download)
> * **MySQL Connector/J (Database Driver):** [Unduh MySQL Connector di Oracle](https://dev.mysql.com/downloads/connector/j/) (Pilih *Platform Independent* untuk versi zip/tar.gz berisi `.jar`)
> * **OpenPDF (Cetak Laporan PDF):** [Unduh OpenPDF di GitHub Releases](https://github.com/LibrePDF/OpenPDF/releases)

**Langkah menambahkan library di IntelliJ IDEA:**
1. Unduh semua file `.jar` dari tautan di atas.
2. Buka **File** > **Project Structure** (`Ctrl + Alt + Shift + S`).
3. Pilih **Libraries** pada panel kiri, lalu klik tombol **+** (New Project Library) > **Java**.
4. Cari dan pilih file `flatlaf-3.x.x.jar`, `mysql-connector-j-x.x.x.jar`, dan `openpdf-x.x.x.jar` yang telah Anda unduh.
5. Kaitkan library tersebut ke modul yang membutuhkannya (disarankan untuk memilih seluruh modul agar terhindar dari error kompilasi).
6. Klik **Apply** lalu **OK**.

### 4. Jalankan Aplikasi
1. Cari kelas utama `MainApplication.java` yang berada di dalam modul `app-launcher`.
2. Klik tombol **Run** (ikon segitiga hijau) pada IDE Anda.
3. Aplikasi akan berjalan di environment lokal Anda.


## Alur Kontribusi (Git Workflow)

Bagi anggota tim yang ingin menambahkan fitur atau memperbaiki bug pada aplikasi ini, gunakan panduan git berikut untuk berkolaborasi dengan aman:

### 1. Ambil Perubahan Terbaru (Pull)
Sebelum Anda mulai menulis kode baru, selalu ambil pembaruan terbaru dari GitHub agar tidak terjadi konflik kode (*conflict code*):

```bash
git pull origin main
```

### 2. Buat Branch Baru
Buatlah branch baru yang spesifik sesuai dengan fitur atau perbaikan yang akan Anda kerjakan:

```bash
// Format branch baru : fitur/nama-fitur atau bugfix/nama-bug
git checkout -b feat/authlogin
```

### 3. Lakukan Perubahan Kode
Silakan edit atau tambahkan file baru sesuai dengan domain modulnya (contoh: `mod-auth` untuk fitur login dan autentikasi, `core-shared` untuk throw dan exception auth).

### 4. Periksa Status File yang Diubah
Lihat daftar file apa saja yang telah Anda ubah atau tambahkan dengan perintah:

```bash
git status
```

### 5. Simpan Perubahan (Add & Commit)
Tambahkan file yang diubah ke area staging, lalu buat pesan commit yang deskriptif:

```bash
# Untuk menambahkan semua file baru/berubah
git add .

# Atau jika hanya ingin menambahkan file spesifik
git add mod-inventory/src/main/java/...

# Buat commit dengan pesan yang jelas (Bisa melihat reference di conventional commit)
git commit -m "Menambahkan [Nama Fitur/Perubahan] ke sistem inventaris"
```

### 6. Kirim Perubahan ke GitHub (Push)
Kirimkan commit lokal Anda ke repositori utama di GitHub:

```bash
git push origin feat/authlogin
```

## Struktur Direktori Proyek

```text
InventarisBarang/
│
├── app-launcher/     # Main entry point application (MainApplication.java)
├── core-shared/      # Exception handling dan utility classes (AuthException, StockException)
├── mod-auth/         # Modul autentikasi pengguna (Admin, Staff, User)
├── mod-inventory/    # Modul manajemen master data (Barang, Kategori)
├── mod-report/       # Modul pembuatan laporan stok dan cetak PDF
├── mod-transaction/  # Modul operasional (Barang Masuk, Barang Keluar)
│
└── README.md         # Dokumentasi proyek (file ini)
```
