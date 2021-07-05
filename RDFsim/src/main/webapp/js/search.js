var URL = "http://localhost:8080/RDFsim/SearchServlet";
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

function sendAjaxPromiseEntitySearch() {
    console.log("Getting the promise ready. Entity to search is: " + getEntityGiven());
    // returns a promise that can be used later. 
    return $.ajax({
        type: "POST",
        url: URL,
        data: {
            entity: getEntityGiven()
        },
        dataType: "json"
    });
}

function sendAjaxGetResponseEntitySearch() {
    sendAjaxPromiseEntitySearch().then(function (data) {
// Run this when your request was successful
        var dataAsJSON = JSON.stringify(data, null, 4);
        console.log("Data response from the server now: " + dataAsJSON);
        drawGraph(data);
        //dummyGraphCreation();
    });
}

function getEntityGiven() {
    return getElem("inputEntity").value;
}

function drawGraph(entitiesJSON) {
    var nodeArr = [];
    var edgeArr = [];

    counter = 1;

    /*for (let [key, value] of Object.entries(entitiesJSON)) {
     console.log(key, value);
     }*/

    for (var k in entitiesJSON) {

        console.log("Adding k = " + k + " to arrays..");

        if (k === "self") {
            nodeArr.push({id: 0, label: k});
        } else {
            nodeArr.push({id: counter, label: k});
            edgeArr.push({from: counter, to: 0});
            counter++;
        }
    }

    console.log("NodeArr = " + nodeArr + "\nEdgeArr = " + edgeArr);

    var nodes = new vis.DataSet(nodeArr);
    var edges = new vis.DataSet(edgeArr);

    var container = document.getElementById("graphContainer");
    var data = {
        nodes: nodes,
        edges: edges,
    };

    var options = {};
    var network = new vis.Network(container, data, options);

}

function dummyGraphCreation() {
    var nodes = new vis.DataSet([
        {id: 1, label: "Node 1"},
        {id: 2, label: "Node 2"},
        {id: 3, label: "Node 3"},
        {id: 4, label: "Node 4"},
        {id: 5, label: "Node 5"},
    ]);
// create an array with edges
    var edges = new vis.DataSet([
        {from: 1, to: 3},
        {from: 1, to: 2},
        {from: 2, to: 4},
        {from: 2, to: 5},
        {from: 3, to: 3},
    ]);
// create a network
    var container = document.getElementById("graphContainer");
    var data = {
        nodes: nodes,
        edges: edges,
    };
    var options = {};
    var network = new vis.Network(container, data, options);
}

$(document).ready(function () {
    console.log("Document Loaded.");
    document.getElementById("inputEntity").addEventListener("keyup", function (event) {
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {

            event.preventDefault();
            console.log("Enter hit, beggining sending...");
            sendAjaxGetResponseEntitySearch();
        }
    });
});
