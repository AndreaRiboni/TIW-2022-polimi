/**
 * Login management
 */
(function() {
    document.getElementById("login-button").addEventListener('click', (e) => {
        var form = document.getElementById("login-form");
        if (form.checkValidity()) {
            makeCall(
                "POST",
                'Login',
                null,
                form,
                function(x) {
                    if (x.readyState == XMLHttpRequest.DONE) {
                        var message = x.responseText;
                        switch (x.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "homepage.html";
                                break;
                            case 400: // bad request
                            case 401: // unauthorized
                            case 500: // server error
                                document.getElementById("errormessage-login").hidden = false;
                                document.getElementById("errormessage-login").textContent = message;
                                setTimeout(
                                    ()=>{document.getElementById("errormessage-login").hidden = true;},
                                    10000
                                )
                                break;
                        }
                    }
                },
                true
            );
        } else {
            form.reportValidity();
        }
    });

})();

(function() {
    document.getElementById("signup-button").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        let psw = document.getElementById("reg-psw");
        let conf_psw = document.getElementById("reg-conf-psw");
        conf_psw.setCustomValidity("");
        let email = document.getElementById("reg-email");
        email.setCustomValidity("");
        const regex = new RegExp("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        if(!regex.exec(email.value)){
            email.setCustomValidity("Wrong email format");
        }
        if(psw.value!==conf_psw.value)
            conf_psw.setCustomValidity("The two passwords must be identical");
        if (form.checkValidity()) {
            makeCall(
                "POST",
                'Register',
                null,
                form,
                function(x) {
                    if (x.readyState == XMLHttpRequest.DONE) {
                        var message = x.responseText;
                        switch (x.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "homepage.html";
                                break;
                            case 400: // bad request
                            case 401: // unauthorized
                            case 500: // server error
                                document.getElementById("errormessage-signup").hidden = false;
                                document.getElementById("errormessage-signup").textContent = message;
                                setTimeout(
                                    ()=>{document.getElementById("errormessage-signup").hidden = true;},
                                    10000
                                )
                                break;
                        }
                    }
                },
                true
            );
        } else {
            form.reportValidity();
        }
    });

})();