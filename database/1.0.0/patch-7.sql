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
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS
$BODY$
    INSERT INTO shepard_data.modlog (guild_id, channel_id)
    VALUES (_guild_id, _channel_id)
    ON CONFLICT (guild_id)
        DO UPDATE
            set channel_id = _channel_id
$BODY$;
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
$BODY$
DECLARE
    result BIGINT;
BEGIN
	SELECT channel_id
	INTO result
	FROM shepard_data.modlog
	WHERE guild_id = _guild_id;

	RETURN result;
END
$BODY$;
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
$BODY$
DECLARE
	result varchar[];
BEGIN
SELECT badwords
INTO result
FROM shepard_data.badwords
WHERE guild_id = _guild_id;

RETURN result;
END
$BODY$;
CREATE OR REPLACE FUNCTION shepard_func.add_badword(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
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
$BODY$;
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
$BODY$
DECLARE
	result varchar[];
BEGIN
SELECT prefixes
INTO result
FROM shepard_data.registert_prefix
WHERE guild_id = _guild_id;

RETURN result;
END
$BODY$;
CREATE OR REPLACE FUNCTION shepard_func.add_registert_prefix(
    _guild_id bigint,
    _word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
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
$BODY$;
CREATE OR REPLACE FUNCTION shepard_func.remove_registert_prefix(
	_guild_id bigint,
	_word character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
BEGIN

UPDATE shepard_data.registert_prefix
SET prefixes = array_remove(registert_prefix.prefixes, lower(_word)::varchar)
WHERE guild_id = _guild_id;

END
$BODY$;
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

AS $BODY$
INSERT INTO shepard_data.temp_ban(guild_id, user_id, unban_date)
VALUES (_guild_id, _user_id, now() + _time::intervall)
$BODY$;
CREATE OR REPLACE FUNCTION shepard_func.get_temp_ban()
    RETURNS TABLE(guild_id bigint, user_id bigint)
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
BEGIN

RETURN QUERY SELECT guild_id, user_id
FROM shepard_data.temp_ban
WHERE
unban_date <= NOW();

END
$BODY$;


CREATE TABLE shepard_data.sauce_request_limit
(
    guild_id    bigint                      NOT NULL,
    user_id     bigint                      NOT NULL,
    "timestamp" timestamp without time zone NOT NULL
);

DROP INDEX IF EXISTS shepard_data.date_order;

CREATE INDEX date_order
    ON shepard_data.sauce_request_limit USING btree
        ("timestamp" DESC NULLS FIRST)
    TABLESPACE pg_default;

CREATE OR REPLACE FUNCTION shepard_func.get_sauce_requests(_guild_id bigint, _user_id bigint, seconds_long int,
                                                           seconds_short int)
    RETURNS TABLE
            (
                total_long       int,
                wait_total_long  int,
                total_short      int,
                wait_total_short int,
                guild_long       int,
                wait_guild_long  int,
                guild_short      int,
                wait_guild_short int,
                user_long        int,
                wait_user_long   int,
                user_short       int,
                wait_user_short  int
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    _temp_result_name text;
BEGIN

    _temp_result_name = 'tmp_requests_' || floor(random() * 1000000000);

    execute format($ex$
        create temporary table %1$s
            (
                total_long       int,
                wait_total_long  int,
                total_short      int,
                wait_total_short int,
                guild_long       int,
                wait_guild_long  int,
                guild_short      int,
                wait_guild_short int,
                user_long        int,
                wait_user_long   int,
                user_short       int,
                wait_user_short  int
            )
            on commit drop;

    Insert into %1$s
        select count(*)::int                                                                                    as total_long,
           0,
           (count(*)
            FILTER (WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval)) ::int          as total_short,
           0,
           (count(*) FILTER ( WHERE guild_id = %5$L::bigint))::int                                                 as guild_long,
           0,
           (count(*) FILTER ( WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval
               AND
                                    guild_id = %5$L::bigint))::int                                                 as guild_short,
           0,
           (count(*) FILTER ( WHERE user_id = %4$L::bigint))::int                                                   as user_long,
           0,
           (count(*) FILTER ( WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval
               AND
                                    user_id = %4$L::bigint))::int                                                   as user_short,
           0
        from shepard_data.sauce_request_limit
        where timestamp > now() - (%3$L::varchar || ' seconds')::interval;
    $ex$, _temp_result_name, seconds_short, seconds_long, _user_id, _guild_id);

    Return query
    execute format($ex$
                    select
                        a.total_long,
                        case
                            when a.total_long != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%3$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%3$L::varchar || ' seconds')::interval)
                            else 0
                            end,
                        a.total_short,
                        case
                            when a.total_short != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%2$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval)
                            else 0
                            end,
                        a.guild_long,
                        case
                            when a.guild_long != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%3$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%3$L::varchar || ' seconds')::interval
                                    AND guild_id = %5$L::bigint)
                            else 0
                            end,
                        a.guild_short,
                        case
                            when a.guild_short != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%2$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval
                                    AND guild_id = %5$L::bigint)
                            else 0
                            end,
                        a.user_long,
                        case
                            when a.user_long != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%3$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%3$L::varchar || ' seconds')::interval
                                    AND user_id = %4$L::bigint)
                            else 0
                            end,
                        a.user_short,
                        case
                            when a.user_short != 0 then
                                (Select (EXTRACT(EPOCH from
                                                 min(timestamp) -
                                                 (now() - (%2$L::varchar || ' seconds')::interval)))::int
                                 from shepard_data.sauce_request_limit
                                 WHERE timestamp > now() - (%2$L::varchar || ' seconds')::interval
                                    AND user_id = %4$L::bigint)
                            else 0
                            end
                 from %1$s a;
    $ex$, _temp_result_name, seconds_short, seconds_long, _user_id, _guild_id);
END
$$;

CREATE OR REPLACE FUNCTION shepard_func.clear_sauce_results() RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE
    FROM shepard_data.sauce_request_limit
    where timestamp < now() - '7 day'::interval;

END
$$;


CREATE OR REPLACE FUNCTION shepard_func.add_sauce_request(_guild_id bigint, _user_id bigint) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform shepard_func.clear_sauce_results();

    Insert Into shepard_data.sauce_request_limit(guild_id, user_id, timestamp)
    VALUES (_guild_id, _user_id, now());

END
$$;