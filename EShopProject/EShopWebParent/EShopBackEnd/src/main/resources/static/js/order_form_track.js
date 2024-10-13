$(document).ready(function () {
    // Sự kiện xóa track
    $("#trackList").on("click", ".linkRemoveTrack", function(e) {
        e.preventDefault();
        deleteTrack($(this));
        updateTrackCountNumber();
    });

    // Sự kiện thêm mới track - sửa lại đúng phần tử
    $("#linkAddTrack").click(function(e) {
        e.preventDefault();
        addNewTrackRecord();
    });

    // Sự kiện thay đổi trạng thái
    $("#trackList").on("change", ".dropdownStatus", function(e) {
        let dropdownList = $(this);
        let rowNumber = dropdownList.attr("rowNumber");
        let selectedOption = $("option:selected", dropdownList);
        let defaultNote = selectedOption.attr("defaultDescription");

        $("#trackNote" + rowNumber).text(defaultNote);
    });
});

function deleteTrack(link) {
    let rowNumber = link.attr("rowNumber");
    $("#rowTrack" + rowNumber).remove();
    $("#emptyLine" + rowNumber).remove();
}

function updateTrackCountNumber() {
    $(".divCountTrack").each( function(index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function addNewTrackRecord() {
    const htmlCode = generateTrackCode();
    $("#trackList").append(htmlCode);
}

function generateTrackCode() {
    let nextCount = $(".hiddenTrackId").length + 1;
    let rowId = "rowTrack" + nextCount;
    let emptyLineId = "emptyLine" + nextCount;
    let trackNoteId = "trackNote" + nextCount;
    let currentDateTime = formatCurrentDateTime();

    return `
        <div class="border rounded mb-4 p-3 shadow-sm" id="${rowId}">
            <input type="hidden" name="trackId" value="0" class="hiddenTrackId">
            <div class="col-2">
                <div class="divCountTrack">${nextCount}</div>
            </div>

            <div class="col-10">
                <div class="form-group row">
                    <label class="col-form-label">Time</label>
                    <div class="col">
                        <input type="datetime-local" name="trackDate" value="${currentDateTime}"
                               class="form-control" style="max-width: 300px">
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-form-label">Status</label>
                    <div class="col">
                        <select name="trackStatus" class="form-control dropdownStatus" required
                                style="max-width: 150px" rowNumber="${nextCount}">
                            ${$("#trackStatusOptions").html()}
                        </select>
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-form-label">Notes</label>
                    <div class="col">
                        <textarea rows="2" cols="10" class="form-control" name="trackNotes"
                                  style="max-width: 300px" id="${trackNoteId}" required></textarea>
                    </div>
                </div>

                <div class="mt-2">
                    <a class="btn btn-danger linkRemoveTrack" href="" rowNumber="${nextCount}">Delete</a>
                </div>

            </div>
        </div>
        <div id="${emptyLineId}" class="row">&nbsp;</div>
    `;
}



function formatCurrentDateTime() {
    const now = new Date();

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');

    // Định dạng thành chuỗi: YYYY-MM-DDTHH:mm
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}


// save order
