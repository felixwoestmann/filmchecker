package me.murks.filmchecker.io;

import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.murks.filmchecker.model.Film;
import me.murks.filmchecker.model.FilmOrderState;
import me.murks.filmchecker.model.FilmStatus;

/**
 * Implementation of the {@link IStatusProvider} interface for Müller stores
 * @author zouroboros
 */
public class PhotoPrintitStatusProvider implements IStatusProvider {
    /**
     * The url to query for film info
     */
    private final String url;
    /**
     * Name of the json property containing the state of the film order
     */
    private static final String SUMMARY_KEY = "summaryStateText";
    /**
     * Name of the json property that contains the suborders array
     */
    private static final String SUBORDERS_KEY = "subOrders";
    /**
     * Json date field that contains the state date
     */
    private static final String SUMMARY_DATE_KEY = "summaryDate";
    /**
     * Json field of the suborders objects that contains the state date
     */
    private static final String SUBORDER_DATE_KEY = "stateDate";
    /**
     * Json field that suborders objects that contains the state text
     */
    private static final String SUBORDER_STATE_KEY = "stateText";
    /**
     * Json field for state code
     */
    private static final String SUMMARY_STATE_CODE_KEY = "summaryStateCode";
    /**
     * Json field for suborder state code
     */
    private static final String SUBORDER_STATE_CODE_KEY = "stateCode";

    /**
     * Value for config parameter used for requesting the film details
     */
    private final String config;
    /**
     * Creates a new PhotoPrintitStatusProvider
     */
    PhotoPrintitStatusProvider(String configId) {
        url = "https://spot.photoprintit.com/spotapi/orderInfo/order";
        config = configId;
    }

    @Override
    public FilmStatus getFilmStatus(Film film) throws IOException {
        String urlParameter = "?config=" + config + "&fullOrderId="
                + film.getShopId() + "-" + film.getOrderNumber();
        try {
            URLConnection connection = new URL(url + urlParameter).openConnection();
            String jsonString = CharStreams.toString(
                    new InputStreamReader( connection.getInputStream(), "UTF-8" ));
            JSONObject jsonObject = new JSONObject(jsonString);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

            if(jsonObject.has(SUBORDERS_KEY) &&
                    jsonObject.getJSONArray(SUBORDERS_KEY).length() > 0) {
                JSONArray subOrders = jsonObject.getJSONArray(SUBORDERS_KEY);
                Date stateDate = format.parse(jsonObject.getString(SUMMARY_DATE_KEY));
                String stateText = jsonObject.getString(SUMMARY_KEY);
                FilmOrderState state = orderState(jsonObject.getString(SUMMARY_STATE_CODE_KEY));

                // get latest suborder date
                for(int i = 0; i < subOrders.length(); i++) {
                    JSONObject subOrder = subOrders.getJSONObject(i);
                    Date subOrderDate = format.parse(subOrder.getString(SUBORDER_DATE_KEY));
                    if(subOrderDate.after(stateDate)) {
                        stateDate = subOrderDate;
                        stateText = subOrder.getString(SUBORDER_STATE_KEY);
                        state = orderState(jsonObject.getString(SUBORDER_STATE_CODE_KEY));
                    }
                }
                return new FilmStatus(stateText, stateDate, state);
            }
            else {
                return new FilmStatus(jsonObject.getString(SUMMARY_KEY),
                        format.parse(jsonObject.getString(SUMMARY_DATE_KEY)),
                        orderState(jsonObject.getString(SUMMARY_STATE_CODE_KEY)));
            }
        } catch(JSONException e) {
            throw new IOException(e);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    private FilmOrderState orderState(String state) {
        if(state.equals("PROCESSING")) {
            return FilmOrderState.PROCESSING;
        } else if(state.equals("SHIPPED")) {
            return FilmOrderState.DONE;
        }
        return FilmOrderState.UNKNOWN;
    }
}
