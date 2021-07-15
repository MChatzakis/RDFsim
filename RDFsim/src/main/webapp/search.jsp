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

        <!-- Top search form -->
        <div class="search-top">
            <a href="/RDFsim" class="rdfsim-logo">
                <img class="rdfsim-logo-img" src="./icons/rdfsim-logo4.png" alt="RDFsim">
            </a>
            <form id = "search-form-id" class = "search-form" action= "./SearchServlet">
                <div id="search-div-id" class="search-div">
                    <input type="text" class="search-input" id="search-input-id" name="entity" placeholder="Insert entity" size="40">
                </div>
            </form>
            <!--<button class = "settings">
                <img src="./icons/setting-icon.png" height="30" width="30">
            </button>-->

        </div>

        <!-- Results -->
        <div class = "results" id = "results-id" >
            <div class = "results-info" id = "results-info-id"> 
                <iframe class = "iframe-wiki" id ="iframe-wiki-id" frameBorder="0" src=""></iframe>
            </div>
            <div class = "results-graph" id = "results-graph-id"> 
                <div class ="graphContainer" id ="graphContainer-id"></div>
            </div>
            <div style="clear:both"></div>
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
