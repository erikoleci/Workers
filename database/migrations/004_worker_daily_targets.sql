-- Run this once in Supabase SQL Editor.
-- Individual per-worker daily m2 target (separate from the existing
-- project-level daily_targets table - this is "this worker should do
-- X m2 today", set by the manager/owner).

create table if not exists worker_daily_targets (
  id uuid primary key default uuid_generate_v4(),
  worker_id uuid not null references workers(id) on delete cascade,
  target_date date not null,
  target_m2 numeric(10,2) not null,
  set_by uuid references users(id) on delete set null,
  created_at timestamptz not null default now(),
  unique (worker_id, target_date)
);
create index idx_worker_daily_targets_worker on worker_daily_targets(worker_id);
create index idx_worker_daily_targets_date on worker_daily_targets(target_date);
