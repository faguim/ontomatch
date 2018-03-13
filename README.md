# Ontomatch REST-based service

Available at [http://ontomatch.lis.ic.unicamp.br](http://ontomatch.lis.ic.unicamp.br)

This service parses a given text and returns [possible]associated entities(URIs) within an ontology. Associations are made using string similarity and distance measures of `rdfs:label` properties and subsequent `owl:AnnotationProperty` annotations linked to the resource -- usually adopted for adding properties acting as alternative labels.

Available **ontologyIDs**:
[`pato`](https://bioportal.bioontology.org/ontologies/PATO),
[`tao`](https://bioportal.bioontology.org/ontologies/TAO),
[`xao`](https://bioportal.bioontology.org/ontologies/XAO),
[`hfo`](https://bioportal.bioontology.org/ontologies/HFO)


The set of algorithms used in this project is based on the project [java-string-similarity](https://github.com/tdebatty/java-string-similarity). Please refer [to this link](https://github.com/tdebatty/java-string-similarity#overview) for further details and credits.

Available **algorithmIDs** for string similarity and distance measures:
[`NormalizedLevenshtein`](https://github.com/tdebatty/java-string-similarity#normalized-levenshtein),
[`JaroWinkler`](https://github.com/tdebatty/java-string-similarity#jaro-winkler),
[`Cosine`](https://github.com/tdebatty/java-string-similarity#cosine-similarity),
[`Jaccard`](https://github.com/tdebatty/java-string-similarity#jaccard-index),
[`MetricLCS`](https://github.com/tdebatty/java-string-similarity#metric-longest-common-subsequence)
[`Levenshtein`](https://github.com/tdebatty/java-string-similarity#levenshtein),
[`OptimalStringAlignment`](https://github.com/tdebatty/java-string-similarity#optimal-string-alignment)


dasdasdasd

### Based on a given similarity threshold (/api/rest/resources)


* **URL:**    `/api/rest/resources`

* **Method:**  `POST`

*  **Required POST Params:**

   * `text=[string]`
   * `n=[float]` [the service will return the **n** most similar entities] [Default = 10]
   * `ontology=[ontologyID]` [see ontology IDs] [Default = hfo]
   * `algorithm=[algorithmID]` [see algorithmIDs] [Default = NormalizedLevenshtein]
   * `floor=[float]` [returns similarities >= floor ]
   * `ceiling=[float]` [returns similarities <= ceiling ]

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:** JSON array <br />


* **Error Responses:**

  * **Code:** `415 Unsupported Media Type` <br />
    **Troubleshooting:** Check parameters


  * **Code:**  `404 NOT FOUND`  <br />
    **Troubleshooting:**  Ontology ID not found

* **Sample Call:**

    * **Code:**
  ```javascript
    $.ajax({
      url: "http://ontomatch.lis.ic.unicamp.br/api/rest/resources",
      dataType: "json",
      type : "POST",
      data: { text: "chest pain", n: 5, ontology:"hfo" },
      success : function(r) {
        console.log(r);
      }
    });
  ```

    * **Return:**  

    ```json
    [
    {"uri":"http://bmi.utah.edu/ontologies/hfontology/C0008031","label":"Chest Pain","similarity":1.0},
    {"uri":"http://bmi.utah.edu/ontologies/hfontology/C0030193","label":"Pain","similarity":0.7071067690849304},
    {"uri":"http://bmi.utah.edu/ontologies/hfontology/C0000737","label":"Abdominal Pain","similarity":0.5}
    ]
    ```
