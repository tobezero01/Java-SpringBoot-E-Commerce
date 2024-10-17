// Sale report comment

var MILLISECONDS_A_DAYS = 24 * 60 * 60 * 1000;


function setupButtonEventHandler(reportType, callbackFunction) {
    // Khởi tạo các trường ngày khi tài liệu được tải
    startDateField = document.getElementById('startDate' + reportType);
    endDateField = document.getElementById('endDate' + reportType);
    var divCustomDateRange = $("#divCustomDateRange" + reportType);

    $(".button_sales_by"+ reportType).on("click", function () {

        $(".button_sales_by" + reportType).each(function () {
            $(this).removeClass("btn-primary").addClass("btn-light");
        });
        $(this).removeClass("btn-light").addClass("btn-primary");

        var period = $(this).attr("period");
        if (period) {
            callbackFunction(period);
            divCustomDateRange.addClass("d-none");
        } else {
            divCustomDateRange.removeClass("d-none");
        }
    });

    initCustomDateRange(reportType);

    $("#buttonViewReportByDateRange"+ reportType).on("click", function () {
        validateDateRange(reportType);
    });
}


function validateDateRange(reportType) {
    startDateField = document.getElementById("startDate" + reportType);

    days = calculateDays(reportType);
    startDateField.setCustomValidity("");
    
    if(days >= 7 && days <= 30) {
        loadSalesReportByDate("custom");
    } else {
        startDateField.setCustomValidity("Dates must be in the range of 7 ... 30 days");
        startDateField.reportValidity();
    }
}



function calculateDays(reportType) {
    startDateField = document.getElementById("startDate" + reportType);
    endDateField = document.getElementById("endDate" + reportType);
    var startDate = startDateField.valueAsDate;
    var endDate = endDateField.valueAsDate;  // Sửa lại thành `valueAsDate`
    

    var differenceInMilliseconds = endDate - startDate;
    return differenceInMilliseconds / MILLISECONDS_A_DAYS;
}



function initCustomDateRange(reportType) {
    startDateField = document.getElementById("startDate" + reportType);
    endDateField = document.getElementById("endDate" + reportType);
    toDate = new Date();
    endDateField.valueAsDate = toDate;

    fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.valueAsDate = fromDate;
}

function getChartTitle(period) {
    if(period == "last_7_days") return 'Sales in last 7 days';
    if(period == "last_28_days") return 'Sales in last 28 days';
    if(period == "last_6_months") return 'Sales in last 6 months';
    if(period == "last_years") return 'Sales in last years';
    if(period == "custom") return 'Custom Date Range';

    return 'Sales in last 7 days';
}



function getDenominator(period, reportType) {
    if(period == "last_7_days") return 7;
    if(period == "last_28_days") return 28;
    if(period == "last_6_months") return 6;
    if(period == "last_years") return 12;
    if(period == "custom") return calculateDays(reportType);
    return 7;
}

function setSellAmount(period, reportType,labelTotalItems) {
    var denominator = getDenominator(period, reportType);

    $("#textTotalGrossSales"+ reportType).text("$" + $.number(totalGrossSales, 2));
    $("#textTotalNetSales"+ reportType).text("$" + $.number(totalNetSales, 2));
    $("#textAvgGrossSales"+ reportType).text("$" + $.number(totalGrossSales / denominator, 2));
    $("#textAvgNetSales"+ reportType).text("$" + $.number(totalNetSales / denominator, 2));
    $("#labelTotalItems"+ reportType).text(labelTotalItems);
    $("#textTotalOrders"+ reportType).text(totalOrders);
}

function formatChartData(data, columnIndex1,columnIndex2) {
    var formatter = new google.visualization.NumberFormat({
        prefix : '$'
    });

    formatter.format(data, columnIndex1);
    formatter.format(data, columnIndex2);
}