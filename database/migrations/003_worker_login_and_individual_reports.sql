-- Run this once in Supabase SQL Editor.
-- Enables individual worker login + per-worker daily m2 reporting
-- (replaces crew-level reporting as the source for per_m2 payroll).

alter table workers add column if not exists username text unique;
alter table workers add column if not exists password_hash text;

create table if not exists worker_daily_reports (
  id uuid primary key default uuid_generate_v4(),
  worker_id uuid not null references workers(id) on delete cascade,
  project_id uuid not null references projects(id) on delete cascade,
  report_date date not null,
  completed_m2 numeric(10,2) not null,
  comments text,
  created_at timestamptz not null default now(),
  unique (worker_id, project_id, report_date)
);
create index idx_worker_daily_reports_worker on worker_daily_reports(worker_id);
create index idx_worker_daily_reports_project on worker_daily_reports(project_id);
create index idx_worker_daily_reports_date on worker_daily_reports(report_date);
