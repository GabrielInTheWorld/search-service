# SearchService

## Requirements

- Docker (docker-compose)
- Java 11
- Maven (3)

## Start

- `docker-compose -f docker-compose.yml up` to start search-dbs: ArangoDB and Apache Solr
- `mvn spring-boot:run` to start Maven development server

## Requests

- `curl -X POST http://localhost:8080/presenter:solr.update -H "Content-Type: application/json" -d '$json'` to insert data in Apache Solr
- `curl -X POST http://localhost:8080/presenter:solr.search -H "Content-Type: application/json" -d '[{"presenter": "search", "data": {"search_query": $query}}]'` to query a search to solr (for example `*:*` to get all entries in solr)
- `curl -X POST http://localhost:8080/presenter:arango.update -H "Content-Type: application/json"` to insert data in ArangoDB
