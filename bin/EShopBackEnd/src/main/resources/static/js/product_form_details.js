    $(document).ready(function () {

        $("a[name='linkRemoveDetail']").each(function (index) {
            $(this).click(function {
                removeDetailByIndex(index);
            });
        });

    });

function removeDetailByIndex(index) {
    $("#divDetail" + id).remove();
}

function addNextDetailSection() {

    allDivDetails = $("[id^='divDetail']");
    divDetailsCount = allDivDetails.length;

    htmlDetailSection = `
        <div class="d-flex align-items-center mb-2" id="divDetail${divDetailsCount}">

            <label class="m-3">Name</label>
            <input type="text" class="form-control w-25 mr-3" name="detailNames" maxlength="255"/>
            <label class="m-3">Value</label>
            <input type="text" class="form-control w-25" name="detailValues" maxlength="255"/>
            <a class="btn fas fa-times-circle fa-2x icon-dark mr-3"
                            title="Remove this section" href="javascript:removeDetailSectionById('${divDetailsCount}')"></a>
        </div>`;

    $("#divProductDetails").append(htmlDetailSection);

}

function removeDetailSectionById(id) {
    $("#divDetail" + id).remove();
}
