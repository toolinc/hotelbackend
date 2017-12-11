package hotelapp.service;

import hotelapp.HotelCacheHelper;
import hotelapp.cache.HotelCacheThreadSafe;
import hotelapp.client.TouristAttractionFinder;
import hotelapp.dao.HotelDao;
import hotelapp.dao.HotelDaoSql;
import hotelapp.model.Hotel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * class that creates the Json Object with the atractions information
 */
public final class HotelAttractionAction implements HotelAction {

    private static final Logger log = LogManager.getRootLogger();
    private static final String ID = "hotelId";
    private static final String RADIUS = "radius";
    private HotelDao hotelDao;

    public HotelAttractionAction() {
        hotelDao = new HotelDaoSql();
        ;
    }

    @Override
    public String doQuery(HttpServletRequest httpRequest) {
        Optional<String> id = Optional.ofNullable(httpRequest.getParameter(ID));
        Optional<String> radius = Optional
                .ofNullable(httpRequest.getParameter(RADIUS));
        Optional<Hotel> hotel = Optional.empty();

        System.out.println("ID " + id.get());
        System.out.println("radius " + radius.get());

        if (id.isPresent()) {
            hotel = Optional.ofNullable(hotelDao.getHotel(id.get()));
        }

        System.out.println("Hotel " + hotel.get().getName());
        if (hotel.isPresent() && id.isPresent() && radius.isPresent()) {
            log.debug(String.format("hotelId: %s, radius: %s", id, radius));
            TouristAttractionFinder attractionFinder =
                    new TouristAttractionFinder((HotelCacheThreadSafe) HotelCacheHelper.HOTEL_DATA);
            return attractionFinder.getHotelAttractions(hotel.get(), Integer.valueOf(radius.get()));
        }
        log.debug("Returning default response.");
        return buildDefaultResponse(id);
    }

    private String buildDefaultResponse(Optional<String> id) {
        StringBuilder sb = new StringBuilder("{" + System.lineSeparator() + "\"success\":")
                .append("false," + System.lineSeparator());
        if (!id.isPresent()) {
            sb.append("\"hotelId\":");
        } else {
            sb.append("\"radius\":");
        }
        return sb.append("\"invalid\"" + System.lineSeparator())
                .append("}").toString();
    }
}
