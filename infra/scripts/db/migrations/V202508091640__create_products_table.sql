create table if not exists products (
    id uuid primary key,
    name text not null unique,
    created_at timestamptz not null default now()
);