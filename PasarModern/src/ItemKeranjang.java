public class ItemKeranjang {
    Produk produk;
    int qty;

    public ItemKeranjang(Produk p, int qty) {
        this.produk = p;
        this.qty = qty;
    }
    public double getSubtotal() {
        return produk.getHarga() * qty;
    }
}