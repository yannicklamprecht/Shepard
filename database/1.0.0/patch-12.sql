ALTER TABLE shepard_data.invite_tracker
    ADD IF NOT EXISTS code TEXT;

ALTER TABLE shepard_data.invite_tracker
    DROP COLUMN IF EXISTS edited;

ALTER TABLE shepard_data.invite_tracker
    DROP COLUMN IF EXISTS joined;

CREATE UNIQUE INDEX IF NOT EXISTS invite_tracker_guild_id_user_id_created_uindex
    ON shepard_data.invite_tracker (guild_id ASC, user_id ASC, created DESC);

DROP FUNCTION IF EXISTS shepard_func.log_invite(_guild_id BIGINT, _user_id BIGINT, _refer_id BIGINT, _source TEXT);

CREATE OR REPLACE FUNCTION shepard_func.log_invite(_guild_id BIGINT, _user_id BIGINT, _refer_id BIGINT, _source TEXT,
                                                   _code TEXT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT INTO shepard_data.invite_tracker (guild_id, user_id, refer_id, source, code)
    VALUES (_guild_id, _user_id, _refer_id, _source, _code)
    ON CONFLICT (guild_id, user_id, created)
        DO NOTHING;
END
$$;
