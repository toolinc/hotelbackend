package hotelapp.dao;

import hotelapp.model.Hotel;

import java.util.List;

public interface HotelDao {

    List<Hotel> getAll(String city);

    Hotel getHotel(String id);
}
