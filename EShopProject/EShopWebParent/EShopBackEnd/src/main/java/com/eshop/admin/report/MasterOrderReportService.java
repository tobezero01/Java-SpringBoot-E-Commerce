package com.eshop.admin.report;

import com.eshop.admin.order.repository.OrderRepository;
import com.eshop.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MasterOrderReportService extends AbstractReportService{

    @Autowired private OrderRepository orderRepository;

    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        //printRawDate(listOrders);
        List<ReportItem> listReportItems =  createReportData(startTime, endTime, reportType);

        calculateSalesForReportData(listOrders, listReportItems);

        printReportData(listReportItems);
        return listReportItems;
    }

    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for (Order order : listOrders) {
            String orderDateString = dateFormat.format(order.getOrderTime());

            ReportItem reportItem = new ReportItem(orderDateString);

            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);
                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        }

    }

    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(reportItem -> {
            System.out.printf("%s , %10.2f , %10.2f , %d \n",
                    reportItem.getIdentifier(), reportItem.getGrossSales(), reportItem.getNetSales(), reportItem.getOrdersCount());
        });
    }


    private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType) {
        List<ReportItem> listReportItems = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dataString = dateFormat.format(currentDate);
        listReportItems.add(new ReportItem(dataString));

        do {
            if (reportType.equals(ReportType.DAY)) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (reportType.equals(ReportType.MONTH)) {
                startDate.add(Calendar.MONTH, 1);
            }

            currentDate = startDate.getTime();
            dataString = dateFormat.format(currentDate);
            listReportItems.add(new ReportItem(dataString));
        } while (startDate.before(endDate));

        return listReportItems;
    }


    private void printRawDate(List<Order> listOrders) {
        listOrders.forEach(order -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f \n" ,
                    order.getId(), order.getOrderTime(), order.getTotal(), order.getProductCost());
        });
    }

}
