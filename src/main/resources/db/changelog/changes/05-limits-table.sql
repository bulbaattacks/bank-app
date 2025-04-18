CREATE TABLE IF NOT EXISTS bank.limits (
    id                bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    card_id           bigint NOT NULL REFERENCES bank.cards(id),
    daily_limit       bigint,
    monthly_limit     bigint
);
