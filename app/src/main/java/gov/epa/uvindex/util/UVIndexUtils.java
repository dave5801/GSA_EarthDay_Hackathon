package gov.epa.uvindex.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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


        // lat,lng, your current location

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }




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