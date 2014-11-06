package glass.batterycaster.saphion.battercaster.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.DisplayMetrics;

import glass.batterycaster.saphion.battercaster.R;

/**
 * Created by sachin.shinde on 6/11/14.
 */
public class ActivityFuncs {

    public static Bitmap newbattery(int mLevel, Context mycontext, int scale,
                                    boolean isconnected) {
        int color1, color2, color3, color4;

        scale = (int) (scale * 0.825);

        color1 = 0xff1e8bd4;// 0xffffffff;//0xff00bff3;//Color.argb(255, 44,
        // 172, 218);
        color2 = 0xa08e8f90;// 0xa000bff3;//0xa01e8bd4;//Color.argb(255, 24, 82,
        // 112);
        color3 = 0x00111111;
        color4 = 0xffffffff;

        Bitmap circleBitmap = Bitmap.createBitmap(scale, scale,
                Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAlpha(220);
        paint.setAntiAlias(true);
        paint.setColor(color3);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(scale / 2, scale / 2,
                (float) ((scale / 2) - (0.24 * (scale / 2))), paint);
        Paint mpaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mpaint.setColor(color3);
        mpaint.setAlpha(Color.alpha(color3));
        mpaint.setAntiAlias(true);

        c.drawCircle(scale / 2, scale / 2,
                (float) ((scale / 2) - (0.1 * (scale / 2))), mpaint);

        Paint mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mypaint.setAntiAlias(true);

        mypaint.setStrokeWidth((float) (scale * 0.0253));// 0.0983

        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setAntiAlias(true);

        mypaint.setColor(color2);

        float left = (float) (scale * 0.05);
        float top = (float) (scale * 0.05);
        float right = scale - (float) (scale * 0.05);
        float bottom = scale - (float) (scale * 0.05);

        RectF rectf = new RectF(left, top, right, bottom);

        float angle = mLevel * 360;
        angle = angle / 100;

        c.drawCircle((float) (scale / 2), (float) (scale / 2),
                (float) ((scale / 2) - (0.1 * (scale / 2))), mypaint);

        mypaint.setStrokeWidth((float) (scale * 0.0783));

        mypaint.setColor(color1);

        c.drawArc(rectf, -90, angle, false, mypaint);

        mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mypaint.setColor(color4);
        mypaint.setAntiAlias(true);

        // float size = (float) (scale * 0.11);

        mypaint.setTextSize(SpToPx(mycontext, 50f));
        mypaint.setTextAlign(Paint.Align.CENTER);
        Typeface tf = Typeface.createFromAsset(mycontext.getAssets(),
                "roboto.ttf");

        mypaint.setTypeface(tf);

        c.drawText(mLevel + "%", (float) (scale / 2),
                (float) (scale * 0.5 + (mypaint.getFontSpacing() / 3.7)),
                mypaint);

        if (isconnected) {
            Bitmap connected = BitmapFactory.decodeResource(
                    mycontext.getResources(), R.drawable.charging);
            int mScale = (int) (scale * 0.2);
            connected = Bitmap.createScaledBitmap(connected, mScale, mScale,
                    true);
            float mX = scale / 2 - connected.getWidth() / 2;
            float mY = (float) (scale * 0.7);
            c.drawBitmap(connected, mX, mY, mypaint);
        }

        return circleBitmap;
    }

    /**
     * Convert dp to px
     *
     * @author Sachin
     * @param i
     * @param mContext
     * @return
     */

    public static int ReturnHeight(float i, Context mContext) {

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        return (int) ((i * displayMetrics.density) + 0.5);

    }

    public static float SpToPx(Context context, Float i) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return i * scaledDensity;
    }

    public static String getBatHealth(Intent batteryIntent) {
        int inthealth = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH,
                0);
        String health = "Unknown";
        switch (inthealth) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                health = "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                health = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health = "Unspecified failure";
                break;
        }

        return health;
    }

}
