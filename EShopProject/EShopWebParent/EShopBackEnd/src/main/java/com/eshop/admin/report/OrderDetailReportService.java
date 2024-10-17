package com.eshop.admin.report;

import com.eshop.admin.order.repository.OrderDetailRepository;
import com.eshop.common.entity.order.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderDetailReportService extends AbstractReportService{

    @Autowired private OrderDetailRepository orderDetailRepository;
    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
        List<OrderDetail> orderDetailList = null;

        if (reportType.equals(ReportType.CATEGORY) ) {
            orderDetailList = orderDetailRepository.findWithCategoryAndTimeBetween(startDate,endDate);
        }else if (reportType.equals(ReportType.PRODUCT)){
            orderDetailList = orderDetailRepository.findWithProductAndTimeBetween(startDate,endDate);
        }
        
        //printRawData(orderDetailList);

        List<ReportItem> reportItemList = new ArrayList<>();
        for (OrderDetail detail : orderDetailList) {
            String identifier = "";
            if (reportType.equals(ReportType.CATEGORY)) {
                identifier = detail.getProduct().getCategory().getName();
            }else if (reportType.equals(ReportType.PRODUCT)){
                identifier = detail.getProduct().getShortName();
            }
            ReportItem reportItem = new ReportItem(identifier);

            float grossSales = detail.getSubtotal() + detail.getShippingCost();
            float netSales = detail.getSubtotal() - detail.getProductCost();

            int itemIndex = reportItemList.indexOf(reportItem);
            if(itemIndex >= 0 && itemIndex < reportItemList.size()) {

                reportItem = reportItemList.get(itemIndex);
                reportItem.addGrossSales(grossSales);
                reportItem.addNetSales(netSales);
                reportItem.increaseProductsCount(detail.getQuantity());
            } else {
                reportItemList.add(new ReportItem(identifier, grossSales, netSales, detail.getQuantity()));
            }
        }
        //printReportData(reportItemList);
        return reportItemList;
    }

    private void printReportData(List<ReportItem> reportItemList) {
        for (ReportItem item : reportItemList) {
            System.out.printf("%-20s, %10.2f, %10.2f, %f \n",
                    item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getGrossSales());
        }
    }

    private void printRawData(List<OrderDetail> orderDetailList) {
        for (OrderDetail detail : orderDetailList) {
            System.out.printf("%d, %-20s, %10.2f, %10.2f, %10.2f \n",
                    detail.getQuantity(), detail.getProduct().getShortName(),
                    detail.getSubtotal(), detail.getProductCost(), detail.getShippingCost());
        }
    }
}
