/**
 * AJAX call management
 */
function makeCall(method, url, formData, formElement, cback, reset = true) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
        cback(req);
    };
    req.open(method, url, true);
    if (formElement == null && formData == null) {
        req.send();
    } else if (formData != null){
        req.send(formData);
    } else {
        let form_data = new FormData(formElement);
        req.send(form_data);
    }
    if (formElement !== null && formElement && reset === true) {
        formElement.reset();
    }
}

//form is submitted when the user types ENTER
(function (){
    var forms = document.getElementsByTagName("form");
    Array.from(forms).forEach(form => {
        var input_fields = form.querySelectorAll('input:not([type="button"]):not([type="hidden"])');
        var button = form.querySelector('input[type="button"]');
        Array.from(input_fields).forEach(input => {
            input.addEventListener("keydown", (e) => {
                if(e.keyCode == 13){
                    e.preventDefault();
                    let click = new Event("click");
                    button.dispatchEvent(click);
                }
            });
        });
    });
})();