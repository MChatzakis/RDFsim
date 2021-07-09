const CONF_URL = "http://localhost:8080/RDFsim/ConfServlet";
const SEARCH_URL = "http://localhost:8080/RDFsim/SearchServlet";

const NO_SAMPLE = 0;
const DBPedia_SAMPLE = 1;
const ARIADNE_SAMPLE = 2;

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
        sample: NO_SAMPLE,
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

function loadDBPediaSample() {
    var config = {
        sample: DBPedia_SAMPLE,
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

function loadAriadneSample() {
    var config = {
        sample: ARIADNE_SAMPLE,
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

