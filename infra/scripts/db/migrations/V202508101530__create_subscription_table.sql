create table if not exists subscription(
    customer_id uuid not null,
    product_id uuid not null,
    primary key (customer_id, product_id)
);