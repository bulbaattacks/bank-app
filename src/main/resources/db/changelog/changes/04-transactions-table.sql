CREATE TABLE IF NOT EXISTS bank.transactions (
    id                bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    from_card         bigint NOT NULL REFERENCES bank.cards(id),
    to_card           bigint NOT NULL REFERENCES bank.cards(id),
    user_id           bigint NOT NULL REFERENCES bank.users(id),
    amount            bigint NOT NULL DEFAULT 0,
    tx_date           date NOT NULL,
    CONSTRAINT different_cards CHECK (from_card <> to_card)
);
