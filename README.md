# EPA UV Index Widget
The [United States Environmental Protection Agency](https://epa.gov/) have been providing their [UV Index](https://play.google.com/store/apps/details?id=gov.epa&hl=en) app for some years. As part of the [GSA Earthday Hackathon 2016](http://open.gsa.gov/EarthDayHackathon/), we are trying to turn this into an [Android widget tailored for beaches](https://developer.epa.gov/tailored-uv-index-forecast-for-beaches/), aimed at users who are either on a beach in the US or going to one.

The basic workflow of the widget is as follows:

 * determine [geolocation of the device](http://developer.android.com/training/location/index.html)
 * check whether that geolocation is a beach (using EPA's [data on zip codes for beaches](https://developer.epa.gov/wp-content/uploads/2016/04/uv_beach_zipcode.zip), which was imported into a MySQL database)
 * if location is not a beach, determine the three nearest beaches via the [Google Maps Android API](https://developers.google.com/maps/documentation/android-api/) - needs to be implemented
 * check time and date of the device
 * use EPA's [UV Index API](https://www.epa.gov/enviro/web-services#uvrest) to serve the UV index forecast for the given beach or the 3 nearest beaches for the given date and time
