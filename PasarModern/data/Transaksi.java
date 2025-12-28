import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaksi {
    private String username; // Milik siapa transaksi ini
    private LocalDateTime tanggal;
    private String detailStruk;
    private double totalBayar;
    private boolean isLunas; // true = LUNAS, false = BELUM DIBAYAR

    // Constructor Baru (Dipakai saat Checkout)
    public Transaksi(String username, String detailStruk, double totalBayar, boolean isLunas) {
        this.username = username;
        this.tanggal = LocalDateTime.now();
        this.detailStruk = detailStruk;
        this.totalBayar = totalBayar;
        this.isLunas = isLunas;
    }

    // Constructor untuk Load dari Database (String tanggal dikonversi balik)
    public Transaksi(String username, String tanggalStr, String detailStruk, double totalBayar, boolean isLunas) {
        this.username = username;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.tanggal = LocalDateTime.parse(tanggalStr, formatter);
        this.detailStruk = detailStruk;
        this.totalBayar = totalBayar;
        this.isLunas = isLunas;
    }

    public String getUsername() { return username; }
    
    public String getTanggalStr() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return tanggal.format(formatter);
    }

    public String getDetailStruk() { return detailStruk; }
    public double getTotalBayar() { return totalBayar; }
    
    public boolean isLunas() { return isLunas; }
    public void setLunas(boolean lunas) { this.isLunas = lunas; }
}