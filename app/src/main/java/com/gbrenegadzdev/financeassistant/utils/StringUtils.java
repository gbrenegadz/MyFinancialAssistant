package com.gbrenegadzdev.financeassistant.utils;

import java.text.DecimalFormat;

public class StringUtils {
    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    public String getDecimal2(double value) {
        return formatter.format(value);
    }
}
