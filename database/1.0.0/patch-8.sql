CREATE TABLE IF NOT EXISTS shepard_data.quote_settings
(
    guild_id   BIGINT
        CONSTRAINT quote_settings_pk
            PRIMARY KEY,
    channel_id BIGINT
);

ALTER TABLE shepard_data.invites
    ALTER COLUMN code TYPE VARCHAR(10) USING code::VARCHAR(10);

ALTER TABLE shepard_data.invites
    ALTER COLUMN name TYPE VARCHAR(100) USING name::VARCHAR(100);

ALTER TABLE shepard_data.quotes
    ADD source TEXT;

ALTER TABLE shepard_data.quotes
    ADD created TIMESTAMP DEFAULT now();

ALTER TABLE shepard_data.quotes
    ADD edited TIMESTAMP DEFAULT now();

DROP FUNCTION IF EXISTS shepard_func.get_quotes(_guild_id VARCHAR);

CREATE OR REPLACE FUNCTION shepard_func.get_quotes(_guild_id BIGINT)
    RETURNS TABLE
            (
                quote_id INTEGER,
                orig_id  INTEGER,
                quote    TEXT,
                source   TEXT,
                created  TIMESTAMP,
                edited   TIMESTAMP
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT row_number() OVER (ORDER BY q.quote_id)::INTEGER AS quote_id,
                        q.quote_id                                       AS orig_id,
                        q.quote,
                        q.source,
                        q.created,
                        q.edited
                 FROM shepard_data.quotes q
                 WHERE guild_id = _guild_id
                 ORDER BY orig_id;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.get_quote_count_by_keyword(_guild_id CHARACTER VARYING, _keyword CHARACTER VARYING);
CREATE OR REPLACE FUNCTION shepard_func.get_quote_count_by_keyword(_guild_id BIGINT, _keyword CHARACTER VARYING) RETURNS INTEGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    result INTEGER;
BEGIN

    SELECT count(1)
    INTO result
    FROM shepard_data.quotes
    WHERE guild_id = _guild_id
      AND (quote ILIKE '%' || _keyword || '%'
        OR source ILIKE '%' || _keyword || '%');

    RETURN result;


END
$$;

DROP FUNCTION IF EXISTS shepard_func.get_quotes_by_keyword(_guild_id CHARACTER VARYING, _keyword CHARACTER VARYING);
CREATE OR REPLACE FUNCTION shepard_func.get_quotes_by_keyword(_guild_id BIGINT, _keyword CHARACTER VARYING)
    RETURNS TABLE
            (
                quote_id INTEGER,
                orig_id  INTEGER,
                quote    TEXT,
                source   TEXT,
                created  TIMESTAMP,
                edited   TIMESTAMP
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT x.quote_id,
                        x.orig_id,
                        x.quote,
                        x.source,
                        x.created,
                        x.edited
                 FROM (SELECT row_number() OVER (ORDER BY q.quote_id) AS quote_id,
                              q.quote_id                              AS orig_id,
                              q.quote,
                              q.source,
                              q.created,
                              q.edited
                       FROM shepard_data.quotes q
                       WHERE guild_id = _guild_id
                       ORDER BY orig_id
                      ) x
                 WHERE x.quote ILIKE '%' || _keyword || '%'
                    OR x.source ILIKE '%' || _keyword || '%';

END
$$;

DROP FUNCTION IF EXISTS shepard_func.get_quote_count(CHARACTER VARYING);
CREATE OR REPLACE FUNCTION shepard_func.get_quote_count(_guild_id BIGINT) RETURNS INTEGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    result INTEGER;
BEGIN

    SELECT count(1)
    INTO result
    FROM shepard_data.quotes
    WHERE guild_id = _guild_id;

    RETURN result;


END
$$;

DROP FUNCTION IF EXISTS shepard_func.add_quote(_guild_id VARCHAR, _quote TEXT);
CREATE FUNCTION shepard_func.add_quote(_guild_id BIGINT, _quote TEXT, _source TEXT) RETURNS INTEGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    result INTEGER;
BEGIN

    INSERT
    INTO shepard_data.quotes(guild_id, quote, source)
    VALUES (_guild_id, _quote, _source);

    SELECT quote_id FROM shepard_data.quotes ORDER BY created DESC LIMIT 1 INTO result;

    RETURN result;
END
$$;

DROP FUNCTION IF EXISTS shepard_func.alter_quote(_guild_id VARCHAR, _quote_id INTEGER, _quote TEXT);
CREATE FUNCTION shepard_func.alter_quote(_guild_id BIGINT, _quote_id INTEGER, _quote TEXT, _source TEXT) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    UPDATE shepard_data.quotes
    SET quote  = coalesce(_quote, quote),
        source = coalesce(_source, source),
        edited = now()
    WHERE quote_id =
          (SELECT orig_id
           FROM
               shepard_func.get_quotes(_guild_id)
           WHERE quote_id = _quote_id
          );

END
$$;

DROP FUNCTION IF EXISTS shepard_func.remove_quote(_guild_id VARCHAR, _quote_id INTEGER);
CREATE FUNCTION shepard_func.remove_quote(_guild_id BIGINT, _quote_id INTEGER) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE
    FROM shepard_data.quotes
    WHERE quote_id =
          (
              SELECT orig_id
              FROM shepard_func.get_quotes(_guild_id)
              WHERE quote_id = _quote_id
          );

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.get_quote(_guild_id BIGINT, _quote_id INTEGER)
    RETURNS TABLE
            (
                quote_id INTEGER,
                orig_id  INTEGER,
                quote    TEXT,
                source   TEXT,
                created  TIMESTAMP,
                edited   TIMESTAMP
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT q.quote_id,
                        q.orig_id,
                        q.quote,
                        q.source,
                        q.created,
                        q.edited
                 FROM shepard_func.get_quotes(_guild_id) q
                 WHERE _quote_id = q.quote_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.get_quote(_quote_id INTEGER)
    RETURNS TABLE
            (
                quote_id INTEGER,
                orig_id  INTEGER,
                quote    TEXT,
                source   TEXT,
                created  TIMESTAMP,
                edited   TIMESTAMP
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT q.quote_id,
                        q.orig_id,
                        q.quote,
                        q.source,
                        q.created,
                        q.edited
                 FROM shepard_func.get_quotes((SELECT guild_id FROM shepard_data.quotes WHERE quote_id = _quote_id)) q
                 WHERE q.orig_id = _quote_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.set_quote_channel(_guild_id BIGINT, _channel_id BIGINT)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN

    INSERT
    INTO
        shepard_data.quote_settings
    (guild_id, channel_id)
    VALUES
    (_guild_id, _channel_id)
    ON CONFLICT(guild_id) DO UPDATE SET channel_id = _channel_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.get_quote_channel(_guild_id BIGINT)
    RETURNS BIGINT
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN (SELECT channel_id FROM shepard_data.quote_settings WHERE guild_id = _guild_id);

END
$$;
