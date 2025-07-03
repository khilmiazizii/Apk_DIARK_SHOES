/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Diark_Shoes_Apk;

import java.awt.Color;
import java.awt.HeadlessException;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import net.proteanit.sql.DbUtils;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
/**
 *
 * @author DELL
 */
public final class Transaksi extends javax.swing.JFrame {
    
    Double totalAmount=0.0;
    Double cash=0.0;
    Double balance=0.0;
    Double bHeight=1.50;

    ArrayList<String> namaAry = new ArrayList<>();
    ArrayList<String> jumlahAry = new ArrayList<>();
    ArrayList<String> hargaAry = new ArrayList<>();
    ArrayList<String> diskonAry = new ArrayList<>();
    ArrayList<String> subtotalAry = new ArrayList<>();
    ArrayList<String> totalAry = new ArrayList<>();
Connection koneksi;
PreparedStatement pst, pst2;
ResultSet rst;
int istok, istok2, iharga, ijumlah, kstok, tstok;
String harga, barang, dbarang, KD, jam, tanggal,ssub,namasepatu;
    /**
     * Creates new form Transaksi
     */
    public Transaksi() {
        initComponents();
        koneksi=DatabaseConnection.connect();
        delay();    
        autonumber();
        buatTransaksiBaru();
        detail();
        sum();
        
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel cmBarang = jTable1.getColumnModel();
        cmBarang.getColumn(0).setPreferredWidth(100);   // Kode_sepatu
        cmBarang.getColumn(1).setPreferredWidth(100);  // Nama_sepatu
        cmBarang.getColumn(2).setPreferredWidth(70);   // Stok
        cmBarang.getColumn(3).setPreferredWidth(100);  // Tipe
        cmBarang.getColumn(4).setPreferredWidth(100);  // Harga
        // Atur kolom untuk tblDataBarang
        jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel cmData = jTable2.getColumnModel();
        cmData.getColumn(0).setPreferredWidth(120);  // Kode_Transaksi
        cmData.getColumn(1).setPreferredWidth(100);  // Kode_Detail
        cmData.getColumn(2).setPreferredWidth(100);  // Kode_sepatu
        cmData.getColumn(3).setPreferredWidth(100);  // Nama_sepatu
        cmData.getColumn(4).setPreferredWidth(80); // Harga
        cmData.getColumn(5).setPreferredWidth(100); // Jumlah
        cmData.getColumn(6).setPreferredWidth(70); // Discount
        cmData.getColumn(7).setPreferredWidth(80); // Subtotal
    }
    
