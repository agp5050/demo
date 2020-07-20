package com.agp.demo.timescaledb;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ClientDemo {
    @Test
    public void testDirectConnect(){
        Properties props = new Properties();
        props.put("jdbc.url", "jdbc:postgresql://172.25.20.85:5432/public");
        props.put("user", "postgres");
        props.put("password", "MYRFxfkCj4b3iFaOEEgF");
//        props.put("ssl", "true");
//        props.put("sslmode", "verify-ca");
//        props.put("sslrootcert", "/path/to/ca.pem");
        try {
            Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"), props);
            System.out.println(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testJdbcMysqlConnect() throws InterruptedException, SQLException {
        HikariConfig config=new HikariConfig();
        config.setUsername("wk_db_user");
        config.setPassword("J@FgbH5_i8hQ");
        config.setJdbcUrl("jdbc:mysql://192.168.161.219:3308/bee_release?useUnicode=true&serverTimezone=GMT%2B8&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("hikari-pool");
        config.setMaximumPoolSize(100);
        config.setConnectionInitSql("select 1");
        HikariDataSource dataSource=new HikariDataSource(config);
        List<Thread> list=new ArrayList<>();
        for (int  i=30;i>0;i--){
            int j = i;
          Thread thread=  new Thread(){
                public void run(){
                    Connection connection=null;
                    try {
                        System.out.println("j:"+j);
                         connection = dataSource.getConnection();
                        System.out.println(connection.hashCode()+", index:"+(30-j)+" System.currentMilliseconds:"+System.currentTimeMillis());
                        Thread.sleep(1000);
//                        connection = dataSource.getConnection();
//
//                        System.out.println("After "+connection.hashCode()+", index:"+(30-j)+" System.currentMilliseconds:"+System.currentTimeMillis());

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
          list.add(thread);
          thread.start();
        }
        for (Thread thread:list){
//            thread.join();
            thread=null;
        }
       Connection connection = dataSource.getConnection();
        System.out.println("Finished.");


    }

    @Test
    public void testTimeScaleDbJdbc() throws SQLException {
        PGSimpleDataSource dataSource=new PGSimpleDataSource();
        dataSource.setUser("postgres");
        dataSource.setPassword("MYRFxfkCj4b3iFaOEEgF");
        dataSource.setURL("jdbc:postgresql://172.25.20.85:5432/");
        dataSource.setDatabaseName("public");
        Connection connection = dataSource.getConnection();
        System.out.println(connection.hashCode());
    }

    @Test
    public void testDuridJdbc() throws SQLException {
        DruidDataSource dataSource=new DruidDataSource();
        dataSource.setUsername("postgres");
        dataSource.setPassword("MYRFxfkCj4b3iFaOEEgF");
        dataSource.setUrl("jdbc:postgresql://172.25.20.85:5432/public");
        dataSource.setDriver(new Driver());
        long connectCount = dataSource.getConnectCount();
        System.out.println(connectCount+" count");
        System.out.println(dataSource.getConnection().hashCode());
    }



    public static void main(String[] args) {
        System.out.println((char)63);
    }
}
