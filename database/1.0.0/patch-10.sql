ALTER TABLE shepard_data.greetings
    ADD IF NOT EXISTS role BIGINT;


DROP FUNCTION IF EXISTS shepard_func.get_greeting_data(VARCHAR);

CREATE OR REPLACE FUNCTION shepard_func.get_greeting_data(_guild_id BIGINT)
    RETURNS TABLE
            (
                message         TEXT,
                private_message TEXT,
                channel_id      BIGINT,
                role            BIGINT
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT g.message,
                        g.private_message,
                        g.channel_id,
                        g.role
                 FROM shepard_data.greetings g
                 WHERE g.guild_id = _guild_id;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.remove_greeting_channel(VARCHAR);

DROP FUNCTION IF EXISTS shepard_func.set_greeting_channel(VARCHAR, VARCHAR);

CREATE OR REPLACE FUNCTION shepard_func.set_greeting_channel(_guild_id BIGINT, _channel_id BIGINT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT
    INTO shepard_data.greetings(guild_id, channel_id)
    VALUES (_guild_id, _channel_id)
    ON CONFLICT (guild_id)
        DO UPDATE SET channel_id = _channel_id;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.set_greeting_text(VARCHAR, TEXT);

CREATE OR REPLACE FUNCTION shepard_func.set_greeting_message(_guild_id BIGINT, _message TEXT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT
    INTO shepard_data.greetings(guild_id, message)
    VALUES (_guild_id, _message)
    ON CONFLICT (guild_id)
        DO UPDATE SET message = _message;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.set_join_role(_guild_id BIGINT, _role_id BIGINT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT
    INTO shepard_data.greetings(guild_id, role)
    VALUES (_guild_id, _role_id)
    ON CONFLICT (guild_id)
        DO UPDATE SET role = _role_id;

END
$$;

ALTER TABLE shepard_data.greetings
    ADD IF NOT EXISTS private_message TEXT;

CREATE OR REPLACE FUNCTION shepard_func.set_private_greeting_message(_guild_id BIGINT, _message TEXT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT
    INTO shepard_data.greetings(guild_id, private_message)
    VALUES (_guild_id, _message)
    ON CONFLICT (guild_id)
        DO UPDATE SET private_message = _message;

END
$$;

-- rework invite queries
DROP FUNCTION IF EXISTS shepard_func.update_invites(VARCHAR, CHARACTER VARYING[]);

CREATE OR REPLACE FUNCTION shepard_func.update_invites(_guild_id BIGINT, _codes CHARACTER VARYING[]) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE
    FROM shepard_data.invites
    WHERE guild_id = _guild_id::BIGINT
      AND code NOT IN (SELECT unnest(_codes));

END
$$;


DROP FUNCTION IF EXISTS shepard_func.upcount_invite(VARCHAR, VARCHAR);

CREATE OR REPLACE FUNCTION upcount_invite(_guild_id BIGINT, _code CHARACTER VARYING) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    UPDATE shepard_data.invites
    SET count = count + 1
    WHERE guild_id = _guild_id
      AND code = _code;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.remove_invite(VARCHAR, VARCHAR);

CREATE OR REPLACE FUNCTION shepard_func.remove_invite(_guild_id BIGINT, _code CHARACTER VARYING) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE
    FROM shepard_data.invites
    WHERE guild_id = _guild_id
      AND code = _code;

END
$$;

ALTER TABLE shepard_data.invites
    ALTER COLUMN code TYPE VARCHAR(10) USING code::VARCHAR(10);

DROP FUNCTION IF EXISTS shepard_func.get_invites(VARCHAR);

CREATE OR REPLACE FUNCTION shepard_func.get_invites(_guild_id BIGINT)
    RETURNS TABLE
            (
                inv_code   CHARACTER VARYING,
                inv_source CHARACTER VARYING,
                inv_used   INTEGER,
                role_id    BIGINT
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT i.code,
                        i.name,
                        i.count,
                        i.role_id
                 FROM shepard_data.invites i
                 WHERE guild_id = _guild_id
                 ORDER BY count DESC;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.add_invite(VARCHAR, VARCHAR, VARCHAR, INTEGER);

CREATE OR REPLACE FUNCTION shepard_func.add_invite(_guild_id BIGINT, _code CHARACTER VARYING,
                                        _name CHARACTER VARYING DEFAULT 'undefined'::CHARACTER VARYING,
                                        _count INTEGER DEFAULT 0) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT INTO shepard_data.invites(guild_id, code, name, count)
    VALUES (_guild_id, _code, _name, _count)
    ON CONFLICT (code)
        DO UPDATE
        SET guild_id = _guild_id, code = _code, name = _name, count = _count;

END
$$;

ALTER TABLE shepard_data.invites
    ADD IF NOT EXISTS role_id BIGINT;

CREATE OR REPLACE FUNCTION shepard_func.add_invite_role(_guild_id BIGINT, _code CHARACTER VARYING, _role_id BIGINT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT INTO shepard_data.invites(guild_id, code, role_id)
    VALUES (_guild_id, _code, _role_id)
    ON CONFLICT (code)
        DO UPDATE
        SET guild_id = _guild_id, code = _code, role_id = _role_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.invite_registered(_guild_id BIGINT, _code CHARACTER VARYING) RETURNS BOOLEAN
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN exists(SELECT 1 FROM shepard_data.invites WHERE guild_id = _guild_id AND code = _code);

END
$$;

