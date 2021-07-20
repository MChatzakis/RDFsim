/*
 * Basic search controller
 * Manos Chatzakis (chatzakis@ics.forth.gr)
 */
var URL = "http://localhost:8080/RDFsim/SearchServlet";
var TOP_K = 0;
var COS_SIM = 1;
var EXPR = 2;
var BIG_GRAPH = 3;

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

function dispElemFromButtonClick(id) {
    var el = getElem(id);
    if (el.style.display === "block") {
        hideElem(id);
    } else {
        showElem(id);
    }
}

/* ---------------------------------- Graph Drawing ---------------------------------- */
function drawGraph(entitiesJSON) {
    var nodeArr = [];
    var edgeArr = [];

    for (var k in entitiesJSON) {
        var currentID = entitiesJSON[k]['id'];
        var currentLabel = k;
        var links = entitiesJSON[k]['links'];

        nodeArr.push({id: currentID, label: formatDBpediaURI(currentLabel), url: currentLabel});

        console.log("Current Label: " + currentLabel);
        console.log("Current ID: " + currentID);
        console.log("Current Links: " + links);

        for (var link in links) {

            var toID = links[link]["toID"];
            var weight = links[link]["weight"];

            console.log("Link toID: " + toID);
            console.log("Weight: " + weight);

            edgeArr.push({from: currentID, to: toID, label: roundTo(weight, 2) + "", length: 250});
        }
    }

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
        },
        nodes: {
            color: {
                border: 'red',
                background: 'white',
                highlight: {
                    border: 'red',
                    background: 'gray'
                },
                hover: {
                    border: 'black',
                    background: 'black'
                }
            },
            font: {
                color: 'black',
                size: 18, // px
                face: 'arial',
            },
            shape: 'box',
        },
        physics: {
            enabled: true,
            barnesHut: {
                theta: 0.5,
                gravitationalConstant: -2000,
                centralGravity: 0.3,
                springLength: 95,
                springConstant: 0.04,
                damping: 0.09,
                avoidOverlap: 0
            }
        }
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
            window.location.href = "./SearchServlet?entity=" + nodeInfo.url;
        }
    });

    network.on("stabilizationIterationsDone", function () {
        network.setOptions({physics: false});
    });

    showElem("graphContainer-id");
}

/* ---------------------------------- Embeddings ---------------------------------- */
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

/* ---------------------------------- Triples ---------------------------------- */
function fillTripleTable(data) {
    var table = getElem("triple-table-id");
    clearElem("triple-table-id");
    var counter = 0;

    for (elem in data) {

        var s = elem["s"];
        var p = elem["p"];
        var o = elem["o"];
        var arr = [s, p, o];

        row = table.insertRow(counter + 1);

        for (var j = 0; j < 3; j++) {
            var cell = row.insertCell(j);
            cell.innerHTML = arr[j] + "";
        }

        counter++;
    }

    showElem("triple-table-id");

}

/* ---------------------------------- Utilities ---------------------------------- */
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

function roundTo(num, points) {
    const x = Math.pow(10, points);
    return Math.round(num * x) / x;
}

function loadFrameResource(url, mode) {
    var wikiLink = "https://en.wikipedia.org/wiki/" + formatDBpediaURI(url);
    var dbpLink = url;

    if (mode === "wikipedia") {
        getElem("iframe-wiki-id").src = wikiLink;
    } else {
        getElem("iframe-wiki-id").src = dbpLink;
    }

    showElem("iframe-wiki-id");
}

/* ---------------------------------- Document Load ---------------------------------- */
$(document).ready(function () {
    var curEn = currentEntity;
    var graphJson = graph;

    console.log("Current entity: " + curEn);
    console.log("Data recieved from server: " + graphJson);

    drawGraph(JSON.parse(graphJson));

    if (triplesRetrieved != null) {
        //hide and show
        var jsonTripleArray = JSON.parse(triplesRetrieved);
        fillTripleTable(jsonTripleArray);
    } else {
        //hide and show
        loadFrameResource(curEn, "wikipedia");
    }

    setElemValue("search-input-id", curEn);
    setElemValue("count-input-id", currCount);
    setElemValue("depth-input-id", currDepth)
});

