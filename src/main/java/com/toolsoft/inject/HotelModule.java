package com.toolsoft.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.sql.DataSource;

/**
 * Specifies the required instances that need to be injected into the classes.
 */
public class HotelModule extends AbstractModule {

  private static final String DRIVER = "com.mysql.jdbc.Driver";
  private static final String URL = "jdbc:mysql://google/HotelAdvisor?cloudSqlInstance=hotelbackend:us-central1:hoteldb&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=98329973&useSSL=false";

  @Override
  protected void configure() {
  }

  @Provides
  DataSource providesPooledDataSource() throws IOException, PropertyVetoException {
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    cpds.setDriverClass(DRIVER);
    cpds.setJdbcUrl(URL);
    cpds.setMinPoolSize(2);
    cpds.setAcquireIncrement(1);
    cpds.setMaxPoolSize(8);
    cpds.setMaxStatements(100);
    return cpds;
  }
}
