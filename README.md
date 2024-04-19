<p align="center">
<img src="https://github.com/MChatzakis/RDFsim/blob/main/RDFsim/src/main/webapp/icons/rdfsim-logo4.png" alt="RDFsim Logo" height="160"> 
</p>


<h1 align="center">RDFsim</h1>
<h2 align="center">Similarity-based browsing over DBpedia using embeddings</h2>

Browsing has been the core access method for the Web from its beginning. Analogously, one good practice for publishing data on the Web is to support dereferenceable URIs, to also enable plain web browsing by users. The information about one URI is usually presented through HTML tables (such as DBpedia and Wikidata pages) and graph representations (by using tools such as LODLive and LODMilla). In most cases, for an entity, the user gets all triples that have that entity as subject or as object. However, sometimes the number of triples is numerous. To tackle this issue, and to reveal similarity (and thus facilitate browsing), in this article we introduce an interactive similarity-based browsing system, called RDFsim, that offers “Parallel Browsing”, that is, it enables the user to see and browse not only the original data of the entity in focus, but also the K most similar entities of the focal entity. The similarity of entities is founded on knowledge graph embeddings; however, the indexes that we introduce for enabling real-time interaction do not depend on the particular method for computing similarity. We detail an implementation of the approach over specific subsets of DBpedia (movies, philosophers and others) and we showcase the benefits of the approach. Finally, we report detailed performance results and we describe several use cases of RDFsim.

## Reference
When using RDFsim, please cite the following paper:
```bibtex
@article{chatzakis2021rdfsim,
  title = {RDFSIM: similarity-based browsing over dbpedia using embeddings},
  author = {Chatzakis, Manos and Mountantonakis, Michalis and Tzitzikas, Yannis},
  journal = {Information},
  volume = {12},
  number = {11},
  pages = {440},
  year = {2021},
  publisher = {MDPI},
}
```
