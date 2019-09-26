package com.gbrenegadzdev.financeassistant.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.models.realm.MonthlyReport;
import com.gbrenegadzdev.financeassistant.utils.Constants;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.MathUtils;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class MonthlyDashboardFragment extends Fragment {
    private static final String TAG = MonthlyDashboardFragment.class.getSimpleName();
    private Realm monthlyDashboardRealm;

    public static final String[] MONTHS = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public static final String[] MONTHS2 = new String[] {
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private MathUtils mathUtils = new MathUtils();
    private DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private RealmResults<MonthlyReport> monthlyIncomeReportRealmResults;
    private RealmResults<MonthlyReport> monthlyExpenseReportRealmResults;

    private CombinedChart mCombindedChartLine;
    private CombinedChart mCombindedChartBar;

    public MonthlyDashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.constraint_monthly_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
//        setupMonthlyReport(); // This is for testing only
        getIncome();
        getExpense();
        setupLineChart();
        setupBarChart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            monthlyDashboardRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void setupRealm() {
        monthlyDashboardRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        if (getActivity() != null) {
            mCombindedChartLine = getActivity().findViewById(R.id.monthly_combined_chart_line);
            mCombindedChartBar = getActivity().findViewById(R.id.monthly_combined_chart_bar);
        }
    }

    private void getIncome() {
        monthlyIncomeReportRealmResults = monthlyDashboardRealm.where(MonthlyReport.class)
                .equalTo(MonthlyReport.REPORT_TYPE, Constants.REPORT_TYPE_INCOME)
                .findAll();
    }

    private void getExpense() {
        monthlyExpenseReportRealmResults = monthlyDashboardRealm.where(MonthlyReport.class)
                .equalTo(MonthlyReport.REPORT_TYPE, Constants.REPORT_TYPE_EXPENSE)
                .findAll();
        if (monthlyExpenseReportRealmResults != null) {
            for (MonthlyReport expenseReport : monthlyExpenseReportRealmResults) {
                Log.e(TAG,"monthlyExpenseReportRealmResults : " + monthlyExpenseReportRealmResults);
            }
        }
    }

    private void setupLineChart() {
        mCombindedChartLine.getDescription().setEnabled(false);
        mCombindedChartLine.setBackgroundColor(Color.WHITE);
        mCombindedChartLine.setDrawGridBackground(false);
        mCombindedChartLine.setDrawBarShadow(false);
        mCombindedChartLine.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mCombindedChartLine.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE
        });


        Legend l = mCombindedChartLine.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mCombindedChartLine.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        YAxis leftAxis = mCombindedChartLine.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mCombindedChartLine.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return MONTHS[(int) value % MONTHS.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());

        xAxis.setAxisMaximum(data.getXMax());

        mCombindedChartLine.setData(data);
        mCombindedChartLine.invalidate();
    }

    private void setupBarChart() {
        mCombindedChartBar.getDescription().setEnabled(false);
        mCombindedChartBar.setBackgroundColor(Color.WHITE);
        mCombindedChartBar.setDrawGridBackground(false);
        mCombindedChartBar.setDrawBarShadow(false);
        mCombindedChartBar.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mCombindedChartBar.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR
        });


        Legend l = mCombindedChartBar.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mCombindedChartBar.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        YAxis leftAxis = mCombindedChartBar.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mCombindedChartBar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return MONTHS2[(int) value % MONTHS2.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 2);

        mCombindedChartBar.setData(data);
        mCombindedChartBar.invalidate();
    }

    private LineData generateLineData() {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();

        double totalIncome = 0;
        if (monthlyIncomeReportRealmResults != null) {
            int index = 0;
            for (MonthlyReport monthlyReport : monthlyIncomeReportRealmResults) {
                Log.e(TAG, "Monthly Income Report : " + monthlyReport.toString());
                totalIncome += monthlyReport.getAmount();
                entries.add(new Entry(index, (float) totalIncome));
                index++;
            }

            if (index < MONTHS.length) {
                for (int i = index; i < MONTHS.length; i++) {
                    entries.add(new Entry(i, (float) 0));
                }
            }
        } else {
            for (int i =0; i < MONTHS.length; i++) {
                entries.add(new Entry(i, (float) totalIncome));
            }
        }

        double totalExpense = 0;
        if (monthlyExpenseReportRealmResults != null) {
            int index = 0;
            for (MonthlyReport monthlyReport : monthlyExpenseReportRealmResults) {
                Log.e(TAG, "Monthly Expense Report : " + monthlyReport.toString());
                totalExpense += monthlyReport.getAmount();
                entries2.add(new Entry(index, (float) totalExpense));
                index++;
            }

            if (index < MONTHS.length) {
                for (int i = index; i < MONTHS.length; i++) {
                    entries2.add(new Entry(i, (float) 0));
                }
            }
        } else {
            for (int i =0; i < MONTHS.length; i++) {
                entries2.add(new Entry(0, (float) totalExpense));
            }
        }

        LineDataSet set1 = new LineDataSet(entries, "Accumulated Income ");
        set1.setColor(getResources().getColor(R.color.material500));
        set1.setLineWidth(2.5f);
        set1.setCircleColor(getResources().getColor(R.color.material500));
        set1.setCircleRadius(5f);
        set1.setFillColor(Color.rgb(240, 238, 70));
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawValues(false);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(getResources().getColor(R.color.md_black_1000));

        LineDataSet set2 = new LineDataSet(entries2, "Accumulated Expense");
        set2.setColor(getResources().getColor(R.color.colorError));
        set2.setLineWidth(2.5f);
        set2.setCircleColor(getResources().getColor(R.color.colorError));
        set2.setCircleRadius(5f);
        set2.setFillColor(Color.rgb(240, 238, 70));
        set2.setMode(LineDataSet.Mode.LINEAR);
        set2.setDrawValues(false);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(getResources().getColor(R.color.md_black_1000));

        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
//        d.addDataSet(set, set2);
        LineData d = new LineData(set1, set2);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        if (monthlyIncomeReportRealmResults != null) {
            int index = 0;
            for (MonthlyReport monthlyReport : monthlyIncomeReportRealmResults) {
                Log.d(TAG, "Monthly Report Income : " + monthlyReport.toString());
                entries1.add(new BarEntry(index, (float) monthlyReport.getAmount()));
                index++;
            }

            if (index < MONTHS.length) {
                for (int i = index; i < MONTHS.length; i++) {
                    entries1.add(new BarEntry(i, (float) 0));
                }
            }
        } else {
            for (int i =0; i < MONTHS.length; i++) {
                entries1.add(new BarEntry(0, (float) 0));
            }
        }

        if (monthlyExpenseReportRealmResults != null) {
            int index = 0;
            for (MonthlyReport monthlyReport : monthlyExpenseReportRealmResults) {
                Log.d(TAG, "Monthly Report Expense : " + monthlyReport.toString());
                entries2.add(new BarEntry(index, (float) monthlyReport.getAmount()));
                index++;
            }

            if (index < MONTHS.length) {
                for (int i = index; i < MONTHS.length; i++) {
                    entries2.add(new BarEntry(i, (float) 0));
                }
            }
        } else {
            for (int i =0; i < MONTHS.length; i++) {
                entries2.add(new BarEntry(0, (float) 0));
            }
        }

        BarDataSet set1 = new BarDataSet(entries1, "Income");
        set1.setColor(getResources().getColor(R.color.material500));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "Expense");
        set2.setColors(getResources().getColor(R.color.colorError));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

