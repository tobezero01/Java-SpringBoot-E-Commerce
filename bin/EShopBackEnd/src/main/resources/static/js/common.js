
        $(document).ready(function () {
            $("#logoutLink").on("click", function(e) {
                e.preventDefault();
                document.logoutForm.submit();
            });

            customizeDropDownMenu();
            customizeTabs();
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

        function customizeTabs() {
                var url = document.location.toString();

                // Kiểm tra nếu URL chứa ký tự #
                if (url.match('#')) {
                    $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
                }

                // Cập nhật URL khi chuyển tab
                $('.nav-tabs a').on('shown.bs.tab', function (e) {
                    window.location.hash = e.target.hash;
                });
        }
