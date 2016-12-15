package com.skp.payment.p2plending.backoffice.common;

/**
 * Created by 1000903 on 2016-11-15.
 */
public class CommonUtils {

    public static String nvl(String src) {
        return nvl(src, "");
    }

    public static String nvl(String src, String replaceVal) {

        if (src == null)
            return replaceVal;
        else if (src.equals(""))
            return replaceVal;
        else
            return src;
    }

    public static boolean isEmpty(String input) {
        return (input == null || input.trim().equals(""));
    }

    public static boolean isNotEmpty(String input) {
        return isEmpty(input) ? false : true;
    }
}
