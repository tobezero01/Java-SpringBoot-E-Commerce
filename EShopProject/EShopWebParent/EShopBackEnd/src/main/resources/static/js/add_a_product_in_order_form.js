$(document).ready(function() {
    $("#products").on("click" , "#linkAddProduct" , function(e) {
        e.preventDefault();
        link = $(this);
        url = link.attr("href");
        $("#addProductModal").on("show.bs.modal", function() {
            $(this).find("iframe").attr("src", url);
        })
        $("#addProductModal").modal('show');

    });
}) ;

function addProduct(productId, productName) {
    $("#addProductModal").modal('hide');
    getShippingCost(productId);
}

function getProductInfo(productId, shippingCost) {
    requestUrl = contextPath + "products/get/" + productId;
    $.get(requestUrl, function(productJson) {
        console.log(productJson);
        productName = productJson.name;
        mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
        productCost = $.number(productJson.cost, 2);
        productPrice = $.number(productJson.price, 2);
        htmlCode = insertProductCode(productId,productName,mainImagePath, productCost,productPrice, shippingCost);
        $("#productList").append(htmlCode);
    }).fail(function(err) {
        showWarningModal(err.responseJSON ? err.responseJSON.message : "Failed to get product info");
    });
}

function getShippingCost(productId) {
    let selectedCountry = $("#country option:selected");
    let countryId = selectedCountry.val();
    let state = $("#state").val() || $("#city").val();

    let requestUrl = contextPath + "get_shipping_cost";
    let params = { productId: productId, countryId: countryId, state: state };

    $.ajax({
        type: 'POST',
        url: requestUrl,
        contentType: "application/x-www-form-urlencoded; charset=UTF-8", // Đảm bảo định dạng dữ liệu
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue); // Thêm header CSRF
        },
        data: params,
    })
    .done(function (shippingCost) {
        getProductInfo(productId, shippingCost);
    })
    .fail(function (err) {
        showWarningModal(err.responseJSON ? err.responseJSON.message : "Failed to get shipping cost.");
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    })
    .always(function () {
        $("#addProductModal").modal('hide');
    });
}


function isProductAlreadyAdded(productId) {
    productExists = false;

    $(".hiddenProductId").each(function (e){
        aProductId = $(this).val();
        if(aProductId == productId) {
            productExists = true;
            return;
        }
    });
}

function insertProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost) {
    nextCount = $(".hiddenProductId").length + 1;
    quantityId = "quantity" + nextCount;
    priceId = "price" + nextCount;
    subtotalId = "subtotal" + nextCount;
    htmlCode = `            <div class="border rounded mb-4 p-3 shadow-sm">
                                <input type="hidden" name="productId" value="${productId}" class="hiddenProductId">
                                <div class="row">
                                    <div class="col-lg-3 col-md-4 col-12">
                                        <img src="${mainImagePath}" class="img-fluid rounded mb-2" alt="Product Image">
                                    </div>

                                    <div class="col-lg-9 col-md-8 col-12">
                                        <h5 class="fw-bold text-primary mb-2">${productName}</h5>

                                        <table class="table table-borderless table-sm">
                                            <tbody>
                                            <tr>
                                                <td class="fw-bold">Product Cost:</td>
                                                <td>
                                                    <input type="text" required class="form-control cost-input"
                                                           rowNumber="${nextCount}"
                                                           value="${productCost}" style="max-width: 250px"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold">Quantity:</td>
                                                <td>
                                                    <input type="number" step="1" min="1" max="5" required class="form-control quantity-input"
                                                           value="1" rowNumber="${nextCount}"
                                                           id="${quantityId}"
                                                           placeholder="Select quantity" style="max-width: 250px"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold">Unit Price:</td>
                                                <td>
                                                    <input type="text" required class="form-control price-input"
                                                           id="${priceId}"
                                                           rowNumber="${nextCount}"
                                                           value="${productPrice}" style="max-width: 250px"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold">Subtotal:</td>
                                                <td>
                                                    <input type="text" readonly class="form-control subtotal-input"
                                                           id="${subtotalId}"
                                                           value="${productPrice}" style="max-width: 250px"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold">Shipping Cost:</td>
                                                <td>
                                                    <input type="text" required class="form-control ship-input"
                                                           rowNumber="${nextCount}"
                                                           value="${shippingCost}" style="max-width: 250px"/>
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                        <a value="Delete" class="btn btn-danger pt-2">Delete</a>
                                    </div>
                                </div>
                            </div>
`;
return htmlCode;
}

