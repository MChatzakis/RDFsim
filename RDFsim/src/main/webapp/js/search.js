/*
 * Basic search controller
 * Manos Chatzakis (chatzakis@ics.forth.gr)
 */

var URL = "http://localhost:8080/RDFsim/SearchServlet";

var TOP_K = 0;
var COS_SIM = 1;
var EXPR = 2;
var BIG_GRAPH = 3;

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

function sendAjaxWithPromise(jsonData) {
    console.log("Getting the promise ready. Data to sent: " + JSON.stringify(jsonData, null, 4));
    return $.ajax({
        type: "POST",
        url: URL,
        data: jsonData,
        dataType: "json"
    });
}

function getElemValue(id) {
    return getElem(id).value;
}

function setElemValue(id, val) {
    getElem(id).value = val;
}

function drawGraph(entitiesJSON, self) {
    var nodeArr = [];
    var edgeArr = [];
    counter = 1;
    nodeArr.push({id: 0, label: formatDBpediaURI(self), url: self});
    for (var k in entitiesJSON) {
        nodeArr.push({id: counter, label: formatDBpediaURI(k), url: k}); //formatted URI use
        console.log("SS: " + roundTo(entitiesJSON[k], 2));
        edgeArr.push({from: counter, to: 0, label: roundTo(entitiesJSON[k], 2) + "", length: 300});
        counter++;
    }

//console.log("NodeArr = " + nodeArr + "\nEdgeArr = " + edgeArr);
    var nodes = new vis.DataSet(nodeArr);
    var edges = new vis.DataSet(edgeArr);
    var container = document.getElementById("graphContainer-id");
    var data = {
        nodes: nodes,
        edges: edges,
    };
    var options = {
        autoResize: true,
        height: '100%',
        width: '100%',
        edges: {
            width: 0.1,
        }, // defined in the edges module.
        nodes: {}, // defined in the nodes module.
    };
    var network = new vis.Network(container, data, options);
    network.on("click", function (params) {
        //console.log("Something was clicked!");
        var nodeID = params.nodes[0];
        if (nodeID) {
            var clickedNode = this.body.nodes[nodeID];
            var nodeInfo = clickedNode.options;
            console.log('clicked node:', nodeInfo.label);
            //console.log('pointer', params.pointer);
            setElemValue("search-input-id", nodeInfo.url);
            //searchEntity();
             window.location.href = "./SearchServlet?entity="+nodeInfo.url;
        }
    });

    network.on("stabilizationIterationsDone", function () {
        network.setOptions({physics: false});
    });

    showElem("graphContainer-id");
}

function createTOPKresultsTable(jsonData, self) {
    var map = new Map();
    var table = getElem("resultTable");
    var counter = 0;
    clearElem("resultTable");
    for (var key in jsonData) {
        map.set(key, jsonData[key]);
    }

    map[Symbol.iterator] = function * () {
        yield * [...this.entries()].sort((a, b) => b[1] - a[1]);
    }

    row = table.insertRow(0);
    var cell = row.insertCell(0);
    cell.innerHTML = "Entity";
    cell = row.insertCell(1);
    cell.innerHTML = "Cos-Sim";
    for (let [key, value] of map) {
        console.log(key + ' ' + value);
        row = table.insertRow(counter + 1);
        var arr = [key, value];
        for (var j = 0; j < 2; j++) {
            var cell = row.insertCell(j);
            cell.innerHTML = arr[j] + "";
        }
        counter++;
    }

    showElem("resultsContainer");
}

function searchEntity() {
    var currentEntity = getElemValue("inputSearchEntity");
    var jsonData = {
        type: TOP_K,
        count: 5,
        entity: currentEntity,
    };
    sendAjaxWithPromise(jsonData).then(function (data) {
        console.log("Data response from the server for TOP K entity search: " + JSON.stringify(data, null, 4));
        //createTOPKresultsTable(data, currentEntity);
        drawGraph(data, currentEntity);
    });
    loadFrameResource(currentEntity);
}

