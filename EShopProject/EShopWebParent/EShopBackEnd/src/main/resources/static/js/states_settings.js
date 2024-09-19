var buttonLoad4States, dropDownCountry4States, dropDownStates, buttonAddState, buttonUpdateState, buttonDeleteState, fieldStateName;

    $(document).ready(function () {
        buttonLoad4States = $("#buttonLoadCountriesForStates");
        dropDownCountry4States = $("#dropDownCountryForStates");
        dropDownStates = $("#dropDownStates");
        buttonAddState = $("#buttonAddState");
        buttonUpdateState = $("#buttonUpdateState");
        buttonDeleteState = $("#buttonDeleteState");
        fieldStateName = $("#fieldStateName");

        buttonLoad4States.click(function () {
            loadCountries4States();
        });

        dropDownCountry4States.on("change", function () {
            loadStates4Country();
        });

        dropDownStates.on("change", function () {
            changeFormStateToSelectedStates();
        });

        buttonAddState.click(function () {
            if ($(this).val() === "Add") {
                addState();
            } else {
                changeFormStateToNew();
            }
        });

        buttonUpdateState.click(function () {
            updateState();
        });

        buttonDeleteState.click(function () {
            deleteState();
        });
    });

    function loadCountries4States() {
        var url = contextPath + "countries/list";
        $.get(url, function (responseJSON) {
            dropDownCountry4States.empty();
            $.each(responseJSON, function (index, country) {
                $("<option>").val(country.id).text(country.name).appendTo(dropDownCountry4States);
            });
        }).done(function () {
            buttonLoad4States.val("Refresh Country List");
            showToastMessage("All countries have been loaded");
        }).fail(function () {
            showToastMessage("ERROR: Could not load countries");
        });
    }

    function loadStates4Country() {
        var selectedCountry = $("#dropDownCountryForStates option:selected");
        var countryId = selectedCountry.val();
        var url = contextPath + "states/list_by_country/" + countryId;
        $.get(url, function (responseJSON) {
            dropDownStates.empty();
            $.each(responseJSON, function (index, state) {
                $("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
            });
        }).done(function () {
            showToastMessage("States for country " + selectedCountry.text() + " have been loaded");
        }).fail(function () {
            showToastMessage("ERROR: Could not load states");
        });
    }

    function addState() {
        var url = contextPath + "states/save";
        var stateName = fieldStateName.val();
        var selectedCountry = $("#dropDownCountryForStates option:selected");
        var countryId = selectedCountry.val();
        var countryName = selectedCountry.text();
        var json = { name: stateName, country: { id: countryId, name: countryName } };

        $.ajax({
            type: 'POST',
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            },
            data: JSON.stringify(json),
            contentType: 'application/json'
        }).done(function (stateId) {
            selectNewlyAddedState(stateId, stateName);
            showToastMessage("State added successfully");
        }).fail(function () {
            showToastMessage("ERROR: Could not connect to server");
        });
    }

    function updateState() {
        var url = contextPath + "states/save";
        var stateName = fieldStateName.val();
        var stateId = dropDownStates.val();
        var selectedCountry = $("#dropDownCountryForStates option:selected");
        var countryId = selectedCountry.val();
        var countryName = selectedCountry.text();
        var json = { id: stateId, name: stateName, country: { id: countryId, name: countryName } };

        $.ajax({
            type: 'POST',
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            },
            data: JSON.stringify(json),
            contentType: 'application/json'
        }).done(function () {
            $("#dropDownStates option:selected").text(stateName);
            showToastMessage("State updated successfully");
        }).fail(function () {
            showToastMessage("ERROR: Could not update state");
        });
    }

    function deleteState() {
        var stateId = dropDownStates.val();
        var url = contextPath + "states/delete/" + stateId;

        $.get(url, function () {
            $("#dropDownStates option[value='" + stateId + "']").remove();
            changeFormStateToNew();
        }).done(function () {
            showToastMessage("State deleted successfully");
        }).fail(function () {
            showToastMessage("ERROR: Could not delete state");
        });
    }

    function selectNewlyAddedState(stateId, stateName) {
        $("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
        $("#dropDownStates option[value='" + stateId + "']").prop("selected", true);
        fieldStateName.val("").focus();
    }

    function changeFormStateToNew() {
        buttonAddState.val("Add");
        fieldStateName.val("").focus();
        buttonUpdateState.prop("disabled", true);
        buttonDeleteState.prop("disabled", true);
    }

    function changeFormStateToSelectedStates() {
        buttonAddState.val("New");
        buttonUpdateState.prop("disabled", false);
        buttonDeleteState.prop("disabled", false);

        selectedStateName = $("#dropDownStates option:selected").text();
        fieldStateName.val(selectedStateName);
    }

    function showToastMessage(message) {
        $("#toastMessage").text(message);
        $(".toast").toast('show');
    }