# AGENTS.md — StrattonApp (FCT / DAW project)

Entry point for any AI agent starting from scratch. Read this first; the detailed audit and roadmap are in `docs/auditoria-2026-10.md`.

## What it is
Management app for an energy advisory firm: clients (supply points: electricity and gas), comercializadoras, asesorías, employees, and an energy-price comparator.
- `Backend/` — Spring Boot 3.2.3 on **Java 17**, Maven (`mvnw`), MySQL in Docker or H2 for local dev. JWT auth, Spring Security, Swagger at `/swagger-ui.html`. Package `com.StrattonApp.Backend`.
- `Frontend/` — Angular 17 (`^17.3`), Bootstrap + SweetAlert, served by nginx in Docker.
- `docs/` — academic documentation (the official project deliverable is a PDF with a required structure; see `docs/README.md`). Do not change the official PDFs.
- `design-system/strattonapp/` — design tokens and assets.
- `.github/workflows/ci.yml` — CI on every push: `mvn -B -q verify` (tests with H2) and `npm ci && npm run build && npm test`.

## Commands
```bash
cd Backend && mvn spring-boot:run                          # API on :8080, MySQL on localhost:3306
cd Backend && mvn spring-boot:run -Dspring-boot.run.profiles=h2   # no database needed
cd Backend && mvn -B -q verify                             # tests (run before committing backend changes)
cd Frontend && npm install && npm start                    # :4200
cd Frontend && npm run build && npm test                   # before committing frontend changes
docker compose up --build                                  # everything on http://localhost
```
Node 20+, JDK 17 (the audit found JDK 21 needed a Lombok fix; keep JDK 17 as the target).

## Hard rules
1. **Seed data is for local development only.** `SEED_DATA=true` creates demo users and clients **only when the database is empty**; `SEED_RESET=true` wipes and reseeds. Never run with those flags against real data.
2. **Secrets:** `.env` is not committed (see `.env.example`). `JWT_SECRET`, database passwords and `REGISTRO_ABIERTO` come from the environment. Do not print secrets in logs or answers.
3. **Don't reopen closed audit findings without checking.** Before saying something is broken or fixed, check the *Estado* column in `docs/auditoria-2026-10.md`. As of the last update, 170 findings are "Corregido"; the open ones are marked "Parcial" (for example RF-4 postal-code lookup, per-supply-point plans, JWT refresh and revocation).
4. **Tokens** are currently kept in `localStorage`. Changing that to a safer storage is a deliberate task, not a drive-by fix.
5. **Commits:** small, English conventional messages, **no co-author or AI attribution trailers**.
6. **Language:** UI text and the docs are in Spanish; code, identifiers and commit messages are in English.

## Known traps
- Roles: the audit found role/roles mismatches before; check both the `@PreAuthorize` rules and the frontend guards when touching permissions.
- `application.properties` has `app.seed.enabled=${SEED_DATA:true}` by default. Keep that in mind when you deploy.
- The comparator reads invoices in the browser (pdf.js + OCR); nothing is sent to an AI service. Keep it that way.
