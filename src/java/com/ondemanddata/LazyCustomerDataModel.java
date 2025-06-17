/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ondemanddata;

/**
 *
 * @author dell
 */


import com.ondemanddata.OnDemandData.Customer;
import org.primefaces.model.LazyDataModel;

import java.sql.*;
import java.util.*;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

public class LazyCustomerDataModel extends LazyDataModel<Customer> {

    private List<Customer> datasource = new ArrayList<>();

   
    private int getTotalRowCount() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM customers");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Customer> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        datasource.clear();

        String query = "SELECT id, name, country, date FROM customers LIMIT ?, ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, first);
            stmt.setInt(2, pageSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                datasource.add(new OnDemandData.Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getDate("date")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setRowCount(getTotalRowCount()); 
        return datasource;
    }
}
