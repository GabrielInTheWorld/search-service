#!/bin/bash

DATA=$(cat example-data.json)
curl --header "Content-Type: application/json" -d '' http://localhost:9011/internal/datastore/writer/truncate_db
# docker-compose exec writer \
#     bash -c "source export-database-variables.sh; echo '$DATA' > /data.json; export DATASTORE_INITIAL_DATA_FILE=/data.json; python cli/create_initial_data.py"
curl -X POST http://localhost:8080/presenter:postgre.update -H "Content-Type: application/json" -d "$DATA"
