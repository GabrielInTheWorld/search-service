# SearchService

## Requirements

- Docker (docker-compose)
- Java 11
- Maven (3)

## Start

- `docker-compose -f docker-compose.yml up` to start search-dbs: ArangoDB and Apache Solr
- `mvn spring-boot:run` to start Maven development server

Example data: You can use `createDummyData.py` to generate sime example data: `python createDummyData.py > example-data.json`

## Requests

- `curl -X POST http://localhost:8080/presenter:solr.update -H "Content-Type: application/json" -d '$json'` to insert data in Apache Solr

  To load the example-data.json into solr, the script `load-db.sh` can be used as a shortcut
- `curl -X POST http://localhost:8080/presenter:solr.search -H "Content-Type: application/json" -d '[{"presenter": "search", "data": {"search_query": $query}}]'` to query a search to solr (for example `*:*` to get all entries in solr)
- `curl -X POST http://localhost:8080/presenter:arango.update -H "Content-Type: application/json"` to insert data in ArangoDB

## Postgresql

`docker-compose exec postgresql psql -U openslides`

`\dt` and `select fqid from models;`

By default, the example data of the OpenSlides repo will be fetched. To load custom data, use `./load-datastore.sh`. It will put the contents of `example-data.json` into the datastore.

