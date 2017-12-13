package com.toolsoft.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.toolsoft.cache.HotelCacheThreadSafe;
import com.toolsoft.cache.ReentrantReadWriteLock;
import com.toolsoft.client.TouristAttractionFinder;
import com.toolsoft.dao.HotelDao;
import com.toolsoft.dao.HotelDaoSql;
import com.toolsoft.dao.HotelLikeDao;
import com.toolsoft.dao.HotelLikeDaoSql;
import com.toolsoft.dao.LoginDao;
import com.toolsoft.dao.LoginDaoSql;
import com.toolsoft.dao.ReviewDao;
import com.toolsoft.dao.ReviewDaoSql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Specifies the required instances that need to be injected into the classes.
 */
public class HotelModule extends AbstractModule {

  private static final String DRIVER = "com.mysql.jdbc.Driver";
  private static final String URL = "jdbc:mysql://google/HotelAdvisor?cloudSqlInstance=hotelbackend:us-central1:hoteldb&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=98329973&useSSL=false";

  @Override
  protected void configure() {
    bind(HotelDao.class).to(HotelDaoSql.class);
    bind(HotelLikeDao.class).to(HotelLikeDaoSql.class);
    bind(LoginDao.class).to(LoginDaoSql.class);
    bind(ReviewDao.class).to(ReviewDaoSql.class);
    bind(ReentrantReadWriteLock.class);
    bind(HotelCacheThreadSafe.class);
    bind(TouristAttractionFinder.class);
  }

  @Provides
  Connection providesConnection() throws ClassNotFoundException, SQLException {
    Class.forName(DRIVER);
    return DriverManager.getConnection(URL);
  }
}
