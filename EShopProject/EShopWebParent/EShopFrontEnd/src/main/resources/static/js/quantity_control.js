$(document).ready(function () {
    // Đảm bảo rằng tệp quantity_control.js có thể truy cập tới showWarningModal
    $(".linkMinus").on("click", function (evt) {
        evt.preventDefault();
        let productId = $(this).attr("pid");
        let quantityInput = $("#quantity" + productId);
        let newQuantity = parseInt(quantityInput.val()) - 1;

        if (newQuantity > 0) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Minimum quantity is 1');
        }
    });


    $(".linkPlus").on("click", function (evt) {
        evt.preventDefault();
        let productId = $(this).attr("pid");
                let quantityInput = $("#quantity" + productId);
                let newQuantity = parseInt(quantityInput.val()) + 1;

                if (newQuantity <= 5) {
                    quantityInput.val(newQuantity);
                } else {
                    showWarningModal('Maximum quantity is 5');
                }
    });
});

function showModalDialog(title, message) {
            $("#modalTitle").text(title);
            $("#modalBody").text(message);
            $("#modalDialog").modal('show');
        }

        function showWarningModal(message) {
            console.log("Warning modal is being called with message: " + message);
            showModalDialog("Warning", message);
        }


        function showErrorModal(message) {
            showModalDialog("Error", message);
        }
