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
            var searchData = '<%=request.getAttribute("data")%>';
            var currentEntity = '<%=request.getAttribute("self")%>';
        </script>
    </head>

    <!-- RDFsim frameworks and styling -->
    <link rel="stylesheet" href="css/search.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
    <script type="text/javascript" src="js/search.js"></script>

    <body>

        <!-- Top K similars 
        <div class = "topKsimilarContainer">
            <div class = "searchContainer">
                <div class  = "search">
                    <p class="search-title">Find top 10 similar entities</p>
                    <input
                        type="input"
                        id="inputSearchEntity"
                        placeholder="Insert entity"
                        />
                    <input
                        type="input"
                        id="inputSearchEntityCount"
                        placeholder="Insert Count"
                        />
                    <button onclick="searchEntity();" >Search</button>
                </div>
            </div>
            <div class = "resultsContainer" id = "resultsContainer">
                <table class = "resultTable" id = "resultTable">
                    <tr>
                        <th>Entity</th>
                        <th>Cos-Sim</th>
                    </tr>
                </table>
            </div>  
            <div class = "graphContainer" id ="graphContainer" >
            </div>
        </div>-->

        <!-- Cosine Similarity 
        <div class = "cosineSimilarityContainer">
            <div class ="cosineSearch">
                <p class="cosine-title">Cosine Similarity</p>
                <input
                    type="input"
                    id="cosineEntity1"
                    placeholder="Entity1"
                    />
                <input
                    type="input"
                    id="cosineEntity2"
                    placeholder="Entity2"
                    />
                <button onclick="compareEntities();" >Search</button>
            </div>
            <div class = "cosineAnswer">
                <p id = "cosineAnswer"></p>
            </div>
        </div>-->

        <!-- Arithmetic Expressions 
        <div class = "arExpressionsContainer">
            <div class ="arExpressionSearch">
                <p class="expr-title">Arithmetic Expressions</p>
                <input
                    type="input"
                    id="entities2add"
                    placeholder="Insert entities to add (eg. en1,en2,...)"
                    />
                <input
                    type="input"
                    id="entities2sub"
                    placeholder="Insert entities to sub (eg. en1,en2,...)"
                    />
                <input
                    type="input"
                    id="entitiesExpressionCount"
                    placeholder="Count (eg.2)"
                    />
                <button onClick = "calculateExpression()"> Calculate </button>
            </div>
            <div class = "expressionAns" id = "exprAns">
                <p id = "exprAnsPar"></p>
            </div>
        </div>-->


        <!-- Top search form -->
        <div class="search-top">
            <a href="/RDFsim" class="rdfsim-logo">
                <img class="rdfsim-logo-img" src="./icons/rdfsim-logo3.png" alt="RDFsim">
            </a>
            <form id = "search-form-id" class = "search-form" action= "./SearchServlet">
                <div id="search-div-id" class="search-div">
                    <input type="text" class="search-input" id="search-input-id" name="entity" placeholder="Insert entity" size="75">
                </div>
            </form>
        </div>

        <!-- Results -->
        <div class = "results" id = "results-id" >
            <div class = "results-info" id = "results-info-id"> 
                <iframe class = "iframe-wiki" id ="iframe-wiki-id" frameBorder="0" src=""></iframe>
            </div>
            <div class = "results-graph" id = "results-graph-id"> 
                <div class ="graphContainer" id ="graphContainer-id"></div>
            </div>
        </div>

        <!-- Footer 
        <div class="copyright">
            <a href="http://www.ics.forth.gr/isl/sar/privacy/TermsOfUse-ISL_EN.pdf" target="_blank">Terms of Use</a>
            |
            <a href="http://www.ics.forth.gr/isl/sar/privacy/PrivacyPolicy-ISL_EN.pdf" style="padding-left:0px!important;" target="_blank">Privacy Policy</a>
            | © Copyright 2020 FOUNDATION FOR RESEARCH &amp; TECHNOLOGY - HELLAS, All rights reserved.
            <div class="footer-images">
                <a href="https://www.ics.forth.gr/isl/"><img src="./icons/isl-logo.png" height="30"></a>
                <a href="https://www.ics.forth.gr/"><img src="./icons/ics-logo.png" height="30"></a>
            </div>
        </div>
        -->
    </body>
</html>
