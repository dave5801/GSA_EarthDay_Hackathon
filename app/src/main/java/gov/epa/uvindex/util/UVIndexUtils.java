package gov.epa.uvindex.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stephentuso on 4/22/16.
 */
public class UVIndexUtils implements LocationListener {

    OkHttpClient client = new OkHttpClient();
    LocationManager locationManager;
    Context context;

    private Double longitude = null;
    private Double latitude = null;

    public interface UVIndexDownloadListener {
        void success(int index);
        void error();
    }

    public UVIndexUtils(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCurrentUVIndex(UVIndexDownloadListener listener) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }

    private String getZipFromLocation(Double latitude, Double longitude) {
        return "";
    }

    private void downloadUVIndexForZip(String zipCode, UVIndexDownloadListener callback) {

    }

    private JSONObject downloadJSON(String url) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    /*-- LocationListener methods --*/
    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
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
}