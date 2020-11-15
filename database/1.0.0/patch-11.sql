ALTER TABLE shepard_data.greetings
    ALTER COLUMN message SET DEFAULT 'Welcome {user_mention}';

CREATE TABLE invited_tracker (
                                 guild_id BIGINT,
                                 user_id  BIGINT,
                                 refer_id BIGINT,
                                 source   TEXT,
                                 created  TIMESTAMP DEFAULT now(),
                                 edited   TIMESTAMP DEFAULT now(),
                                 joined   INTEGER   DEFAULT 1
);

CREATE INDEX invited_tracker_guild_id_refer_id_index
    ON invited_tracker (guild_id, refer_id);

CREATE UNIQUE INDEX invited_tracker_pk
    ON invited_tracker (guild_id, user_id, refer_id);

CREATE UNIQUE INDEX invited_tracker_guild_id_user_id_uindex
    ON invited_tracker (guild_id, user_id);


CREATE OR REPLACE FUNCTION shepard_func.log_invite(_guild_id BIGINT, _user_id BIGINT, _refer_id BIGINT, _source TEXT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT INTO shepard_data.invited_tracker (guild_id, user_id, refer_id, source)
    VALUES (_guild_id, _user_id, _refer_id, _source)
    ON CONFLICT (guild_id, user_id)
        DO UPDATE
        SET refer_id = _refer_id, edited = now(), source = _source, joined = excluded.joined + 1;

END
$$;

ALTER TABLE shepard_data.invites
    ADD user_id BIGINT;

DROP FUNCTION IF EXISTS shepard_func.get_invites(_guild_id BIGINT);

CREATE OR REPLACE FUNCTION shepard_func.get_invites(_guild_id BIGINT)
    RETURNS TABLE
            (
                inv_code   CHARACTER VARYING,
                inv_source CHARACTER VARYING,
                inv_used   INTEGER,
                role_id    BIGINT,
                user_id    BIGINT
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT i.code,
                        i.name,
                        i.count,
                        i.role_id,
                        i.user_id
                 FROM shepard_data.invites i
                 WHERE guild_id = _guild_id
                 ORDER BY count DESC;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.add_invite(_guild_id BIGINT, _code VARCHAR, _name VARCHAR, _count INTEGER);

CREATE OR REPLACE FUNCTION shepard_func.add_invite(_guild_id BIGINT, _user_id BIGINT, _code CHARACTER VARYING,
                                                   _name CHARACTER VARYING,
                                                   _count INTEGER DEFAULT 0) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT INTO shepard_data.invites(guild_id, user_id, code, name, count)
    VALUES (_guild_id, _user_id, _code, _name, _count)
    ON CONFLICT (code)
        DO UPDATE
        SET guild_id = _guild_id, user_id = _user_id, code = _code, name = _name, count = _count;

END
$$;

