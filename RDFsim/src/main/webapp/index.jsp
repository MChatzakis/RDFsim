<%-- 
    Document   : index.jsp
    Created on : Jun 29, 2021, 1:47:51 PM
    Author     : manos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <link rel="stylesheet" href="css/Doctor.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
    <script type="text/javascript" src="js/Nurse_controller.js"></script>
    <link
        rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
        />
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RDFsim</title>
    </head>


    <body>

        <div class="row  justify-content-center " style="margin:5px";>
            <form  id="conf-form" class="row col-6  justify-content-center mt-3 table-dark ">

                <div class="row">
                    <div class="form-group col ">
                        <label for="sparql-endpoint">SPARQL Endpoint</label>
                        <input type="text" name="sparql" value="https://dbpedia.org/sparql" class="form-control" id="sparql" placeholder="">
                    </div>
                    <div class="form-group col ">
                        <label for="sparql-endpoint">API</label>
                        <input type="text" name="type" value="word2vec" class="form-control" id="sparql" placeholder="">
                    </div>
                </div>

                <div>
                    <input type="submit" value="Apply" class="btn btn-primary" id="submitButton"  onClick="sendConf()">
                </div>

            </form>
        </div>

    </body>
</html>
