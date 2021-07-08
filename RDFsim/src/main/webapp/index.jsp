<%-- 
    Document   : index.jsp
    Created on : Jun 29, 2021, 1:47:51 PM
    Author     :    Manos Chatzakis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <!-- RDFsim Frameworks -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="js/conf.js"></script>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>

    <body>
        <!-- Title -->
        <div class = "title" id = "title-id">
            <h3>RDFsim Configuration</h3>
        </div>

        <!-- Configurations -->
        <div class = "confs" id = "confs-id">

            <!-- Endpoint -->
            <div class = "endpoint" id = "endpoint-id">
                <p>Endpoint:</p>
                <input type="input" id="endpointConf-id" placeholder="Insert Endpoint"/>
            </div>

            <!-- Query -->
            <div class = "query" id = "query-id">
                <p>Query:</p>
                <input type="input" id="queryConf-id" placeholder="Insert Query"/>
            </div>

            <!-- Offset -->
            <div class = "offset" id = "offset-id">
                <p>Offset:</p>
                <input type="input" id="offsetConf-id" placeholder="Insert Endpoint"/>
            </div>

            <!-- Limit -->
            <div class = "limit" id = "limit-id">
                <p>Limit:</p>
                <input type="input" id="limitConf-id" placeholder="Insert Limit"/>
            </div>

            <!-- Send button -->
            <div>
                <br>
                <button class = "confButton" id = "confButton-id" onclick="sendConf();">Configure</button>
            </div>

            <!-- Configurations End-->
        </div>

        <!-- Presaved Sample load -->
        <div>
             <button class = "sampleButton" id = "sampleButton-id" onclick="sendSample();">Load Sample</button>
        </div>

    </body>
</html>
