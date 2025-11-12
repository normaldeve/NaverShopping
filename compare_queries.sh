#!/bin/bash

USER="root"
PASS="your_password"
DB="your_database"
OUTPUT_FILE="query_results.csv"

echo "query_type,keyword,execution_time_ms,rows_examined,cost" > $OUTPUT_FILE

KEYWORDS=("한샘" "모션침대" "수납형 이층침대")

run_query() {
  local TYPE=$1
  local KEYWORD=$2
  local QUERY

  if [ "$TYPE" == "LIKE" ]; then
    QUERY="EXPLAIN ANALYZE SELECT * FROM products WHERE name LIKE '%$KEYWORD%';"
  else
    QUERY="EXPLAIN ANALYZE SELECT * FROM products WHERE MATCH(name) AGAINST('$KEYWORD' IN NATURAL LANGUAGE MODE);"
  fi

  echo "Running $TYPE query for '$KEYWORD'..."

  RESULT=$(mysql -u $USER -p$PASS -D $DB -Bse "$QUERY" 2>&1 \
    | tr -d '\n' \
    | sed 's/\x1b\[[0-9;]*m//g')

  EXEC_TIME=$(echo "$RESULT" | grep -o 'actual time=[0-9.]*\.\.[0-9.]*' | awk -F'..' '{print $2}' | head -1)
  ROWS=$(echo "$RESULT" | grep -o 'rows=[0-9.e+-]*' | head -1 | sed 's/rows=//')
  COST=$(echo "$RESULT" | grep -o 'cost=[0-9.]*' | head -1 | sed 's/cost=//' | head -1)

  echo "  → time=${EXEC_TIME:-0}ms, rows=${ROWS:-0}, cost=${COST:-0}"
  echo "$TYPE,$KEYWORD,${EXEC_TIME:-},${ROWS:-},${COST:-}" >> $OUTPUT_FILE
}

for KEY in "${KEYWORDS[@]}"; do
  run_query "LIKE" "$KEY"
done

for KEY in "${KEYWORDS[@]}"; do
  run_query "FULLTEXT" "$KEY"
done

echo "✅ CSV export complete: $OUTPUT_FILE"