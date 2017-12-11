package hotelapp.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Specifies the behavior of the Hotel action to be performed.
 */
public interface HotelAction {

    /**
     * Performs the desired action for a given {@link HttpServletRequest}. And produces the desired
     * result as a {@link String}.
     */
    String doQuery(HttpServletRequest httpRequest);
}
