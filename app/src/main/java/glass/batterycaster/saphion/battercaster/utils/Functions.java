package glass.batterycaster.saphion.battercaster.utils;

/**
 * Created by sachin.shinde on 6/11/14.
 */
public class Functions {

    public static String updateTemperature(float temperature, boolean bool,
                                           boolean nbool) {
        if (nbool) {
            if (bool) {
                return temperature + "° C";

            } else {
                return adjustLenght(String
                        .valueOf(((float) (((temperature)) / 0.56) + 32)))
                        + "° F";
            }
        } else {
            if (bool) {
                String temp = temperature + "";
                if ((temp).contains(".")) {
                    temp = temp.substring(0, temp.indexOf("."));
                }
                return temp + "°";

            } else {
                String temp = adjustLenght(String
                        .valueOf(((float) (((temperature)) / 0.56) + 32))) + "";
                if ((temp).contains(".")) {
                    temp = temp.substring(0, temp.indexOf("."));
                }
                return temp + "°";
            }
        }
    }

    public static String adjustLenght(String s) {
        if (s.length() > 4) {
            s = s.substring(0, 4);
        }
        return s;
    }

}
