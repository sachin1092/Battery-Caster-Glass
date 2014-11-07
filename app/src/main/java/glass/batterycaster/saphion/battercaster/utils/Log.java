package glass.batterycaster.saphion.battercaster.utils;

/**
 * Created by sachin.shinde on 11/9/14.
 */
public class Log {

    public static final String TAG = "Battery Caster Glass";
    public static final boolean DEBUG = false;

    public static void d(Object... msg){
        if(!DEBUG)
            return;
        String new_msg = "";
        for(Object m : msg)
            new_msg = new_msg + " " + m.toString();
        android.util.Log.d(TAG, new_msg);
    }

    public static void w(Object... msg){
        if(!DEBUG)
            return;
        String new_msg = "";
        for(Object m : msg)
            new_msg = new_msg + " " + m.toString();
        android.util.Log.w(TAG, new_msg);
    }
    public static void e(Object... msg){
        if(!DEBUG)
            return;
        String new_msg = "";
        for(Object m : msg)
            new_msg = new_msg + " " + m.toString();
        android.util.Log.e(TAG, new_msg);
    }
    public static void i(Object... msg){
        if(!DEBUG)
            return;
        String new_msg = "";
        for(Object m : msg)
            new_msg = new_msg + " " + m.toString();
        android.util.Log.i(TAG, new_msg);
    }

}
