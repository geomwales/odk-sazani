package org.odk.collect.android.tasks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.android.gms.location.LocationListener;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.MainMenuActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.location.client.LocationClient;
import org.odk.collect.android.location.client.LocationClients;
import org.odk.collect.utilities.Point;
import org.odk.collect.utilities.Polygon;

public class GeoFencingJob extends Job implements LocationListener {

    private static final long ONE_MINUTE_PERIOD = 60000;
    private static final long FIFTEEN_MINUTES_PERIOD = 900000;
    private static final long ONE_HOUR_PERIOD = 3600000;
    private static final long SIX_HOURS_PERIOD = 21600000;
    private static final long ONE_DAY_PERIOD = 86400000;

    public static final String TAG = "geofencingJob";

    private LocationClient m_client;
    private Handler handler;
    private NotificationChannel mChannel;
    private String CHANNEL_ID = "my_channel_01";

    public GeoFencingJob() {
        org.odk.collect.android.application.Collect.getInstance().getComponent().inject(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if(mChannel==null) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
            }
            Context context = org.odk.collect.android.application.Collect.getInstance().getApplicationContext();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {

        Log.d("token", "Start Job Called");
        if(m_client == null)
        {
            m_client = LocationClients.clientForContext(org.odk.collect.android.application.Collect.getInstance().getApplicationContext());
        }
        if(m_client.isMonitoringLocation())
        {
            m_client.stop();
        }
        // Use high accuracy
        m_client.setUpdateIntervals(30000,30000);
        m_client.start();

        LocationListener ll = this;
        if(handler == null)
        {
            handler = new Handler(org.odk.collect.android.application.Collect.getInstance().getApplicationContext().getMainLooper());
        }

        handler.post(new Runnable()
        {
            public void run()
            {
                m_client.requestLocationUpdates(ll);
            }
        });



        return Result.SUCCESS;
    }



    public static void schedulePeriodicJob(String selectedOption) {
        if (selectedOption.equals(Collect.getInstance().getString(R.string.never_value))) {
            JobManager.instance().cancelAllForTag(TAG);
        } else {
            long period = FIFTEEN_MINUTES_PERIOD;
            if (selectedOption.equals(Collect.getInstance().getString(R.string.every_one_hour_value))) {
                period = ONE_HOUR_PERIOD;
            } else if (selectedOption.equals(Collect.getInstance().getString(R.string.every_six_hours_value))) {
                period = SIX_HOURS_PERIOD;
            } else if (selectedOption.equals(Collect.getInstance().getString(R.string.every_24_hours_value))) {
                period = ONE_DAY_PERIOD;
            }

            new JobRequest.Builder(TAG)
                    .setPeriodic(period)
                    .setUpdateCurrent(true)
                //    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            //lm.removeUpdates(locListener);
            double Longitude = location.getLongitude();
            double Latitude = location.getLatitude();
          /*  Toast.makeText(org.odk.collect.android.application.Collect.getInstance().getApplicationContext(),
                            "Longitude :" + Longitude + " Latitude :" + Latitude,
                            Toast.LENGTH_LONG).show();*/

            Polygon polygon = Polygon.Builder()
                    .addVertex(new Point(6.048857f, 80.211021f)) // change polygon  according to your location
                    .addVertex(new Point(6.048137f, 80.210546f))
                    .addVertex(new Point(6.048590f, 80.210197f))
                    .addVertex(new Point(6.049163f, 80.211050f)).build();

            Context context = org.odk.collect.android.application.Collect.getInstance().getApplicationContext();


            //  isInside(polygon, new Point((float)Latitude, (float)Longitude));

            // if (polygon.contains(new Point((double) Latitude, (double) Longitude)) == true)
            if (polygon.contains(new Point((double) Latitude, (double) Longitude)) != true)
            {

              // Toast.makeText(org.odk.collect.android.application.Collect.getInstance().getApplicationContext(), "You are inside", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, MainMenuActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                // Build notification
                // Actions are just fake

                Notification.Builder notibuild = new Notification.Builder(context);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notibuild.setChannelId(CHANNEL_ID);
                }

                Notification noti = notibuild
                        .setContentTitle("You are inside!")
                        .setContentText("Launch the app").setSmallIcon(R.drawable.mapbox_compass_icon)
                        .setContentIntent(pIntent)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, noti);

            } else {

              //Toast.makeText(context, "You are outside", Toast.LENGTH_LONG).show();

            }
            // /////////////do something//////////////
            // insertSalesOrder();

            Log.i("TTTAG", "Latitude:" + Latitude);
            Log.i("TTTAG", "Longitude:" + Longitude);
            /*
            //if (gpsProgressDialog.isShowing()) {
            //    gpsProgressDialog.dismiss();
            //}
             */

            m_client.stopLocationUpdates();
        }
    }
}
