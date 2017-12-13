package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.toolsoft.dao.HotelLikeDao;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

@Singleton
public final class HotelLike extends HttpServlet {

  private static final String USER = "username";
  private static final String HOTEL = "hotelId";
  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
  private final HotelLikeDao hotelLikeDao;

  @Inject
  public HotelLike(HotelLikeDao hotelLikeDao) {
    this.hotelLikeDao = checkNotNull(hotelLikeDao);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    Optional<String> hotel = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(HOTEL)));
    Builder<Object, Object> builder = ImmutableMap.builder();
    if (user.isPresent() && hotel.isPresent() && !Strings.isNullOrEmpty(user.get()) && !Strings
        .isNullOrEmpty(hotel.get())) {
      try {
        hotelLikeDao.store(Integer.valueOf(hotel.get()), user.get());
        builder.put(SUCCESS, Boolean.TRUE)
            .put(MESSAGE, String.format("A new hotel %s was added.", hotel.get()));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, String.format("%s", exc.getMessage()));
      } catch (NumberFormatException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, "HotelId is not a number.");
      }
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User or Hotel is empty.");
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }
}
