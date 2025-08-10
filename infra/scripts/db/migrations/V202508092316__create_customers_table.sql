create table if not exists customers (
    id uuid primary key,
    name text not null,
    email text unique not null,
    created_at timestamptz not null default now()
)