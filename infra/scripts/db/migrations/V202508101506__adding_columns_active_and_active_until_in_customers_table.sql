alter table customers add column if not exists active boolean;
alter table customers add column if not exists active_until timestamptz;