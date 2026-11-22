#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
EVENT_ID="${EVENT_ID:-race-event-$(date +%s)}"
SEAT_ID="${SEAT_ID:-A-1}"
REQUESTS="${REQUESTS:-40}"
PARALLELISM="${PARALLELISM:-40}"
OUT_DIR="${OUT_DIR:-build/concurrency-results}"

mkdir -p "$OUT_DIR"
RESULTS="$OUT_DIR/responses.tsv"
: > "$RESULTS"

echo "Target: event=$EVENT_ID seat=$SEAT_ID"
echo "Requests: $REQUESTS (parallelism=$PARALLELISM)"
echo "Writing responses to: $RESULTS"

export BASE_URL EVENT_ID SEAT_ID RESULTS

seq 1 "$REQUESTS" | xargs -P "$PARALLELISM" -I '{}' bash -c '
  n="$1"
  body=$(printf "{\"eventId\":\"%s\",\"seatId\":\"%s\",\"customerId\":\"customer-%s\"}" "$EVENT_ID" "$SEAT_ID" "$n")
  code=$(curl -sS -o "$RESULTS.response.$n.json" -w "%{http_code}" \
    -X POST "$BASE_URL/api/bookings" \
    -H "Content-Type: application/json" \
    -d "$body" || printf "000")
  printf "%s\t%s\n" "$code" "$n" >> "$RESULTS"
' _ '{}'

echo
echo "HTTP status counts:"
awk -F '\t' '{count[$1]++} END {for (code in count) print code, count[code]}' "$RESULTS" | sort

echo
echo "Checking persisted state for target seat..."
curl -sS "$BASE_URL/api/bookings" | python3 -c '
import json, os, sys
event_id = os.environ["EVENT_ID"]
seat_id = os.environ["SEAT_ID"]
data = json.load(sys.stdin)
matches = [b for b in data if b.get("eventId") == event_id and b.get("seatId") == seat_id]
print(json.dumps(matches, indent=2))
print(f"Persisted bookings for target seat: {len(matches)}")
if len(matches) > 1:
    print("INVARIANT VIOLATION: more than one booking exists for the same event and seat")
    raise SystemExit(1)
if len(matches) == 1:
    print("Seat uniqueness invariant holds in persisted data.")
else:
    print("No booking found for target; inspect request results and service logs.")
    raise SystemExit(1)
'

echo
echo "Probe finished. Preserve this directory and application logs as experiment evidence."
