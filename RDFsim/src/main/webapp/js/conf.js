const CONF_URL = "http://localhost:8080/RDFsim/ConfServlet";
const SEARCH_URL = "http://localhost:8080/RDFsim/SearchServlet";

function getElem(id) {
    return document.getElementById(id);
}

function hideElem(id) {
    getElem(id).style.display = "none";
}

function clearElem(id) {
    getElem(id).innerHTML = "";
}

function showElem(id) {
    getElem(id).style.display = "block";
}

function getElemValue(id) {
    return getElem(id).value;
}

function sendConf() {

    var config = {
        sample: false,
        endpoint: getElemValue("endpointConf-id"),
        query: getElemValue("queryConf-id"),
        offset: getElemValue("offsetConf-id"),
        limit: getElemValue("limitConf-id"),
    };

    sendAjaxWithPromise(config, CONF_URL).then(function (data) {
        console.log("Data response from the server: " + JSON.stringify(data, null, 4));
        window.location.href = SEARCH_URL;
    });

}

function sendSample(){
    var config = {
        sample: true,
        endpoint: "-",
        query: "-",
        offset: 0,
        limit: 0,
    };

    sendAjaxWithPromise(config, CONF_URL).then(function (data) {
        console.log("Data response from the server: " + JSON.stringify(data, null, 4));
        window.location.href = SEARCH_URL;
    });
}

function sendAjaxWithPromise(jsonData, URL) {
    console.log("Data to sent: " + JSON.stringify(jsonData, null, 4));
    return $.ajax({
        type: "POST",
        url: URL,
        data: jsonData,
        dataType: "json"
    });
}

$(document).ready(function () {
    console.log("Conf document ready");
});

