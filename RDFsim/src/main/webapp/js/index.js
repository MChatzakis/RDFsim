/* ---------------------------------- AutoComplete ---------------------------------- */
function autoCompleteIndex(prefix) {
    var dataListID = "autoCompleteList-id";
    var URL = "SearchServlet";
    var dataset = getElemValue("dataset-selection-id");

    //console.log("Starting auto complete sequence with prefix: " + prefix + " for dataset: " + dataset);
    autoComplete(prefix, dataListID, URL, dataset);
}

