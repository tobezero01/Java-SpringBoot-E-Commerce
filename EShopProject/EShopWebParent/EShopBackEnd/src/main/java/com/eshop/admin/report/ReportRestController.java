package com.eshop.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportRestController {
    @Autowired private MasterOrderReportService masterOrderReportService;

    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period) {
        System.out.println("Period = " + period);
        switch (period) {
            case "last_7_days" : return masterOrderReportService.getReportDataLast7Days();
            case "last_28_days" : return masterOrderReportService.getReportDataLast28Days();
            case "last_6_months" : return masterOrderReportService.getReportDataLast6Months();
            case "last_years" : return masterOrderReportService.getReportDataLastYears();
            default: return masterOrderReportService.getReportDataLast7Days();
        }

    }

}
