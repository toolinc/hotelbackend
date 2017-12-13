package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.toolsoft.dao.ReviewDao;
import com.toolsoft.model.Review;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

@Singleton
public final class HotelReviews extends HttpServlet {

  private static final Logger log = Logger.getLogger(HotelReviews.class.getName());
  private static final String HOTEL_ID = "hotelId";
  private static final String NUM = "num";
  private static final String ANONYMOUS = "anonymous";
  private static final String ACTION = "action";
  private static final String DELETE = "delete";
  private static final String UPDATE = "update";
  private final ReviewDao reviewDao;

  @Inject
  public HotelReviews(ReviewDao reviewDao) {
    this.reviewDao = checkNotNull(reviewDao);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter(HOTEL_ID));
    String num = StringEscapeUtils.escapeHtml4(request.getParameter(NUM));
    if (hotelId == null || hotelId.isEmpty() || num == null || num.isEmpty()) {
      hotelId = ANONYMOUS;
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
    }
    Optional<Integer> numReviews = Optional.empty();
    if (Optional.ofNullable(num).isPresent()) {
      numReviews = Optional.ofNullable(Integer.valueOf(num));
    }
    setCors(response);
    setJsonContentType(response);
    response.getWriter().println(getReview(Optional.ofNullable(hotelId), numReviews));
  }

  private String getReview(Optional<String> id, Optional<Integer> numReviews) {
    StringBuilder sb = new StringBuilder("{" + System.lineSeparator() + "\"success\":");
    Optional<TreeSet<Review>> reviews = Optional.empty();
    if (id.isPresent()) {
      TreeSet<Review> set = reviewDao.get(Integer.valueOf(id.get()));
      log.info("Set size " + set.size());
      reviews = Optional.ofNullable(set);
    }
    if (reviews.isPresent() && numReviews.isPresent()) {
      sb.append("true," + System.lineSeparator());
      sb.append("\"hotelId\":\"" + id.get() + "\"," + System.lineSeparator());
      sb.append("\"reviews\": [");
      Iterator<Review> iterator = reviews.get().iterator();
      int count = 0;
      while (iterator.hasNext() && count < numReviews.get()) {
        Review review = iterator.next();
        sb.append(System.lineSeparator() + "{" + System.lineSeparator());
        sb.append("\"reviewId\": \"" + review.reviewId() + "\"," + System.lineSeparator());
        sb.append("\"title\": \"" + review.reviewTitle() + "\"," + System.lineSeparator());
        sb.append("\"user\":\"" + review.username() + "\"," + System.lineSeparator());
        sb.append("\"reviewText\":\"" + review.review() + "\"," + System.lineSeparator());
        sb.append("\"date\":\"" + review.date() + "\"," + System.lineSeparator());
        sb.append("\"rating\":\"" + review.rating() + "\"," + System.lineSeparator());
        sb.append("\"votes\":\"" + review.votes() + "\"," + System.lineSeparator());
        sb.append("\"isRecom\":\"" + review.recom() + "\"" + System.lineSeparator());
        count++;
        if (!iterator.hasNext() || count == numReviews.get()) {
          sb.append("}" + System.lineSeparator());
        } else {
          sb.append("},");
        }
      }
      sb.append("]" + System.lineSeparator() + "}");
    } else {
      sb.append("false," + System.lineSeparator());
      sb.append("\"hotelId\":");
      sb.append("\"invalid\"" + System.lineSeparator());
      sb.append("}");
    }
    return sb.toString();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String operation = request.getParameter(ACTION);
    String user = "";
    operation = StringEscapeUtils.escapeHtml4(operation);
    setCors(response);
    setJsonContentType(response);
    if (!operation.equals(DELETE)) {
      response.getWriter().println(operationQuery(operation, user, request));
    } else {
      response.getWriter().println(deleteQuery(request));
    }
  }

  private boolean operationQuery(String operation, String user, HttpServletRequest request) {
    Optional<String> id = Optional.ofNullable(request.getParameter("id"));
    Optional<String> title = Optional.ofNullable(request.getParameter("title"));
    Optional<String> text = Optional.ofNullable(request.getParameter("text"));
    Optional<Integer> rating =
        Optional.ofNullable(Integer.valueOf(request.getParameter("rating")));
    Optional<Boolean> isRecom =
        Optional.ofNullable(Boolean.parseBoolean(request.getParameter("isRecom")));
    if (id.isPresent() && title.isPresent() && text.isPresent() && rating.isPresent()
        && isRecom.isPresent()) {
      Review review = Review.builder()
          .setRecom(isRecom.get())
          .setRating(rating.get())
          .setReview(text.get())
          .setReviewId(id.get())
          .setReviewTitle(title.get())
          .setUsername(user)
          .build();
      if (UPDATE.equals(operation)) {
        reviewDao.update(review);
      } else {
        reviewDao.create(review);
      }
      return true;
    }
    return false;
  }

  private boolean deleteQuery(HttpServletRequest httpRequest) {
    Optional<String> id = Optional.ofNullable(httpRequest.getParameter("id"));
    if (id.isPresent()) {
      reviewDao.delete(id.get());
      return true;
    }
    return false;
  }
}
