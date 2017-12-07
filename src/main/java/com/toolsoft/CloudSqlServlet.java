package com.toolsoft;

import com.google.common.base.Stopwatch;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CloudSQL", value = "/sql")
public class CloudSqlServlet extends HttpServlet {

  Connection conn;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    final String selectSql = "SELECT * FROM Hotels";

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }

    PrintWriter out = resp.getWriter();
    resp.setContentType("text/plain");

    Stopwatch stopwatch = Stopwatch.createStarted();
    try (ResultSet rs = conn.prepareStatement(selectSql).executeQuery()) {
      stopwatch.stop();
      while (rs.next()) {
        String name = rs.getString("name");
        String city = rs.getString("city");
        out.print("Name: " + name + " city: " + city + "\n");
      }
      out.println("Elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    } catch (SQLException e) {
      throw new ServletException("SQL error", e);
    }
  }

  @Override
  public void init() throws ServletException {
    try {
      String url;

      Properties properties = new Properties();
      try {
        properties.load(
            getServletContext().getResourceAsStream("/WEB-INF/classes/config.properties"));
        url = properties.getProperty("sqlUrl");
      } catch (IOException e) {
        log("no property", e);  // Servlet Init should never fail.
        return;
      }

      log("connecting to: " + url);
      try {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url);
      } catch (ClassNotFoundException e) {
        throw new ServletException("Error loading JDBC Driver", e);
      } catch (SQLException e) {
        throw new ServletException("Unable to connect to PostGre", e);
      }

    } finally {
      // Nothing really to do here.
    }
  }
}
