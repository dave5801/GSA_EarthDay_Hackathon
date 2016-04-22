package gov.epa.uvindex.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import gov.epa.uvindex.R;
import gov.epa.uvindex.util.UVIndexUtils;

/**
 * (Cluttered) Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, "Loading...");

        final UVIndexUtils utils = new UVIndexUtils(context);
        utils.getCurrentZip(new UVIndexUtils.AsyncCallback() {
            @Override
            public void success(String message) {
                final String zip = message;
                Log.i("TAG", "Zip: " + zip);
                utils.zipIsNearBeach(zip, new UVIndexUtils.AsyncCallback() {
                    @Override
                    public void success(final String message) {
                        Log.i("TAG", "RESPONSE: " +  message);
                        final String beach = message;
                        if (message == null || message.equals("false") || message.equals("")) {
                            views.setTextViewText(R.id.appwidget_text, "Not near beach");
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                            return;
                        }

                        utils.getUVIndexForZip(zip, new UVIndexUtils.AsyncCallback() {
                            @Override
                            public void success(String message) {
                                views.setTextViewText(R.id.appwidget_text, beach + ": " + message);
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void error() {
                                views.setTextViewText(R.id.appwidget_text, "ERROR");
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        });

                    }

                    @Override
                    public void error() {
                        views.setTextViewText(R.id.appwidget_text, "ERROR");
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                });
            }

            @Override
            public void error() {
                views.setTextViewText(R.id.appwidget_text, "ERROR");
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

