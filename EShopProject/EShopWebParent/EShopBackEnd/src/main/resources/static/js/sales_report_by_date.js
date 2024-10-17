var data;
var chartOptions;
var totalNetSales;
var totalGrossSales;
var totalOrders;

$(document).ready(function () {
    setupButtonEventHandler("_date", loadSalesReportByDate);
});





function loadSalesReportByDate(period) {
    if (period == "custom") {
        startDate = $("#startDate_date").val();
        endDate = $("#endDate_date").val();
        requestUrl = contextPath + "reports/sales_by_date/" + startDate + "/" + endDate;
    } else {
        requestUrl = contextPath + "reports/sales_by_date/" + period;
    }
    $.get(requestUrl, function(responseJSON) {
        prepareChartDataForSalesByDate(responseJSON);
        customizeChartForSalesByDate(period);
        formatChartData(data,1,2);
        drawChartForSalesByDate(period);
        setSellAmount(period, '_date', "Total Items");
    });

}



function prepareChartDataForSalesByDate(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');
    data.addColumn('number', 'Orders');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalOrders = 0;

    $.each(responseJSON, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.ordersCount]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalOrders += parseInt(reportItem.ordersCount);
    });

}



function customizeChartForSalesByDate(period) {
    chartOptions = {
        title : getChartTitle(period),
        height : 360,
        legend : {position : 'top'},
        series :  {
            0 : {targetAxisIndex : 0},
            1 : {targetAxisIndex : 0},
            2 : {targetAxisIndex : 1}
        },
        vAxes : {
            0 : {title : 'Sales Amount', format : 'currency'},
            1 : {title : 'Number of Orders'}
        }

    };


}



function drawChartForSalesByDate() {
    var salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
    salesChart.draw(data, chartOptions);


}



