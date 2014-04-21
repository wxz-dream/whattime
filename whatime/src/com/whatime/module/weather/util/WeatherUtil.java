package com.whatime.module.weather.util;

import com.whatime.R;

public class WeatherUtil
{
 // 工具方法，该方法负责把返回的天气图标字符串，转换为程序的图片资源ID。
    public static int parseIcon(String strIcon)
    {
        if (strIcon == null)
            return -1;
        if ("0.gif".equals(strIcon))
            return R.drawable.a_0;
        if ("1.gif".equals(strIcon))
            return R.drawable.a_1;
        if ("2.gif".equals(strIcon))
            return R.drawable.a_2;
        if ("3.gif".equals(strIcon))
            return R.drawable.a_3;
        if ("4.gif".equals(strIcon))
            return R.drawable.a_4;
        if ("5.gif".equals(strIcon))
            return R.drawable.a_5;
        if ("6.gif".equals(strIcon))
            return R.drawable.a_6;
        if ("7.gif".equals(strIcon))
            return R.drawable.a_7;
        if ("8.gif".equals(strIcon))
            return R.drawable.a_8;
        if ("9.gif".equals(strIcon))
            return R.drawable.a_9;
        if ("10.gif".equals(strIcon))
            return R.drawable.a_10;
        if ("11.gif".equals(strIcon))
            return R.drawable.a_11;
        if ("12.gif".equals(strIcon))
            return R.drawable.a_12;
        if ("13.gif".equals(strIcon))
            return R.drawable.a_13;
        if ("14.gif".equals(strIcon))
            return R.drawable.a_14;
        if ("15.gif".equals(strIcon))
            return R.drawable.a_15;
        if ("16.gif".equals(strIcon))
            return R.drawable.a_16;
        if ("17.gif".equals(strIcon))
            return R.drawable.a_17;
        if ("18.gif".equals(strIcon))
            return R.drawable.a_18;
        if ("19.gif".equals(strIcon))
            return R.drawable.a_19;
        if ("20.gif".equals(strIcon))
            return R.drawable.a_20;
        if ("21.gif".equals(strIcon))
            return R.drawable.a_21;
        if ("22.gif".equals(strIcon))
            return R.drawable.a_22;
        if ("23.gif".equals(strIcon))
            return R.drawable.a_23;
        if ("24.gif".equals(strIcon))
            return R.drawable.a_24;
        if ("25.gif".equals(strIcon))
            return R.drawable.a_25;
        if ("26.gif".equals(strIcon))
            return R.drawable.a_26;
        if ("27.gif".equals(strIcon))
            return R.drawable.a_27;
        if ("28.gif".equals(strIcon))
            return R.drawable.a_28;
        if ("29.gif".equals(strIcon))
            return R.drawable.a_29;
        if ("30.gif".equals(strIcon))
            return R.drawable.a_30;
        if ("31.gif".equals(strIcon))
            return R.drawable.a_31;
        return 0;
    }
}
