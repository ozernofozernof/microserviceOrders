create table if not exists users (
    id uuid primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(20) not null
    );

create table if not exists orders (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    description text not null,
    status varchar(20) not null,
    created_at timestamp not null
    );

create index if not exists idx_orders_user on orders(user_id);