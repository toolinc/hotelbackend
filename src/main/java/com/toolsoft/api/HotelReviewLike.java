package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.toolsoft.dao.ReviewLikeDao;
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
public class HotelReviewLike extends HttpServlet {

  private static final String USER = "username";
  private static final String REVIEW = "reviewId";
  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
  private final ReviewLikeDao reviewLikeDao;

  @Inject
  public HotelReviewLike(ReviewLikeDao reviewLikeDao) {
    this.reviewLikeDao = checkNotNull(reviewLikeDao);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Builder<Object, Object> builder = ImmutableMap.builder();
    Optional<Vote> vote = createVote(request, builder);
    if (vote.isPresent()) {
      try {
        reviewLikeDao.store(vote.get().reviewId(), vote.get().user());
        builder.put(SUCCESS, Boolean.TRUE)
            .put(MESSAGE, String.format("A new review %s was added.", vote.get().reviewId()));
      } catch (IllegalStateException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, String.format("%s", exc.getMessage()));
      }
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().printf(new Gson().toJson(builder.build()));
  }

  private static final Optional<Vote> createVote(HttpServletRequest request,
      Builder<Object, Object> builder) {
    Optional<String> user = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(USER)));
    Optional<String> review = Optional
        .ofNullable(StringEscapeUtils.escapeHtml4(request.getParameter(REVIEW)));
    if (user.isPresent() && review.isPresent() && !Strings.isNullOrEmpty(user.get())
        && !Strings.isNullOrEmpty(review.get())) {
      try {
        return Optional.of(Vote.create(user.get(), Integer.valueOf(review.get())));
      } catch (NumberFormatException exc) {
        builder.put(SUCCESS, Boolean.FALSE)
            .put(MESSAGE, "ReviewId is not a number.");
      }
    } else {
      builder.put(SUCCESS, Boolean.FALSE)
          .put(MESSAGE, "User or Review is empty.");
    }
    return Optional.empty();
  }

  @AutoValue
  static abstract class Vote {

    abstract String user();

    abstract int reviewId();

    static Vote create(String user, int reviewId) {
      return new AutoValue_HotelReviewLike_Vote(user, reviewId);
    }
  }
}
