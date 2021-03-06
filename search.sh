#!/bin/sh

QUERY="kill"
DB="postgre"
PRESENTER="search"
DATA="{""}"

function usage()
{
    echo ""
    echo "./search.sh"
    echo "\t-h --help"
    echo "\t--query | -q=$QUERY"
    echo "\t--db | -db=$DB"
    echo "\t--presenter | -p=$PRESENTER"
    echo "\t--data | -d=$DATA"
    echo ""
}

while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`
    case $PARAM in
        -h | --help)
            usage
            exit
            ;;
        -db | --db)
            DB=$VALUE
            ;;
        -q | --query)
            QUERY=$VALUE
            ;;
        -p | --presenter)
            PRESENTER=$VALUE
            ;;
        -d | --data)
            DATA=$VALUE
            ;;
        *)
            echo "ERROR: unknown parameter \"$PARAM\""
            usage
            exit 1
            ;;
    esac
    shift
done


echo "QUERY is $QUERY";
echo "DB is $DB";
echo "PRESENTER is $PRESENTER"

curl -H "Content-Type: application/json" http://localhost:8080/presenter -d "[{\"presenter\": \"$PRESENTER\", \"data\": {\"db\": \""$DB"\", \"search_query\": \"$QUERY\" }}]"