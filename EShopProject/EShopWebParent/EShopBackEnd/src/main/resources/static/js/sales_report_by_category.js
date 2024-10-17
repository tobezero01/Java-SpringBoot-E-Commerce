var data;
var chartOptions;


$(document).ready(function () {
    setupButtonEventHandler("_category", loadSalesReportByDateForCategory);
});


function loadSalesReportByDateForCategory(period) {
    if (period == "custom") {
        startDate = $("#startDate_category").val();
        endDate = $("#endDate_category").val();
        requestUrl = contextPath + "reports/category/" + startDate + "/" + endDate;
    } else {
        requestUrl = contextPath + "reports/category/" + period;
    }
    $.get(requestUrl)
        .done(function(responseJSON) {
            prepareChartDataForSalesByCategory(responseJSON);
            customizeChartForSalesByCategory();
            formatChartData(data, 1, 2);
            drawChartForSalesByCategory(period);
            setSellAmount(period, '_category', "Total Products");
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            console.error("Error loading data: " + textStatus, errorThrown);
        });

}


function prepareChartDataForSalesByCategory(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Category');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    var rows = []; // Mảng để chứa dữ liệu

    $.each(responseJSON, function(index, reportItem) {
        rows.push([reportItem.identifier, reportItem.grossSales, reportItem.netSales]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    });

    data.addRows(rows); // Thêm tất cả hàng cùng một lúc
}




function customizeChartForSalesByCategory() {
    chartOptions = {
        height : 360, // height
        legend : {position : 'right'}
    };


}


function drawChartForSalesByCategory() {
    var salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
    salesChart.draw(data, chartOptions);


}



