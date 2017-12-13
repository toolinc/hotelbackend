package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.hotelsToJson;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.toolsoft.dao.HotelLikeDao;
import com.toolsoft.model.Hotel;
import java.io.IOException;
import java.util.List;
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
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    setCors(response);
    setJsonContentType(response);
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    if (user.isPresent()) {
      try {
        List<Hotel> hotels = hotelLikeDao.get(user.get());
        response.getWriter().print(hotelsToJson(hotels));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, exc.getMessage());
        response.getWriter().printf(new Gson().toJson(builder.build()));
      }
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User is empty.");
      response.getWriter().printf(new Gson().toJson(builder.build()));
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<Like> like = createLike(request, builder);
    if (like.isPresent()) {
      try {
        hotelLikeDao.store(like.get().hotelId(), like.get().user());
        builder.put(SUCCESS, Boolean.TRUE)
            .put(MESSAGE, String.format("A new hotel %s was added.", like.get().hotelId()));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, String.format("%s", exc.getMessage()));
      }
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
    resp.setHeader("Access-Control-Allow-Headers", "GET, POST, DELETE");
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    setCors(response);
    setJsonContentType(response);
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    if (user.isPresent()) {
      try {
        int removed = hotelLikeDao.remove(user.get());
        builder.put(SUCCESS, Boolean.TRUE)
            .put(MESSAGE, String.format("#%d Hotels were removed.", removed));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, exc.getMessage());
      }
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User is empty.");
    }
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  private static final Optional<Like> createLike(HttpServletRequest request,
      Builder<Object, Object> builder) {
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    Optional<String> hotel = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(HOTEL)));
    if (user.isPresent() && hotel.isPresent() && !Strings.isNullOrEmpty(user.get()) && !Strings
        .isNullOrEmpty(hotel.get())) {
      try {
        return Optional.of(Like.create(user.get(), Integer.valueOf(hotel.get())));
      } catch (NumberFormatException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, "HotelId is not a number.");
      }
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User or Hotel is empty.");
    }
    return Optional.empty();
  }

  @AutoValue
  static abstract class Like {

    abstract String user();

    abstract int hotelId();

    static Like create(String user, int hotelId) {
      return new AutoValue_HotelLike_Like(user, hotelId);
    }
  }
}
