package vn.edu.khtn.googlemapsforseminar02;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12/20/2016.
 */

public class GetDirectionsTask {
    private String mRequest;

    public GetDirectionsTask(String _mRequest) {
        this.mRequest = _mRequest;
    }

    public ArrayList<LatLng> testDirection() {
        ArrayList<LatLng> ret = new ArrayList<LatLng>();
        try {
            URL url;
            url = new URL(mRequest);

            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");

            Directions results = new Gson().fromJson(reader, Directions.class);
            Directions.Route[] routes = results.getRoutes();
            Directions.Leg[] leg = routes[0].getLegs();
            Directions.Leg.Step[] steps = leg[0].getSteps();
            for (Directions.Leg.Step step : steps) {
                String polyline = step.getPolyline().getPoints();
                List list = decodePolyline(polyline);
                for (int l = 0; l < list.size(); l++) {
                    ret.add((LatLng) list.get(l));
                }
            }
            return ret;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
