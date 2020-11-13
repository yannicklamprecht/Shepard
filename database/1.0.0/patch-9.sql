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
                 FROM (SELECT row_number() OVER (ORDER BY q.quote_id)::INTEGER AS quote_id,
                              q.quote_id                                       AS orig_id,
                              q.quote,
                              q.source,
                              q.created,
                              q.edited
                       FROM shepard_data.quotes q
                       WHERE guild_id = _guild_id
                       ORDER BY orig_id
                      ) x
                 WHERE x.quote ILIKE '%' || _keyword || '%'
                    OR x.source ILIKE '%' || _keyword || '%'
                    OR (isnumeric(_keyword) AND x.quote_id = _keyword::INTEGER);

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
                 FROM shepard_func.get_quotes((SELECT q2.guild_id FROM shepard_data.quotes q2 WHERE q2.quote_id = _quote_id)) q
                 WHERE q.orig_id = _quote_id;

END
$$;

CREATE OR REPLACE FUNCTION isnumeric(text) RETURNS BOOLEAN AS $$
DECLARE x NUMERIC;
BEGIN
    x = $1::NUMERIC;
    RETURN TRUE;
EXCEPTION WHEN others THEN
    RETURN FALSE;
END;
$$
    STRICT
    LANGUAGE plpgsql IMMUTABLE;