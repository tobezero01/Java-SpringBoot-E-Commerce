$(document).ready(function() {
    $("#productList").on("click" , ".linkRemove" , function(e) {
       e.preventDefault();
       if(doesOrderHaveOnlyOneProduct()) {
           showWarningModal("Could not remove the product, The order must have at least one product");
       } else {
           removeProduct($(this));
           updateOrderAmount();
       }
    });
}) ;

function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length ;
    return productCount == 1;
}