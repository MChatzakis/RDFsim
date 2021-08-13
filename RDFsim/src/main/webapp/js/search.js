/*
 * Basic search controller
 * Manos Chatzakis (chatzakis@ics.forth.gr)
 */

/* ---------------------------------- Graph Drawing ---------------------------------- */
function drawTagCloud(entitiesJSON) {
    var data = [];
    for (var k in entitiesJSON) {
        var currentID = entitiesJSON[k]['id'];
        var currentLabel = k;
        var links = entitiesJSON[k]['links'];
        var value = 0;

        for (var link in links) {
            value = links[link]["weight"];
        }

        if (currentID === 0) {
            data.push({x: formatDBpediaURI(currentLabel), value: 100, URI: currentLabel});
        } else {
            data.push({x: formatDBpediaURI(currentLabel), value: roundTo(value, 2) * 100, URI: currentLabel});
        }
    }

    chart = anychart.tagCloud(data);

    chart.container("graphContainer-id");

    chart.listen("pointClick", function (e) {
        window.location.href = "./SearchServlet?entity=" + e.point.get("URI");
    });

    chart.draw();

    showElem("graphContainer-id");
}

function drawGraph(entitiesJSON, depth) {
    var nodeArr = [];
    var edgeArr = [];

    for (var k in entitiesJSON) {
        var currentID = entitiesJSON[k]['id'];
        var currentLabel = k;
        var links = entitiesJSON[k]['links'];

        if (currentID === 0 && depth > 1) {
            nodeArr.push({id: currentID, label: formatDBpediaURI(currentLabel), url: currentLabel, color: "red"});
        } else {
            nodeArr.push({id: currentID, label: formatDBpediaURI(currentLabel), url: currentLabel});
        }

        //console.log("Current Label: " + currentLabel);
        //console.log("Current ID: " + currentID);
        //console.log("Current Links: " + links);

        for (var link in links) {

            var toID = links[link]["toID"];
            var weight = links[link]["weight"];
            var isUL = links[link]["isUL"];

            var arrowsConf = "from";
            //console.log("Link toID: " + toID);
            //console.log("Weight: " + weight);

            //arrows: "to, from",
            if (isUL || depth == 1) {
                //console.log("askfiusdgs");
                arrowsConf = "";
            }

            edgeArr.push({from: currentID, to: toID, label: roundTo(weight, 2) + "", length: 250, arrows: arrowsConf});
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
        var nodeID = params.nodes[0];
        if (nodeID) {
            var clickedNode = this.body.nodes[nodeID];
            var nodeInfo = clickedNode.options;

            //console.log('clicked node:', nodeInfo.label);
            //setElemValue("search-input-id", nodeInfo.url);

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

/* ---------------------------------- Triples ---------------------------------- */
function fillTripleTable(fromData, toData, selfEntity) {
    var table = getElem("triple-table-id");
    clearElem("triple-table-id");
    var counter = 0;

    row = table.insertRow(counter++);
    var cell = row.insertCell(0);
    cell.innerHTML = "<th>Subject</th>";
    var cell = row.insertCell(1);
    cell.innerHTML = "<th>Predicate</th>";
    var cell = row.insertCell(2);
    cell.innerHTML = "<th>Object</th>";

    var pref = "./SearchServlet?entity=";
    var address = "";

    for (elem in fromData) {

        var p = fromData[elem]["p"].replaceAll("@_@", "'");
        var o = fromData[elem]["o"].replaceAll("@_@", "'");

        var subject = formatDBpediaURI(selfEntity);
        var predicate = formatDBpediaURI(p) + ",(<a href=\"" + p + " \">src</a>)";
        var object = "<a href=\"" + pref + o + " \">" + formatDBpediaURI(o) + "</a>(<a href=\"" + o + "\">src</a>)";

        var arr = [subject, predicate, object];

        row = table.insertRow(counter);

        for (var j = 0; j < arr.length; j++) {
            var cell = row.insertCell(j);
            cell.innerHTML = arr[j] + "";
        }

        counter++;
    }

    for (elem in toData) {

        var s = toData[elem]["s"].replaceAll("@_@", "'");
        var p = toData[elem]["p"].replaceAll("@_@", "'");

        var object = formatDBpediaURI(selfEntity);
        var predicate = formatDBpediaURI(p) + ",(<a href=\"" + p + " \">src</a>)";
        var subject = "<a href=\"" + pref + s + " \">" + formatDBpediaURI(s) + "</a>(<a href=\"" + s + "\">src</a>)";

        var arr = [subject, predicate, object];

        row = table.insertRow(counter);

        for (var j = 0; j < arr.length; j++) {
            var cell = row.insertCell(j);
            cell.innerHTML = arr[j] + "";
        }

        counter++;
    }

    showElem("triple-table-id");
}

function drawTripleGraph(fromData, toData, selfEntity) {
    var limit = 100000;
    var counter = 0;
    var nodeArr = [];
    var edgeArr = [];

    nodeArr.push({id: counter++, label: formatDBpediaURI(selfEntity), url: selfEntity});

    for (elem in fromData) {

        var p = fromData[elem]["p"].replaceAll("@_@", "'");
        var o = fromData[elem]["o"].replaceAll("@_@", "'");

        nodeArr.push({id: counter, label: formatDBpediaURI(o), url: o});
        edgeArr.push({from: counter++, to: 0, label: formatDBpediaURI(p) + "", length: 250, });

        if (counter === limit) {
            break;
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
        var nodeID = params.nodes[0];
        if (nodeID) {
            var clickedNode = this.body.nodes[nodeID];
            var nodeInfo = clickedNode.options;
            window.location.href = nodeInfo.url;
        }
    });

    network.on("stabilizationIterationsDone", function () {
        network.setOptions({physics: false});
    });

    showElem("graphContainer-id");

}

/* ---------------------------------- Utilities ---------------------------------- */
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

    /* --------- Setting current attributes --------- */
    var attributes = JSON.parse(rawAttributes);

    var curEn = attributes["self"];
    var depth = attributes["depth"];
    var count = attributes["count"];
    var visMode = attributes["visMode"];
    var currTriples = attributes["triples"];
    var infoS = attributes["infoService"];
    var graphJson = attributes["graph"];

    var valArr = [curEn, graphJson, currTriples, infoS, count, depth, visMode];
    if (!allValuesAreSet(valArr)) {
        redirToErrorPage();
        return;
    }

    /* --------- Logging --------- */
    console.log("Data: " + JSON.stringify(attributes, 0, 2));

    /* --------- Visualization Service Setup --------- */
    switch (visMode) {
        case 0:
            drawGraph(graphJson, depth);
            break;
        case 1:
            drawTagCloud(graphJson);
            break;
        case 2:
            drawTripleGraph(currTriples["asSubject"], currTriples["asObject"], curEn);
            break;
    }

    /* --------- Information Service Setup --------- */
    switch (infoS) {
        case 0:
            hideElem('triple-table-id');
            loadFrameResource(curEn, "wikipedia");
            break;
        case 1:
            hideElem('triple-table-id');
            loadFrameResource(curEn, "dbpedia");
            break;
        case 2:
            hideElem('iframe-wiki-id');
            fillTripleTable(currTriples["asSubject"], currTriples["asObject"], curEn);
            break;
    }

    /* --------- UI updates --------- */
    setElemValue("search-input-id", formatDBpediaURI(curEn));
    setElemValue("count-input-id", count);
    setElemValue("depth-input-id", depth);

    setOptionValue("service-selection-id", infoS);
    setOptionValue("vis-mode-id", visMode);
});

/* ---------------------------------- AutoComplete ---------------------------------- */
function autoCompleteSearch(prefix) {
    var dataListID = "autoCompleteList-id";
    var URL = "SearchServlet";

    console.log("Starting auto complete sequence with prefix: " + prefix);
    autoComplete(prefix, dataListID, URL);
}