        private void simpan(){
        String tgl=jTextField6.getText();
        String jam=jTextField9.getText();
        String kembalian = jTextField8.getText(); // Ambil dari field kembalian
      try {
            String sql="insert into struk_pembelian (Kode_Transaksi,Kembalian,Tanggal,Jam,Total) value (?,?,?,?,?)";
            pst=koneksi.prepareStatement(sql);
            pst.setString(1, jkod.getText());
            pst.setString(2, kembalian);
            pst.setString(3, tgl);
            pst.setString(4, jam);
            pst.setString(5, jtot.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data Tersimpan");
        } catch (SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
            }
    }

        
     public void buatTransaksiBaru() {
    try {
        String kodeTransaksi = jkod.getText();       // hasil dari autonumber
        String tanggal = jTextField6.getText();      // Tanggal dari delay()
        String jam = jTextField9.getText();          // Jam dari delay()

        String sql = "INSERT INTO struk_pembelian (Kode_Transaksi, Kembalian, Tanggal, Jam, Total) VALUES (?, ?, ?, ?, ?)";
        pst = koneksi.prepareStatement(sql);
        pst.setString(1, kodeTransaksi);
        pst.setInt(2, 0);
        pst.setString(3, tanggal);   // pastikan formatnya yyyy-MM-dd
        pst.setString(4, jam);       // format HH:mm:ss
        pst.setInt(5, 0);            // total awal = 0
        pst.execute();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Gagal membuat transaksi baru: " + e.getMessage());
    }
}


    
   private void total() {
    int total, bayar, kembali;
    try {
        bayar = Integer.parseInt(jTextField7.getText());  // input bayar
        total = Integer.parseInt(jtot.getText());         // total belanja
        kembali = bayar - total;

        // Tampilkan nilai kembalian walaupun minus
        jTextField8.setText(String.valueOf(kembali));
        
            // Pastikan field aktif agar warnanya bisa tampil
        jTextField8.setEditable(false);  // agar user tidak bisa edit
        jTextField8.setEnabled(true);    // tetap aktif agar warna bisa berubah

        // Ubah warna text tergantung kondisi
        if (kembali < 0) {
            jTextField8.setForeground(Color.RED);     // merah jika uang kurang
        } else {
            jTextField8.setForeground(Color.BLACK);   // hitam jika cukup
        }

    } catch (NumberFormatException e) {
        jTextField8.setText("-");                       // simbol default
        jTextField8.setForeground(Color.GRAY);          // abu-abu jika error
    }
}


    
    public void clsr(){
    jjum.setText("");
    jdis.setText("");
    }
    
private String generateKodeDetail() {
    String kode = "D001";
    try {
        String sql = "SELECT Kode_Detail FROM detail_barang ORDER BY Kode_Detail DESC LIMIT 1";
        pst = koneksi.prepareStatement(sql);
        rst = pst.executeQuery();
        if (rst.next()) {
            String lastKode = rst.getString("Kode_Detail");
            String numberStr = lastKode.replaceAll("[^0-9]", "");
            int number = Integer.parseInt(numberStr);
            kode = String.format("D%03d", number + 1);
        }
    } catch (SQLException | NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Gagal generate Kode Detail\n" + e);
    }
    return kode;
}



    public void cari(){
     try {
        String sql = "SELECT * FROM data_sepatu WHERE Nama_sepatu LIKE ?";
        pst = koneksi.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pst.setString(1, "%" + jTextField1.getText() + "%");
        rst=pst.executeQuery();
        jTable1.setModel(DbUtils.resultSetToTableModel(rst));
         // Atur kolom untuk tblBarang
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel cmBarang = jTable1.getColumnModel();
        cmBarang.getColumn(0).setPreferredWidth(100);   // Kode_sepatu
        cmBarang.getColumn(1).setPreferredWidth(100);  // Nama_sepatu
        cmBarang.getColumn(2).setPreferredWidth(70);   // Stok
        cmBarang.getColumn(3).setPreferredWidth(100);  // Tipe
        cmBarang.getColumn(4).setPreferredWidth(100);  // Harga
       } catch (SQLException e){ JOptionPane.showMessageDialog(null, e);} 
    }
    
    public void kurangi_stok(){
    int qty;
    qty=Integer.parseInt(jjum.getText());
    kstok=istok-qty;
    }
    
    private void subtotal(){
    int diskon, jumlah, sub, totalHarga, diskonNominal;
            if (jdis.getText().equals("")) {diskon=0;}
            else {diskon= Integer.parseInt(jdis.getText());}
         jumlah= Integer.parseInt(jjum.getText());
         // Menghitung total harga sebelum diskon
        totalHarga = jumlah * iharga;
        // Menghitung diskon yang diterapkan dalam persen
        diskonNominal = (totalHarga * diskon) / 100;
        // Menghitung subtotal setelah diskon
        sub = totalHarga - diskonNominal;
        // Mengonversi subtotal ke string
        ssub = String.valueOf(sub);     
    }
    
public void tambah_stok(){
    try {
        int baris = jTable2.getSelectedRow();
        if (baris >= 0) {
            String kodeSepatu = jTable2.getValueAt(baris, jTable2.getColumnModel().getColumnIndex("Kode_sepatu")).toString();
            int jumlah = Integer.parseInt(jTable2.getValueAt(baris, jTable2.getColumnModel().getColumnIndex("Jumlah")).toString());

            // Ambil stok lama dari database
            String query = "SELECT Stok FROM data_sepatu WHERE Kode_sepatu = ?";
            pst = koneksi.prepareStatement(query);
            pst.setString(1, kodeSepatu);
            rst = pst.executeQuery();

            if (rst.next()) {
                int stokLama = rst.getInt("Stok");
                int stokBaru = stokLama + jumlah;

                // Update stok
                String update = "UPDATE data_sepatu SET Stok = ? WHERE Kode_sepatu = ?";
                pst2 = koneksi.prepareStatement(update);
                pst2.setInt(1, stokBaru);
                pst2.setString(2, kodeSepatu);
                pst2.executeUpdate();
            }
        }
    } catch (SQLException | NumberFormatException e){
        JOptionPane.showMessageDialog(null, "Gagal mengembalikan stok: " + e);
    }
}

    public void ambil_stock(){
    try {
    String sql = "SELECT * FROM data_sepatu WHERE Kode_sepatu = ?";
    pst = koneksi.prepareStatement(sql);
    pst.setString(1, barang);
    rst=pst.executeQuery();
    if (rst.next()) {    
    String stok=rst.getString(("Stok"));
    istok2= Integer.parseInt(stok);
    }
}catch (SQLException | NumberFormatException e) {JOptionPane.showMessageDialog(null, e);}
    }
    
    public void sum(){
        int totalBiaya = 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) jTable2.getModel();
        int jumlah = jTable2.getRowCount();
        for (int i=0; i<jumlah; i++){
        subtotal = Integer.parseInt(dataModel.getValueAt(i, 7).toString());
        totalBiaya += subtotal;
        }
        jtot.setText(String.valueOf(totalBiaya));
    }
    
