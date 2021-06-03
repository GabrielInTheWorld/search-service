# SearchService

## Requirements

- Docker (docker-compose)
- Java 11
- Maven (3)

## Start

Run: `docker-compose -f docker-compose.yml up` to start the complete search-service with dependencies (postgresql, redis)

Example data: You can use `createDummyData.py` to generate sime example data: `python createDummyData.py > example-data.json`

## Requests

- `curl -X POST http://localhost:8080/presenter:postgre.update -H "Content-Type: application/json" -d '$json'` to insert data in PostgreSql by the search-service (not recommended)

  To load the example-data.json into solr, the script `load-db.sh` can be used as a shortcut

- `curl -X POST http://localhost:8080/presenter:postgre.search -H "Content-Type: application/json" -d '[{"presenter": "search", "data": {"search_query": $query}}]'` to query the search-service (for example `kill` to get all entries, which has `kill` in their content)

## Results

- Search through the whole database (~20000 entries) with one word, which appears in most of the entries, takes around 200ms (for the search-service) and around 50ms for a direct query against PostgreSql (through `explain analyze`)

- Search through the whole database (~20000 entries) with one word, which appears in only a few entries (~3), takes around 4ms (for the search-service) and around 0.08ms for a direct query against PostgreSql (through `explain analyze`)

## Postgresql

`docker-compose exec postgresql psql -U openslides`

`\dt` and `select fqid from models;`

By default, the example data of the OpenSlides repo will be fetched. To load custom data, use `./load-datastore.sh`. It will put the contents of `example-data.json` into the datastore.

## Parser

There are some dedicated parser built-in in PostgreSql. Read [PostgreSql#Parser](https://www.postgresql.org/docs/current/textsearch-parsers.html) for more information.
