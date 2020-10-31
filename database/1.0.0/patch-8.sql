create table if not exists shepard_data.quote_settings
(
    guild_id   BIGINT
        constraint quote_settings_pk
            primary key,
    channel_id BIGINT
);