    public void autonumber(){
    try{
        String sql = "SELECT MAX(RIGHT(Kode_Transaksi,3)) AS NO FROM struk_pembelian";
        pst = koneksi.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rst=pst.executeQuery();
        while (rst.next()) {
                if (rst.first() == false) {
                    jkod.setText("TRX001");
                } else {
                    rst.last();
                    int auto_id = rst.getInt(1) + 1;
                    String no = String.valueOf(auto_id);
                    int NomorJual = no.length();
                    for (int j = 0; j < 3 - NomorJual; j++) {
                        no = "0" + no;
                    }
                    jkod.setText("TRX" + no);
                }
            }
        rst.close();
        }catch (SQLException e){JOptionPane.showMessageDialog(null, e);}
    }
    
public void detail() {
    try {
        String kodeTransaksi = jkod.getText(); // ambil kode transaksi aktif
        String sql = "SELECT * FROM detail_barang WHERE Kode_Transaksi = ? ORDER BY Kode_Detail DESC";
        pst = koneksi.prepareStatement(sql);
        pst.setString(1, kodeTransaksi);
        rst = pst.executeQuery();
        jTable2.setModel(DbUtils.resultSetToTableModel(rst));
        // Atur kolom 
        jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel cmData = jTable2.getColumnModel();
        cmData.getColumn(0).setPreferredWidth(120);  // Kode_Transaksi
        cmData.getColumn(1).setPreferredWidth(100);  // Kode_Detail
        cmData.getColumn(2).setPreferredWidth(100);  // Kode_sepatu
        cmData.getColumn(3).setPreferredWidth(100);  // Nama_sepatu
        cmData.getColumn(4).setPreferredWidth(80); // Harga
        cmData.getColumn(5).setPreferredWidth(100); // Jumlah
        cmData.getColumn(6).setPreferredWidth(70); // Discount
        cmData.getColumn(7).setPreferredWidth(80); // Subtotal
    } catch (SQLException e){
        JOptionPane.showMessageDialog(null, e);
    }
}


    
   public void delay(){
    Thread clock;
    clock = new Thread(){
        @Override
        public void run(){
            for(;;){
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");      // Jam
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");   // Tanggal

                jTextField6.setText(format2.format(cal.getTime())); // Tanggal
                jTextField9.setText(format.format(cal.getTime()));  // Jam

                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Transaksi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    clock.start();
}

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel11 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jjum = new javax.swing.JTextField();
        jdis = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jtot = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jkod = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(960, 540));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel11.setBackground(java.awt.Color.gray);

        jButton10.setText("Logout");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
        });
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jPanel12.setBackground(new java.awt.Color(153, 153, 153));

