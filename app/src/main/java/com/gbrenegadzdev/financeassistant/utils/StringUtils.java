package com.gbrenegadzdev.financeassistant.utils;

import java.text.DecimalFormat;

public class StringUtils {
    DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

    public String getDecimal2(double value) {
        return formatter.format(value);
    }

    public String getString(String string, int index) {
        String[] array = string.split("-", -1);
        return array[index].trim();
    }
}
