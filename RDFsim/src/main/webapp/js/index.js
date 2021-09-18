/* ---------------------------------- AutoComplete ---------------------------------- */
function autoCompleteIndex(prefix) {
    var dataListID = "autoCompleteList-id";
    var URL = "SearchServlet";
    var dataset = getElemValue("dataset-selection-id");

    //console.log("Starting auto complete sequence with prefix: " + prefix + " for dataset: " + dataset);
    autoComplete(prefix, dataListID, URL, dataset);
}

/* ---------------------------------- Available Datasets ---------------------------------- */
function fillAvailableDatasets() {
    var item_id = "dataset-selection-id";
    var URL = "SearchServlet";

    var options = "";
    var data2sent = {
        type: DATASETS_CODE,
    };

    sendAjax(data2sent, URL).then(function (data) {

        for (var i = 0; i < data.length; i++) {
            var dataset = data[i];
            options += '<option value="' + dataset + '" >' + dataset + '</option>';
        }
        
        //options += '<option value="KGVec2Go_dbpedia">' + dataset + '</option>';
        var name = "KGVec2Go_dbpedia";
        options += '<option value="' + name + '" >' + name + '</option>';
        
        console.log("Options set: " + options);
        setInnerHTML(item_id, options);
    });
}

/* ---------------------------------- On ready ---------------------------------- */
$(document).ready(function () {
    fillAvailableDatasets();
});