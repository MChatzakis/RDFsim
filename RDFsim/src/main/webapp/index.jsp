<%-- 
    Document   : index.jsp
    Created on : Jun 29, 2021, 1:47:51 PM
    Author     : manos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <!--<link rel="stylesheet" href="css/.css">-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
    <script type="text/javascript" src="js/conf.js"></script>
    <!--<link
        rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
        />-->

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>

    <body>
        <div>
            <h3>RDFsim Configuration</h3>
        </div>

        <div>
            <form  id="conf-form" action  = "http://localhost:8080/RDFsim/SearchServlet">
                <!-- Endpoint Selection -->
                <label for="sparql-endpoint">SPARQL Endpoint: </label>
                <input type="text" name="sparql" value="https://dbpedia.org/sparql" class="form-control" id="sparql" placeholder="">
                <br>

                <!-- API Selection -->
                <label for="emb-api">Embedding API</label>
                <select id="apis" name="apis">
                    <option value="word2vec">word2vec</option>
                    <option value="BERT">BERT</option>
                    <option value="GloVe">GloVe</option>
                </select><br>

                <!-- API Parameters -->

                <!-- Submit -->
                <input type="submit" value="Apply" class="btn btn-primary" id="submitButton"  onClick="sendConf()">
            </form>
        </div>
    </body>
</html>
