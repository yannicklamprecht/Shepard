CREATE TABLE IF NOT EXISTS shepard_data.modlog
(
    guild_id bigint NOT NULL,
    channel_id bigint,
    CONSTRAINT modlog_pkey PRIMARY KEY (guild_id)
);

CREATE OR REPLACE FUNCTION shepard_func.set_modLog(
    _guild_id bigint,
    _channel_id bigint)
    RETURNS void
    LANGUAGE plpgsql
    COST 100
    VOLATILE

AS
$$
BEGIN

    INSERT INTO shepard_data.modlog(guild_id, channel_id)
    VALUES (_guild_id, _channel_id)
    ON CONFLICT (guild_id)
        DO UPDATE
            set channel_id = _channel_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_data.delete_modlog(_guild_id bigint) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE FROM shepard_data.modlog WHERE guild_id = _guild_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.get_modlog(_guild_id bigint) RETURNS BIGINT
    LANGUAGE plpgsql
AS
$$
DECLARE
    result BIGINT;
BEGIN
    SELECT channel_id
    INTO result
    FROM shepard_data.modlog
    WHERE guild_id = _guild_id;

    RETURN result;
END
$$;

CREATE TABLE IF NOT EXISTS shepard_data.badwords
(
    guild_id bigint NOT NULL,
    badwords character varying[] COLLATE pg_catalog."default",
    CONSTRAINT badwords_pkey PRIMARY KEY (guild_id)
);
CREATE OR REPLACE FUNCTION shepard_func.get_badwords(
    _guild_id bigint)
    RETURNS varchar[]
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS
$$
DECLARE
    result varchar[];
BEGIN
    SELECT badwords
    INTO result
    FROM shepard_data.badwords
    WHERE guild_id = _guild_id;

    RETURN result;
END
$$;

CREATE OR REPLACE FUNCTION shepard_func.add_badword(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $$
BEGIN

    insert into shepard_data.badwords(guild_id, badwords)
    VALUES(_guild_id, ARRAY[lower(_word)])
    on CONFLICT (guild_id)
        DO
            UPDATE SET badwords =
                           (select
                                array_agg(dist_words)
                            from
                                (SELECT
                                     DISTINCT words as dist_words
                                 from
                                     unnest(array_append(badwords.badwords, lower(_word)::varchar)) as words
                                ) a);

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.remove_badword(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS $BODY$
BEGIN

    UPDATE shepard_data.badwords
    SET badwords = array_remove(badwords.badwords, lower(_word)::varchar)
    WHERE guild_id = _guild_id;

END
$BODY$;

CREATE TABLE IF NOT EXISTS shepard_data.registert_prefix
(
    guild_id bigint NOT NULL,
    prefixes character varying[] COLLATE pg_catalog."default",
    CONSTRAINT registert_prefix_pkey PRIMARY KEY (guild_id)
);

CREATE OR REPLACE FUNCTION shepard_func.get_registert_prefix(
    _guild_id bigint)
    RETURNS varchar[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$$
DECLARE
    result varchar[];
BEGIN
    SELECT prefixes
    INTO result
    FROM shepard_data.registert_prefix
    WHERE guild_id = _guild_id;

    RETURN result;
END
$$;

CREATE OR REPLACE FUNCTION shepard_func.add_registert_prefix(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$$
BEGIN

    insert into shepard_data.registert_prefix(guild_id, prefixes)
    VALUES(_guild_id, ARRAY[lower(_word)])
    on CONFLICT (guild_id)
        DO
            UPDATE SET prefixes =
                           (select
                                array_agg(dist_words)
                            from
                                (SELECT
                                     DISTINCT words as dist_words
                                 from
                                     unnest(array_append(registert_prefix.prefixes, lower(_word)::varchar)) as words
                                ) a);

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.remove_registert_prefix(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS $$
BEGIN

    UPDATE shepard_data.registert_prefix
    SET prefixes = array_remove(registert_prefix.prefixes, lower(_word)::varchar)
    WHERE guild_id = _guild_id;

END
$$;

CREATE TABLE IF NOT EXISTS shepard_data.temp_ban
(
    guild_id bigint NOT NULL,
    user_id bigint NOT NULL,
    unban_date timestamp without time zone,
    CONSTRAINT temp_bans_pkey PRIMARY KEY (guild_id, user_id)
);

CREATE OR REPLACE FUNCTION shepard_func.add_temp_ban(
    _guild_id bigint,
    _user_id bigint,
    _time character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $$
BEGIN
    INSERT INTO shepard_data.temp_ban(guild_id, user_id, unban_date)
VALUES (_guild_id, _user_id, now() + _time::interval);
END
$$;

CREATE OR REPLACE FUNCTION shepard_func.get_temp_ban()
    RETURNS TABLE(guild_id bigint, user_id bigint)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS $$
BEGIN

    RETURN QUERY SELECT guild_id, user_id
                 FROM shepard_data.temp_ban
                 WHERE
                         unban_date <= NOW();

END
$$;
