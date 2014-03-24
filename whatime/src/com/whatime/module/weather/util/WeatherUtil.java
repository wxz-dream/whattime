package com.whatime.module.weather.util;

import com.whatime.R;

public class WeatherUtil
{
 // 工具方法，该方法负责把返回的天气图标字符串，转换为程序的图片资源ID。
    public static int parseIcon(String strIcon)
    {
        if (strIcon == null)
            return -1;
        if ("a_0".equals(strIcon))
            return R.drawable.a_0;
        if ("a_1".equals(strIcon))
            return R.drawable.a_1;
        if ("a_2".equals(strIcon))
            return R.drawable.a_2;
        if ("a_3".equals(strIcon))
            return R.drawable.a_3;
        if ("a_4".equals(strIcon))
            return R.drawable.a_4;
        if ("a_5".equals(strIcon))
            return R.drawable.a_5;
        if ("a_6".equals(strIcon))
            return R.drawable.a_6;
        if ("a_7".equals(strIcon))
            return R.drawable.a_7;
        if ("a_8".equals(strIcon))
            return R.drawable.a_8;
        if ("a_9".equals(strIcon))
            return R.drawable.a_9;
        if ("a_10".equals(strIcon))
            return R.drawable.a_10;
        if ("a_11".equals(strIcon))
            return R.drawable.a_11;
        if ("a_12".equals(strIcon))
            return R.drawable.a_12;
        if ("a_13".equals(strIcon))
            return R.drawable.a_13;
        if ("a_14".equals(strIcon))
            return R.drawable.a_14;
        if ("a_15".equals(strIcon))
            return R.drawable.a_15;
        if ("a_16".equals(strIcon))
            return R.drawable.a_16;
        if ("a_17".equals(strIcon))
            return R.drawable.a_17;
        if ("a_18".equals(strIcon))
            return R.drawable.a_18;
        if ("a_19".equals(strIcon))
            return R.drawable.a_19;
        if ("a_20".equals(strIcon))
            return R.drawable.a_20;
        if ("a_21".equals(strIcon))
            return R.drawable.a_21;
        if ("a_22".equals(strIcon))
            return R.drawable.a_22;
        if ("a_23".equals(strIcon))
            return R.drawable.a_23;
        if ("a_24".equals(strIcon))
            return R.drawable.a_24;
        if ("a_25".equals(strIcon))
            return R.drawable.a_25;
        if ("a_26".equals(strIcon))
            return R.drawable.a_26;
        if ("a_27".equals(strIcon))
            return R.drawable.a_27;
        if ("a_28".equals(strIcon))
            return R.drawable.a_28;
        if ("a_29".equals(strIcon))
            return R.drawable.a_29;
        if ("a_30".equals(strIcon))
            return R.drawable.a_30;
        if ("a_31".equals(strIcon))
            return R.drawable.a_31;
        return 0;
    }
}