        jLabel16.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel16.setText("Data Barang");
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel17.setText("Transaksi");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel17)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel16)
                .addContainerGap(79, Short.MAX_VALUE))
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addGap(39, 39, 39)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTextField6.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jTextField6.setEnabled(false);
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextField9.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jTextField9.setEnabled(false);
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        jLabel1.setText("Form Transaksi");

        jLabel7.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel7.setText("Masukan Nama Barang");

        jToggleButton1.setText("Cari");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 85));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kode_Sepatu", "Nama_Sepatu", "Stock", "Tipe", "Harga"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jjum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jjumActionPerformed(evt);
            }
        });
        jjum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jjumPropertyChange(evt);
            }
        });

        jLabel8.setText("Jumlah");

        jLabel4.setText("Discount");

        jLabel10.setText("Tambahkan");

        jButton1.setText("+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel2.setText("Kode Transaksi");

        jLabel13.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel13.setText("Data Barang");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jLabel12.setText("Hapus");

        jButton3.setText("-");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        jLabel3.setText("Total ");

        jtot.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        jtot.setEnabled(false);

        jTextField7.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jTextField7.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jTextField7MouseDragged(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField7KeyTyped(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel5.setText("Bayar");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jLabel6.setText("Kembalian");

        jTextField8.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jTextField8.setEnabled(false);
        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 204, 204));
        jButton5.setText("Bayar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(204, 204, 204));
        jButton6.setText("New");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jkod.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jkod.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jToggleButton1))
                            .addComponent(jLabel7))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jkod, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(44, 44, 44)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jjum, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(22, 22, 22)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jdis, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addGap(45, 45, 45))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addGap(62, 62, 62))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jtot, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(252, 252, 252)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel12)
                                .addComponent(jButton3))
                            .addGap(100, 100, 100)))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jdis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jjum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jkod, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtot, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
try {
    // Ambil semua data detail transaksi berdasarkan kode transaksi
    String sqlAmbilDetail = "SELECT Kode_sepatu, Jumlah FROM detail_barang WHERE Kode_Transaksi = ?";
    PreparedStatement pstAmbil = koneksi.prepareStatement(sqlAmbilDetail);
    pstAmbil.setString(1, jkod.getText());
    ResultSet rsDetail = pstAmbil.executeQuery();

    while (rsDetail.next()) {
        String kodeSepatu = rsDetail.getString("Kode_sepatu");
        int jumlah = rsDetail.getInt("Jumlah");

        // Tambahkan kembali ke stok barang
        String sqlUpdateStok = "UPDATE data_sepatu SET Stok = Stok + ? WHERE Kode_sepatu = ?";
        PreparedStatement pstUpdate = koneksi.prepareStatement(sqlUpdateStok);
        pstUpdate.setInt(1, jumlah);
        pstUpdate.setString(2, kodeSepatu);
        pstUpdate.executeUpdate();
    }

    // Setelah stok dikembalikan, hapus data di detail_barang
    String sqlHapusDetail = "DELETE FROM detail_barang WHERE Kode_Transaksi = ?";
    PreparedStatement pstHapusDetail = koneksi.prepareStatement(sqlHapusDetail);
    pstHapusDetail.setString(1, jkod.getText());
    pstHapusDetail.executeUpdate();

    // Hapus struk pembelian utama
    String sqlHapusStruk = "DELETE FROM struk_pembelian WHERE Kode_Transaksi = ?";
    PreparedStatement pstHapusStruk = koneksi.prepareStatement(sqlHapusStruk);
    pstHapusStruk.setString(1, jkod.getText());
    pstHapusStruk.executeUpdate();

    JOptionPane.showMessageDialog(null, "Transaksi berhasil dibatalkan dan stok dikembalikan.");

} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Gagal membatalkan transaksi: " + e.getMessage());
}

    // Pindah ke halaman login atau keluar aplikasi
    new Login().setVisible(true);
    this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        new Transaksi().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        cari();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
            try {
         int row = jTable1.getSelectedRow();
         String tabel_klik = (jTable1.getModel().getValueAt(row, 0).toString());
         String sql = "select * from data_sepatu where Kode_sepatu='" + tabel_klik + "'";
         pst = koneksi.prepareStatement(sql);
         rst = pst.executeQuery();
         if (rst.next()) {
             barang = rst.getString("Kode_sepatu");
             namasepatu = rst.getString("Nama_sepatu");
             String stok = rst.getString("Stok");
             istok = Integer.parseInt(stok);
             istok2 = istok; // <<< baris tambahan untuk update stok yang dipilih
             harga = rst.getString("Harga");
             iharga = Integer.parseInt(harga);
         }
     } catch (SQLException | NumberFormatException e) {
         JOptionPane.showMessageDialog(null, e);
     }

    }//GEN-LAST:event_jTable1MouseClicked

    private void jjumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jjumActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jjumActionPerformed

    private void jjumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jjumPropertyChange

    }//GEN-LAST:event_jjumPropertyChange

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

 // Cek apakah ada baris yang dipilih di JTable
 int row = jTable1.getSelectedRow();
 if (row == -1) {  // Tidak ada baris yang dipilih
     JOptionPane.showMessageDialog(null, "Pilih barang terlebih dahulu.");
     return;
}      
// Validasi jumlah tidak kosong
String jumlahStr = jjum.getText().trim();
if (jumlahStr.isEmpty()) {
    JOptionPane.showMessageDialog(null, "Jumlah belum diisi");
    return;
}

