
//add image form computer
$(document).ready(function () {
		$("#buttonCancel").on("click", function () {
			window.location = moduleUrl;
		});

		$("#fileImage").change(function() {
		    if(!checkFileSize(this)) {
		        return;
		    }
		    showImageThumbnail(this);

		});
	});

	function checkFileSize(fileInput) {
	     fileSize = fileInput.files[0].size;

    	if ( fileSize > MAX_FILE_SIZE) {
    	    fileInput.setCustomValidity("You must choose an image less than 1MB!") ;
    	    fileInput.reportValidity();
    	    return false;
    	} else {
    		fileInput.setCustomValidity("") ;
    		return true;
    	}
	}

	//js add image
	function showImageThumbnail(fileInput) {
        var file = fileInput.files[0];                            // Lấy tệp (file) đầu tiên từ phần tử đầu vào tệp (file input).

        var reader = new FileReader();                            // Tạo một đối tượng FileReader để đọc nội dung của tệp.
        reader.onload = function(e) {
            $("#thumbnail").attr("src", e.target.result);
        };

        reader.readAsDataURL(file);
    }


    // Hàm để hiển thị modal dialog
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