//        float groupSpace = 0.2f;
//        float barSpace = 0.01f; // x2 dataset
//        float barWidth = 0.2f; // x2 dataset

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);
        d.setDrawValues(false);

        // make this BarData object grouped
        d.groupBars(0 + 0.5f, groupSpace, barSpace); // start at x = 0

        return d;
    }

    private void setupMonthlyReport() {
        final MonthlyReport checkIncomeMonthlyReport = monthlyDashboardRealm.where(MonthlyReport.class)
                .findFirst();
        if (checkIncomeMonthlyReport == null) {
            monthlyDashboardRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < MONTHS.length; i++) {
                        int amount = (int) mathUtils.getRandom(12000, 15000);

                        final MonthlyReport newMonthlyReport = new MonthlyReport();
                        newMonthlyReport.setMonthlyReportId(UUID.randomUUID().toString());
                        newMonthlyReport.setReportType(Constants.REPORT_TYPE_INCOME);
                        newMonthlyReport.setMonth(MONTHS[i]);
                        newMonthlyReport.setAmount(amount);
                        newMonthlyReport.setCreatedDatetime(dateTimeUtils.getCurrentDatetime());

                        realm.insert(newMonthlyReport);
                    }
                }
            });
        }

        final MonthlyReport checkExpenseMonthlyReport = monthlyDashboardRealm.where(MonthlyReport.class)
                .findFirst();
        if (checkExpenseMonthlyReport == null) {
            monthlyDashboardRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < MONTHS.length; i++) {
                        int amount = (int) mathUtils.getRandom(10000, 10000);

                        final MonthlyReport newMonthlyReport = new MonthlyReport();
                        newMonthlyReport.setMonthlyReportId(UUID.randomUUID().toString());
                        newMonthlyReport.setReportType(Constants.REPORT_TYPE_EXPENSE);
                        newMonthlyReport.setMonth(MONTHS[i]);
                        newMonthlyReport.setAmount(amount);
                        newMonthlyReport.setCreatedDatetime(dateTimeUtils.getCurrentDatetime());

                        realm.insert(newMonthlyReport);
                    }
                }
            });
        }
    }
}
