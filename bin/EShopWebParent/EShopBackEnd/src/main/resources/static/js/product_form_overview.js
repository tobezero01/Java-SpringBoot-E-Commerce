
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
        getCategoriesForNewForm();


    });

    function getCategoriesForNewForm()  {
        catIdField = $("#categoryId");
        editMode = false;

        if(catIdField.length) {
            editMode = true;
        }
        if(!editMode) getCategories();
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

        params = { id: productId, name: productName, _csrf: csrfValue };

        $.post(urlCheck, params, function(response) {
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
