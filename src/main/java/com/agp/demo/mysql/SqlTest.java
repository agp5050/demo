package com.agp.demo.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlTest {
    public static void main(String[] args) {
        try {
            Connection root = DriverManager.getConnection("jdbc:mysql://node1:3306/test", "root", "123456");
            PreparedStatement preparedStatement = root.prepareStatement("select * from tbl1 where a > ? and b < ?");
            preparedStatement.setInt(1,1000);
            preparedStatement.setInt(2,10000);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i=0,n=metaData.getColumnCount();i<n;i++){
                System.out.print("getColumnName "+(i+1)+" :"+metaData.getColumnName(i+1)+"\t");
                System.out.print("getColumnType "+(i+1)+" :"+metaData.getColumnTypeName(i+1)+"\t");
            }
            System.out.println();
            while (resultSet.next()){
                for (int i=0,n=metaData.getColumnCount();i<n;i++){
                    System.out.print(resultSet.getObject(i+1)+"\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
