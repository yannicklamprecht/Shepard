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
CREATE TABLE shepard_data.badwords
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
