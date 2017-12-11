package hotelapp.service;

import hotelapp.dao.ReviewDaoSql;
import hotelapp.model.InvalidRatingException;
import hotelapp.model.Review;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;

/**
 * class that creates the Json Object with the reviews information
 */
public final class HotelReviewAction implements HotelAction {

    private static final String ID = "hotelId";
    private static final String NUMREVIEWS = "num";
    private final ReviewDaoSql reviewDao;

    public HotelReviewAction() {
        reviewDao = new ReviewDaoSql();
    }

    @Override
    public String doQuery(HttpServletRequest httpRequest) {
        StringBuilder sb = new StringBuilder("{" + System.lineSeparator() + "\"success\":");
        Optional<String> id = Optional.ofNullable(httpRequest.getParameter(ID));
        Optional<String> num = Optional.ofNullable(httpRequest.getParameter(NUMREVIEWS));
        Optional<Integer> numReviews = Optional.empty();
        Optional<TreeSet<Review>> reviews = Optional.empty();
        if (num.isPresent()) {
            numReviews = Optional.ofNullable(Integer.valueOf(num.get()));
        }
        if (id.isPresent()) {
            // reviews = Optional.ofNullable(HotelCacheHelper.HOTEL_DATA.getReviews(id.get()));
            TreeSet<Review> set = reviewDao.get(Integer.valueOf(id.get()));
            System.out.println("Set size " + set.size());
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
                sb.append("\"reviewId\": \"" + review.getReviewId() + "\"," + System.lineSeparator());
                sb.append("\"title\": \"" + review.getReviewTitle() + "\"," + System.lineSeparator());
                sb.append("\"user\":\"" + review.getUsername() + "\"," + System.lineSeparator());
                sb.append("\"reviewText\":\"" + review.getReview() + "\"," + System.lineSeparator());
                sb.append("\"date\":\"" + review.getDate() + "\"," + System.lineSeparator());
                sb.append("\"rating\":\"" + review.getRating() + "\"," + System.lineSeparator());
                sb.append("\"isRecom\":\"" + review.isRecom() + "\"" + System.lineSeparator());
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


    public boolean operationQuery(String user, HttpServletRequest httpRequest) throws InvalidRatingException, ParseException {
        Optional<String> operation = Optional.ofNullable(httpRequest.getParameter("action"));
        Optional<String> id = Optional.ofNullable(httpRequest.getParameter("id"));
        Optional<String> title = Optional.ofNullable(httpRequest.getParameter("title"));
        Optional<String> text = Optional.ofNullable(httpRequest.getParameter("text"));
        Optional<Integer> rating = Optional.ofNullable(Integer.valueOf(httpRequest.getParameter("rating")));
        Optional<Boolean> isRecom = Optional.ofNullable(Boolean.parseBoolean(httpRequest.getParameter("isRecom")));
        if (id.isPresent() && title.isPresent() && text.isPresent() && rating.isPresent() && isRecom.isPresent()) {
            Review review = Review.Builder.newBuilder().setRecom(isRecom.get()).setRating(rating.get())
                    .setReview(text.get()).setReviewId(id.get()).setReviewTitle(title.get()).setUsername(user).build();
            if (operation.get().equals("update")) {
                reviewDao.update(review);
            } else {
                reviewDao.create(review);
            }
            return true;
        }
        return false;
    }

    public boolean deleteQuery(HttpServletRequest httpRequest) {
        Optional<String> id = Optional.ofNullable(httpRequest.getParameter("id"));
        if (id.isPresent()) {
            reviewDao.delete(id.get());
            return true;
        }
        return false;
    }
}
