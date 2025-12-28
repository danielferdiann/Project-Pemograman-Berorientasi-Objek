# Pasar Jaten Apps 🛒

**Pasar Jaten Apps** adalah aplikasi manajemen pasar modern berbasis desktop yang dirancang untuk mendigitalkan alur transaksi tradisional. Aplikasi ini menyediakan solusi *end-to-end*, mulai dari manajemen stok barang, proses belanja pelanggan, hingga verifikasi pembayaran oleh admin.


## ✨ Fitur Utama
- **User Interface Modern:** Menggunakan **JavaFX** untuk pengalaman pengguna yang bersih dan interaktif.
- **Manajemen Transaksi:** Mendukung opsi *Delivery* dan *Pick-up*.
- **Validasi Stok Real-time:** Mencegah stok minus saat barang dimasukkan ke keranjang.
- **Sistem Pembayaran Dua Tahap:** Admin dapat memverifikasi bukti pembayaran sebelum transaksi dianggap lunas.
- **Arsip Struk Otomatis:** Sistem secara otomatis memindahkan file resi dari folder `struk/` (belum bayar) ke folder `struk_lunas/` setelah diverifikasi.
- **Database Berbasis Teks:** Penyimpanan data mandiri menggunakan file `.txt`, menjamin portabilitas tanpa perlu konfigurasi database yang rumit.

## 🛠️ Teknologi yang Digunakan
- **Bahasa Pemrograman:** Java
- **Library UI:** JavaFX
- **Penyimpanan Data:** Flat File System (.txt)
- **IDE:** Visual Studio Code / IntelliJ IDEA

## 📁 Struktur Data
Aplikasi ini mengelola data secara lokal melalui folder berikut:
- `data/`: Menyimpan database pengguna, produk, dan riwayat transaksi.
- `struk/`: Folder resi untuk transaksi yang sedang diproses (pending).
- `struk_lunas/`: Arsip resi untuk transaksi yang telah berhasil diverifikasi.
