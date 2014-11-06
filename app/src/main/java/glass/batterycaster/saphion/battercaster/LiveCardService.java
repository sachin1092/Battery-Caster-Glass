package glass.batterycaster.saphion.battercaster;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import glass.batterycaster.saphion.battercaster.utils.ActivityFuncs;
import glass.batterycaster.saphion.battercaster.utils.Functions;
import glass.batterycaster.saphion.battercaster.utils.PreferenceHelper;
import glass.batterycaster.saphion.battercaster.utils.TimeFuncs;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class LiveCardService extends Service {

    public static final String MAIN_TEMP = "main_temp";
    public static final String PREF_NAME = "saphion.batterycaster_preferences";

    private static final String LIVE_CARD_TAG = "LiveCardService";

    private LiveCard mLiveCard;
    boolean isConnected;
    String temperature;
    String health;
    int level;
    String willlast;
    SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            updateCard();

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.publish(PublishMode.REVEAL);
        } else {
            mLiveCard.navigate();
        }
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mIntentFilter.addAction("saphion.battery.update");
        getBaseContext().registerReceiver(batteryReceiver, mIntentFilter);
        return START_STICKY;
    }


    @SuppressLint("NewApi")
    public void updateCard() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.live_card);
        remoteViews.setTextViewText(R.id.tvBat, readBattery() + "");
        remoteViews.setTextViewText(R.id.tvStatsTempVal, temperature);
        remoteViews.setTextViewText(R.id.tvLeftStatTitle, isConnected ? "Charged in" : "Empty in");
        remoteViews.setTextViewText(R.id.tvLeftStatVal, willlast);
        remoteViews.setTextViewText(R.id.tvPlugged, isConnected ? "Plugged" : "Unplugged");

        String mainText = getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS).getString(PreferenceHelper.LAST_CHARGED,
                TimeFuncs.getCurrentTimeStamp());
        String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
                TimeFuncs.GetItemDate(mainText),
                TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));
        if (!time.equals("0 Minute(s)"))
            mainText = time + " ago";
        else
            mainText = "right now";

        remoteViews.setTextViewText(R.id.tvPluggedVal, mainText);

        if (level == 100 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            remoteViews.setTextViewTextSize(R.id.tvBat, TypedValue.COMPLEX_UNIT_SP, 70);
        }
        mLiveCard.setViews(remoteViews);
    }

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateCard();
            triggerFunc(intent.getAction());
        }

    };

    public int readBattery() {
        Intent batteryIntent = getBaseContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int rawlevel = batteryIntent
                .getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        double scale = batteryIntent
                .getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                -1);
        isConnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
        level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = (int) ((rawlevel * 100) / scale);
            Log.d("rawLevel: ", "" + rawlevel);
            Log.d("scale: ", "" + scale);
        }


        temperature = Functions.updateTemperature(
                (float) ((float) (batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_TEMPERATURE, 0)) / 10),
                getSharedPreferences(PREF_NAME,
                        Context.MODE_MULTI_PROCESS).getBoolean(
                        MAIN_TEMP, true), true);
        health = ActivityFuncs.getBatHealth(batteryIntent);

        if (isConnected) {
            long diff;
            diff = getSharedPreferences(PREF_NAME,
                    Context.MODE_MULTI_PROCESS).getLong(
                    PreferenceHelper.BAT_CHARGE, 81);
            willlast = TimeFuncs.convtohournminnday(diff
                    * (100 - level));
        } else {
            long diff;
            diff = (long) (getSharedPreferences(PREF_NAME,
                    Context.MODE_MULTI_PROCESS).getLong(
                    PreferenceHelper.BAT_DISCHARGE, 792));

            willlast = TimeFuncs.convtohournminnday(diff * (level));
        }

        return level;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        getBaseContext().unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    public void triggerFunc(String action){

        mPref = getSharedPreferences(PREF_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor mPrefEditor = mPref.edit();
        Status mStat = readBatteryStat();
        if ((mStat.getConnected() && mStat.getLevel() == 100)
                || action == Intent.ACTION_POWER_DISCONNECTED) {

            // Log.Toast(getBaseContext(), "Disconnected", Toast.LENGTH_LONG);
            int diff;
            if ((diff = mStat.getLevel()
                    - mPref.getInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
                    mStat.getLevel())) > 3) {
                calcStat(mStat, mPref, mPrefEditor, diff, true);
            }

            if (mPref
                    .getInt(PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL, -99) == -99
                    || diff < 3) {
                mPrefEditor.putInt(
                        PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
                        mStat.getLevel());
                mPrefEditor.putString(
                        PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
                        TimeFuncs.getCurrentTimeStamp());
            }

        }

        if ((!mStat.getConnected() && mStat.getLevel() <= 1)
                || action == Intent.ACTION_POWER_CONNECTED) {

            // Log.Toast(getBaseContext(), "Connected", Toast.LENGTH_LONG);

            int diff;
            if ((diff = mPref.getInt(
                    PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
                    mStat.getLevel())
                    - mStat.getLevel()) > 3) {
                calcStat(mStat, mPref, mPrefEditor, diff, false);
            }

            if (mPref.getInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL, -99) == -99
                    || diff < 3) {
                mPrefEditor.putInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
                        mStat.getLevel());
                mPrefEditor.putString(
                        PreferenceHelper.STAT_CONNECTED_LAST_TIME,
                        TimeFuncs.getCurrentTimeStamp());
            }
        }

        mPrefEditor.commit();

    }

    private void calcStat(Status mStat, SharedPreferences mPrefs,
                          SharedPreferences.Editor mPrefEditor, int diff, boolean b) {

        // Step 1: retrieve previous time
        // Step 2: calculate time difference
        // Step 3: scale it to 1% by dividing it with #diff
        // Step 4: take the previous average and multiply with count
        // Step 5: add the new time difference with result from previous step
        // Step 6: increment the count
        // Step 7: divide the Step 5 result with new count
        // Step 8: save the values

        if (b) {

            long prevAvg = mPrefs.getLong(
                    PreferenceHelper.STAT_CHARGING_AVGTIME, 90);
            long count = mPrefs.getLong(
                    PreferenceHelper.STAT_DISCONNECTED_COUNT, 0);
            long newAvg = prevAvg;
            // retrieve previous time
            String prevTime = mPrefs.getString(
                    PreferenceHelper.STAT_CONNECTED_LAST_TIME,
                    TimeFuncs.getCurrentTimeStamp());
            // Calculate difference
            long timeDiff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
                    TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));
            // scale to 1% by dividing by #diff
            if (diff != 0) {
                timeDiff = timeDiff / diff;

                // take the previous average and multiply with count
                prevAvg = prevAvg * count;
                // increment the count
                count = count + 1;
                // add the new time difference with result from previous
                // step
                // divide the Step 5 result with new count
                newAvg = (prevAvg + timeDiff) / (count);

            }

            mPrefEditor.putLong(PreferenceHelper.STAT_CHARGING_AVGTIME, newAvg);
            mPrefEditor
                    .putLong(PreferenceHelper.STAT_DISCONNECTED_COUNT, count);
            mPrefEditor.putString(PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
                    TimeFuncs.getCurrentTimeStamp());
            mPrefEditor.putInt(PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
                    mStat.getLevel());

        } else {
            long prevAvg = mPrefs.getLong(
                    PreferenceHelper.STAT_DISCHARGING_AVGTIME, 612);
            long count = mPrefs.getLong(PreferenceHelper.STAT_CONNECTED_COUNT,
                    0);
            long newAvg = prevAvg;
            // retrieve previous time
            String prevTime = mPrefs.getString(
                    PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
                    TimeFuncs.getCurrentTimeStamp());
            // Calculate difference
            long timeDiff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
                    TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));
            // scale to 1% by dividing by #diff
            if (diff != 0) {
                timeDiff = timeDiff / diff;

                // take the previous average and multiply with count
                prevAvg = prevAvg * count;
                // increment the count
                count = count + 1;
                // add the new time difference with result from previous
                // step
                // divide the Step 5 result with new count
                newAvg = (prevAvg + timeDiff) / (count);

            }

            mPrefEditor.putLong(PreferenceHelper.STAT_DISCHARGING_AVGTIME,
                    newAvg);
            mPrefEditor.putLong(PreferenceHelper.STAT_CONNECTED_COUNT, count);
            mPrefEditor.putString(PreferenceHelper.STAT_CONNECTED_LAST_TIME,
                    TimeFuncs.getCurrentTimeStamp());
            mPrefEditor.putInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
                    mStat.getLevel());
        }

        mPrefEditor.commit();

    }

    public class Status {
        private boolean isConnected;
        private int mLevel;

        public Status(int mLevel, boolean isConnected) {
            this.mLevel = mLevel;
            this.isConnected = isConnected;
        }

        public int getLevel() {
            return this.mLevel;
        }

        public boolean getConnected() {
            return this.isConnected;
        }
    }

    public Status readBatteryStat() {
        Intent batteryIntent = getBaseContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int rawlevel = batteryIntent
                .getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        double scale = batteryIntent
                .getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                -1);
        isConnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
        level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = (int) ((rawlevel * 100) / scale);
            Log.d("rawLevel: ","" + rawlevel);
            Log.d("scale: ","" + scale);
        }

        return new Status(level, isConnected);
    }
}
