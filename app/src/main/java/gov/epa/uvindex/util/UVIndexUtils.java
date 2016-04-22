package gov.epa.uvindex.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by stephentuso on 4/22/16.
 */
public class UVIndexUtils implements LocationListener {

    private Double longitude = null;
    private Double latitude = null;

    public UVIndexUtils(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //TODO (stephentuso): figure out good interval and add check for permission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);
    }

    private String getZipFromLocation(Double latitude, Double longitude) {
        return "";
    }

    public void getUVIndexForZip(String zipCode) {

    }

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