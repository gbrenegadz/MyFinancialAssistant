package com.gbrenegadzdev.financeassistant.models.realm;

import android.util.Log;
import android.widget.TextView;

import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.exceptions.RealmException;

public class MonthlyReport extends RealmObject {
    private static final String TAG = MonthlyReport.class.getSimpleName();

    public static final int REPORT_TYPE_INCOME = 1;
    public static final int REPORT_TYPE_EXPENSE = 2;
    public static final int REPORT_TYPE_TOP_INCOME = 3;
    public static final int REPORT_TYPE_TOP_EXPENSE = 4;
    public static final int REPORT_TYPE_TOP_PRODUCT = 5;
    public static final int REPORT_TYPE_TOP_ENTITY_SPENT_TO = 6;

    public static final String MONTHLY_REPORT_ID = "monthlyReportId";
    public static final String REPORT_TYPE = "reportType";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String AMOUNT = "amount"; // Update the value whenever income or expense is done
    public static final String CREATED_DATETIME = "createdDatetime";

    public MonthlyReport() {
    }

    public MonthlyReport(int reportType, int year, String month, double amount) {
        this.reportType = reportType;
        this.year = year;
        this.month = month;
        this.amount = amount;
    }

    @PrimaryKey
    private String monthlyReportId;
    private int reportType;
    private int year;
    private String month;
    private double amount;
    private Date createdDatetime;

    public String getMonthlyReportId() {
        return monthlyReportId;
    }

    public void setMonthlyReportId(String monthlyReportId) {
        this.monthlyReportId = monthlyReportId;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
        return "MonthlyReport{" +
                "monthlyReportId='" + monthlyReportId + '\'' +
                ", reportType=" + reportType +
                ", year=" + year +
                ", month='" + month + '\'' +
                ", amount=" + amount +
                ", createdDatetime=" + createdDatetime +
                '}';
    }

    /**
     * Methods for saving or updating Monthly Report
     */
    public void addUpdateAmount() {
        Log.d(TAG, "Amount : " + amount + "\tMonth : " + month + "\tYear : " + year + "\tReport Type : " + reportType);
        final Realm realm = Realm.getDefaultInstance();
        try {
            final MonthlyReport updateMonthlyReport = realm.where(MonthlyReport.class)
                    .equalTo(MonthlyReport.REPORT_TYPE, reportType)
                    .equalTo(MonthlyReport.YEAR, year)
                    .equalTo(MonthlyReport.MONTH, month)
                    .findFirst();
            if (updateMonthlyReport != null) {
                final double newAmount = updateMonthlyReport.getAmount() + amount;
                updateMonthlyReport.setAmount(newAmount);

                realm.insertOrUpdate(updateMonthlyReport);
            } else {
                final DateTimeUtils dateTimeUtils = new DateTimeUtils();
                final MonthlyReport newMonthlyReport = new MonthlyReport();
                newMonthlyReport.setMonthlyReportId(UUID.randomUUID().toString());
                newMonthlyReport.setReportType(reportType);
                newMonthlyReport.setYear(year);
                newMonthlyReport.setMonth(month);
                newMonthlyReport.setAmount(amount);
                newMonthlyReport.setCreatedDatetime(dateTimeUtils.getCurrentDatetime());

                realm.insert(newMonthlyReport);
            }
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } finally {
            realm.close();
        }
    }
}
