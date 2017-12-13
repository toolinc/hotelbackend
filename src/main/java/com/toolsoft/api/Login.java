package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.toolsoft.dao.LoginDao;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Singleton
public final class Login extends HttpServlet {

  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private static final String USER = "username";
  private static final String PASS = "password";
  private static final String LAST_LOGIN = "lastLogin";
  private static final String SESSION = "JSESSIONID";
  private static final int MAX_AGE = 30 * 60;
  private final LoginDao loginDao;

  @Inject
  public Login(LoginDao loginDao) {
    this.loginDao = checkNotNull(loginDao);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession httpSession = request.getSession(false);
    setCors(response);
    setJsonContentType(response);
    Builder<Object, Object> builder = ImmutableMap.builder();
    builder.put(USER, httpSession.getAttribute(USER));
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String user = request.getParameter(USER);
    String pass = request.getParameter(PASS);
    HttpSession httpSession = request.getSession();
    httpSession.setAttribute(USER, user);
    httpSession.setMaxInactiveInterval(MAX_AGE);
    Date lastLogin = loginDao.authenticateUser(user, pass);
    setCors(response);
    setJsonContentType(response);
    addCookie(response, USER, user);
    addCookie(response, LAST_LOGIN, FORMAT.format(lastLogin));
    Builder<Object, Object> builder = ImmutableMap.builder();
    builder.put(SESSION, httpSession.getId())
        .put(USER, httpSession.getAttribute(USER))
        .put(LAST_LOGIN, lastLogin);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  private static final void addCookie(HttpServletResponse response, String name, String value) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(MAX_AGE);
    response.addCookie(cookie);
  }
}
