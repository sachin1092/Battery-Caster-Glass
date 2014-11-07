package glass.batterycaster.saphion.battercaster.utils;

/**
 * Created by sachin.shinde on 6/11/14.
 */


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFuncs {
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a", Locale.US);// dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static Date GetItemDate(final String date) {
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat format = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a", Locale.US);
        format.setCalendar(cal);

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converts a Given Seconds to Days, Hours and Minutes String
     */
    public static String convToHourAndMinDay(long sec) {
        int hour = (int) (sec / 3600);
        int minute = (int) (sec % 3600);
        minute = (int) (minute / 60);
        int day = 0;
        if (hour >= 24) {
            day = hour / 24;
            hour = hour % 24;
        }
        if (day != 0)
            return day + " Day(s) " + hour + " Hour(s) " + minute
                    + " Minute(s)";
        else {
            if (hour != 0)
                return hour + " Hour(s) " + minute + " Minute(s)";
            else
                return minute + " Minute(s)";
        }
    }

    /**
     * Getting Difference in Seconds between two Time-Stamps
     */
    public static long newDiff(Date date1, Date date2) {
        long milliSec1 = date1.getTime();
        long milliSec2 = date2.getTime();

        long timeDifInMilliSec;
        if (milliSec1 >= milliSec2) {
            timeDifInMilliSec = milliSec1 - milliSec2;
        } else {
            timeDifInMilliSec = milliSec2 - milliSec1;
        }

        long timeDifSeconds = timeDifInMilliSec / 1000;
        return timeDifSeconds;
    }

}

