CREATE TABLE IF NOT EXISTS bank.cards (
    id                bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    number            varchar(64) NOT NULL UNIQUE,
    user_id           bigint NOT NULL REFERENCES bank.users(id),
    validity_period   date NOT NULL,
    status            varchar(64) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
    is_atm            BOOLEAN DEFAULT FALSE
);

