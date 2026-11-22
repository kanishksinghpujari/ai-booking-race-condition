# AI Booking Race Condition Experiment

A reproducible Spring Boot project for investigating a deceptively simple correctness property:

> For a given event and seat, at most one booking may be confirmed.

The project is deliberately structured in two phases:

1. **Baseline application + ordinary tests** — implements seat booking with a check-then-insert flow. These tests cover normal sequential behavior and are expected to pass. They do not prove concurrency safety.
2. **Independent concurrency probe** — sends simultaneous requests for the same seat and checks the database/API outcomes. This is intentionally separate from the baseline test suite.

This setup is intended for an engineering experiment. Do not use it for real ticket sales or production workloads.

## Stack

- Java 21
- Spring Boot 3.3
- Spring Web
- Spring Data JPA
- PostgreSQL 16
- Flyway
- JUnit 5
- Testcontainers for integration tests
- Docker Compose

## Run locally

Requirements: Docker Desktop (or Docker Engine + Compose), Java 21, Maven 3.9+.

Start PostgreSQL and the API:

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`.

Create a booking:

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"eventId":"concert-2026","seatId":"A-1","customerId":"customer-1"}'
```

List bookings:

```bash
curl http://localhost:8080/api/bookings
```

Health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

## Run tests

```bash
mvn test
```

The Testcontainers integration test starts a disposable PostgreSQL container. Docker must be available.

## Run the independent concurrency probe

With the application running and a clean database:

```bash
bash scripts/concurrent-booking-test.sh
```

The script uses only `curl` and Python 3. It sends many simultaneous booking attempts for the same event/seat, then checks the returned HTTP statuses and the booking list.

Expected invariant: **exactly one confirmed booking for the target event and seat**.

The baseline implementation intentionally uses an application-level availability check followed by an insert. This is a known check-then-act pattern: the separate operations are not an atomic reservation. Whether the race reproduces depends on scheduling and environment; run it multiple times and record the observed result. Do not claim a race occurred unless the output demonstrates it.

## Experiment protocol

To make the article defensible:

1. Record the initial commit SHA.
2. Run `mvn test` and save the complete output.
3. Start the service with an empty database.
4. Run the concurrency script several times; save its output and the database/API state.
5. Record the exact number of attempts, successful responses, conflict responses, and persisted bookings.
6. Only after observing the behavior, make a fix in a separate commit.
7. Add a regression test that proves the invariant under concurrent transactions.
8. Re-run the entire suite and the probe. Record the results and elapsed time.

Do not use generated tests as the only oracle. The independent concurrency probe checks the externally visible invariant.

## API

- `POST /api/bookings` — create a booking
- `GET /api/bookings` — list bookings
- `GET /api/bookings/{id}` — retrieve a booking
- `GET /actuator/health` — health check

A duplicate seat booking is represented as `409 Conflict` when the database uniqueness constraint rejects it.

## Important experiment notes

- The database has a unique constraint on `(event_id, seat_id)`. This guarantees that the database cannot persist two bookings for the same seat, even if concurrent requests both pass the initial availability check.
- The baseline service intentionally does not translate every possible persistence race into a polished domain response yet. The probe may therefore expose a consistency/API error-handling issue rather than duplicate persisted rows.
- The concurrency test is an independent test tool, not proof of a bug. The evidence must come from an actual run.
- Capture exact logs and results before writing first-person claims.

## License

MIT
