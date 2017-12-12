package com.toolsoft.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.toolsoft.cache.HotelCacheThreadSafe;
import com.toolsoft.model.Hotel;
import com.toolsoft.model.TouristAttraction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Singleton
public final class TouristAttractionFinder {

  private static final String host = "maps.googleapis.com";
  private static final String path = "/maps/api/place/textsearch/json";
  private static final String FIND_TYPE = "tourist%20attractions+in+";
  private static final String API_KEY = "AIzaSyBhFjxfTNwfFdz1fX-EQRWeEJXwNKixQkk";
  private static final int PORT = 443;
  private static final int MILE_TO_METERS = 1609;

  private final HotelCacheThreadSafe hdata;
  private final Map<String, List<TouristAttraction>> listAttractions = new HashMap<>();

  @Inject
  public TouristAttractionFinder(HotelCacheThreadSafe hdata) {
    this.hdata = checkNotNull(hdata);
  }

  /**
   * Creates a secure socket to communicate with googleapi's server that provides Places API, sends
   * a GET request (to find attractions close to the hotel within a given radius), and gets a
   * response as a string. Removes headers from the response string and parses the remaining json to
   * get Attractions info. Adds attractions to the HotelCacheThreadSafe.
   */
  public void fetchAttractions(int radiusInMiles) {
    List<String> hotels = hdata.getHotels();
    PrintWriter out = null;
    BufferedReader in = null;
    SSLSocket socket = null;
    try {
      for (String id : hotels) {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(host, PORT);
        String request = getRequest(id, radiusInMiles * MILE_TO_METERS);
        System.out.println("Request: " + request);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        out.println(request);
        out.flush();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
          if (!line.matches("^[A-Z].*")) {
            sb.append(line);
            sb.append(System.lineSeparator());
          }
        }
        loadAttractions(id, sb.toString());
      }
    } catch (IOException e) {
      System.out.println(
          "An IOException occured while writing to the socket stream or reading from the stream: "
              + e);
    } finally {
      try {
        out.close();
        in.close();
        socket.close();
      } catch (IOException e) {
        System.out
            .println("An exception occured while trying to close the streams or the socket: " + e);
      }
    }
  }


  public String getAttractions(String hId, int radiusInMiles) {
    PrintWriter out = null;
    BufferedReader in = null;
    SSLSocket socket = null;
    StringBuffer sb = new StringBuffer();
    try {
      SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      socket = (SSLSocket) factory.createSocket(host, PORT);
      String request = getRequest(hId, radiusInMiles * MILE_TO_METERS);
      System.out.println("Request: " + request);
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      out.println(request);
      out.flush();
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        if (!line.matches("^[A-Z].*") && !line.isEmpty()) {
          sb.append(line);
          sb.append(System.lineSeparator());
        }
      }
    } catch (IOException e) {
      System.out.println(
          "An IOException occured while writing to the socket stream or reading from the stream: "
              + e);
    } finally {
      try {
        out.close();
        assert in != null;
        in.close();
        socket.close();
      } catch (IOException e) {
        System.out
            .println("An exception occured while trying to close the streams or the socket: " + e);
      }
    }
    return sb.toString();
  }


  public String getHotelAttractions(Hotel hotel, int radiusInMiles) {
    PrintWriter out = null;
    BufferedReader in = null;
    SSLSocket socket = null;
    StringBuffer sb = new StringBuffer();
    try {
      SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      socket = (SSLSocket) factory.createSocket(host, PORT);
      String request = getRequest(hotel, radiusInMiles * MILE_TO_METERS);

      System.out.println("Request: " + request);

      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      out.println(request);
      out.flush();
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        if (!line.matches("^[A-Z].*") && !line.isEmpty()) {
          sb.append(line);
          sb.append(System.lineSeparator());
        }
      }
    } catch (IOException e) {
      System.out.println(
          "An IOException occured while writing to the socket stream or reading from the stream: "
              + e);
    } finally {
      try {
        assert out != null;
        out.close();
        in.close();
        socket.close();
      } catch (IOException e) {
        System.out
            .println("An exception occured while trying to close the streams or the socket: " + e);
      }
    }
    return sb.toString();
  }


  /**
   * A method that creates a GET request for the given host and resource
   *
   * @return HTTP GET request returned as a string
   */
  private String getRequest(Hotel hotel, int radius) {
    String request = "GET " + path
        + "?query=" + FIND_TYPE + ((String) hotel.address().city()).replace(" ", "+")
        + "&location=" + hotel.address().lat() + "," + hotel.address().lon()
        + "&radius=" + radius
        + "&key=" + API_KEY

        + " HTTP/1.1" + System.lineSeparator()
        + "Host: " + host + System.lineSeparator()
        + "Connection: close" + System.lineSeparator()
        + System.lineSeparator();
    return request;
  }


  /**
   * A method that creates a GET request for the given host and resource
   *
   * @return HTTP GET request returned as a string
   */
  private String getRequest(String hotelId, int radius) {
    Object[] location = hdata.getLocationHotel(hotelId);
    String request = "GET " + path
        + "?query=" + FIND_TYPE + ((String) location[2]).replace(" ", "+")
        + "&location=" + location[0] + "," + location[1]
        + "&radius=" + radius
        + "&key=" + API_KEY
        + " HTTP/1.1" + System.lineSeparator()
        + "Host: " + host + System.lineSeparator()
        + "Connection: close" + System.lineSeparator()
        + System.lineSeparator();
    return request;
  }

  /**
   * Print attractions near the hotels to a file. The format is described in the lab description.
   */
  public void printAttractions(Path filename) {
    StringBuilder sb = new StringBuilder();
    listAttractions.forEach((hotelId, attractions) -> {
      sb.append("Attractions near ");
      sb.append(hdata.getHotelName(hotelId));
      sb.append(", ");
      sb.append(hotelId);
      sb.append(System.lineSeparator());
      attractions.forEach((attraction) -> {
        sb.append(attraction);
        sb.append(System.lineSeparator());
      });
      sb.append("++++++++++++++++++++");
      sb.append(System.lineSeparator());
    });
    sb.deleteCharAt(sb.length() - 1);
    try (PrintWriter writer = new PrintWriter(filename.toString())) {
      writer.println(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load the attraction in the Map
   *
   * @param hotelId - Hotel id
   * @param attractionsList - String JSON Object with all the attractions
   */
  private void loadAttractions(String hotelId, String attractionsList) {
    try {
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(attractionsList);
      JSONArray attractions = (JSONArray) ((JSONObject) obj).get("results");
      Iterator<JSONObject> iterator = attractions.iterator();
      List<TouristAttraction> list = new ArrayList<>();
      while (iterator.hasNext()) {
        double rating;
        JSONObject att = iterator.next();
        if (att.get("rating") == null) {
          rating = 0;
        } else if (att.get("rating").getClass() == Long.class) {
          rating = ((Long) att.get("rating")).doubleValue();
        } else {
          rating = (Double) att.get("rating");
        }
        TouristAttraction attraction = TouristAttraction.builder()
            .setId((String) att.get("id"))
            .setName((String) att.get("name"))
            .setAddress((String) att.get("formatted_address"))
            .setRating(rating)
            .build();
        list.add(attraction);
      }
      listAttractions.put(hotelId, list);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}


