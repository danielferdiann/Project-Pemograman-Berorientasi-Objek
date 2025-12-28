import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AppData {
    public static HashMap<String, User> users = new HashMap<>();
    public static ArrayList<Toko> daftarToko = new ArrayList<>();
    public static User currentUser;
    public static Toko selectedToko;
    public static ArrayList<ItemKeranjang> keranjang = new ArrayList<>();
    
    public static double totalBelanja = 0;
    public static double ongkir = 0;
    public static double grandTotal = 0;

    public static void initDummyData() {
        // 1. Load Users
        users.put("admin", new User("admin", "admin", "Admin Pasar", "0812345", "Jl. JavaFX No 1"));
        loadUsersFromDatabase();

        // 2. Load Toko
        loadTokoFromDatabase();
        if (daftarToko.isEmpty()) {
            Toko t1 = new Toko("Toko Sembako Bu Budi");
            t1.tambahProduk(new Produk("Beras 5kg", 60000, 10));
            daftarToko.add(t1);
        }
        
        // 3. LOAD TRANSAKSI (WAJIB SETELAH USER DILOAD)
        loadTransaksiFromDatabase();
    }
    
    // --- DATABASE USER ---
    private static void loadUsersFromDatabase() {
        File file = new File("users_db.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 5) {
                    users.put(data[0], new User(data[0], data[1], data[2], data[3], data[4]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- DATABASE TOKO ---
    private static void loadTokoFromDatabase() {
        File file = new File("toko_db.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 4) {
                    String namaToko = data[0];
                    // Cari toko existing
                    Toko tTarget = null;
                    for(Toko t : daftarToko) if(t.namaToko.equals(namaToko)) tTarget = t;
                    
                    if(tTarget == null) {
                        tTarget = new Toko(namaToko);
                        daftarToko.add(tTarget);
                    }
                    tTarget.tambahProduk(new Produk(data[1], Double.parseDouble(data[2]), Integer.parseInt(data[3])));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public static void saveProdukToDatabase(String namaToko, Produk p) {
        try {
            // "true" artinya append (nambah di baris baru, bukan menimpa)
            java.io.FileWriter fw = new java.io.FileWriter("toko_db.txt", true);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            
            // Format: NamaToko;NamaBarang;Harga;Stok
            // Kita ubah harga jadi long biar gak ada koma .0
            String line = namaToko + ";" + p.getNama() + ";" + (long)p.getHarga() + ";" + p.getStok();
            
            bw.write(line);
            bw.newLine(); // Pindah baris
            bw.close();
            
            System.out.println("Produk baru berhasil disimpan ke toko_db.txt: " + line);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            System.out.println("Gagal menyimpan ke database toko.");
        }
    }

    // --- DATABASE TRANSAKSI (BARU) ---
    
    // 1. Load Transaksi
    private static void loadTransaksiFromDatabase() {
        File file = new File("transaksi_db.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: username;tanggal;total;isLunas;isiStruk(encoded)
                String[] data = line.split(";");
                if (data.length >= 5) {
                    String uName = data[0];
                    String tgl = data[1];
                    double total = Double.parseDouble(data[2]);
                    boolean lunas = Boolean.parseBoolean(data[3]);
                    // Kembalikan __NEWLINE__ jadi enter
                    String struk = data[4].replace("__NEWLINE__", "\n");

                    Transaksi t = new Transaksi(uName, tgl, struk, total, lunas);

                    // Masukkan ke history user yang bersangkutan
                    if (users.containsKey(uName)) {
                        users.get(uName).tambahTransaksi(t);
                    } else if (uName.equals("admin")) { // Handle history admin
                        users.get("admin").tambahTransaksi(t);
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 2. Save Transaksi Baru (Append)
    public static void saveTransaksiToDatabase(Transaksi t) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transaksi_db.txt", true))) {
            // Ubah enter jadi text biasa biar database gak rusak barisnya
            String cleanStruk = t.getDetailStruk().replace("\n", "__NEWLINE__");
            
            String line = t.getUsername() + ";" + 
                          t.getTanggalStr() + ";" + 
                          (long)t.getTotalBayar() + ";" + 
                          t.isLunas() + ";" + 
                          cleanStruk;
            
            bw.write(line);
            bw.newLine();
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // 3. Update Status (Rewrite All - Cara paling aman untuk file txt sederhana)
    public static void updateAllTransaksiDatabase() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transaksi_db.txt"))) {
            // Loop semua user dan semua transaksinya
            for (User u : users.values()) {
                for (Transaksi t : u.getRiwayat()) {
                    String cleanStruk = t.getDetailStruk().replace("\n", "__NEWLINE__");
                    String line = t.getUsername() + ";" + 
                                  t.getTanggalStr() + ";" + 
                                  (long)t.getTotalBayar() + ";" + 
                                  t.isLunas() + ";" + 
                                  cleanStruk;
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static void resetSession() {
        keranjang.clear();
        totalBelanja = 0; ongkir = 0; grandTotal = 0;
    }
}