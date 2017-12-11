package com.toolsoft.api;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Singleton
public final class Login extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession httpSession = request.getSession();
    httpSession.setMaxInactiveInterval(30*60);
    Cookie userName = new Cookie("user", "jovani");
    userName.setMaxAge(30*60);
    response.addCookie(userName);

    response.setContentType("application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");
    PrintWriter out = response.getWriter();
    out.printf("sessionId: %s, attrinutes: %s", httpSession.getId(),
        httpSession.getAttributeNames().hasMoreElements());
  }
}
