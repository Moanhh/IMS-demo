/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author minhanhhoang
 */

//Class to retrieve connection for database and login verfication.
public class ConnectionFactory {

    

    Connection conn ;
    Statement s ;
    ResultSet rs ;

    public ConnectionFactory(){
        
        

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "Moanh20@");
            s = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    public Connection getConn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "Moanh20@");
            System.out.println("Connected successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return conn;
    }

    //Login verification method
    public boolean checkLogin(String username, String password, String userType){
        String query = "SELECT * FROM users WHERE username='"
                + username
                + "' AND password='"
                + password
                + "' AND usertype='"
                + userType
                + "' LIMIT 1";

        try {
            rs = s.executeQuery(query);
            if(rs.next()) return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }
    
}
