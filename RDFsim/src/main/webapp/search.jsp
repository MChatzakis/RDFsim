<%-- 
    Document   : search
    Created on : Jul 5, 2021, 1:20:08 PM
    Author     : Manos Chatzakis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>


    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>

        <script type="text/javascript">
            var rawAttributes = '<%=request.getAttribute("attributes")%>';
            rawAttributes = rawAttributes.replace(/&#39;/g, "'");
        </script>
    </head>

    <!-- RDFsim frameworks and styling -->
    <link rel="stylesheet" href="css/general.css">
    <link rel="stylesheet" href="css/search.css">
    <link rel="stylesheet" href="css/copyright.css">
    <link rel="icon" href="./icons/rdfsim-icon.png">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
    <script src="https://cdn.anychart.com/releases/8.10.0/js/anychart-core.min.js"></script>
    <script src="https://cdn.anychart.com/releases/8.10.0/js/anychart-tag-cloud.min.js"></script>

    <script type="text/javascript" src="js/utilities.js"></script>
    <script type="text/javascript" src="js/search.js"></script>

    <body>

        <!-- Top search form -->
        <div class="search-top">

            <!-- RDFsim Logo -->
            <a href="/RDFsim" class="rdfsim-logo">
                <img class="rdfsim-logo-img" src="./icons/rdfsim-logo4.png" alt="RDFsim">
            </a>

            <!-- Search Form -->
            <form id = "search-form-id" action= "./SearchServlet">
                <div id="search-div-id" class="search-div">
                    <input type="text" class="search-input" id="search-input-id" name="entity" placeholder="Search..." size="40" list="autoCompleteList-id" onkeyup="autoCompleteSearch(this.value);" autocomplete="off">
                    <img class = "search-img" id = "search-img-id" src = "./icons/search-icon-3.png">
                    <datalist id="autoCompleteList-id"> </datalist>
                </div>
            </form>

        </div>

        <!-- Setting buttons -->
        <div class = "settings-buttons" id="settings-buttons-id">

            <!-- Info Frame Confs -->
            <div class="settings-button-info-container" id="settings-button-info-container-id">
                <button class="settings-button-info" id ="settings-button-info-id" onClick="dispElemFromButtonClick('info-conf-id');">
                    <img src="icons/settings-icon-trans.png" width="26" height="26" alt="searchIcon"/>
                </button>

                <form class = "info-conf" id ="info-conf-id">
                    <label for="info-conf">Information service:</label>
                    <select name="info-service" id="service-selection-id">
                        <option value="0">Wikipedia</option>
                        <option value="1">DBpedia</option>
                        <option value="2">Triples</option>
                    </select>
                    <br>
                    <input type="submit" value="Configure!">
                </form>
            </div>

            <!-- Graph Frame Confs -->
            <div class="settings-button-graph-container" id="settings-button-graph-container-id">
                <button class="settings-button-graph" id="settings-button-graph-id" onClick="dispElemFromButtonClick('graph-conf-id');" >
                    <img src="icons/settings-icon-trans.png" width="26" height="26" alt="searchIcon"/>
                </button>
                <form class = "graph-conf" id ="graph-conf-id" action= "./SearchServlet">
                    <label>Mode:</label>
                    <select name="vis-mode" id="vis-mode-id">
                        <option value="0">Similarity Graph</option>
                        <option value="1">Similarity Tag Cloud</option>
                        <option value="2">Triple Graph (Knowledge Subgraph)</option>
                    </select>
                    <br>    
                    <label>Similars:</label>
                    <input type="text" size="3" name="count" id = "count-input-id" class = "count-input">
                    <br>
                    <label>Depth:</label>
                    <input type="text" size="3" name="depth" id = "depth-input-id" class = "depth-input">
                    <br>
                    <input type="submit" value="Configure!">
                </form>
            </div>
        </div>

        <!-- Results -->
        <div class = "results" id = "results-id" >
            <div class = "results-info" id = "results-info-id"> 
                <iframe class = "iframe-wiki" id ="iframe-wiki-id" frameBorder="0" src=""></iframe>
                <table class="triple-table" id = "triple-table-id">
                    <tr>
                        <th>Predicate</th>
                        <th>Object</th>
                    </tr>
                </table>
            </div>
            <div class = "results-graph" id = "results-graph-id"> 
                <div class ="graphContainer" id ="graphContainer-id"></div>
            </div>
            <div style="clear:both"></div>
        </div>

        <!-- Footer -->
        <div class="copyright" style="position:relative;">
            <a class = "about-link" href="./about.jsp">About</a>
            <a href="http://www.ics.forth.gr/isl/sar/privacy/TermsOfUse-ISL_EN.pdf" target="_blank">Terms of Use</a>
            |
            <a href="http://www.ics.forth.gr/isl/sar/privacy/PrivacyPolicy-ISL_EN.pdf" style="padding-left:0px!important;" target="_blank">Privacy Policy</a>
            | © Copyright 2021 FOUNDATION FOR RESEARCH &amp; TECHNOLOGY - HELLAS, All rights reserved.
            <div class="footer-images">
                <a href="https://www.ics.forth.gr/isl/"><img src="./icons/isl-logo.png" height="26"></a>
                <a href="https://www.ics.forth.gr/"><img src="./icons/ics-logo.png" height="26"></a>
            </div>
        </div>


    </body>

</html>
