
 $(document).ready(function () {
            $("#logoutLink").on("click", function(e) {
                e.preventDefault();
                document.logoutForm.submit();
            });

            customizeDropDownMenu();
        });


        // dropdown detail user function
        function customizeDropDownMenu() {
            $(".navbar .dropdown").hover(
                function () {
                    $(this).find('.dropdown-menu').first().stop(true, true).delay(150).slideDown();
                },
                function () {
                    $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
                }
            );

            $(".dropdown > a").click(function() {
                location.href = this.href;
            });
        }