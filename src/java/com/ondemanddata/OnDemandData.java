/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ondemanddata;

/**
 *
 * @author Muhammad
 */
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

@ManagedBean(name = "onDemandData")
@ViewScoped
public class OnDemandData implements Serializable {

    private LazyCustomerDataModel lazyModel;

    @PostConstruct
    public void init() {
        createTableIfNotExists();
        if (getCustomerCount() < 100000) {
            insertCustomers(100000);
        }
        lazyModel = new LazyCustomerDataModel();
    }

    public LazyCustomerDataModel getLazyModel() {
        return lazyModel;
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS customers ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "name VARCHAR(100),"
                + "country VARCHAR(100),"
                + "date DATE)";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCustomerCount() {
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void insertCustomers(int count) {
    System.out.println("Inserting " + count + " customers...");
    String[] countries = {"Nigeria", "Ghana", "Kenya", "South Africa", "Egypt"};
    Random rand = new Random();
    final int batchSize = 1000;  // Use constant for batch size

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(name, country, date) VALUES (?, ?, ?)")) {

        conn.setAutoCommit(false);
        
        for (int i = 0; i < count; i++) {
            stmt.setString(1, "Customer " + i);
            stmt.setString(2, countries[rand.nextInt(countries.length)]);
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.addBatch();

            // Execute batch every 1000 records, but not at i=0
            if (i > 0 && (i + 1) % batchSize == 0) {
                stmt.executeBatch();
                conn.commit();  // Commit after each batch
                System.out.println("Inserted " + (i + 1) + " records");
            }
        }
        
        // Execute remaining records in the batch
        stmt.executeBatch();
        conn.commit();
        System.out.println("Insert complete. Total inserted: " + count);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    public static class Customer implements Serializable {

        private int id;
        private String name;
        private String country;
        private Date date;

        public Customer() {
        }

        public Customer(int id, String name, String country, Date date) {
            this.id = id;
            this.name = name;
            this.country = country;
            this.date = date;
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

}
