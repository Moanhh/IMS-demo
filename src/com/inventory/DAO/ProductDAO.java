/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.DAO;

import com.inventory.DTO.ProductDTO;
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

// Data Access Object for Products, Purchase, Stock and Sales
public class ProductDAO {

    Connection conn = null;
    PreparedStatement prepStatement = null;
    PreparedStatement prepStatement2 = null;
    Statement s = null;
    Statement s2 = null;
    ResultSet rs = null;

    public ProductDAO() {
        try {
            conn = new ConnectionFactory().getConn();
            s = conn.createStatement();
            s2 = conn.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public ResultSet getSuppInfo() {
        try {
            String query = "SELECT * FROM suppliers";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getCustInfo() {
        try {
            String query = "SELECT * FROM customers";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getProdStock() {
        try {
            String query = "SELECT * FROM currentstock";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getProdInfo() {
        try {
            String query = "SELECT * FROM products";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public Double getProdCost(String prodCode) {
        Double costPrice = null;
        try {
            String query = "SELECT costprice FROM products WHERE productcode='" +prodCode+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                costPrice = rs.getDouble("costprice");
        } catch (SQLException e) {
            System.out.println(e);
        }
        return costPrice;
    }

    public Double getProdSell(String prodCode) {
        Double sellPrice = null;
        try {
            String query = "SELECT sellprice FROM products WHERE productcode='" +prodCode+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                sellPrice = rs.getDouble("sellprice");
        } catch (SQLException e) {
            System.out.println(e);
        }
        return sellPrice;
    }

    String suppCode;
    public String getSuppCode(String suppName) {
        try {
            String query = "SELECT suppliercode FROM suppliers WHERE fullname='" +suppName+ "'";
            rs = s.executeQuery(query);
            while (rs.next()) {
                suppCode = rs.getString("suppliercode");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return suppCode;
    }

    String prodCode;
    public String getProdCode(String prodName) {
        try {
            String query = "SELECT productcode FROM products WHERE productname='" +prodName+ "'";
            rs = s.executeQuery(query);
            while (rs.next()) {
                suppCode = rs.getString("productcode");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return prodCode;
    }

    String custCode;
    public String getCustCode(String custName) {
        try {
            String query = "SELECT customercode FROM suppliers WHERE fullname='" +custName+ "'";
            rs = s.executeQuery(query);
            while (rs.next()) {
                suppCode = rs.getString("customercode");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return custCode;
    }

    // Method to check for availability of stock in Inventory
    boolean flag = false;
    public boolean checkStock(String prodCode) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" +prodCode+ "'";
            rs = s.executeQuery(query);
            while (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return flag;
    }

    // Methods to add a new product
    public void addProductDAO(ProductDTO productDTO) {
        try {
            String query = "SELECT * FROM products WHERE productname='"
                    + productDTO.getProdName()
                    + "' AND costprice='"
                    + productDTO.getCostPrice()
                    + "' AND sellprice='"
                    + productDTO.getSellPrice()
                    + "' AND brand='"
                    + productDTO.getBrand()
                    + "'";
            rs = s.executeQuery(query);
            if (rs.next())
                JOptionPane.showMessageDialog(null, "Product has already been added.");
            else
                addFunction(productDTO);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addFunction(ProductDTO productDTO) {
        try {
            String query = "INSERT INTO products VALUES(null,?,?,?,?,?)";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, productDTO.getProdCode());
            prepStatement.setString(2, productDTO.getProdName());
            prepStatement.setDouble(3, productDTO.getCostPrice());
            prepStatement.setDouble(4, productDTO.getSellPrice());
            prepStatement.setString(5, productDTO.getBrand());

            String query2 = "INSERT INTO currentstock VALUES(?,?)";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setString(1, productDTO.getProdCode());
            prepStatement2.setInt(2, productDTO.getQuantity());

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product added and ready for sale.");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }

    // Method to add a new purchase transaction
    public void addPurchaseDAO(ProductDTO productDTO) {
        try {
            String query = "INSERT INTO purchaseinfo VALUES(null,?,?,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, productDTO.getSuppCode());
            prepStatement.setString(2, productDTO.getProdCode());
            prepStatement.setString(3, productDTO.getDate());
            prepStatement.setInt(4, productDTO.getQuantity());
            prepStatement.setDouble(5, productDTO.getTotalCost());

            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Purchase log added.");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }

        String prodCode = productDTO.getProdCode();
        if(checkStock(prodCode)) {
            try {
                String query = "UPDATE currentstock SET quantity=quantity+? WHERE productcode=?";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setInt(1, productDTO.getQuantity());
                prepStatement.setString(2, prodCode);

                prepStatement.executeUpdate();
            } catch (SQLException throwables) {
                System.out.println(throwables);
            }
        }
        else if (!checkStock(prodCode)) {
            try {
                String query = "INSERT INTO currentstock VALUES(?,?)";
                prepStatement = (PreparedStatement) conn.prepareStatement(query);
                prepStatement.setString(1, productDTO.getProdCode());
                prepStatement.setInt(2, productDTO.getQuantity());

                prepStatement.executeUpdate();
            } catch (SQLException throwables) {
                System.out.println(throwables);
            }
        }
        deleteStock();
    }

    // Method to update existing product details
    public void editProdDAO(ProductDTO productDTO) {
        try {
            String query = "UPDATE products SET productname=?,costprice=?,sellprice=?,brand=? WHERE productcode=?";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, productDTO.getProdName());
            prepStatement.setDouble(2, productDTO.getCostPrice());
            prepStatement.setDouble(3, productDTO.getSellPrice());
            prepStatement.setString(4, productDTO.getBrand());
            prepStatement.setString(5, productDTO.getProdCode());

            String query2 = "UPDATE currentstock SET quantity=? WHERE productcode=?";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setInt(1, productDTO.getQuantity());
            prepStatement2.setString(2, productDTO.getProdCode());

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product details updated.");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }

    // Methods to handle updating of stocks in Inventory upon any transaction made
    public void editPurchaseStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" +code+ "'";
            rs = s.executeQuery(query);
            if(rs.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity-? WHERE productcode=?";
                prepStatement = conn.prepareStatement(query2);
                prepStatement.setInt(1, quantity);
                prepStatement.setString(2, code);
                prepStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }
    public void editSoldStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" +code+ "'";
            rs = s.executeQuery(query);
            if(rs.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity+? WHERE productcode=?";
                prepStatement = conn.prepareStatement(query2);
                prepStatement.setInt(1, quantity);
                prepStatement.setString(2, code);
                prepStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }
    public void deleteStock() {
        try {
            String query = "DELETE FROM currentstock WHERE productcode NOT IN(SELECT productcode FROM purchaseinfo)";
            String query2 = "DELETE FROM salesinfo WHERE productcode NOT IN(SELECT productcode FROM products)";
            s.executeUpdate(query);
            s.executeUpdate(query2);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }

    // Method to permanently delete a product from inventory
    public void deleteProductDAO(String code) {
        try {
            String query = "DELETE FROM products WHERE productcode=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, code);

            String query2 = "DELETE FROM currentstock WHERE productcode=?";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setString(1, code);

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();

            JOptionPane.showMessageDialog(null, "Product has been removed.");
        } catch (SQLException e){
            System.out.println(e);
        }
        deleteStock();
    }

    public void deletePurchaseDAO(int ID){
        try {
            String query = "DELETE FROM purchaseinfo WHERE purchaseID=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, ID);
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e){
            System.out.println(e);
        }
        deleteStock();
    }

    public void deleteSaleDAO(int ID) {
        try {
            String query = "DELETE FROM salesinfo WHERE salesID=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, ID);
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e){
            System.out.println(e);
        }
        deleteStock();
    }

    // Sales transaction handling
    public void sellProductDAO(ProductDTO productDTO, String username) {
        int quantity = 0;
        String prodCode ;
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" +productDTO.getProdCode()+ "'";
            rs = s.executeQuery(query);
            while (rs.next()) {
                prodCode = rs.getString("productcode");
                quantity = rs.getInt("quantity");
            }
            if (productDTO.getQuantity()>quantity)
                JOptionPane.showMessageDialog(null, "Insufficient stock for this product.");
            else if (productDTO.getQuantity()<=0)
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity");
            else {
                String stockQuery = "UPDATE currentstock SET quantity=quantity-'"
                        +productDTO.getQuantity()
                        +"' WHERE productcode='"
                        +productDTO.getProdCode()
                        +"'";
                String salesQuery = "INSERT INTO salesinfo(date,productcode,customercode,quantity,revenue,soldby)" +
                        "VALUES('"+productDTO.getDate()+"','"+productDTO.getProdCode()+"','"+productDTO.getCustCode()+
                        "','"+productDTO.getQuantity()+"','"+productDTO.getTotalRevenue()+"','"+username+"')";
                s.executeUpdate(stockQuery);
                s.executeUpdate(salesQuery);
                JOptionPane.showMessageDialog(null, "Product sold.");
            }
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }

    // Products data set retrieval for display
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT productcode,productname,costprice,sellprice,brand FROM products ORDER BY pid";
            rs = s.executeQuery(query);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return rs;
    }

    // Purchase table data set retrieval
    public ResultSet getPurchaseInfo() {
        try {
            String query = "SELECT PurchaseID,purchaseinfo.ProductCode,ProductName,Quantity,Totalcost " +
                    "FROM purchaseinfo INNER JOIN products " +
                    "ON products.productcode=purchaseinfo.productcode ORDER BY purchaseid;";
            rs = s.executeQuery(query);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return rs;
    }

    // Stock table data set retrieval
    public ResultSet getCurrentStockInfo() {
        try {
            String query = """
                    SELECT currentstock.ProductCode,products.ProductName,
                    currentstock.Quantity,products.CostPrice,products.SellPrice
                    FROM currentstock INNER JOIN products
                    ON currentstock.productcode=products.productcode;
                    """;
            rs = s.executeQuery(query);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return rs;
    }

    // Sales table data set retrieval
    public ResultSet getSalesInfo() {
        try {
            String query = """
                    SELECT salesid,salesinfo.productcode,productname,
                    salesinfo.quantity,revenue,users.name AS Sold_by
                    FROM salesinfo INNER JOIN products
                    ON salesinfo.productcode=products.productcode
                    INNER JOIN users
                    ON salesinfo.soldby=users.username;
                    """;
            rs = s.executeQuery(query);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return rs;
    }

    // Search method for products
    public ResultSet getProductSearch(String text) {
        try {
            String query = "SELECT productcode,productname,costprice,sellprice,brand FROM products " +
                    "WHERE productcode LIKE '%"+text+"%' OR productname LIKE '%"+text+"%' OR brand LIKE '%"+text+"%'";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getProdFromCode(String text) {
        try {
            String query = "SELECT productcode,productname,costprice,sellprice,brand FROM products " +
                    "WHERE productcode='" +text+ "' LIMIT 1";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    // Search method for sales
    public ResultSet getSalesSearch(String text) {
        try {
            String query = """
                           SELECT salesid,salesinfo.productcode,productname,
                                               salesinfo.quantity,revenue,users.name AS Sold_by
                                               FROM salesinfo INNER JOIN products
                                               ON salesinfo.productcode=products.productcode
                                               INNER JOIN users
                                               ON salesinfo.soldby=users.username
                                               INNER JOIN customers
                                               ON customers.customercode=salesinfo.customercode
                           WHERE salesinfo.productcode LIKE '%"""+text+"%' OR productname LIKE '%"+text+"%' " +
                    "OR users.name LIKE '%"+text+"%' OR customers.fullname LIKE '%"+text+"%' ORDER BY salesid;";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    // Search method for purchase logs
    public ResultSet getPurchaseSearch(String text) {
        try {
            String query = "SELECT PurchaseID,purchaseinfo.productcode,products.productname,quantity,totalcost " +
                    "FROM purchaseinfo INNER JOIN products ON purchaseinfo.productcode=products.productcode " +
                    "INNER JOIN suppliers ON purchaseinfo.suppliercode=suppliers.suppliercode" +
                    "WHERE PurchaseID LIKE '%"+text+"%' OR productcode LIKE '%"+text+"%' OR productname LIKE '%"+text+"%' " +
                    "OR suppliers.fullname LIKE '%"+text+"%' OR purchaseinfo.suppliercode LIKE '%"+text+"%' " +
                    "OR date LIKE '%"+text+"%' ORDER BY purchaseid";
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public ResultSet getProdName(String code) {
        try {
            String query = "SELECT productname FROM products WHERE productcode='" +code+ "'";
            rs = s.executeQuery(query);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return rs;
    }

    public String getSuppName(int ID) {
        String name = null;
        try {
            String query = "SELECT fullname FROM suppliers " +
                    "INNER JOIN purchaseinfo ON suppliers.suppliercode=purchaseinfo.suppliercode " +
                    "WHERE purchaseid='" +ID+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                name = rs.getString("fullname");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return name;
    }

    public String getCustName(int ID) {
        String name = null;
        try {
            String query = "SELECT fullname FROM customers " +
                    "INNER JOIN salesinfo ON customers.customercode=salesinfo.customercode " +
                    "WHERE salesid='" +ID+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                name = rs.getString("fullname");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return name;
    }

    public String getPurchaseDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM purchaseinfo WHERE purchaseid='" +ID+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                date = rs.getString("date");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return date;
    }
    public String getSaleDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM salesinfo WHERE salesid='" +ID+ "'";
            rs = s.executeQuery(query);
            if (rs.next())
                date = rs.getString("date");
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
        return date;
    }


    // Method to display product-related data set in tabular form
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
