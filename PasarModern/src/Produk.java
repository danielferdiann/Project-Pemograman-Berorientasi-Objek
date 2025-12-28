import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Produk {
    // Menggunakan JavaFX Properties agar TableView otomatis update jika nilai berubah
    private final SimpleStringProperty nama;
    private final SimpleDoubleProperty harga;
    private final SimpleIntegerProperty stok;

    public Produk(String nama, double harga, int stok) {
        this.nama = new SimpleStringProperty(nama);
        this.harga = new SimpleDoubleProperty(harga);
        this.stok = new SimpleIntegerProperty(stok);
    }

    // 1. Standard Getters (Untuk mengambil nilai biasa)
    public String getNama() { return nama.get(); }
    public double getHarga() { return harga.get(); }
    public int getStok() { return stok.get(); }

    // 2. Standard Setters (Untuk mengubah nilai)
    public void setNama(String n) { this.nama.set(n); }
    public void setHarga(double h) { this.harga.set(h); }
    public void setStok(int s) { this.stok.set(s); }

    // 3. PROPERTY GETTERS (WAJIB ADA AGAR TABLEVIEW BERFUNGSI NORMAL)
    // Nama method harus berakhiran "Property"
    public SimpleStringProperty namaProperty() { return nama; }
    public SimpleDoubleProperty hargaProperty() { return harga; }
    public SimpleIntegerProperty stokProperty() { return stok; }
}