subtotal();
kurangi_stok();

// Cek apakah stok kosong sebelum melanjutkan
if (istok2 <= 0) {
    JOptionPane.showMessageDialog(null, "Stok barang habis, tidak bisa ditambahkan.");
    return;
}

try {
    String diskon;
    if (jdis.getText().equals("")) {
        diskon = "0";
    } else {
        diskon = jdis.getText();
    }

    // Kode_Detail unik setiap barang
    KD = generateKodeDetail();
    String sql = "INSERT INTO detail_barang (Kode_Transaksi, Kode_Detail, Kode_sepatu, Nama_sepatu, Harga, Jumlah, Discount, Subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    String update = "UPDATE data_sepatu SET Stok='" + kstok + "' WHERE Kode_sepatu='" + barang + "'";

    pst = koneksi.prepareStatement(sql);
    pst2 = koneksi.prepareStatement(update);

    pst.setString(1, jkod.getText());
    pst.setString(2, KD);
    pst.setString(3, barang);
    pst.setString(4, namasepatu);
    pst.setString(5, harga);
    pst.setString(6, jjum.getText()); // jumlah yang sudah divalidasi
    pst.setString(7, diskon );
    pst.setString(8, ssub);

    pst.execute();
    pst2.execute();
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, e);
}

detail();
sum();
cari();
clsr();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        try {
            int row=jTable2.getSelectedRow();
            dbarang=(jTable2.getModel().getValueAt(row, 1).toString());
            String sql="select * from detail_barang where Kode_sepatu='"+dbarang+"'";
            pst=koneksi.prepareStatement(sql);
            rst=pst.executeQuery();
            if (rst.next()) {
                String jumlah=rst.getString(("Jumlah"));
                ijumlah= Integer.parseInt(jumlah);
            }
        }catch (SQLException | NumberFormatException e) {JOptionPane.showMessageDialog(null, e);}
        ambil_stock();
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
try {
    int baris = jTable2.getSelectedRow();
    if (baris >= 0) {
        // Ambil data sebelum baris dihapus dari tabel
        int indexKode = jTable2.getColumnModel().getColumnIndex("Kode_sepatu");
        int indexJumlah = jTable2.getColumnModel().getColumnIndex("Jumlah");

        String kodeSepatu = jTable2.getValueAt(baris, indexKode).toString();
        int jumlah = Integer.parseInt(jTable2.getValueAt(baris, indexJumlah).toString());

        // Tambah stok dulu
        String getStok = "SELECT Stok FROM data_sepatu WHERE Kode_sepatu = ?";
        pst = koneksi.prepareStatement(getStok);
        pst.setString(1, kodeSepatu);
        rst = pst.executeQuery();

        if (rst.next()) {
            int stokLama = rst.getInt("Stok");
            int stokBaru = stokLama + jumlah;

            String updateStok = "UPDATE data_sepatu SET Stok = ? WHERE Kode_sepatu = ?";
            pst2 = koneksi.prepareStatement(updateStok);
            pst2.setInt(1, stokBaru);
            pst2.setString(2, kodeSepatu);
            pst2.executeUpdate();
        }

        // Hapus baris dari detail_barang
        int indexKodeDetail = jTable2.getColumnModel().getColumnIndex("Kode_Detail");
        String kodeDetail = jTable2.getValueAt(baris, indexKodeDetail).toString();

        String sql = "DELETE FROM detail_barang WHERE Kode_Detail = ?";
        pst = koneksi.prepareStatement(sql);
        pst.setString(1, kodeDetail);
        pst.executeUpdate();

        JOptionPane.showMessageDialog(null, "Data berhasil dihapus & stok dikembalikan.");
    } else {
        JOptionPane.showMessageDialog(null, "Pilih dulu data yang ingin dihapus");
    }
} catch (SQLException | NumberFormatException e) {
    JOptionPane.showMessageDialog(null, "Gagal: " + e);
}

