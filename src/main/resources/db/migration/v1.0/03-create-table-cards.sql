--changeset rassul:03-create-table-cards

CREATE TABLE IF NOT EXISTS cards (
    id SERIAL PRIMARY KEY,
    encrypted_card_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name VARCHAR(100),
    expiry_date DATE,
    status VARCHAR(20),
    balance NUMERIC(19,2),
    user_id BIGINT,
    CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users(id)
);