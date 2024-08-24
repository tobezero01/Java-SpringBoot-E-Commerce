    var extraImagesCount = 0;

    dropdownBrands = $("#brand");
    dropdownCategories = $("#category");

    $(document).ready(function () {

        // Add richText
        $("#shortDescription").richText();
        $("#fullDescription").richText();

        dropdownBrands.change(function() {
            dropdownCategories.empty();
            getCategories();
        });
        getCategories();

        $("input[name='extraImage']").each(function(index) {
            extraImagesCount++;
            $(this).change(function () {
                showExtraImageThumbnail(this, index);
            });
        });

    });

    function showExtraImageThumbnail(fileInput, index) {
        var file = fileInput.files[0];  // Lấy tệp đầu tiên từ input
        var reader = new FileReader();  // Tạo FileReader để đọc nội dung tệp

        reader.onload = function(e) {
            $("#extraThumbnail" + index).attr("src", e.target.result);
        };
        reader.readAsDataURL(file);

        if (index >= extraImagesCount - 1) {
            addNextExtraImageSection(index + 1);
        }
    }

    function addNextExtraImageSection(index) {
        htmlExtraImage = `
            <div class="col border m-3 p-2" id="divExtraImage${index}">
                <div id="extraImageHeader${index}">
                    <label>Extra Image #${index + 1}</label>
                </div>
                <div class="m-2">
                    <img src="@{/images/image_null.png}" id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid">
                </div>
                <div>
                    <input type="file" name="extraImage"
                        onchange="showExtraImageThumbnail(this, ${index})"
                        accept="image/png, image/jpeg" />
                </div>
            </div>`;

        htmlLinkRemove = `
            <a class="btn fas fa-times-circle fa-2x icon-dark float-right"
                title="Remove this image" href="javascript:removeExtraImage(${index - 1})"></a>`;

        $("#divProductImages").append(htmlExtraImage);
        $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
        extraImagesCount++;
    }

    function removeExtraImage(index) {
        $("#divExtraImage" + index).remove();
    }

    function getCategories() {
        brandId = dropdownBrands.val();
        url = brandModuleURL + "/" + brandId + "/categories";

        $.get(url, function(responseJson) {
            $.each(responseJson, function(index, category) {
                $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
            });
        });
    }

    function checkUnique(form) {
        productId = $("#id").val();
        productName = $("#name").val();
        csrfValue = $("input[name='_csrf']").val();

        url = "[[@{/products/check_unique}]]";
        params = { id: productId, name: productName, _csrf: csrfValue };

        $.post(url, params, function(response) {
            if (response == "OK") {
                form.submit();
            } else if (response == "Duplicate") {
                showWarningModal("Duplicated product: " + productName);
            } else {
                showErrorModal("Unknown response from server");
            }
        }).fail(function() {
            showErrorModal("Could not connect to server");
        });

        return false;
    }