detail();  // refresh tabel
sum();     // refresh total
cari();    // refresh pencarian


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    // Ambil data dari form
String kodeTransaksi = jkod.getText();
String total = jtot.getText();
String jam = jTextField9.getText();
String tanggal = jTextField6.getText();
String bayarStr = jTextField7.getText();     // kolom Bayar
String kembalianStr = jTextField8.getText(); // dari field Kembalian

// Cek jika field bayar masih kosong
if (bayarStr.isEmpty()) {
    JOptionPane.showMessageDialog(null, "Masukkan nominal pembayaran terlebih dahulu.");
    return;
}

try {
    int kembalian = Integer.parseInt(kembalianStr); // Validasi angka

    if (kembalian < 0) {
        JOptionPane.showMessageDialog(null, "Uang tidak cukup! Silakan masukkan nominal yang sesuai.");
        return;
    }

    // Konfirmasi cetak
    int konfirmasi = JOptionPane.showConfirmDialog(null, "Apakah ingin mencetak Struk?", "Cetak Struk", JOptionPane.YES_NO_OPTION);
    if (konfirmasi == JOptionPane.YES_OPTION) {

        // Ambil semua data dari JTable2 ke array list
        removeAllArray(); // Kosongkan array dulu
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String nama = model.getValueAt(i, 3).toString();    // Kolom Nama_sepatu
            String jumlah = model.getValueAt(i, 5).toString();  // Kolom Jumlah
            String harga = model.getValueAt(i, 4).toString();   // Kolom Harga
            String diskon = model.getValueAt(i, 6).toString();
            String subtotal = model.getValueAt(i, 7).toString();
            int totalPerBarang = Integer.parseInt(jumlah) * Integer.parseInt(harga);

            namaAry.add(nama);
            jumlahAry.add(jumlah);
            hargaAry.add(harga);
            diskonAry.add(diskon);
            subtotalAry.add(subtotal);
            totalAry.add(String.valueOf(totalPerBarang));
        }

        System.out.println("Item dicetak: " + namaAry.size());
        for (int i = 0; i < namaAry.size(); i++) {
            System.out.println(namaAry.get(i) + " | " + jumlahAry.get(i) + " | " + hargaAry.get(i));
        }


        // Cetak struk
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintable(), getPageFormat(pj));
        pj.print();

        // Setelah cetak berhasil, simpan ke database
        String sql = "UPDATE struk_pembelian SET Total = ?, Kembalian = ?, Jam = ?, Tanggal = ? WHERE Kode_Transaksi = ?";
        pst = koneksi.prepareStatement(sql);
        pst.setString(1, total);
        pst.setInt(2, kembalian);
        pst.setString(3, jam);
        pst.setString(4, tanggal);
        pst.setString(5, kodeTransaksi);
        pst.executeUpdate();

        JOptionPane.showMessageDialog(null, "Transaksi berhasil disimpan.");
    }

    // Reset tampilan setelah transaksi selesai
    removeAllArray();
    autonumber();
    buatTransaksiBaru();
    detail();
    jtot.setText("");
    jTextField7.setText("");
    jTextField8.setText("");
    jTextField1.setText("");
    cari();
    clsr();

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(null, "Nilai kembalian tidak valid. Pastikan hanya angka.");
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Gagal menyimpan transaksi: " + e.getMessage());
} catch (PrinterException e) {
    JOptionPane.showMessageDialog(null, "Gagal mencetak struk: " + e.getMessage());
}

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        autonumber();        // generate kode transaksi baru (misal TRX008)
        buatTransaksiBaru(); // fungsi ini akan buat entri di tabel transaksi
        detail();            // kosongkan tabel barang yang ditampilkan
        clsr();              // clear form input jumlah dan diskon
        jtot.setText("");        // kosongkan total
        jTextField7.setText(""); // kosongkan bayar
        jTextField8.setText(""); // kosongkan kembalian
        jTextField1.setText(""); // kosongkan input cari barang
        cari();               // clear form

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // TODO add your handling code here:
        new StokBarang().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
        // TODO add your handling code here:
        new Login().setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton10MouseClicked

    private void jTextField7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // blokir karakter selain angka dan backspace
            JOptionPane.showMessageDialog(null, "Hanya boleh angka!");  }
    }//GEN-LAST:event_jTextField7KeyTyped

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        // TODO add your handling code here:
      total();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jTextField7MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField7MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7MouseDragged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
