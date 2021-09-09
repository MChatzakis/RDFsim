# RDFsim

This is the ongoing repository of the paper "RDFsim: Similarity-Based Browsing over DBpedia using Embeddings", Manos Chatzakis, Mike Mountantonakis and Yannis Tzitzikas.

<img src="https://github.com/MChatzakis/RDFsim/blob/main/RDFsim/src/main/webapp/icons/rdfsim-logo4.png" alt="RDFsim Logo" height="200"> 

## What is RDFsim?
RDFsim is a similarity browsing tool to browse over RDF Knowledge Graphs. 

## What is Similarity Based Browsing (SBB)?
Similarity Based Browsing is a specific type of browsing that allows the user to explore information by discovering the semanticaly similar entities to a given one.

## How is SBB implemented in RDFsim?
Similarity Based Browsing is implemented using our "Similarity Graph" structure, which offers an inderactive way to browse, by creating a network of semantically similar entities.

## What does "semantically similar entities" mean in RDFsim?
Semantically similar entities means that we are trying to match entities which has the same meaning. RDFsim discover such entities by exploiting Knowledge Graph Embeddings, through NLP libraries, such as word2vec.

## Where do the data come from?
For now, RDFsim works on datasets created by DBpedia, using SPARQL queries. However, it can be easily extended to support other (RDF) databases.

## How to use RDFsim?
RDFsim stable web application is available at 62.217.127.128:8080/RDFsim/ and at this dedicated [repository](https://github.com/MChatzakis/RDFsim-PublicVersion)

## Licence
This project is licenced under the regulation of software of Information Systems Laboratory, of Institute of Computer Science, FORTH.

## Contact
For any recomendation or bug report, dont hesitate to create an issue or contact the authors. Any advice is always welcome.
