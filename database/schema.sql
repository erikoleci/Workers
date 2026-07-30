-- ============================================================
-- BuildCrew Manager — Database Schema (PostgreSQL / Supabase)
-- ============================================================

-- EXTENSIONS
create extension if not exists "uuid-ossp";

-- ============================================================
-- COMPANIES (tenant root)
-- ============================================================
create table companies (
  id uuid primary key default uuid_generate_v4(),
  name text not null,
  subscription_plan text not null default 'trial',
  created_at timestamptz not null default now()
);

-- ============================================================
-- USERS
-- ============================================================
create table users (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  name text not null,
  email text not null unique,
  password_hash text not null,
  role text not null check (role in ('owner','manager','crew_leader')),
  phone text,
  status text not null default 'active' check (status in ('active','inactive')),
  created_at timestamptz not null default now()
);
create index idx_users_company on users(company_id);

-- ============================================================
-- WORKERS
-- ============================================================
create table workers (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  full_name text not null,
  phone text,
  position text,
  pay_type text not null check (pay_type in ('daily','per_m2')),
  daily_salary numeric(10,2),
  price_per_m2 numeric(10,2),
  employment_date date,
  status text not null default 'active' check (status in ('active','inactive')),
  created_at timestamptz not null default now()
);
create index idx_workers_company on workers(company_id);

-- ============================================================
-- CLIENTS
-- ============================================================
create table clients (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  company_name text not null,
  contact_person text,
  phone text,
  email text,
  address text,
  created_at timestamptz not null default now()
);
create index idx_clients_company on clients(company_id);

-- ============================================================
-- PROJECTS
-- ============================================================
create table projects (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  client_id uuid references clients(id) on delete set null,
  name text not null,
  address text,
  start_date date,
  deadline date,
  contract_value numeric(12,2),
  total_m2 numeric(10,2),
  assigned_crew_id uuid, -- FK added after crews table
  status text not null default 'active' check (status in ('active','delayed','completed','cancelled')),
  created_at timestamptz not null default now()
);
create index idx_projects_company on projects(company_id);
create index idx_projects_client on projects(client_id);

-- ============================================================
-- CREWS
-- ============================================================
create table crews (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  name text not null,
  leader_id uuid references users(id) on delete set null,
  current_project_id uuid references projects(id) on delete set null,
  status text not null default 'active' check (status in ('active','inactive')),
  created_at timestamptz not null default now()
);
create index idx_crews_company on crews(company_id);

alter table projects
  add constraint fk_projects_assigned_crew
  foreign key (assigned_crew_id) references crews(id) on delete set null;

-- ============================================================
-- CREW MEMBERS (N:M workers <-> crews)
-- ============================================================
create table crew_members (
  id uuid primary key default uuid_generate_v4(),
  crew_id uuid not null references crews(id) on delete cascade,
  worker_id uuid not null references workers(id) on delete cascade,
  joined_at timestamptz not null default now(),
  unique (crew_id, worker_id)
);
create index idx_crew_members_crew on crew_members(crew_id);
create index idx_crew_members_worker on crew_members(worker_id);

-- ============================================================
-- DAILY TARGETS
-- ============================================================
create table daily_targets (
  id uuid primary key default uuid_generate_v4(),
  project_id uuid not null references projects(id) on delete cascade,
  target_date date not null,
  target_m2 numeric(10,2) not null,
  unique (project_id, target_date)
);
create index idx_daily_targets_project on daily_targets(project_id);

-- ============================================================
-- DAILY REPORTS
-- ============================================================
create table daily_reports (
  id uuid primary key default uuid_generate_v4(),
  project_id uuid not null references projects(id) on delete cascade,
  crew_id uuid not null references crews(id) on delete cascade,
  report_date date not null,
  completed_m2 numeric(10,2) not null,
  worked_hours numeric(5,2),
  comments text,
  created_by uuid references users(id) on delete set null,
  created_at timestamptz not null default now(),
  unique (project_id, crew_id, report_date)
);
create index idx_daily_reports_project on daily_reports(project_id);
create index idx_daily_reports_crew on daily_reports(crew_id);
create index idx_daily_reports_date on daily_reports(report_date);

-- ============================================================
-- PAYROLL
-- ============================================================
create table payroll (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  worker_id uuid not null references workers(id) on delete cascade,
  period_start date not null,
  period_end date not null,
  base_amount numeric(10,2) not null default 0,
  bonuses numeric(10,2) not null default 0,
  deductions numeric(10,2) not null default 0,
  final_amount numeric(10,2) not null default 0,
  status text not null default 'pending' check (status in ('pending','paid')),
  created_at timestamptz not null default now()
);
create index idx_payroll_company on payroll(company_id);
create index idx_payroll_worker on payroll(worker_id);

-- ============================================================
-- EXPENSES
-- ============================================================
create table expenses (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  project_id uuid references projects(id) on delete set null,
  category text not null,
  amount numeric(10,2) not null,
  expense_date date not null,
  description text,
  created_at timestamptz not null default now()
);
create index idx_expenses_company on expenses(company_id);
create index idx_expenses_project on expenses(project_id);

-- ============================================================
-- NOTIFICATIONS
-- ============================================================
create table notifications (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  user_id uuid references users(id) on delete cascade,
  type text not null,
  message text not null,
  is_read boolean not null default false,
  created_at timestamptz not null default now()
);
create index idx_notifications_company on notifications(company_id);
create index idx_notifications_user on notifications(user_id);

-- ============================================================
-- AUDIT LOGS
-- ============================================================
create table audit_logs (
  id uuid primary key default uuid_generate_v4(),
  company_id uuid not null references companies(id) on delete cascade,
  user_id uuid references users(id) on delete set null,
  action text not null,
  entity text not null,
  entity_id uuid,
  created_at timestamptz not null default now()
);
create index idx_audit_logs_company on audit_logs(company_id);

-- ============================================================
-- ROW LEVEL SECURITY (Multi-Tenant Isolation)
-- ============================================================
-- Convention: auth.jwt() ->> 'company_id' holds the tenant id from JWT claims.

alter table users enable row level security;
alter table workers enable row level security;
alter table clients enable row level security;
alter table projects enable row level security;
alter table crews enable row level security;
alter table crew_members enable row level security;
alter table daily_targets enable row level security;
alter table daily_reports enable row level security;
alter table payroll enable row level security;
alter table expenses enable row level security;
alter table notifications enable row level security;
alter table audit_logs enable row level security;

create policy tenant_isolation_users on users
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_workers on workers
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_clients on clients
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_projects on projects
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_crews on crews
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_crew_members on crew_members
  using (
    exists (
      select 1 from crews c
      where c.id = crew_members.crew_id
      and c.company_id = (auth.jwt() ->> 'company_id')::uuid
    )
  );

create policy tenant_isolation_daily_targets on daily_targets
  using (
    exists (
      select 1 from projects p
      where p.id = daily_targets.project_id
      and p.company_id = (auth.jwt() ->> 'company_id')::uuid
    )
  );

create policy tenant_isolation_daily_reports on daily_reports
  using (
    exists (
      select 1 from projects p
      where p.id = daily_reports.project_id
      and p.company_id = (auth.jwt() ->> 'company_id')::uuid
    )
  );

create policy tenant_isolation_payroll on payroll
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_expenses on expenses
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_notifications on notifications
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);

create policy tenant_isolation_audit_logs on audit_logs
  using (company_id = (auth.jwt() ->> 'company_id')::uuid);
