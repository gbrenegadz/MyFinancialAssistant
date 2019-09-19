package com.gbrenegadzdev.financeassistant.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.gbrenegadzdev.financeassistant.MainActivity;
import com.gbrenegadzdev.financeassistant.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChartUtils {
    private static final String TAG = ChartUtils.class.getSimpleName();

    /**
     * ============================================================================================
     * Start
     * Setup and Populate Bar Chart
     * ============================================================================================
     */
    public void setupChart(HorizontalBarChart barChart) {
        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(false);
    }

    public void setupXAxis(HorizontalBarChart barChart, final ArrayList<String> xLabels) {
        //Display the axis on the left (contains the labels 1*, 2* and so on)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);

        //Set label count to 5 as we are displaying 5 star rating
        if (xLabels.size() < 5) {
            xAxis.setLabelCount(xLabels.size());
        } else {
            xAxis.setLabelCount(5);
        }

        // Now add the labels to be added on the vertical axis
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabels.get((int) value);
            }

        });
    }

    public void setupYAxis(HorizontalBarChart barChart, double maxValue) {
        YAxis yLeft = barChart.getAxisLeft();

        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.setAxisMaximum((float) maxValue);
        yLeft.setAxisMinimum(0f);
        yLeft.setEnabled(false);
        yLeft.setTextSize(16);

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(true);
        yRight.setEnabled(false);
    }

    public void setGraphData(Context context, HorizontalBarChart barChart, ArrayList<BarEntry> entries) {
        // Plot entries to chart
        Collections.sort(entries, new Sortbyroll());
        BarDataSet barDataSet = new BarDataSet(entries, "");

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
                ContextCompat.getColor(barChart.getContext(), R.color.md_red_500),
                ContextCompat.getColor(barChart.getContext(), R.color.md_deep_orange_400),
                ContextCompat.getColor(barChart.getContext(), R.color.md_yellow_A700),
                ContextCompat.getColor(barChart.getContext(), R.color.md_green_700),
                ContextCompat.getColor(barChart.getContext(), R.color.md_indigo_700));

        //Set bar shadows
        barChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);
        data.setValueTextSize(16);
        data.setValueTextColor(context.getResources().getColor(R.color.white));


        //Finally set the data and refresh the graph
        barChart.setData(data);
        barChart.invalidate();
        barChart.notifyDataSetChanged();

        //Add animation to the graph
        barChart.animateY(1000);
    }

    /**
     * ============================================================================================
     * End
     * Setup and Populate Bar Chart
     * ============================================================================================
     *
     */

    class Sortbyroll implements Comparator<BarEntry> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(BarEntry a, BarEntry b) {
            int compareInt = (int) (a.getY() - b.getY());
            Log.d(TAG, "Compare Int : " + compareInt);
            return compareInt;
        }
    }
}
