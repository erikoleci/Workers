# BuildCrew Manager

Cloud-based SaaS platform for construction subcontractors (plastering, drywall,
painting, facades, insulation, flooring, tiling, interior finishing — any m²-based
trade) to manage crews, workers, clients, projects, daily production, and payroll
across multiple construction sites.

**Stack:** Vue 3 + Vite + Vuetify 3 + TypeScript + Pinia (frontend) · Java + Quarkus +
Maven (backend) · PostgreSQL/Supabase with Row Level Security (multi-tenant).

## Status — Phase 1: Architecture, Auth, Core Entities ✅

- [x] Database schema (`database/schema.sql`) — 12 tables, multi-tenant RLS
- [x] JWT authentication (login, RSA-signed tokens, role claims)
- [x] Core entities + full CRUD: Workers, Clients, Crews (+ members)
- [x] Dashboard summary endpoint
- [x] Frontend shell: routing, auth guard, Vuetify (light/dark), PWA
- [ ] Projects, Daily Reports/Targets, Payroll, Expenses, Notifications, Reports,
      Analytics — **Phase 2 onward**, per the phased build plan.

## Running locally

### Backend (buildcrew-backend/)
```bash
cd buildcrew-backend
./generate-jwt-keys.sh          # generates privateKey.pem / publicKey.pem (gitignored)
cp .env.example .env            # fill in your Supabase Postgres credentials
mvn quarkus:dev                 # requires Maven — not verified in this environment,
                                 # pom.xml is valid; run `mvn -v` to confirm your install
```
Then run `database/schema.sql` against your Supabase project (SQL Editor) before
starting the backend, so the tables the entities map to actually exist.

### Frontend (buildcrew-frontend/)
```bash
cd buildcrew-frontend
npm install
cp .env.example .env            # VITE_API_URL=http://localhost:8080
npm run dev
```
Verified in this environment: `npm install`, `vue-tsc --noEmit` (typecheck), and
`vite build` (including PWA service worker generation) all pass cleanly.

## What was missing before this commit

The previous commits pushed only hand-written source files (entities, services,
views, stores) without the project skeleton needed to actually build or run them:
no `pom.xml`, no `package.json`/`vite.config.ts`, no `main.ts`/`App.vue`/router
wiring, no JWT keys. This commit adds all of it and verifies the frontend builds
end-to-end; the backend `pom.xml` is written to match the exact Quarkus extensions
the existing code already imports (Panache, RESTEasy Reactive, SmallRye JWT,
Elytron bcrypt) but could not be verified with `mvn` in this environment — please
confirm `mvn quarkus:dev` starts cleanly on your machine as the next step.
