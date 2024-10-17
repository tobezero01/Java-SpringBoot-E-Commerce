$(document).ready(function () {
    $(".linkMinus").on("click", function (evt) {
        evt.preventDefault();
        decreaseQuantity($(this));
    });

    $(".linkPlus").on("click", function (evt) {
        evt.preventDefault();
        increaseQuantity($(this));
    });

    $(".linkRemove").on("click", function (evt) {
            evt.preventDefault();
            removeProductFromCart.call($(this));
        });
});

function removeProductFromCart() {
    var link = $(this);  // Lấy đối tượng link
    var url = link.attr("href");  // Lấy URL từ href của thẻ link

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);  // Thêm CSRF header
        }
    }).done(function(response) {
        var rowNumber = link.attr("rowNumber");  // Lấy rowNumber từ thuộc tính của thẻ
        removeProductHTML(rowNumber);  // Xóa sản phẩm khỏi giao diện
        updateTotal();  // Cập nhật tổng tiền
        showModalDialog("Shopping Cart", response);  // Hiển thị modal với thông báo
    })
    .fail(function() {
        showErrorModal("Error while removing product from shopping cart");
    });
}


function removeProductHTML (rowNumber) {
    $("#row" + rowNumber).remove();
}

function decreaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Minimum quantity is 1');
    }
}

function increaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Maximum quantity is 5');
    }
}

function updateQuantity(productId, quantity) {
    var url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);  // Add CSRF header
        }
    }).done(function(updatedSubtotal) {
        updateSubtotal(productId, updatedSubtotal);
        updateTotal(); // Tính tổng tiền sau khi cập nhật số lượng
    })
    .fail(function() {
        showErrorModal("Error while updating quantity to shopping cart");
    });
}

function updateSubtotal(productId, updatedSubtotal) {
    // Format subtotal với jquery.number.js
    let formattedSubtotal = $.number(updatedSubtotal, 2); // Định dạng với 2 chữ số sau dấu thập phân
    $("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
    let total = 0;
    $(".subtotal").each(function () {
        let subtotal = $(this).text().replace(/[^0-9.-]+/g, ""); // Loại bỏ ký tự không phải số
        total += parseFloat(subtotal);
    });

    let formattedTotal = $.number(total, 2); // Định dạng tổng tiền với 2 chữ số thập phân
    $("#total").text(formattedTotal);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal('show');
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}
