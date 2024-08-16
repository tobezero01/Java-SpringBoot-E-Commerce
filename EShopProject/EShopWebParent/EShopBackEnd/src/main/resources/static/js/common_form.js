
//add image form computer
$(document).ready(function () {
		$("#buttonCancel").on("click", function () {
			window.location = moduleUrl;
		});

		$("#fileImage").change(function() {
		    fileSize = this.files[0].size;

		    if ( fileSize > 1048576) {
		        this.setCustomValidity("You must choose an image less than 1MB!") ;
		        this.reportValidity();
		    } else {
		        this.setCustomValidity("") ;
		        showImageThumbnail(this);
		    }

		});
	});

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