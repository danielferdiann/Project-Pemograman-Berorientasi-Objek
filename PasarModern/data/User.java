import java.util.ArrayList;

public class User {
    String username, password, namaLengkap, noTelp, alamat;
    ArrayList<Transaksi> riwayatBelanja = new ArrayList<>();
    
    public User(String username, String password, String nama, String noTelp, String alamat) {
        this.username = username;
        this.password = password;
        this.namaLengkap = nama;
        this.noTelp = noTelp;
        this.alamat = alamat;
    }

    public void tambahTransaksi(Transaksi t) {
        riwayatBelanja.add(t);
    }
    
    public ArrayList<Transaksi> getRiwayat() {
        return riwayatBelanja;
    }
}