try {
    // Ambil semua data detail transaksi berdasarkan kode transaksi
    String sqlAmbilDetail = "SELECT Kode_sepatu, Jumlah FROM detail_barang WHERE Kode_Transaksi = ?";
    PreparedStatement pstAmbil = koneksi.prepareStatement(sqlAmbilDetail);
    pstAmbil.setString(1, jkod.getText());
    ResultSet rsDetail = pstAmbil.executeQuery();

    while (rsDetail.next()) {
        String kodeSepatu = rsDetail.getString("Kode_sepatu");
        int jumlah = rsDetail.getInt("Jumlah");

        // Tambahkan kembali ke stok barang
        String sqlUpdateStok = "UPDATE data_sepatu SET Stok = Stok + ? WHERE Kode_sepatu = ?";
        PreparedStatement pstUpdate = koneksi.prepareStatement(sqlUpdateStok);
        pstUpdate.setInt(1, jumlah);
        pstUpdate.setString(2, kodeSepatu);
        pstUpdate.executeUpdate();
    }

    // Setelah stok dikembalikan, hapus data di detail_barang
    String sqlHapusDetail = "DELETE FROM detail_barang WHERE Kode_Transaksi = ?";
    PreparedStatement pstHapusDetail = koneksi.prepareStatement(sqlHapusDetail);
    pstHapusDetail.setString(1, jkod.getText());
    pstHapusDetail.executeUpdate();

    // Hapus struk pembelian utama
    String sqlHapusStruk = "DELETE FROM struk_pembelian WHERE Kode_Transaksi = ?";
    PreparedStatement pstHapusStruk = koneksi.prepareStatement(sqlHapusStruk);
    pstHapusStruk.setString(1, jkod.getText());
    pstHapusStruk.executeUpdate();

    JOptionPane.showMessageDialog(null, "Transaksi berhasil dibatalkan dan stok dikembalikan.");

} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Gagal membatalkan transaksi: " + e.getMessage());
}




    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Transaksi().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField jdis;
    private javax.swing.JTextField jjum;
    private javax.swing.JTextField jkod;
    private javax.swing.JTextField jtot;
    // End of variables declaration//GEN-END:variables

    
