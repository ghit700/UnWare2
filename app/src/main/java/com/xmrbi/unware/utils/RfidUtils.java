package com.xmrbi.unware.utils;

/**
 * Created by wzn on 2018/5/22.
 */

public class RfidUtils {
    // 判断扫出来的rfid是否符合规范XXXXXXXAAACCCCCCCCCCCCCC
    public static Boolean isAccord(String s) {
        Boolean fl = false;
        if (s != null) {
            if (s.length() > 10) {
                if (s.subSequence(0, 10).equals("0000000AAA")) {
                    fl = true;
                } else {
                    fl = false;
                }
            } else {
                fl = false;
            }

        } else {
            fl = false;
        }
        return fl;
    }
}
