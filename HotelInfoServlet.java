package hotelapp.servlet;

import hotelapp.service.HotelInfoAction;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HotelInfoServlet extends HttpServlet {

    private HotelInfoAction action;
    // private HotelDao hotelDao;

    public HotelInfoServlet() {
        action = new HotelInfoAction();
        // hotelDao = new HotelDaoSql();
    }

    /**
     * A method that gets executed when the get request is sent to the
     * HelloServlet from the path /hotelInfo
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {
        PrintWriter out = response.getWriter();

        System.out.println("Inside doGet Hotel");

        // hotelDao.getAll();

        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String city = request.getParameter("city");

        System.out.println(city);

        // if (hotelId == null || hotelId.isEmpty()) {
        //     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // } else {
        response.setStatus(HttpServletResponse.SC_OK);
        // }
        city = StringEscapeUtils.escapeHtml4(city); // need to "clean up" whatever
        // out.println(action.doQuery(request));
        out.println(action.getQuery(city));
    }
}