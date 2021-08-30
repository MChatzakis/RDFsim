<%-- 
    Document   : index
    Created on : Jul 14, 2021, 2:43:48 AM
    Author     : manos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <link rel="stylesheet" href="css/general.css">
    <link rel="stylesheet" href="css/index.css">
    <link rel="stylesheet" href="css/copyright.css">
    <link rel="icon" href="./icons/rdfsim-icon.png">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>

    <script type="text/javascript" src="js/utilities.js"></script>
    <script type="text/javascript" src="js/index.js"></script>

    <body>

        <!-- Main Container -->
        <div class = "home" id = "home-id">

            <!-- Logo -->
            <div class = "logo" id = "logo-id">
                <img class="logo-img" src="./icons/rdfsim-logo4.png" alt="rdfsim-logo">
            </div>

            <!-- Search -->
            <form id = "search-form-id" action= "./SearchServlet">
                <div id="search-div-id" class="search-div">
                    <input type="text" class="search-input" id="search-input-id" name="entity" placeholder="Search..." size="60" list="autoCompleteList-id" onkeyup="autoCompleteIndex(this.value);" autocomplete="off">
                    <img class = "search-img" id = "search-img-id" src = "./icons/search-icon-3.png">
                    <datalist id="autoCompleteList-id">
                        
                    </datalist>
                </div>
                <span class="home-description">Similarity browsing tool for RDF Databases</span>
                <br>
                <br>
                <label for="dataset-conf">Dataset:</label>
                <select name="dataset" id="dataset-selection-id">
                    <option value="dbpedia_philosophers">DBpedia Philosophers</option>
                    <option value="dbpedia_movies">DBpedia Movies</option>
                    <option value="dbpedia_video_games">DBpedia Video Games</option>
                    <option value="dbpedia_programming_langs">DBpedia Programming Languages</option>
                </select>
            </form>
        </div>

        <!--Footer -->
        <div class="copyright" style="position:absolute;">
            <a class = "about-link" href="./about.jsp">About</a>
            <a href="http://www.ics.forth.gr/isl/sar/privacy/TermsOfUse-ISL_EN.pdf" target="_blank">Terms of Use</a>
            |
            <a href="http://www.ics.forth.gr/isl/sar/privacy/PrivacyPolicy-ISL_EN.pdf" style="padding-left:0px!important;" target="_blank">Privacy Policy</a>
            |  © Copyright 2021 FOUNDATION FOR RESEARCH &amp; TECHNOLOGY - HELLAS, All rights reserved.
            <div class="footer-images">
                <a href="https://www.ics.forth.gr/isl/"><img src="./icons/isl-logo.png" height="30"></a>
                <a href="https://www.ics.forth.gr/"><img src="./icons/ics-logo.png" height="30"></a>
            </div>
        </div>

    </body>

</html>
