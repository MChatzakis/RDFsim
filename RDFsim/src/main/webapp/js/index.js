var inputFormID = "search-input-id";
var dataListID = "autoCompleteList-id";
var URL = "IndexServlet";

getElem(inputFormID).addEventListener(' onkeyup', function(){
   var prefix = getElemValue(inputFormID);
   autoComplete(prefix, dataListID, URL);
});


