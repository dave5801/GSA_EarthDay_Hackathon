package gov.epa.uvindex.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stephentuso on 4/22/16.
 */
public class UVIndexUtils {

    LocationManager locationManager;
    Context context;

    private Double longitude = null;
    private Double latitude = null;

    public interface AsyncCallback {
        void success(String message);
        void error();
    }

    public UVIndexUtils(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCurrentZip(final AsyncCallback callback) {
        getLocation(new AsyncCallback() {
            @Override
            public void success(String message) {
                String zip = getZipFromLocation(latitude, longitude);
                if (zip == null) {
                    callback.error();
                    return;
                }
                callback.success(zip);
            }

            @Override
            public void error() {
                callback.error();
            }
        });
    }

    public void zipIsNearBeach(String zip, final AsyncCallback callback) {
        getURL("http://dev‐central.byethost18.com/uv‐index/select.php?zip=" + zip, new AsyncCallback() {
            @Override
            public void success(String message) {
                if (message.equals("null")) {
                    callback.success("false");
                }
                try {
                    JSONObject object = new JSONObject(message);
                    JSONArray array = object.getJSONArray("data");
                    callback.success(array.getJSONObject(0).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.success("false");
                }
            }

            @Override
            public void error() {
                callback.error();
            }
        });
    }

    public void getUVIndexForCurrentLocation(final AsyncCallback callback) {
        Log.i("TAG", "Getting location...");
        getLocation(new AsyncCallback() {
            @Override
            public void success(String message) {
                Log.i("TAG", "Getting zip...");
                String zip = getZipFromLocation(latitude, longitude);
                if (zip == null) {
                    Log.i("TAG", "ZIPCODE ERROR");
                    callback.error();
                }
                Log.i("TAG", "Getting index...");
                getUVIndexForZip(zip, callback);

            }

            @Override
            public void error() {
                callback.error();
            }
        });
    }

    public void getUVIndexForZip(final String zipCode, final AsyncCallback callback) {
        getURL("https://iaspub.epa.gov/enviro/efservice/getEnvirofactsUVHOURLY/ZIP/" + zipCode + "/JSON", new AsyncCallback() {
            @Override
            public void success(String message) {
                callback.success(getCurrentUVIndexFromJSON(zipCode, message));
            }

            @Override
            public void error() {
                callback.error();
            }
        });
    }

    private void getLocation(final AsyncCallback listener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "UV Index needs location permissions", Toast.LENGTH_LONG).show();
            listener.error();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Make sure accuracy is good enough before return location;
                if (location.hasAccuracy() && location.getAccuracy() < 100000f) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    listener.success("");
                    locationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    private String getZipFromLocation(Double latitude, Double longitude) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        /*
         String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
         String city = addresses.get(0).getLocality();
         String state = addresses.get(0).getAdminArea();
         String country = addresses.get(0).getCountryName();
         String postalCode = addresses.get(0).getPostalCode();
         String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
         */

        return addresses.get(0).getPostalCode();
    }

    public String getCurrentUVIndexFromJSON(String zipCode, String jsonString) {

        final String dateFormat = "MMM/dd/yyyy hh aa";

        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(jsonString);

            String date = new SimpleDateFormat(dateFormat).format(new Date());

            for(int i =0; i < jsonArray.length();i++){
                //arr[i] = jsonArray.getString(i);
                JSONObject obj = jsonArray.getJSONObject(i);

                if(obj.getString("DATE_TIME").equalsIgnoreCase(date)){
                    return jsonArray.getJSONObject(i).getString("UV_VALUE");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Date not found";

    }

    private void getURL(String url, AsyncCallback callback) {
        HTTPGetTask task = new HTTPGetTask(callback);
        task.execute(url);
    }

}
