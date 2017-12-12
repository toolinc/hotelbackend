package hotelapp.cache;

import hotelapp.concurrent.WorkQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class HotelCacheThreadSafeBuilder. Loads hotel info from input files to HotelCacheThreadSafe
 * (using multithreading).
 */
public final class HotelCacheThreadSafeBuilder {

    private final static Logger log = LogManager.getRootLogger();
    private final HotelCacheThreadSafe hdata;
    private final WorkQueue executor;
    private int numTasks;

    /**
     * Constructor for cass HotelCacheThreadSafeBuilder.
     */
    public HotelCacheThreadSafeBuilder(HotelCacheThreadSafe data) {
        hdata = data;
        executor = new WorkQueue(4);
    }

    /**
     * Constructor for class HotelCacheThreadSafeBuilder that takes HotelCacheThreadSafe and
     * the number of threads to create as a parameter.
     */
    public HotelCacheThreadSafeBuilder(HotelCacheThreadSafe data, int numThreads) {
        hdata = data;
        executor = new WorkQueue(numThreads);
    }

    /**
     * Read the json file with information about the hotels and load it into the
     * appropriate data structure(s).
     */
    public void loadHotelInfo(String jsonFilename) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new String(Files.readAllBytes(Paths.get(jsonFilename))));
            JSONArray hotelsArray = (JSONArray) ((JSONObject) obj).get("sr");
            for (int i = 0; i < hotelsArray.size(); i++) {
                Map<Object, Object> mapHotleJson = (Map<Object, Object>) hotelsArray.get(i);
                Map<Object, Object> mapLLHotel = (Map<Object, Object>) mapHotleJson.get("ll");
                hdata.addHotel(((String) mapHotleJson.get("id")), ((String) mapHotleJson.get("f")),
                        ((String) mapHotleJson.get("ci")),
                        ((String) mapHotleJson.get("pr")), ((String) mapHotleJson.get("ad")),
                        Double.parseDouble((String) mapLLHotel.get("lat")),
                        Double.parseDouble((String) mapLLHotel.get("lng")));
            }
        } catch (FileSystemNotFoundException | IOException | ParseException e) {
            log.error(
                    "Exception while running the loadHotelInfo HotelCacheThreadSafeBuilder class: " + e);
        }
    }


    /**
     * Loads reviews from json files. Recursively processes subfolders. Each json file with reviews
     * should be processed concurrently (you need to create a new runnable job for each json file that
     * you encounter)
     */
    public void loadReviews(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    loadReviews(entry);
                }
                if (entry.toString().endsWith(".json")) {
                    FileWorker parseJason = new FileWorker(String.valueOf(entry));
                    executor.execute(parseJason);
                    addNumTasks();
                }
            }
        } catch (IOException e) {
            log.error(
                    "Exception while running the getPathsReviews HotelCacheThreadSafeBuilder class: " + e);
        }
    }

    /**
     * Prints all hotel info to the file. Calls hdata's printToFile method.
     */
    public void printToFile(Path filename) {
        waitUntilFinished();
        log.debug("Print to File");
        hdata.printToFile(filename);
    }

    /**
     * Wait until there is no more threads loading reviews
     */
    private synchronized void waitUntilFinished() {
        while (numTasks > 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                log.error("Exception while running the waitUntilFinished: " + e);
            }
        }
    }

    /**
     * add one to the number of Threads running
     */
    public synchronized void addNumTasks() {
        this.numTasks++;
    }

    /**
     * subtract one to the number of Threads running
     */
    public synchronized void removeNumTasks() {
        this.numTasks--;
        if (this.numTasks == 0) {
            notifyAll();
        }
    }

    /**
     * Class HotelCache -parses each json file with reviews
     */
    private final class FileWorker implements Runnable {

        private final String pathFile;
        private final HotelCache localHData;

        /**
         * Constructor for class HotelCacheThreadSafeBuilder.
         *
         * @param pathFile path from the Json file.
         */
        FileWorker(String pathFile) {
            this.pathFile = pathFile;
            localHData = new HotelCache();
        }

        /**
         * This method will run in each thread to add a new review
         */
        @Override
        public void run() {
            log.debug("Runnable task start: " + this);
            JSONParser parser = new JSONParser();
            try {
                JSONObject obj = (JSONObject) parser
                        .parse(new String(Files.readAllBytes(Paths.get(pathFile))));
                JSONObject reviewDetails = (JSONObject) obj.get("reviewDetails");
                HashMap<Object, Object> reviewCollection = (HashMap<Object, Object>) reviewDetails
                        .get("reviewCollection");
                JSONArray reviewArray = (JSONArray) reviewCollection.get("review");
                Iterator<Map<Object, Object>> iterator = reviewArray.iterator();
                String hotelID = "";
                while (iterator.hasNext()) {
                    Map<Object, Object> mapReviewJson = iterator.next();
                    hotelID = (String) mapReviewJson.get("hotelId");
                    localHData.addReview(hotelID, ((String) mapReviewJson.get("reviewId")),
                            Integer.valueOf(((Long) mapReviewJson.get("ratingOverall")).intValue()),
                            ((String) mapReviewJson.get("title")), ((String) mapReviewJson.get("reviewText")),
                            Boolean.parseBoolean(((String) mapReviewJson.get("isRecommended")).toLowerCase()),
                            ((String) mapReviewJson.get("reviewSubmissionTime")),
                            ((String) mapReviewJson.get("userNickname")));
                }
                if (reviewArray.size() > 0) {
                    hdata.mergeReviews(localHData);
                }
            } catch (ParseException | IOException e) {
                log.error("Exception while running the run method ParseJson innerclass: " + e);
            } finally {
                removeNumTasks();
                log.debug("Runnable task end: " + this);
            }
        }
    }
}
