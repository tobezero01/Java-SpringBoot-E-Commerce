var buttonLoad;
var dropDownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;
$(document).ready(function () {
    buttonLoad = $("#buttonLoadCountries");
    dropDownCountry = $("#dropDownCountries");
    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");


    buttonLoad.click(function () {
        loadCountries();
    });

    dropDownCountry.on("change" , function() {
        changeFormStateToSelectedCountry();
    });

    buttonAddCountry.click(function () {
        if(buttonAddCountry.val() == "Add") {
            addCountry();
        } else {
            changeFormStateToNewInCountrySettings();
        }

    });

    buttonUpdateCountry.click(function() {
        updateCountry();
    });

    buttonDeleteCountry.click(function() {
        deleteCountry();
    });
});

function deleteCountry() {
    optionValue = dropDownCountry.val();
    countryId = optionValue.split("-")[0];

    url = contextPath + "countries/delete/" + countryId;

    $.ajax({
            type: 'DELETE',
            url: url,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);  // Thêm header CSRF để bảo vệ form
            }
        }).done(function (){
            $("#dropDownCountries option[value='" + optionValue + "']").remove();
            changeFormStateToNewInCountrySettings();
            showToastMessage("The country have been deleted");
        }).fail(function() {
            showToastMessage("ERROR : Could not connect to server or encountered an error");
        });
}

function updateCountry() {
    url = contextPath + "countries/save";  // Đường dẫn API để lưu quốc gia
    countryName = fieldCountryName.val();  // Lấy giá trị từ trường "Tên quốc gia"
    countryCode = fieldCountryCode.val();  // Lấy giá trị từ trường "Mã quốc gia"
    json = {name: countryName, code: countryCode};  // Tạo đối tượng JSON từ dữ liệu đã nhập

    countryId = dropDownCountry.val().split("-")[0];

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);  // Thêm header CSRF để bảo vệ form
        },
        data: JSON.stringify(json),  // Gửi dữ liệu dưới dạng JSON
        contentType: 'application/json'
    }).done(function(countryId) {
        $("#dropDownCountries option:selected").text(countryName);
        $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
        showToastMessage("The country have been updated successfully");
        changeFormStateToNewInCountrySettings();
    }).fail(function() {
        showToastMessage("ERROR : Could not connect to server or encountered an error");
    });
}

function addCountry() {
    url = contextPath + "countries/save";  // Đường dẫn API để lưu quốc gia
    countryName = fieldCountryName.val();  // Lấy giá trị từ trường "Tên quốc gia"
    countryCode = fieldCountryCode.val();  // Lấy giá trị từ trường "Mã quốc gia"
    json = {name: countryName, code: countryCode};  // Tạo đối tượng JSON từ dữ liệu đã nhập

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);  // Thêm header CSRF để bảo vệ form
        },
        data: JSON.stringify(json),  // Gửi dữ liệu dưới dạng JSON
        contentType: 'application/json'
    }).done(function(countryId) {
        selectNewlyAddedCountry(countryId , countryCode, countryName);
        showToastMessage("The country have been added successfully");
    }).fail(function() {
        showToastMessage("ERROR : Could not connect to server or encountered an error");
    });
}

function selectNewlyAddedCountry(countryId , countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);
    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}


function changeFormStateToNewInCountrySettings() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country name");
    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected countries ")
    selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);

}

function loadCountries() {
    url = contextPath +  "countries/list";
    $.get(url , function(responseJSON) {
        dropDownCountry.empty();
        $.each(responseJSON, function(index, country) {
            optionValue = country.id + "-" + country.code;
            //alert(optionValue);
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        });
    }).done(function (){
        buttonLoad.val("Refresh List");
        showToastMessage("All countries have been loaded");
    }).fail(function() {
        showToastMessage("ERROR : Could not connect to server or encountered an error");
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}