package com.toolsoft.api;

import com.google.gson.Gson;
import com.toolsoft.dao.HotelDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class HotelInfo extends HttpServlet {

  @Inject
  private HotelDao hotelDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");
    PrintWriter out = response.getWriter();
    Gson gson = new Gson();
    out.print(gson.toJson(hotelDao.getAll()));
  }
}
