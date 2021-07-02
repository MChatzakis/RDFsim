var url = "http://localhost:8080/RDFsim/ConfServlet";

$(document).ready(function () {

});

function sendConf() {
    var jsonForm = {
        'category': "configurations",
        'sparql_endpoint': $('input[name=sparql]').val()
    };

    console.log('Form: ' + jsonForm);
    sendForm(jsonForm, "#conf-form");
}

function sendForm(jsonForm, id) {
    
    console.log("Sending Form");
    $(id).submit(function (event) {
        $.ajax({
            type: 'POST', /*POST request*/
            url: url,
            data: jsonForm,
            dataType: 'json',
            success: function (results) {
                return results;
            }}).done(function (data) {
            console.log(data);
        });
    });
}

