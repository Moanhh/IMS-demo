/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.DAO;

import com.inventory.DTO.CustomerDTO;
import com.inventory.Database.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author minhanhhoang
 */

// Data Access Object for Customers
public class CustomerDAO {
    Connection conn ;
    PreparedStatement prepStatement;
    Statement s ;
    ResultSet rs ;

    public CustomerDAO() {
        try {
            conn = new ConnectionFactory().getConn();
            s = conn.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Methods to add new custoemr
    public void addCustomerDAO(CustomerDTO customerDTO) {
        try {
            String query = "SELECT * FROM customers WHERE fullname='"
                    +customerDTO.getFullName()
                    + "' AND location='"
                    +customerDTO.getLocation()
                    + "' AND phone='"
                    +customerDTO.getPhone()
                    + "'";
            rs = s.executeQuery(query);
            if (rs.next())
                JOptionPane.showMessageDialog(null, "Customer already exists.");
            else
                addFunction(customerDTO);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addFunction(CustomerDTO customerDTO) {
        try {
            String query = "INSERT INTO customers VALUES(null,?,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, customerDTO.getCustCode());
            prepStatement.setString(2, customerDTO.getFullName());
            prepStatement.setString(3, customerDTO.getLocation());
            prepStatement.setString(4, customerDTO.getPhone());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "New customer has been added.");
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    // Method to edit existing customer details
    public  void editCustomerDAO(CustomerDTO customerDTO) {
        try {
            String query = "UPDATE customers SET fullname=?,location=?,phone=? WHERE customercode=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, customerDTO.getFullName());
            prepStatement.setString(2, customerDTO.getLocation());
            prepStatement.setString(3, customerDTO.getPhone());
            prepStatement.setString(4, customerDTO.getCustCode());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer details have been updated.");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Method to delete existing customer
    public void deleteCustomerDAO(String custCode) {
        try {
            String query = "DELETE FROM customers WHERE customercode='" +custCode+ "'";
            s.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Customer removed.");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Method to retrieve data set to be displayed
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT customercode,fullname,location,phone FROM customers";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    // Method to retrieve search data
    public ResultSet getCustomerSearch(String text) {
        try {
            String query = "SELECT customercode,fullname,location,phone FROM customers " +
                    "WHERE customercode LIKE '%"+text+"%' OR fullname LIKE '%"+text+"%' OR " +
                    "location LIKE '%"+text+"%' OR phone LIKE '%"+text+"%'";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getCustName(String custCode) {
        try {
            String query = "SELECT * FROM customers WHERE customercode='" +custCode+ "'";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getProdName(String prodCode) {
        try {
            String query = "SELECT productname,currentstock.quantity FROM products " +
                    "INNER JOIN currentstock ON products.productcode=currentstock.productcode " +
                    "WHERE currentstock.productcode='" +prodCode+ "'";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    // Method to display data set in tabular form
    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<String>();
        int colCount = metaData.getColumnCount();

        for (int col=1; col <= colCount; col++){
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int col=1; col<=colCount; col++) {
                vector.add(resultSet.getObject(col));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

}
