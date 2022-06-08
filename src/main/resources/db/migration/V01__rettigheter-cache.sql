CREATE TABLE rettigheter_cache
(
    id               BIGSERIAL PRIMARY KEY,
    norsk_ident      VARCHAR UNIQUE           NOT NULL,
    rettigheter_json JSONB                    NOT NULL,
    expires_after    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
