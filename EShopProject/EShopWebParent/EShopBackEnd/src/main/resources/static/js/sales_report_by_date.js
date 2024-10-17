var data;
var chartOptions;
var totalNetSales ;
var totalGrossSales ;
var totalOrders;
var MILLISECONDS_A_DAYS = 24 * 60 * 60 * 1000;

$(document).ready(function () {
    $(".button_sales_by_date").on("click" , function() {
        divCustomDateRange = $("#divCustomDateRange");
        startDateField = document.getElementById('startDate');
        endDateField = document.getElementById('endDate');

        $(".button_sales_by_date").each(function(e) {
            $(this).removeClass("btn-primary").addClass("btn-light");
        });
        $(this).removeClass("btn-light").addClass("btn-primary");

        period = $(this).attr("period");
        if(period) {
            loadSalesReportByDate(period);
            divCustomDateRange.addClass("d-none");
        } else {
            divCustomDateRange.removeClass("d-none");
        }

    });
    initCustomDateRange();

    $("#buttonViewReportByDateRange").on("click", function() {
        validateDateRange();
    });

});



function validateDateRange() {
    days = calculateDay();
    alert(days);
}



function calculateDay() { 
    startDate = startDateField.val();
    endDate = endDateField.val();
    differentInMilliseconds = endDate - startDate;
    return differentInMilliseconds / MILLISECONDS_A_DAYS;
}



function initCustomDateRange() {
    toDate = new Date();
    endDateField.valueAsDate = toDate;

    fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.valueAsDate = fromDate;
}



function loadSalesReportByDate(period) {
    requestUrl = contextPath + "reports/sales_by_date/" + period;
    $.get(requestUrl, function(responseJSON) {
        prepareChartData(responseJSON);
        customizeChart(period);
        drawChart(period);
    });

}



function prepareChartData(responseJSON) {
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



function customizeChart(period) {
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

    var formatter = new google.visualization.NumberFormat({
        prefix : '$'
    });

    formatter.format(data, 1);
    formatter.format(data, 2);

}



function drawChart(period) {
    var salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
    salesChart.draw(data, chartOptions);

   var denominator = getDenominator(period);

    $("#textTotalGrossSales").text("$" + $.number(totalGrossSales, 2));
    $("#textTotalNetSales").text("$" + $.number(totalNetSales, 2));
    $("#textAvgGrossSales").text("$" + $.number(totalGrossSales / denominator, 2));
    $("#textAvgNetSales").text("$" + $.number(totalNetSales / denominator, 2));
    $("#textTotalOrders").text(totalOrders);
}



function getChartTitle(period) {
    if(period == "last_7_days") return 'Sales in last 7 days';
    if(period == "last_28_days") return 'Sales in last 28 days';
    if(period == "last_6_months") return 'Sales in last 6 months';
    if(period == "last_years") return 'Sales in last years';
    return 'Sales in last 7 days';
}



function getDenominator(period) {
    if(period == "last_7_days") return 7;
    if(period == "last_28_days") return 28;
    if(period == "last_6_months") return 6;
    if(period == "last_years") return 12;
    return 7;
}

