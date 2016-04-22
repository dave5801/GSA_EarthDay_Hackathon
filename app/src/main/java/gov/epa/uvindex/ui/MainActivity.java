package gov.epa.uvindex.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import gov.epa.uvindex.R;
import gov.epa.uvindex.util.UVIndexUtils;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        textView.setText("Loading...");


        final UVIndexUtils utils = new UVIndexUtils(this);
        utils.getCurrentZip(new UVIndexUtils.AsyncCallback() {
            @Override
            public void success(String message) {
                final String zip = message;
                Log.i("TAG", "Zip: " + zip);
                utils.zipIsNearBeach(zip, new UVIndexUtils.AsyncCallback() {
                    @Override
                    public void success(String message) {
                        Log.i("TAG", "RESPONSE: " +  message);
                        if (message == null || message.equals("false") || message.equals("")) {
                            textView.setText("Not near beach.");
                            return;
                        }

                        utils.getUVIndexForZip(zip, new UVIndexUtils.AsyncCallback() {
                            @Override
                            public void success(String message) {
                                textView.setText(message);
                            }

                            @Override
                            public void error() {
                                textView.setText("Error");
                            }
                        });

                    }

                    @Override
                    public void error() {
                        textView.setText("Error getting beach");
                    }
                });
            }

            @Override
            public void error() {

            }
        });

    }

}
