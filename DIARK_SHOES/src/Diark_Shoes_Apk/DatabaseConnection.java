/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Diark_Shoes_Apk;

/**
 *
 * @author DELL
 */
    import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    public static Connection connect() {
        Connection koneksi = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            koneksi = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/db_diark_shoes", "root", "");
            System.out.println("Koneksi berhasil");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return koneksi;
    }

}


