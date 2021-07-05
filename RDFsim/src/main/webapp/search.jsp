<%-- 
    Document   : search
    Created on : Jul 5, 2021, 1:20:08 PM
    Author     : manos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <link rel="stylesheet" href="css/search.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script
        type="text/javascript"
        src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"
    ></script>
    <script type="text/javascript" src="js/search.js"></script>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>
    <body>

        <div class = "rdfSim">

            <div class = "searchContainer">
                <div class  = "search">
                    <p class="search-title">Search Entities</p>
                    <input
                        type="input"
                        id="inputEntity"
                        autocomplete="off"
                        placeholder="Hit Enter to Search"
                        />
                </div>
            </div>

            <div class = "resultsContainer">

            </div>

            <div class = "graphContainer" id ="graphContainer" >

            </div>

        </div>

    </body>
</html>
