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
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
    <script type="text/javascript" src="js/search.js"></script>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>
    <body>

        <!-- Top K similars -->
        <div class = "topKsimilarContainer">

            <!-- Similar Search Form -->
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
            <!-- Result table -->
            <div class = "resultsContainer" id = "resultsContainer">
                <table class = "resultTable" id = "resultTable">
                    <tr>
                        <th>Entity</th>
                        <th>Cos-Sim</th>
                    </tr>
                </table>
            </div>  
            <!-- Similarity Graph Drawing -->
            <div class = "graphContainer" id ="graphContainer" >
            </div>
        </div>

        <!-- Cosine Similarity -->
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
        </div>

        <!-- Arithmetic Expressions -->
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
        </div>

    </body>
</html>
