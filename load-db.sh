#!/bin/bash

DATA=$(cat example-data.json)
curl -X POST http://localhost:8080/presenter:solr.update -H "Content-Type: application/json" -d "$DATA"
