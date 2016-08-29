# UMLRecExperiment

This project comprises 3 subprojects:

- *oruml-approaches*: it implements the following 3 algorithms proposed in Saulo Toledo's master thesis: content-based recommendation, bag-of-words recommendation and random recommendation;

- *oruml-combinations*: it runs all combinations of parameters for OntoRec for the Saulo Toledo's master thesis proposed ontology;

- *oruml-web*: web application presented in Saulo Toledo's master thesis. This tool implements several proposals by the PhD student [Thaciana Cerqueira](https://github.com/thacianacerqueira), that is using this tool to calculate the same experiment for sequence diagrams.

All of them use a compiled version of the [OntoRec algorithm](https://github.com/saulotoledo/OntoRec) in order to compute the knowledge-based recommendations.

They also uses the class diagrams that can be downloaded [here](http://www.saulotoledo.com.br/masters_thesis/uml_recsys_database.tar.gz), and extract information from them by using a compiled version of the [UMLProfileExtractor](https://github.com/saulotoledo/UMLProfileExtractor) project.

The class diagrams should be downloaded and added to the folder `oruml-web/data/uml_diagrams/class_diagrams/`. After that one should configure the application at `oruml-web/src/main/resources/application.properties`.

