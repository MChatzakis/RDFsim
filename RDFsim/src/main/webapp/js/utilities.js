/*
 * Basic Utilities
 * Manos Chatzakis (chatzakis@ics.forth.gr)
 */

/* ---------------------------------- Basic control functions ---------------------------------- */
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

function setElemValue(id, val) {
    getElem(id).value = val;
}

function dispElemFromButtonClick(id) {
    var el = getElem(id);
    if (el.style.display === "block") {
        hideElem(id);
    } else {
        showElem(id);
    }
}

function setOptionValue(id, index) {
    var op = getElem(id);
    op.options.selectedIndex = index;
}

function setInnerHTML(id, val) {
    getElem(id).innerHTML = val;
}

/* ---------------------------------- Utilities ---------------------------------- */
function formatDBpediaURI(URI) {
    //console.log("Formatting URI");

    var formattedURI = URI;
    var beforeSplitters = ['/', '#'];

    for (var s = 0; s < beforeSplitters.length; s++) {
        var arr = formattedURI.split(beforeSplitters[s]);
        formattedURI = arr[arr.length - 1];
    }

    formattedURI = formattedURI.replaceAll("_", " ");

    return formattedURI;
}

function roundTo(num, points) {
    const x = Math.pow(10, points);
    return Math.round(num * x) / x;
}

function allValuesAreSet(arr) {
    for (val in arr) {
        if (typeof val === "undefined") {
            return false;
        }
    }

    return true;
}

function redirToErrorPage() {
    window.location.href = "./error.jsp";
}

function sendAjax(jsonData, URL) {
    //console.log("Getting the promise ready. Data to sent: " + JSON.stringify(jsonData, null, 4));
    return $.ajax({
        type: "POST",
        url: URL,
        data: jsonData,
        dataType: "json"
    });
}

/* ---------------------------------- AutoComplete ---------------------------------- */
var AUTOCOMPLETE_CODE = 0;

function autoComplete(prefix, listID, URL, dataset = "empty") {

    /*if (dataset === "null") {
        console.log("Setting null val");
        dataset = "empty";
    }*/

    if (prefix.length >= 3 && !prefix.startsWith("http://")) {
        var options = "";
        var data2sent = {
            type: AUTOCOMPLETE_CODE,
            prefix: prefix,
            dataset: dataset
        };

        sendAjax(data2sent, URL).then(function (data) {

            for (var i = 0; i < data.length; i++) {
                var recEntity = data[i];
                //console.log("AutoComplete: Adding as option entity " + recEntity);
                options += '<option value="' + recEntity + '" >' + recEntity + '</option>';
            }

            console.log("Options set: " + options);
            setInnerHTML(listID, options);
        });
    }

}

