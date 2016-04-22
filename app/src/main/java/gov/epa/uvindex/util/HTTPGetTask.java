package gov.epa.uvindex.util;

import android.os.AsyncTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stephentuso on 4/22/16.
 */
public class HTTPGetTask extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();
    UVIndexUtils.AsyncCallback callback;

    private boolean errorOccurred = false;

    public HTTPGetTask(UVIndexUtils.AsyncCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        final String url = strings[0];
        Request request = new Request.Builder()
                .header("Cookie", "referrer=; __test=f1e424bb9fb80c4266caa27d63c9ed2a")
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            errorOccurred = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (errorOccurred) {
            callback.error();
        } else {
            callback.success(s);
        }
    }
}
