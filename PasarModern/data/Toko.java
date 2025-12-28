import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Toko {
    String namaToko;
    ObservableList<Produk> daftarProduk = FXCollections.observableArrayList();

    public Toko(String namaToko) {
        this.namaToko = namaToko;
    }

    public void tambahProduk(Produk p) {
        daftarProduk.add(p);
    }
}