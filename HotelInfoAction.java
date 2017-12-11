package hotelapp.service;

import hotelapp.HotelCacheHelper;
import hotelapp.dao.HotelDao;
import hotelapp.dao.HotelDaoSql;
import hotelapp.model.Address;
import hotelapp.model.Hotel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * class that creates the Json Object with the Hotel information
 */
public final class HotelInfoAction implements HotelAction {

    private static final String ID = "hotelId";
    private HotelDao hotelDao;


    public HotelInfoAction() {
        hotelDao = new HotelDaoSql();
        ;
    }

    @Override
    public String doQuery(HttpServletRequest httpRequest) {
        Optional<String> id = Optional.ofNullable(httpRequest.getParameter(ID));
        Optional<String> hotelName = Optional.empty();
        if (id.isPresent()) {
            hotelName = Optional.ofNullable(HotelCacheHelper.HOTEL_DATA.getHotelName(id.get()));
        }
        StringBuilder sb = new StringBuilder("{" + System.lineSeparator() + "\"success\":");
        if (id.isPresent() && hotelName.isPresent()) {
            Address address = HotelCacheHelper.HOTEL_DATA.getAddress(id.get());
            sb.append("true," + System.lineSeparator());
            sb.append("\"hotelId\":\"" + id.get() + "\"," + System.lineSeparator());
            sb.append("\"name\":\"" + hotelName.get() + "\"," + System.lineSeparator());
            sb.append("\"addr\":\"" + address.getStreetAddress() + "\"," + System.lineSeparator());
            sb.append("\"city\":\"" + address.getCity() + "\"," + System.lineSeparator());
            sb.append("\"state\":\"" + address.getState() + "\"," + System.lineSeparator());
            sb.append("\"lat\":\"" + address.getLat() + "\"," + System.lineSeparator());
            sb.append("\"lng\":\"" + address.getLon() + "\"" + System.lineSeparator());
            sb.append("}");
        } else {
            sb.append("false," + System.lineSeparator());
            sb.append("\"hotelId\":");
            sb.append("\"invalid\"" + System.lineSeparator());
            sb.append("}");
        }
        return sb.toString();
    }

    public String getQuery(String city) {

        Optional<List<Hotel>> hotels = Optional.ofNullable(hotelDao.getAll(city));

        StringBuilder sb = new StringBuilder("[");
        if (hotels.isPresent()) {
            for (Hotel hotel : hotels.get()) {
                sb.append("{" + System.lineSeparator() + "\"success\":");
                Address address = hotel.getAddress();
                sb.append("true," + System.lineSeparator());
                sb.append("\"hotelId\":\"" + hotel.gethId() + "\"," + System.lineSeparator());
                sb.append("\"name\":\"" + hotel.getName() + "\"," + System.lineSeparator());
                sb.append("\"rating\":\"" + hotel.getAverageRating() + "\"," + System.lineSeparator());
                sb.append("\"addr\":\"" + address.getStreetAddress() + "\"," + System.lineSeparator());
                sb.append("\"city\":\"" + address.getCity() + "\"," + System.lineSeparator());
                sb.append("\"state\":\"" + address.getState() + "\"," + System.lineSeparator());
                sb.append("\"lat\":\"" + address.getLat() + "\"," + System.lineSeparator());
                sb.append("\"lng\":\"" + address.getLon() + "\"" + System.lineSeparator());
                sb.append("},");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.lastIndexOf(","));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
