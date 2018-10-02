package me.murks.filmchecker.io;

import com.google.common.io.CharStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import me.murks.filmchecker.model.Film;
import me.murks.filmchecker.model.FilmStatus;

/**
 * {@see IStatusProvider} implementation for dm
 */
class DmStatusProvider implements IStatusProvider {
    /**
     * The url to query for film info
     */
    private final String url;
    /**
     * Name of the json property containing the state of the film order
     */
    private static final String SUMMARY_KEY = "summaryStateText";

    /**
     * Value for config parameter used for requesting the film details
     */
    private final String config;

    /**
     * Creates a new DmStatusProvider
     */
    public DmStatusProvider() {
        this("1320");
    }

    /**
     * Creates a new DmStatusProvider using the given config value
     * @param config The value for the config parameter
     */
    DmStatusProvider(String config) {
        url = "https://spot.photoprintit.com/spotapi/orderInfo/forShop";
        this.config = config;
    }

    @Override
    public FilmStatus getFilmStatus(Film film) throws IOException {
        String urlParameter = "?config=" + config + "&order=" + film.getOrderNumber() + "&shop=" + film.getShopId();
        try {
            URLConnection connection = new URL(url + urlParameter).openConnection();
            String jsonString = CharStreams.toString( new InputStreamReader( connection.getInputStream(), "UTF-8" ) );
            JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.has(SUMMARY_KEY)) {
                return new FilmStatus(jsonObject.getString(SUMMARY_KEY));
            }
        } catch(JSONException e) {
            throw new IOException(e);
        }
        throw new IOException();
    }
}
