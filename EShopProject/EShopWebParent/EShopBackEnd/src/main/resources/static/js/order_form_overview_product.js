var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function() {
    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subtotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmount();
    formatProductAmount();

    $("#productList").on("change", ".quantity-input", function(e) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmount($(this));
    } );

    $("#productList").on("change", ".price-input", function(e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmount($(this));
    } );

    $("#productList").on("change", ".cost-input", function(e) {
        updateOrderAmount($(this));
    } );

    $("#productList").on("change", ".ship-input", function(e) {
        updateOrderAmount($(this));
    } );
});

function updateOrderAmount() {
    totalCost = 0.0;
    $(".cost-input").each(function () {
        costInputField = $(this);
        rowNumber = costInputField.attr("rowNumber");
        quantityValue = $("#quantity" + rowNumber).val();

        productCost = parseFloat(costInputField.val().replace(",", ""));
        totalCost += parseInt(quantityValue) * productCost;
    });
    setAndFormatNumberForField("productCost", totalCost);


    orderSubtotal = 0.0;
    $(".subtotal-input").each(function () {
        subtotalField = $(this);
        productSubtotal = parseFloat(subtotalField.val().replace(",", ""));
        orderSubtotal += productSubtotal;
    });
    setAndFormatNumberForField("subtotal", orderSubtotal);


    shippingCost = 0.0;
    $(".ship-input").each(function () {
         shipField = $(this);
         productShip = parseFloat(shipField.val().replace(",", ""));
         shippingCost += productShip;
    });
    setAndFormatNumberForField("shippingCost", shippingCost);


    tax = parseFloat(fieldTax.val().replace(",", ""));
    orderTotal = orderSubtotal + tax + shippingCost;
    setAndFormatNumberForField("total", orderTotal);

}

function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val();
    rowNumber = input.attr("rowNumber");
    priceField = $("#price" + rowNumber);
    priceValue = parseFloat(priceField.val().replace(",", ""));
    newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function updateSubtotalWhenPriceChanged(input) {
    priceValue = input.val().replace(",", "");
    rowNumber = input.attr("rowNumber");
    quantityField = $("#quantity" + rowNumber);
    quantityValue = quantityField.val();
    newSubtotal = parseFloat(quantityValue) * parseFloat(priceValue);

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);

}

function setAndFormatNumberForField(fieldId, fieldValue) {
    formattedValue = $.number(fieldValue, 2);
    $("#" + fieldId).val(formattedValue);
}


function formatProductAmount() {
    $(".cost-input").each(function(e) {
        formatNumberForField($(this));
    }) ;

    $(".price-input").each(function(e) {
        formatNumberForField($(this));
    }) ;

    $(".ship-input").each(function(e) {
        formatNumberForField($(this));
    }) ;

    $(".subtotal-input").each(function(e) {
         formatNumberForField($(this));
    }) ;

}

function formatOrderAmount() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);

}

function formatNumberForField(fieldRef) {
    let value = fieldRef.val();
    if ($.isNumeric(value)) {
        fieldRef.val($.number(value, 2));
    } else {
        fieldRef.val("0.00");  // Đặt mặc định là 0.00 nếu không hợp lệ
    }
}


// save order

function processFormBeforeSubmit() {
    setCountryName();

    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",", ""));
}

function setCountryName() {
    selectedCountry = $("#country option:selected");
    countryName = selectedCountry.text();
    $("#countryName").val(countryName);
}



