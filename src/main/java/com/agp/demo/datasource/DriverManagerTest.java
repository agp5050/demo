package com.agp.demo.datasource;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DriverManagerTest {
    public static void main(String[] args) throws SQLException {
        //SPI
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        DriverManager.getConnection("url"); //从备选列表driver中，遍历进行getConnection。哪个能成用哪个。
        while (drivers.hasMoreElements()){
            Driver driver = drivers.nextElement();
            System.out.println(driver.getClass().getName());
        }
    }
}
