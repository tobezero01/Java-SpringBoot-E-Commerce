var data;
var chartOptions;


$(document).ready(function () {
    setupButtonEventHandler("_product", loadSalesReportByDateForProduct);
});


function loadSalesReportByDateForProduct(period) {
    if (period == "custom") {
        startDate = $("#startDate_product").val();
        endDate = $("#endDate_product").val();
        requestUrl = contextPath + "reports/product/" + startDate + "/" + endDate;
    } else {
        requestUrl = contextPath + "reports/product/" + period;
    }
    $.get(requestUrl)
        .done(function(responseJSON) {
            prepareChartDataForSalesByProduct(responseJSON);
            customizeChartForSalesByProduct();
            formatChartData(data, 2, 3);
            drawChartForSalesByProduct(period);
            setSellAmount(period, '_product', "Total Products");
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            console.error("Error loading data: " + textStatus, errorThrown);
        });

}

function prepareChartDataForSalesByProduct(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Product');
    data.addColumn('number', 'Quantity');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    // Thay vì sử dụng biến `rows`, hãy thêm trực tiếp vào `data`
    $.each(responseJSON, function(index, reportItem) {
        data.addRow([reportItem.identifier, reportItem.productsCount, reportItem.grossSales, reportItem.netSales]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    });
}




function customizeChartForSalesByProduct() {
    chartOptions = {
        height : 360,
        width : 780,// height
        showRowNumber : true,
        page : 'enable',
        sortColumn : 2,
        sortAscending : false
    };


}



function drawChartForSalesByProduct(period) {
    var salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
    salesChart.draw(data, chartOptions);
}




