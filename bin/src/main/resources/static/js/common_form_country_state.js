        var dropDownCountries;
        var dropDownStates;

        $(document).ready(function () {
            dropDownCountries = $("#country");
            dropDownStates = $("#listStates");

            dropDownCountries.on("change" , function () {
                loadStates4Country();
                $("#state").val("").focus();
            });
            loadStates4Country();

            $("#buttonCancel").click(function () {
                window.location = moduleUrl;
            });
        });

        function loadStates4Country() {
            var selectedCountry = $("#country option:selected");
            var countryId = selectedCountry.val();
            var url = contextPath +  "states/list_by_country/" + countryId;

            $.get(url, function(responseJSON) {
                dropDownStates.empty();
                $.each(responseJSON, function(index, state) {
                    $("<option>").val(state.name).text(state.name).appendTo(dropDownStates);
                });
            }).fail(function() {
                showErrorModal("ERROR: Could not load states");
            });
        }