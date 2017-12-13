package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.toolsoft.dao.LoginDao;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;

@Singleton
public final class Login extends HttpServlet {

  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private static final String USER = "username";
  private static final String PASS = "password";
  private static final String LAST_LOGIN = "lastLogin";
  private static final String SESSION = "JSESSIONID";
  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
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
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<Auth> auth = createAuth(request, builder);
    if (auth.isPresent()) {
      try {
        Date lastLogin = loginDao.authenticateUser(auth.get().user(), auth.get().password());
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(USER, auth.get().user());
        httpSession.setMaxInactiveInterval(MAX_AGE);
        builder.put(SESSION, httpSession.getId())
            .put(USER, httpSession.getAttribute(USER))
            .put(LAST_LOGIN, lastLogin);
        addCookie(response, USER, auth.get().user());
        addCookie(response, LAST_LOGIN, FORMAT.format(lastLogin));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, String.format("Unable to authenticate user %s.", auth.get().user()));
      }
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<Auth> auth = createAuth(request, builder);
    if (auth.isPresent()) {
      try {
        loginDao.registerUser(auth.get().user(), auth.get().password());
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, String.format("%s.", exc.getMessage()));
      }
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  private static final Optional<Auth> createAuth(HttpServletRequest request,
      Builder<Object, Object> builder) {
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    Optional<String> pass = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(PASS)));
    if (user.isPresent() && pass.isPresent() && !Strings.isNullOrEmpty(user.get()) && !Strings
        .isNullOrEmpty(pass.get())) {
      return Optional.of(Auth.create(user.get(), pass.get()));
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User or Password is empty.");
    }
    return Optional.empty();
  }

  @AutoValue
  static abstract class Auth {

    abstract String user();

    abstract String password();

    static Auth create(String user, String password) {
      return new AutoValue_Login_Auth(user, password);
    }
  }

  private static final void addCookie(HttpServletResponse response, String name, String value) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(MAX_AGE);
    response.addCookie(cookie);
  }
}
