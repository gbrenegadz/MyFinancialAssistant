package com.gbrenegadzdev.financeassistant.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyYAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {

        // format values to 1 decimal digit
        mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mFormat.format(value) + " $";
    }

    /** this is only needed if numbers are returned, else return 0 */
    public int getDecimalDigits() { return 1; }
}
