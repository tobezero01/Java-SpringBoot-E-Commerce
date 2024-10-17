package com.eshop.admin.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractReportService {
    protected DateFormat dateFormat;

    public List<ReportItem> getReportDataLast7Days(ReportType reportType) {
        return getReportDataLastXDays(7, reportType);
    }

    public List<ReportItem> getReportDataLast28Days(ReportType reportType) {
        return getReportDataLastXDays(28, reportType);
    }

    protected List<ReportItem> getReportDataLastXDays(int days, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();
        System.out.println("Start time = " + startTime);
        System.out.println("End time = " + endTime);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    public List<ReportItem> getReportDataLast6Months(ReportType reportType) {
        return getReportDataLastXMonth(6,reportType);
    }
    public List<ReportItem> getReportDataLastYears(ReportType reportType) {

        return getReportDataLastXMonth(12,reportType);
    }

    protected List<ReportItem> getReportDataLastXMonth(int months,ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -(months - 1));
        Date startTime = calendar.getTime();
        System.out.println("Start time = " + startTime);
        System.out.println("End time = " + endTime);

        dateFormat = new SimpleDateFormat("yyyy-MM");
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }
    public List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime, ReportType reportType) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }
    protected abstract List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) ;
}
