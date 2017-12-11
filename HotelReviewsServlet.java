package hotelapp.servlet;

import hotelapp.model.InvalidRatingException;
import hotelapp.service.HotelReviewAction;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

public class HotelReviewsServlet extends LoginBaseServlet {

    private HotelReviewAction action;

    public HotelReviewsServlet() {
        action = new HotelReviewAction();
    }

    /**
     * A method that gets executed when the get request is sent to the
     * HelloServlet from the path /reviews
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String hotelId = request.getParameter("hotelId");
        String num = request.getParameter("num");
        if (hotelId == null || hotelId.isEmpty() || num == null || num.isEmpty()) {
            hotelId = "anonymous";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        hotelId = StringEscapeUtils.escapeHtml4(hotelId); // need to "clean up" whatever
        out.println(action.doQuery(request));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String operation = request.getParameter("action");
        String user = getUsername(request);
        operation = StringEscapeUtils.escapeHtml4(operation); // need to "clean up" whatever
        if (!operation.equals("delete")) {
            try {
                out.println(action.operationQuery(user, request));
            } catch (InvalidRatingException | ParseException e) {
                e.printStackTrace();
            }
        } else {
            out.println(action.deleteQuery(request));
        }
    }
}