function createBigGraph() {
    var currentEntity = getElemValue("inputSearchEntity");
    var jsonData = {
        type: BIG_GRAPH,
        count: getElemValue("inputSearchEntityCount"),
        depth: 3,
        entity: currentEntity,
    };
    sendAjaxWithPromise(jsonData).then(function (data) {
        console.log("Data response from the server for BIG graph: " + JSON.stringify(data, null, 4));
        drawBigGraph(data);
    });
}

function drawBigGraph(jsonData) {
    var nodeArr = [];
    var edgeArr = [];
    for (var k in jsonData) {
        var name = k;
        var idN = k["label"];
        nodeArr.push({id: idN, label: name});
    }

    for (var k in jsonData) {
        var name = k;
        var idN = k["label"];
        for (var t in k["links"]) {
            var fromN = label;
            var toN = t["label"]
            edgeArr.push({from: fromN, to: toN});
        }
    }

    var nodes = new vis.DataSet(nodeArr);
    var edges = new vis.DataSet(edgeArr);
    var container = document.getElementById("graphContainer");
    var data = {
        nodes: nodes,
        edges: edges,
    };
    var options = {};
    var network = new vis.Network(container, data, options);
    showElem("graphContainer");
}

function compareEntities() {
    var ent1 = getElemValue("cosineEntity1");
    var ent2 = getElemValue("cosineEntity2");
    var jsonData = {
        type: COS_SIM,
        en1: getElemValue("cosineEntity1"),
        en2: getElemValue("cosineEntity2"),
    };
    sendAjaxWithPromise(jsonData).then(function (data) {
        var dataAsJSON = JSON.stringify(data, null, 4);
        console.log("Data response from the server for cosine similarity: " + dataAsJSON);
        var cosVal = data.cosSim;
        getElem("cosineAnswer").innerHTML = "Cosine similarity is " + cosVal + ".";
    });
}

function updateExpressionAns(toAddArr, toSubArr, data) {
    clearElem("exprAnsPar");
    elem = getElem("exprAnsPar");
    var answer = "";
    for (var i = 0; i < toAddArr.length; i++) {
        answer += toAddArr[i];
        if (i < toAddArr.length - 1) {
            answer += " + ";
        }
    }

    for (var i = 0; i < toSubArr.length; i++) {
        if (i === 0) {
            answer += " - ";
        }

        answer += toSubArr[i];
        if (i < toSubArr.length - 1) {
            answer += "-";
        }
    }

    answer += " = [" + data["expr_result"] + "].";
    elem.innerHTML = answer;
}

function calculateExpression() {

    var resCount = getElemValue("entitiesExpressionCount");
    var toAddArr = getElemValue("entities2add").split(",");
    var toSubArr = getElemValue("entities2sub").split(",");
    var jsonData = {
        type: EXPR,
        count: getElemValue("entitiesExpressionCount"),
        positives: getElemValue("entities2add"),
        negatives: getElemValue("entities2sub")
    };
    sendAjaxWithPromise(jsonData).then(function (data) {
        console.log("Data response from the server for expression: " + JSON.stringify(data, null, 4));
        updateExpressionAns(toAddArr, toSubArr, data);
    });
}

function formatDBpediaURI(URI) {
    console.log("Formatting URI");
    var formattedURI = URI;
    var beforeSplitters = ['/', '#', ':'];
    for (var s = 0; s < beforeSplitters.length; s++) {
        var arr = formattedURI.split(beforeSplitters[s]);
        formattedURI = arr[arr.length - 1];
    }

    return formattedURI;
}

function loadFrameResource(url) {
    getElem("iframe-wiki-id").src = url;
    showElem("iframe-wiki-id");
}

function roundTo(num, points) {
    const x = Math.pow(10, points);
    return Math.round(num * x) / x;
}

$(document).ready(function () {
    console.log("Current entity: " + currentEntity);
    console.log("Data recieved from server: " + searchData);
    drawGraph(JSON.parse(searchData),currentEntity);
    loadFrameResource(currentEntity);
    setElemValue("search-input-id", currentEntity);
});