public PageFormat getPageFormat(PrinterJob pj) {
    PageFormat pf = pj.defaultPage();
    Paper paper = pf.getPaper();

    double width = cm_to_pp(8);    // 8 cm = 226.77 point
    double height = cm_to_pp(25);  // 25 cm = 708.66 point

    paper.setSize(width, height);
    paper.setImageableArea(5, 10, width - 10, height - 20); // margin dalam kertas
    pf.setOrientation(PageFormat.PORTRAIT);
    pf.setPaper(paper);
    return pf;
}

protected static double cm_to_pp(double cm) {
    return toPPI(cm * 0.393600787); // 1 cm = 0.3936 inch
}

protected static double toPPI(double inch) {
    return inch * 72d;
}


private void removeAllArray() {
    namaAry.clear();
    jumlahAry.clear();
    hargaAry.clear();
    totalAry.clear();
    diskonAry.clear();
    subtotalAry.clear();
}

public class BillPrintable implements Printable {

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        double pageWidth = pageFormat.getImageableWidth();
        Font font = new Font("Monospaced", Font.PLAIN, 9);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int y = 20;
        int yShift = 10;
        int xLeft = 10;
        int xLeft2 = 20;
        int xRight = (int) pageWidth - 80;
        int xRight2 = (int) pageWidth - 90;

        // Fungsi untuk center text
        java.util.function.BiConsumer<String, Integer> centerText = (text, yy) -> {
            int textWidth = fm.stringWidth(text);
            int x = (int) (pageWidth - textWidth) / 2;
            g2d.drawString(text, x, yy);
        };

        // Header
        centerText.accept("-------------------------------------", y); y += yShift;
        centerText.accept("DIARK SHOES", y); y += yShift;
        centerText.accept("Jalan Oki No.6 RT003/RW006", y); y += yShift;
        centerText.accept("Kel. Ciputat kec. Ciputat", y); y += yShift;
        centerText.accept("Kab. Tangerang Selatan", y); y += yShift;
        centerText.accept("www.instagram.com/Diark_Shoes/", y); y += yShift;
        centerText.accept("-------------------------------------", y); y += yShift;
        g2d.drawString("No  : " + jkod.getText(), xLeft, y);
        g2d.drawString("Jam : " + jTextField9.getText(), xRight2, y); y += yShift;
        g2d.drawString("Tgl : " + jTextField6.getText(), xLeft, y); y += yShift;
        centerText.accept("-------------------------------------", y); y += yShift;

        // Nama Menu Header
        g2d.drawString("Nama Menu", xLeft, y);
        g2d.drawString("Subtotal", xRight, y); y += yShift;
        centerText.accept("-------------------------------------", y); y += yShift;

        // Detail item
        for (int i = 0; i < namaAry.size(); i++) {
            g2d.drawString(namaAry.get(i), xLeft, y); y += yShift;
            g2d.drawString(jumlahAry.get(i) + " * " + hargaAry.get(i), xLeft + 10, y); y += yShift;
            g2d.drawString("Diskon : " +diskonAry.get(i)  + " % " , xLeft2, y);
            g2d.drawString(subtotalAry.get(i), xRight, y); y += yShift;
        }

        centerText.accept("-------------------------------------", y); y += yShift;
        g2d.drawString("Total belanja   :", xLeft, y);
        g2d.drawString(jtot.getText(), xRight, y); y += yShift;

        g2d.drawString("Tunai           :", xLeft, y);
        g2d.drawString(jTextField7.getText(), xRight, y); y += yShift;

        g2d.drawString("Kembali         :", xLeft, y);
        g2d.drawString(jTextField8.getText(), xRight, y); y += yShift;

        centerText.accept("*************************************", y); y += yShift;
        centerText.accept("TERIMA KASIH ATAS KUNJUNGAN ANDA", y); y += yShift;
        centerText.accept("Mohon maaf, barang tidak bisa", y); y += yShift;
        centerText.accept("dikembalikan setelah dibeli.", y); y += yShift;

        return PAGE_EXISTS;
    }
}


}

