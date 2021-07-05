var url = "http://localhost:8080/RDFsim/ConfServlet";


$(document).ready(function () {
    //what we should do when doc is ready?

});

function sendConf() {
  var jsonForm = {
    category: "configurations",
    sparql_endpoint: $("input[name=sparql]").val(),
  };

  console.log("Form: " + jsonForm);
  sendForm(jsonForm, "#conf-form");
}

function sendForm(jsonForm, id) {
  console.log("Sending Form");
  $(id).submit(function (event) {
    $.ajax({
      type: "POST" /*POST request*/,
      url: url,
      data: jsonForm,
      dataType: "json",
      success: function (results) {
        return results;
      },
    }).done(function (data) {
      console.log(data);
    });
  });
}

function sendXmlForm(url, reqID, formData) {
  var request = new XMLHttpRequest();
  request.onreadystatechange = function () {
    if (this.readyState === 4 && this.status === 200) {
        //callbacks
    }
  };
  request.open("POST", url);
  request.setRequestHeader(
    "Content-Type",
    "application/x-www-form-urlencoded;"
  );
  request.send(formData);
}
