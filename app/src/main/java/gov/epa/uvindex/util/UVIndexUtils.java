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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stephentuso on 4/22/16.
 */
public class UVIndexUtils {

    OkHttpClient client = new OkHttpClient();
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
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getUVIndexForCurrentLocation(final AsyncCallback listener) {
        getLocation(new AsyncCallback() {
            @Override
            public void success(String message) {
                String zip = getZipFromLocation(latitude, longitude);
                if (zip == null) {
                    listener.error();
                }
                /*String index =*/ getUVIndexForZip(zip);
                listener.success("");
            }

            @Override
            public void error() {
                listener.error();
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
            listener.error();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Make sure accuracy is good enough before return location;
                if (location.hasAccuracy() && location.getAccuracy() < 100f) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    listener.success("");
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

    public void getUVIndexForZip(String zipCode) {

        JSONArray jsonArray;

        try {
            String jsonString = getURL("https://iaspub.epa.gov/enviro/efservice/getEnvirofactsUVHOURLY/ZIP/"+zipCode+"/JSON");
            jsonArray = new JSONArray(jsonString);

            String date = new SimpleDateFormat("MMM/dd/yy hh aa").format(new Date());
            String[] arr =new String[jsonArray.length()];
            for(int i =0; i < jsonArray.length();i++){
                arr[i] = jsonArray.getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getURL(String url) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
