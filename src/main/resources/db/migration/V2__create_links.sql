create table links
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT      NOT NULL,
    original_url TEXT        NOT NULL,
    short_code   VARCHAR(20) NOT NULL UNIQUE,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_links_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE
                CASCADE

);

CREATE INDEX idx_links_user_id
    ON links (user_id);