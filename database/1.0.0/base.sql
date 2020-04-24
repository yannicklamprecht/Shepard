--
-- PostgreSQL database dump
--

-- Dumped from database version 11.7 (Debian 11.7-2.pgdg90+1)
-- Dumped by pg_dump version 12.2 (Debian 12.2-2.pgdg90+1)

-- Started on 2020-04-19 00:44:18 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 13 (class 2615 OID 18620)
-- Name: shepard_api; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA shepard_api;


--
-- TOC entry 12 (class 2615 OID 16581)
-- Name: shepard_data; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA shepard_data;


--
-- TOC entry 11 (class 2615 OID 16514)
-- Name: shepard_func; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA shepard_func;


--
-- TOC entry 8 (class 2615 OID 16670)
-- Name: shepard_settings; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA shepard_settings;


--
-- TOC entry 786 (class 1247 OID 16550)
-- Name: adress; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.adress AS
(
    adress_id integer,
    orig_id   integer,
    name      character varying(32),
    adress    character varying(128)
);


--
-- TOC entry 789 (class 1247 OID 16553)
-- Name: context_data; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.context_data AS
(
    admin_only             boolean,
    users_with_permissions character varying(18)[],
    nsfw                   boolean,
    user_check_state       boolean,
    user_list_type         character varying(20),
    user_list              character varying(18)[],
    guild_check_state      boolean,
    guild_list_type        character varying(20),
    guild_list             character varying(18)[],
    user_cooldown          integer,
    guild_cooldown         integer
);


--
-- TOC entry 792 (class 1247 OID 16556)
-- Name: greeting; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.greeting AS
(
    message    text,
    channel_id character varying(18)
);


--
-- TOC entry 797 (class 1247 OID 16559)
-- Name: invite; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.invite AS
(
    inv_code   character varying(7),
    inv_used   integer,
    inv_source character varying(50)
);


--
-- TOC entry 800 (class 1247 OID 16562)
-- Name: minecraft_link; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.minecraft_link AS
(
    discord_id character varying(18),
    uuid       character varying(32)
);


--
-- TOC entry 803 (class 1247 OID 16565)
-- Name: prefix; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.prefix AS
(
    guild_id character varying(18),
    prefix   character varying(3)
);


--
-- TOC entry 806 (class 1247 OID 16568)
-- Name: quote; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.quote AS
(
    quote_id integer,
    quote    text,
    orig_id  integer
);


--
-- TOC entry 809 (class 1247 OID 16571)
-- Name: ticket_category; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.ticket_category AS
(
    category_id      character varying,
    creation_message text,
    keyword          character varying(32)
);


--
-- TOC entry 878 (class 1247 OID 16774)
-- Name: ticket_type; Type: TYPE; Schema: shepard_func; Owner: -
--

CREATE TYPE shepard_func.ticket_type AS
(
    category_id      character varying,
    creation_message text,
    keyword          character varying
);


--
-- TOC entry 866 (class 1247 OID 16719)
-- Name: list_types; Type: TYPE; Schema: shepard_settings; Owner: -
--

CREATE TYPE shepard_settings.list_types AS ENUM (
    'WHITELIST',
    'BLACKLIST'
    );


--
-- TOC entry 393 (class 1255 OID 18621)
-- Name: kudos_globaluser(integer, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_globaluser(_page integer, _pagesize integer)
    RETURNS TABLE
            (
                user_id bigint,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        SELECT a.user_id::BIGINT,
               a.score
        FROM shepard_func.get_rubber_points_global_top_score(_page * _pagesize) a
        LIMIT _pagesize
        OFFSET
        (_page - 1) * _pagesize;

END
$$;


--
-- TOC entry 334 (class 1255 OID 18633)
-- Name: kudos_globaluser(bigint[], integer, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_globaluser(user_ids bigint[], _page integer, _pagesize integer)
    RETURNS TABLE
            (
                user_id bigint,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        SELECT a.user_id::BIGINT,
               a.score
        FROM (
                 select g.user_id::varchar, total_score as score
                 from (SELECT a.user_id::varchar, sum(a.score)::bigint as total_score
                       from shepard_data.rubber_points a
                       group by a.user_id) as g
                 where g.user_id = ANY (user_ids)
                 order by score desc
                 limit _pagesize * _page
             ) a
        LIMIT _pagesize
        OFFSET
        (_page - 1) * _pagesize;

END
$$;


--
-- TOC entry 396 (class 1255 OID 18646)
-- Name: kudos_globaluser_filter(bigint[], integer, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_globaluser_filter(_user_ids bigint[], _page integer, _pagesize integer)
    RETURNS TABLE
            (
                rank    bigint,
                user_id bigint,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT a.rank,
               a.user_id::BIGINT,
               a.score
        FROM (
                 select a.rank, a.user_id, a.score
                 FROM (
                          select ROW_NUMBER() OVER (ORDER BY total_score desc) as rank, g.user_id, total_score as score
                          from (SELECT a.user_id, sum(a.score)::bigint as total_score
                                from shepard_data.rubber_points a
                                group by a.user_id) as g
                          order by score desc
                      ) a
                 where a.user_id = ANY (_user_ids)
             ) a
        LIMIT _pagesize
        OFFSET
        (_page - 1) * _pagesize;

END
$$;


--
-- TOC entry 383 (class 1255 OID 18636)
-- Name: kudos_globaluser_filter_pagecount(bigint[], integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_globaluser_filter_pagecount(user_ids bigint[], _pagesize integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    SELECT ceil(count(*) / _pagesize::numeric)
    FROM (
             select distinct g.user_id
             from shepard_data.rubber_points g
             where g.user_id = ANY (user_ids)
         ) a
    INTO result;

    RETURN result;
END
$$;


--
-- TOC entry 333 (class 1255 OID 18631)
-- Name: kudos_globaluser_pagecount(integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_globaluser_pagecount(_pagesize integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    SELECT ceil(count(*) / _pagesize::numeric)
    from (
             select distinct user_id
             from shepard_data.rubber_points
         ) a
    into
        result;

    RETURN result;
END
$$;


--
-- TOC entry 384 (class 1255 OID 18638)
-- Name: kudos_guild(bigint, integer, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_guild(_guild_id bigint, _page integer, _pagesize integer)
    RETURNS TABLE
            (
                user_id bigint,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        SELECT a.user_id::BIGINT,
               a.score
        FROM shepard_func.get_rubber_points_top_score(_guild_id::varchar, _page * _pagesize) a
        LIMIT _pagesize
        OFFSET
        (_page - 1) * _pagesize;

END
$$;


--
-- TOC entry 397 (class 1255 OID 18645)
-- Name: kudos_guild_filter(bigint[], bigint, integer, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_guild_filter(_user_ids bigint[], _guild_id bigint, _page integer, _pagesize integer)
    RETURNS TABLE
            (
                rank    bigint,
                user_id bigint,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT a.rank,
               a.user_id::BIGINT,
               a.score
        FROM (
                 select a.rank, a.user_id, a.score
                 FROM (
                          select ROW_NUMBER() OVER (ORDER BY total_score desc) as rank, g.user_id, total_score as score
                          from (
                                   SELECT a.user_id,
                                          sum(a.score)::bigint as total_score
                                   from shepard_data.rubber_points a
                                   where a.guild_id = _guild_id
                                   group by a.user_id) as g
                          order by score desc
                      ) a
                 where a.user_id = ANY (_user_ids)
             ) a
        LIMIT _pagesize
        OFFSET
        (_page - 1) * _pagesize;

END
$$;


--
-- TOC entry 395 (class 1255 OID 18642)
-- Name: kudos_guild_filter_pagecount(bigint[], bigint, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_guild_filter_pagecount(_user_ids bigint[], _guild_id bigint, _pagesize integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    SELECT ceil(count(*) / _pagesize::numeric)
    FROM (
             select distinct g.user_id
             from shepard_data.rubber_points g
             where g.user_id = ANY (_user_ids)
               and g.guild_id = _guild_id
         ) a
    INTO result;

    RETURN result;
END
$$;


--
-- TOC entry 394 (class 1255 OID 18640)
-- Name: kudos_guild_pagecount(bigint, integer); Type: FUNCTION; Schema: shepard_api; Owner: -
--

CREATE FUNCTION shepard_api.kudos_guild_pagecount(_guild_id bigint, _pagesize integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    SELECT ceil(count(*) / _pagesize::numeric)
    from (
             select distinct user_id
             from shepard_data.rubber_points a
             where guild_id = _guild_id
         ) a
    into
        result;

    RETURN result;

END
$$;


--
-- TOC entry 379 (class 1255 OID 17410)
-- Name: add_and_get_jackpot(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_and_get_jackpot(_guild_id character varying, _score integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    jackpot integer;
BEGIN

    Insert into shepard_data.kudo_gamble_jackpot(guild_id, jackpot)
    VALUES (_guild_id::bigint, greatest(_score, 0))
    ON CONFLICT(guild_id)
        DO UPDATE
        set jackpot = _score +
                      (select j.jackpot from shepard_data.kudo_gamble_jackpot j where j.guild_id = _guild_id::bigint);

    select j.jackpot from shepard_data.kudo_gamble_jackpot j where guild_id = _guild_id::bigint into jackpot;

    return jackpot;

END
$$;


--
-- TOC entry 276 (class 1255 OID 16740)
-- Name: add_changelog_role(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_changelog_role(_guild_id character varying, _role_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN


    insert into shepard_data.role_changelog(guild_id, observing_roles)
    VALUES (_guild_id::BIGINT, array [_role_id::BIGINT])
    on CONFLICT (guild_id)
        DO UPDATE
        SET observing_roles = (select array_agg(distinct x)
                               from unnest(array_append(shepard_data.role_changelog.observing_roles,
                                                        _role_id::BIGINT)) x);

END
$$;


--
-- TOC entry 274 (class 1255 OID 16741)
-- Name: add_context_guild(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_context_guild(_context_name character varying, _guild_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    insert into shepard_settings.context_guild(context_id, guild_id)
    VALUES (_context_id, _guild_id::BIGINT)
    on conflict (context_id, guild_id)
        do nothing;

END
$$;


--
-- TOC entry 328 (class 1255 OID 16885)
-- Name: add_context_role_permission(character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_context_role_permission(_context_name character varying, _guild_id character varying,
                                                         _role_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    insert into shepard_settings.context_role_permission(context_id, role_id, guild_id)
    VALUES (_context_id, _role_id::BIGINT, _guild_id::BIGINT)
    on conflict (role_id, context_id, guild_id)
        do nothing;

END
$$;


--
-- TOC entry 275 (class 1255 OID 16743)
-- Name: add_context_user(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_context_user(_context_name character varying, _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    insert into shepard_settings.context_user(context_id, user_id)
    VALUES (_context_id, _user_id::BIGINT)
    ON CONFLICT
        DO NOTHING;

END
$$;


--
-- TOC entry 277 (class 1255 OID 16742)
-- Name: add_context_user_permission(character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_context_user_permission(_context_name character varying, _guild_id character varying,
                                                         _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    insert into shepard_settings.context_user_permission(context_id, user_id, guild_id)
    VALUES (_context_id, _user_id::BIGINT, _guild_id::BIGINT)
    on conflict (user_id, context_id, guild_id)
        do nothing;

END
$$;


--
-- TOC entry 378 (class 1255 OID 17382)
-- Name: add_free_rubber_points(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_free_rubber_points(_user_id character varying, _amount integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    update shepard_data.free_rubber_points set score = least(score + _amount, 100) where user_id = _user_id::bigint;
END
$$;


--
-- TOC entry 338 (class 1255 OID 17240)
-- Name: add_free_rubber_points(character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_free_rubber_points(_guild_id character varying, _user_id character varying,
                                                    _amount integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    update shepard_data.free_rubber_points
    set score = least(score + _amount, 100)
    where guild_id = _guild_id::bigint
      and user_id = _user_id::bigint;
END
$$;


--
-- TOC entry 342 (class 1255 OID 16910)
-- Name: add_guess_game_image(character varying, character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_guess_game_image(_cropped_image character varying, _full_image character varying,
                                                  _hentai boolean) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.hentai_or_not(cropped_image, full_image, hentai)
    VALUES (_cropped_image, _full_image, _hentai);

END
$$;


--
-- TOC entry 367 (class 1255 OID 16928)
-- Name: add_guess_game_score(character varying, character varying[], integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_guess_game_score(_guild_id character varying, _user_ids character varying[],
                                                  _score integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    u varchar;
BEGIN

    FOREACH u in array _user_ids
        LOOP
            Insert into shepard_data.hentai_or_not_scores(guild_id, user_id, score)
            VALUES (_guild_id::bigint, u::bigint, greatest(_score, 0))
            ON CONFLICT(guild_id, user_id)
                DO UPDATE
                set score = greatest(_score + (select sc.score
                                               from shepard_data.hentai_or_not_scores sc
                                               where sc.guild_id = _guild_id::bigint and user_id = u::bigint), 0);
        end loop;

END
$$;


--
-- TOC entry 278 (class 1255 OID 16744)
-- Name: add_invite(character varying, character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_invite(_guild_id character varying, _code character varying,
                                        _name character varying DEFAULT 'undefined'::character varying,
                                        _count integer DEFAULT 0) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.invites(guild_id, code, name, count)
    VALUES (_guild_id::BIGINT, _code, _name, _count)
    on CONFLICT (code)
        DO UPDATE
        SET guild_id = _guild_id::BIGINT, code = _code, name = _name, count = _count;

END
$$;


--
-- TOC entry 381 (class 1255 OID 17408)
-- Name: add_jackpot(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_jackpot(_guild_id character varying, _score integer) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    jackpot integer;
BEGIN

    Insert into shepard_data.kudo_gamble_jackpot(guild_id, jackpot)
    VALUES (_guild_id::bigint, greatest(_score, 0))
    ON CONFLICT(guild_id)
        DO UPDATE
        set jackpot = _score +
                      (select j.jackpot from shepard_data.kudo_gamble_jackpot j where j.guild_id = _guild_id::bigint);

    select j.jackpot from shepard_data.kudo_gamble_jackpot j where guild_id = _guild_id::bigint into jackpot;

    return jackpot;

END
$$;


--
-- TOC entry 279 (class 1255 OID 16745)
-- Name: add_minecraft_link_code(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_minecraft_link_code(_code character varying, _uuid character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    uuid_save varchar;
BEGIN

    uuid_save = Replace(_uuid, '-', '');

    select shepard_func.remove_old_minecraft_link_codes();

    insert into shepard_data.minecraft_link_codes(code, uuid, expired_at)
    VALUES (_code, uuid_save, now() + '10 minutes'::INTERVAL)
    on CONFLICT (code)
        DO NOTHING;

END
$$;


--
-- TOC entry 340 (class 1255 OID 16951)
-- Name: add_monitoring_address(character varying, character varying, character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_monitoring_address(_guild_id character varying, _address character varying,
                                                    _name character varying, _mcip boolean) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.monitoring_ips(guild_id, name, address, mcip)
    VALUES (_guild_id::BIGINT, _name, _address, _mcip)
    ON CONFLICT(guild_id, address)
        DO UPDATE
        SET name = _name, mcip = _mcip;

END
$$;


--
-- TOC entry 280 (class 1255 OID 16747)
-- Name: add_quote(character varying, text); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_quote(_guild_id character varying, _quote text) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.quotes(guild_id, quote)
    VALUES (_guild_id::BIGINT, _quote);

END
$$;


--
-- TOC entry 372 (class 1255 OID 17271)
-- Name: add_reminder_date(character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_reminder_date(_guild_id character varying, _user_id character varying,
                                               _channel_id character varying, _message character varying,
                                               _date character varying, _time character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    date timestamp;
BEGIN
    date = to_timestamp(_date || ' ' || _time, 'DD.MM. hh24:mi') +
           (extract(year from now())::varchar || ' year'::varchar)::interval;
    if (date < now()) then
        date = date + '1 year'::interval;
    end if;

    insert into shepard_data.reminder(guild_id, user_id, channel_id, message, time)
    VALUES (_guild_id::BIGINT, _user_id::bigint, _channel_id::bigint, _message, date);

END
$$;


--
-- TOC entry 375 (class 1255 OID 17272)
-- Name: add_reminder_interval(character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_reminder_interval(_guild_id character varying, _user_id character varying,
                                                   _channel_id character varying, _message character varying,
                                                   _time character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.reminder(guild_id, user_id, channel_id, message, time)
    VALUES (_guild_id::BIGINT, _user_id::bigint, _channel_id::bigint, _message, now() + _time::interval);

END
$$;


--
-- TOC entry 377 (class 1255 OID 17383)
-- Name: add_rubber_points(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_rubber_points(_user_id character varying, _score integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    Insert into shepard_data.rubber_points(guild_id, user_id, score)
    VALUES (0, _user_id::bigint, greatest(_score, 0))
    ON CONFLICT(guild_id, user_id)
        DO UPDATE
        set score = greatest(_score + (select sc.score
                                       from shepard_data.rubber_points sc
                                       where user_id = _user_id::bigint and sc.guild_id = 0::bigint), 0);

END
$$;


--
-- TOC entry 363 (class 1255 OID 17021)
-- Name: add_rubber_points(character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_rubber_points(_guild_id character varying, _user_id character varying,
                                               _score integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    Insert into shepard_data.rubber_points(guild_id, user_id, score)
    VALUES (_guild_id::bigint, _user_id::bigint, greatest(_score, 0))
    ON CONFLICT(guild_id, user_id)
        DO UPDATE
        set score = greatest(_score + (select sc.score
                                       from shepard_data.rubber_points sc
                                       where user_id = _user_id::bigint and sc.guild_id = _guild_id::bigint), 0);

END
$$;


--
-- TOC entry 345 (class 1255 OID 16830)
-- Name: add_ticket_type(character varying, character varying, text, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.add_ticket_type(_guild_id character varying, _category_id character varying,
                                             _creation_message text, _keyword character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    old_data RECORD;
BEGIN

    select category_id, creation_message
    into old_data
    from shepard_data.ticket_types
    where guild_id = _guild_id::BIGINT
      and keyword = lower(_keyword);

    insert into shepard_data.ticket_types(guild_id, category_id, creation_message, keyword)
    VALUES (_guild_id::BIGINT, coalesce(_category_id::BIGINT, old_data.category_id), _creation_message, lower(_keyword))
    ON CONFLICT(guild_id, keyword)
        DO update
        set category_id      = coalesce(_category_id::BIGINT, old_data.category_id),
            creation_message = coalesce(_creation_message, old_data.creation_message)
    where shepard_data.ticket_types.guild_id = _guild_id::BIGINT
      and shepard_data.ticket_types.keyword = lower(_keyword);

END
$$;


--
-- TOC entry 281 (class 1255 OID 16749)
-- Name: alter_quote(character varying, integer, text); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.alter_quote(_guild_id character varying, _quote_id integer, _quote text) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    update shepard_data.quotes
    set quote = _quote
    where quote_id =
          (select orig_id
           from shepard_func.get_quotes(_guild_id)
           where quote_id = _quote_id
          );

END
$$;


--
-- TOC entry 364 (class 1255 OID 17310)
-- Name: change_hentai_image_flag(character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.change_hentai_image_flag(_link character varying, _flag boolean) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    update shepard_data.hentai_or_not set hentai = _flag where cropped_image = _link or full_image = _link;
END
$$;


--
-- TOC entry 286 (class 1255 OID 16750)
-- Name: create_ticket_channel(character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.create_ticket_channel(_guild_id character varying, _channel_id character varying,
                                                   _ticket_owner_id character varying,
                                                   _keyword character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
BEGIN

    insert into shepard_data.ticket_channel(guild_id, ticket_owner, channel_id, ticket_type_id)
    VALUES (_guild_id::BIGINT,
            _ticket_owner_id::BIGINt,
            _channel_id::BIGINT,
            (select index
             from shepard_data.ticket_types
             where guild_id = _guild_id::BIGINT
               and keyword = lower(_keyword)));
END
$$;


--
-- TOC entry 390 (class 1255 OID 17404)
-- Name: get_and_clear_jackpot(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_and_clear_jackpot(_guild_id character varying) RETURNS bigint
    LANGUAGE plpgsql
AS
$$
DECLARE
    jackpot integer;
BEGIN

    perform shepard_func.add_jackpot(_guild_id, 0);

    select j.jackpot from shepard_data.kudo_gamble_jackpot j where guild_id = _guild_id::bigint into jackpot;

    Update shepard_data.kudo_gamble_jackpot j set jackpot = 0 where j.guild_id = _guild_id::bigint;

    return jackpot;

END
$$;


--
-- TOC entry 351 (class 1255 OID 16863)
-- Name: get_changelog_channel(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_changelog_channel(_guild_id character varying) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar;
BEGIN

    SELECT channel_id::VARCHAR
    INTO result
    FROM shepard_data.role_changelog
    WHERE guild_id = _guild_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 287 (class 1255 OID 16752)
-- Name: get_changelog_roles(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_changelog_roles(_guild_id character varying) RETURNS character varying[]
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
BEGIN

    SELECT observing_roles
    INTO result
    FROM shepard_data.role_changelog
    WHERE guild_id = _guild_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 282 (class 1255 OID 16753)
-- Name: get_channel_ids_by_owner(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_channel_ids_by_owner(_guild_id character varying, _user_id character varying) RETURNS character varying[]
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
BEGIN

    SELECT array_agg(channel_id)
    INTO result
    FROM shepard_data.ticket_channel
    WHERE ticket_owner = _user_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 385 (class 1255 OID 16754)
-- Name: get_context_data(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_context_data(_context_name character varying) RETURNS shepard_func.context_data
    LANGUAGE plpgsql
AS
$$
DECLARE
    result      shepard_func.context_data;
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    select admin_only
    into result.admin_only
    from shepard_settings.context_admin
    where context_id = _context_id;

    select nsfw
    into result.nsfw
    from shepard_settings.context_nsfw
    where context_id = _context_id;

    select state
    into result.user_check_state
    from shepard_settings.context_user_check_state
    where context_id = _context_id;

    select list_type::varchar
    into result.user_list_type
    from shepard_settings.context_user_list_type
    where context_id = _context_id;

    select array_agg(user_id::varchar)
    into result.user_list
    from shepard_settings.context_user
    where context_id = _context_id;

    select state
    into result.guild_check_state
    from shepard_settings.context_guild_check_state
    where context_id = _context_id;

    select list_type::varchar
    into result.guild_list_type
    from shepard_settings.context_guild_list_type
    where context_id = _context_id;

    select array_agg(guild_id::varchar)
    into result.guild_list
    from shepard_settings.context_guild
    where context_id = _context_id;

    select user_cooldown
    into result.user_cooldown
    from shepard_settings.context_cooldown
    where context_id = _context_id;

    select guild_cooldown
    into result.guild_cooldown
    from shepard_settings.context_cooldown
    where context_id = _context_id;

    RETURN result;

END
$$;


--
-- TOC entry 283 (class 1255 OID 16755)
-- Name: get_context_role_permissions(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_context_role_permissions(_context_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                role_id  character varying
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    RETURN QUERY SELECT r.guild_id::varchar, r.role_id::varchar
                 FROM shepard_settings.context_role_permission r
                 WHERE context_id = _context_id;

END
$$;


--
-- TOC entry 284 (class 1255 OID 16756)
-- Name: get_context_user_permissions(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_context_user_permissions(_context_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                user_id  character varying
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    RETURN QUERY SELECT u.guild_id::varchar, u.user_id::varchar
                 FROM shepard_settings.context_user_permission u
                 WHERE context_id = _context_id;

END
$$;


--
-- TOC entry 380 (class 1255 OID 17297)
-- Name: get_expired_reminder(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_expired_reminder()
    RETURNS TABLE
            (
                guild_id   character varying,
                channel_id character varying,
                user_id    character varying,
                message    character varying
            )
    LANGUAGE plpgsql
    ROWS 100
AS
$$
BEGIN

    return query select r.guild_id::varchar, r.channel_id::varchar, r.user_id::varchar, r.message::varchar
                 from shepard_data.reminder r
                 where r.time < now();

END
$$;


--
-- TOC entry 370 (class 1255 OID 16991)
-- Name: get_free_rubber_points(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_free_rubber_points(_guild_id character varying, _user_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
declare
    pre_score integer;
BEGIN
    Insert into shepard_data.free_rubber_points (guild_id, user_id, score)
    values (_guild_id::bigint, _user_id::bigint, 50)
    on conflict
        do nothing;

    select score
    from shepard_data.free_rubber_points
    where user_id = _user_id::bigint
      and guild_id = _guild_id::bigint
    into pre_score;

    return pre_score;
END
$$;


--
-- TOC entry 285 (class 1255 OID 16757)
-- Name: get_greeting_data(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_greeting_data(_guild_id character varying) RETURNS shepard_func.greeting
    LANGUAGE plpgsql
AS
$$
DECLARE
    result_record shepard_func.greeting;
BEGIN

    SELECT message, channel_id::varchar
    INTO result_record.message, result_record.channel_id
    FROM shepard_data.greetings
    WHERE guild_id = _guild_id::BIGINT;

    RETURN result_record;

END
$$;


--
-- TOC entry 358 (class 1255 OID 16969)
-- Name: get_guess_game_global_top_score(integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_guess_game_global_top_score(_limit integer)
    RETURNS TABLE
            (
                user_id character varying,
                score   integer
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query
        select g.user_id::varchar, total_score as score
        from (SELECT a.user_id::varchar, sum(a.score)::integer as total_score
              from shepard_data.hentai_or_not_scores a
              group by a.user_id) as g
        order by score desc
        limit _limit;

END
$$;


--
-- TOC entry 330 (class 1255 OID 16935)
-- Name: get_guess_game_global_user_score(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_guess_game_global_user_score(_user_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    select sum(score) from shepard_data.hentai_or_not_scores where user_id = _user_id::bigint into result;

    return result;
END
$$;


--
-- TOC entry 359 (class 1255 OID 16970)
-- Name: get_guess_game_top_score(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_guess_game_top_score(_guild_id character varying, _limit integer)
    RETURNS TABLE
            (
                user_id character varying,
                score   integer
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query select u.user_id::varchar, u.score
                 from shepard_data.hentai_or_not_scores u
                 where guild_id = _guild_id::bigint
                 order by u.score desc
                 limit _limit;

END
$$;


--
-- TOC entry 331 (class 1255 OID 16934)
-- Name: get_guess_game_user_score(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_guess_game_user_score(_guild_id character varying, _user_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    select score
    from shepard_data.hentai_or_not_scores
    where user_id = _user_id::bigint
      and guild_id = _guild_id::bigint
    into result;

    return result;

END
$$;


--
-- TOC entry 356 (class 1255 OID 16938)
-- Name: get_hentai_image_data(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_hentai_image_data()
    RETURNS TABLE
            (
                cropped_image character varying,
                full_image    character varying,
                hentai        boolean
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY select u.cropped_image, u.full_image, u.hentai
                 from shepard_data.hentai_or_not u
                     offset floor(random() * (select count(*) from shepard_data.hentai_or_not))
                 limit 1;

END
$$;


--
-- TOC entry 366 (class 1255 OID 17311)
-- Name: get_image_set(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_image_set(_link character varying)
    RETURNS TABLE
            (
                cropped_image character varying,
                full_image    character varying,
                hentai        boolean
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    return query select r.cropped_image, r.full_image, r.hentai
                 from shepard_data.hentai_or_not r
                 where r.cropped_image = _link
                    or r.full_image = _link;
END
$$;


--
-- TOC entry 259 (class 1255 OID 16758)
-- Name: get_invites(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_invites(_guild_id character varying)
    RETURNS TABLE
            (
                inv_code   character varying,
                inv_source character varying,
                inv_used   integer
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY SELECT code, name, count
                 FROM shepard_data.invites
                 WHERE guild_id = _guild_id::BIGINT
                 order by count desc;

END
$$;


--
-- TOC entry 343 (class 1255 OID 17371)
-- Name: get_language(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_language()
    RETURNS TABLE
            (
                guild_id character varying,
                prefix   character varying
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    Return QUERY SELECT l.guild_id::varchar, l.locale_code
                 FROM shepard_data.language l;
END
$$;


--
-- TOC entry 365 (class 1255 OID 17372)
-- Name: get_language(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_language(_guild_id character varying) RETURNS character
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar;
BEGIN

    SELECT locale_code
    INTO result
    FROM shepard_data.language
    WHERE guild_id = _guild_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 260 (class 1255 OID 16759)
-- Name: get_minecraft_link_user_id(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_minecraft_link_user_id(_user_id character varying) RETURNS shepard_func.minecraft_link
    LANGUAGE plpgsql
AS
$$
DECLARE
    result_record shepard_func.minecraft_link;
BEGIN

    SELECT user_id::varchar, uuid
    INTO result_record.user_id, result_record.uuid
    FROM shepard_data.minecraft_link
    WHERE user_id = _user_id::BIGINT;

    RETURN result_record;

END
$$;


--
-- TOC entry 296 (class 1255 OID 16761)
-- Name: get_minecraft_link_uuid(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_minecraft_link_uuid(_uuid character varying) RETURNS shepard_func.minecraft_link
    LANGUAGE plpgsql
AS
$$
DECLARE
    result_record shepard_func.minecraft_link;
    uuid_save     varchar;
BEGIN

    uuid_save = Replace(_uuid, '-', '');

    SELECT user_id::varchar, uuid
    INTO result_record.user_id, result_record.uuid
    FROM shepard_data.minecraft_link
    WHERE uuid = uuid_save;

    RETURN result_record;

END
$$;


--
-- TOC entry 261 (class 1255 OID 16760)
-- Name: get_minecraft_link_uuid_by_code(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_minecraft_link_uuid_by_code(_code character varying) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar;
BEGIN

    select remove_old_minecraft_link_codes();

    SELECT uuid
    INTO result
    FROM shepard_data.minecraft_link_codes
    WHERE code ~ _code;

    delete
    from shepard_data.minecraft_link_codes
    where code ~ _code;

    RETURN result;

END
$$;


--
-- TOC entry 368 (class 1255 OID 16962)
-- Name: get_monitoring_addresses_for_guild(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_monitoring_addresses_for_guild(_guild_id character varying)
    RETURNS TABLE
            (
                address_id integer,
                orig_id    integer,
                name       character varying,
                address    character varying,
                mcip       boolean
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform setval('shepard_func.ip_count', 1, false);

    RETURN QUERY select nextval('shepard_func.ip_count')::integer as address_id,
                        i.index                                   as orig_id,
                        i.name                                    as name,
                        i.address                                 as address,
                        i.mcip                                    as mcip
                 from shepard_data.monitoring_ips i
                 where guild_id = _guild_id::BIGINT
                 order by orig_id;

END
$$;


--
-- TOC entry 288 (class 1255 OID 16763)
-- Name: get_monitoring_channel(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_monitoring_channel(_guild_id character varying) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar;
BEGIN

    select channel_id
    into result
    from shepard_data.monitoring_channel
    where guild_id = _guild_id::BIGINT;

    return result;

END
$$;


--
-- TOC entry 289 (class 1255 OID 16764)
-- Name: get_muted_users(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_muted_users()
    RETURNS TABLE
            (
                guild_id character varying,
                user_id  character varying
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    select remove_old_mutes();

    Return Query SELECT guild_id, user_id, unmute_time
                 FROM shepard_data.muted_users
                 WHERE guild_id = guild_id::BIGINT;

END
$$;


--
-- TOC entry 348 (class 1255 OID 16852)
-- Name: get_next_ticket_count(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_next_ticket_count(_guild_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
Declare
    result integer;
BEGIN

    select (counter + 1) % 1000 into result from shepard_data.ticket_count where guild_id = _guild_id::BIGINT;

    result = coalesce(result, 1);

    Insert into shepard_data.ticket_count(guild_id, counter)
    values (_guild_id::BIGINT, result)
    ON CONFLICT (guild_id)
        DO Update
        set counter = result;

    return result;
END
$$;


--
-- TOC entry 389 (class 1255 OID 17390)
-- Name: get_permission_overrides(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_permission_overrides(_context_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                override boolean
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query
        select p.guild_id::varchar,
               p.override
        from shepard_settings.context_permission_override p
        where context_id = shepard_settings.get_or_create_context_id(_context_name);

END
$$;


--
-- TOC entry 290 (class 1255 OID 16765)
-- Name: get_prefix(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_prefix(_guild_id character varying) RETURNS character
    LANGUAGE plpgsql
AS
$$

DECLARE
    result char;
BEGIN

    SELECT prefix
    INTO result
    FROM shepard_data.prefix
    WHERE guild_id = _guild_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 297 (class 1255 OID 16766)
-- Name: get_prefixes(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_prefixes()
    RETURNS TABLE
            (
                guild_id character varying,
                prefix   character varying
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    Return QUERY SELECT p.guild_id::varchar, p.prefix
                 FROM shepard_data.prefix p;
END
$$;


--
-- TOC entry 335 (class 1255 OID 16866)
-- Name: get_quote_count(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_quote_count(_guild_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
declare
    result integer;
BEGIN

    select count(1)
    into result
    from shepard_data.quotes
    where guild_id = _guild_id::BIGINT;

    return result;


END
$$;


--
-- TOC entry 337 (class 1255 OID 16870)
-- Name: get_quote_count_by_keyword(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_quote_count_by_keyword(_guild_id character varying, _keyword character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
declare
    result integer;
BEGIN

    select count(1)
    into result
    from shepard_data.quotes
    where guild_id = _guild_id::BIGINT
      and quote ilike '%' || _keyword || '%';

    return result;


END
$$;


--
-- TOC entry 291 (class 1255 OID 16767)
-- Name: get_quotes(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_quotes(_guild_id character varying)
    RETURNS TABLE
            (
                quote_id integer,
                orig_id  integer,
                quote    text
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform setval('shepard_func.quote_counter', 1, false);

    RETURN QUERY select nextval('shepard_func.quote_counter')::integer as quote_id,
                        shepard_data.quotes.quote_id                   as orig_id,
                        shepard_data.quotes.quote
                 from shepard_data.quotes
                 where guild_id = _guild_id::BIGINT
                 order by orig_id asc;

END
$$;


--
-- TOC entry 336 (class 1255 OID 16868)
-- Name: get_quotes_by_keyword(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_quotes_by_keyword(_guild_id character varying, _keyword character varying)
    RETURNS TABLE
            (
                quote_id integer,
                orig_id  integer,
                quote    text
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform setval('shepard_func.quote_counter', 1, false);

    RETURN QUERY select x.quote_id, x.orig_id, x.quote
                 from (select nextval('shepard_func.quote_counter')::integer as quote_id,
                              shepard_data.quotes.quote_id                   as orig_id,
                              shepard_data.quotes.quote
                       from shepard_data.quotes
                       where guild_id = _guild_id::BIGINT
                       order by orig_id asc) x
                 where x.quote ilike '%' || _keyword || '%';

END
$$;


--
-- TOC entry 392 (class 1255 OID 18609)
-- Name: get_rubber_points_global_top_score(integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_rubber_points_global_top_score(_limit integer)
    RETURNS TABLE
            (
                user_id character varying,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query
        select g.user_id::varchar, total_score as score
        from (SELECT a.user_id::varchar, sum(a.score)::bigint as total_score
              from shepard_data.rubber_points a
              group by a.user_id) as g
        order by score desc
        limit _limit;

END
$$;


--
-- TOC entry 357 (class 1255 OID 16973)
-- Name: get_rubber_points_global_user_score(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_rubber_points_global_user_score(_user_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    select sum(score) from shepard_data.rubber_points where user_id = _user_id::bigint into result;

    return result;
END
$$;


--
-- TOC entry 398 (class 1255 OID 18607)
-- Name: get_rubber_points_top_score(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_rubber_points_top_score(_guild_id character varying, _limit integer)
    RETURNS TABLE
            (
                user_id character varying,
                score   bigint
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query select u.user_id::varchar, u.score
                 from shepard_data.rubber_points u
                 where guild_id = _guild_id::bigint
                 order by u.score desc
                 limit _limit;

END
$$;


--
-- TOC entry 360 (class 1255 OID 16972)
-- Name: get_rubber_points_user_score(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_rubber_points_user_score(_guild_id character varying, _user_id character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN

    select score
    from shepard_data.rubber_points
    where user_id = _user_id::bigint
      and guild_id = _guild_id::bigint
    into result;

    return result;

END
$$;


--
-- TOC entry 349 (class 1255 OID 16860)
-- Name: get_ticket_channel_by_keyword(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_channel_by_keyword(_guild_id character varying, _keyword character varying) RETURNS character varying[]
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
    _index integer;
BEGIN

    _index = (select index
              from shepard_data.ticket_types
              where keyword = lower(_keyword)
                and guild_id = _guild_id::BIGINT);

    SELECT array_agg(channel_id)::varchar[]
    INTO result
    FROM shepard_data.ticket_channel
    WHERE ticket_type_id = _index;

    RETURN result;

END
$$;


--
-- TOC entry 329 (class 1255 OID 16855)
-- Name: get_ticket_channel_owner(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_channel_owner(_guild_id character varying, _channel_id character varying) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar;
BEGIN

    SELECT ticket_owner::varchar
    INTO result
    FROM shepard_data.ticket_channel
    WHERE guild_id = _guild_id::BIGINT
      and channel_id = _channel_id::BIGINT;

    RETURN result;

END
$$;


--
-- TOC entry 295 (class 1255 OID 16768)
-- Name: get_ticket_channel_owner_roles(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_channel_owner_roles(_guild_id character varying, _channel_id character varying) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
BEGIN

    SELECT owner_roles::varchar[]
    INTO result
    FROM shepard_data.ticket_roles
    WHERE ticket_type_id =
          (select ticket_type_id
           from shepard_data.ticket_channel
           where guild_id = _guild_id::BIGINT
             and channel_id = _channel_id::BIGINT
          );

    RETURN result;

END
$$;


--
-- TOC entry 292 (class 1255 OID 16770)
-- Name: get_ticket_owner_roles(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_owner_roles(_guild_id character varying, _keyword character varying) RETURNS character varying[]
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
    _index integer;
BEGIN

    _index =
            (select index
             from shepard_data.ticket_types
             where keyword = lower(_keyword)
               and guild_id = _guild_id::BIGINT
            );

    SELECT owner_roles::varchar[]
    INTO result
    FROM shepard_data.ticket_roles
    WHERE ticket_type_id = _index;

    RETURN result;

END
$$;


--
-- TOC entry 293 (class 1255 OID 16771)
-- Name: get_ticket_support_roles(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_support_roles(_guild_id character varying, _keyword character varying) RETURNS character varying[]
    LANGUAGE plpgsql
AS
$$
DECLARE
    result varchar[];
    _index integer;
BEGIN

    _index = (select index
              from shepard_data.ticket_types
              where keyword = lower(_keyword)
                and guild_id = _guild_id::BIGINT);

    SELECT support_roles::varchar[]
    INTO result
    FROM shepard_data.ticket_roles
    WHERE ticket_type_id = _index;

    RETURN result;

END
$$;


--
-- TOC entry 347 (class 1255 OID 16858)
-- Name: get_ticket_type_by_channel(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_type_by_channel(_guild_id character varying, _channel_id character varying) RETURNS shepard_func.ticket_type
    LANGUAGE plpgsql
AS
$$
DECLARE
    result shepard_func.ticket_type;
BEGIN

    SELECT category_id::varchar, creation_message, keyword
    INTO result.category_id, result.creation_message, result.keyword
    FROM shepard_data.ticket_types
    WHERE guild_id = _guild_id::BIGINT
      and keyword = (SELECT keyword
                     FROM shepard_data.ticket_types
                     WHERE index =
                           (select ticket_type_id
                            from shepard_data.ticket_channel
                            where guild_id = _guild_id::BIGINT
                              and channel_id = _channel_id::BIGINT
                           ));

    RETURN result;
END
$$;


--
-- TOC entry 382 (class 1255 OID 18618)
-- Name: get_ticket_type_by_keyword(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_type_by_keyword(_guild_id character varying, _keyword character varying)
    RETURNS TABLE
            (
                category_id      character varying,
                creation_message text,
                keyword          character varying
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    result shepard_func.ticket_type;
BEGIN
    return query SELECT a.category_id::varchar, a.creation_message, a.keyword
                 FROM shepard_data.ticket_types a
                 WHERE guild_id = _guild_id::BIGINT
                   and a.keyword = lower(_keyword);
END
$$;


--
-- TOC entry 327 (class 1255 OID 16816)
-- Name: get_ticket_types(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_ticket_types(_guild_id character varying)
    RETURNS TABLE
            (
                id               integer,
                orig_id          integer,
                category_id      character varying,
                creation_message text,
                keyword          character varying
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform setval('shepard_func.type_counter', 1, false);

    RETURN QUERY select nextval('shepard_func.type_counter')::integer as id,
                        t.index                                       as orig_id,
                        t.category_id::VARCHAR,
                        t.creation_message,
                        t.keyword
                 from shepard_data.ticket_types t
                 where guild_id = _guild_id::BIGINT
                 order by orig_id asc;

END
$$;


--
-- TOC entry 371 (class 1255 OID 17273)
-- Name: get_user_reminder(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.get_user_reminder(_guild_id character varying, _user_id character varying)
    RETURNS TABLE
            (
                reminder_id   integer,
                orig_id       integer,
                message       text,
                reminder_time timestamp without time zone
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    perform setval('shepard_func.reminder_count', 1, false);

    RETURN QUERY
        select nextval('shepard_func.reminder_count')::integer as reminder_id,
               re.index                                        as orig_id,
               re.message,
               re.time
        from (
                 select *
                 from shepard_data.reminder r
                 where guild_id = _guild_id::BIGINT
                   and user_id = _user_id::bigint
                 order by r.time asc
             ) re;

END
$$;


--
-- TOC entry 294 (class 1255 OID 16777)
-- Name: remove_changelog_channel(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_changelog_channel(_guild_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    Update shepard_data.role_changelog
    set channel_id = null
    where guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 352 (class 1255 OID 16862)
-- Name: remove_changelog_role(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_changelog_role(_guild_id character varying, _role_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.role_changelog(guild_id)
    VALUES (_guild_id::BIGINT)
    on CONFLICT (guild_id)
        DO UPDATE
        SET observing_roles = array_remove(shepard_data.role_changelog.observing_roles, _role_id::BIGINT);

END
$$;


--
-- TOC entry 354 (class 1255 OID 16871)
-- Name: remove_context_guild(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_context_guild(_context_name character varying, _guild_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    delete
    from shepard_settings.context_guild
    where context_id = _context_id
      and guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 312 (class 1255 OID 16780)
-- Name: remove_context_role_permission(character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_context_role_permission(_context_name character varying,
                                                            _guild_id character varying,
                                                            _role_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    delete
    from shepard_settings.context_role_permission
    where context_id = _context_id
      and role_id = _role_id::BIGINT
      and guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 298 (class 1255 OID 16782)
-- Name: remove_context_user(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_context_user(_context_name character varying, _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    delete
    from shepard_settings.context_user
    where context_id = _context_id
      and user_id = _user_id::BIGINT;

END
$$;


--
-- TOC entry 311 (class 1255 OID 16781)
-- Name: remove_context_user_permission(character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_context_user_permission(_context_name character varying,
                                                            _guild_id character varying,
                                                            _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    delete
    from shepard_settings.context_user_permission
    where context_id = _context_id
      and user_id = _user_id::BIGINT
      and guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 376 (class 1255 OID 17294)
-- Name: remove_expired_reminder(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_expired_reminder() RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    delete from shepard_data.reminder r where r.time < now();
END
$$;


--
-- TOC entry 299 (class 1255 OID 16783)
-- Name: remove_greeting_channel(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_greeting_channel(_guild_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    Update shepard_data.greetings
    set channel_id = null
    where guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 341 (class 1255 OID 16911)
-- Name: remove_hentai_image(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_hentai_image(_url character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    DELETE
    from shepard_data.hentai_or_not
    where cropped_image = _url
       or full_image = _url;

END
$$;


--
-- TOC entry 310 (class 1255 OID 16784)
-- Name: remove_invite(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_invite(_guild_id character varying, _code character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.invites
    where guild_id = _guild_id::BIGINT
      and code = _code;

END
$$;


--
-- TOC entry 344 (class 1255 OID 16958)
-- Name: remove_monitoring_address_by_index(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_monitoring_address_by_index(_guild_id character varying, _address_id integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.monitoring_ips
    where index =
          (select orig_id
           from shepard_func.get_monitoring_addresses_for_guild(_guild_id)
           where address_id = _address_id
          );

END
$$;


--
-- TOC entry 300 (class 1255 OID 16786)
-- Name: remove_monitoring_channel(character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_monitoring_channel(_guild_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.monitoring_channel
    where guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 301 (class 1255 OID 16787)
-- Name: remove_mute(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_mute(_guild_id character varying, _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.muted_users
    where guild_id = _guild_id::BIGINT
      and user_id::BIGINT;

END
$$;


--
-- TOC entry 309 (class 1255 OID 16788)
-- Name: remove_old_minecraft_link_codes(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_old_minecraft_link_codes() RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.minecraft_link_codes
    where expired_at < now();

END
$$;


--
-- TOC entry 302 (class 1255 OID 16789)
-- Name: remove_old_mutes(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_old_mutes() RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
BEGIN

    delete
    from shepard_data.muted_users
    where unmute_time < now();

END
$$;


--
-- TOC entry 303 (class 1255 OID 16790)
-- Name: remove_quote(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_quote(_guild_id character varying, _quote_id integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.quotes
    where quote_id =
          (
              select orig_id
              from shepard_func.get_quotes(_guild_id)
              where quote_id = _quote_id
          );

END
$$;


--
-- TOC entry 374 (class 1255 OID 17282)
-- Name: remove_reminder(character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_reminder(_guild_id character varying, _user_id character varying,
                                             _reminder_id integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.reminder
    where index =
          (
              select orig_id
              from shepard_func.get_user_reminder(_guild_id, _user_id)
              where reminder_id = _reminder_id
          );

END
$$;


--
-- TOC entry 305 (class 1255 OID 16793)
-- Name: remove_ticket_channel(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_ticket_channel(_guild_id character varying, _channel_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.ticket_channel
    where guild_id = _guild_id::BIGINT
      and channel_id = _channel_id::BIGINT;

END
$$;


--
-- TOC entry 346 (class 1255 OID 16792)
-- Name: remove_ticket_channel_by_user(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_ticket_channel_by_user(_guild_id character varying, _user_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.ticket_channel
    where guild_id = _guild_id::BIGINT
      and ticket_owner = _user_id::BIGINT;

END
$$;


--
-- TOC entry 308 (class 1255 OID 16795)
-- Name: remove_ticket_type_by_index(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_ticket_type_by_index(_guild_id character varying, _id integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _index integer;
BEGIN

    _index = (select orig_id from shepard_func.get_ticket_types(_guild_id) where id = _id);

    delete
    from shepard_data.ticket_types
    where index = _index;

    delete
    from shepard_data.ticket_channel
    where ticket_type_id = _index;

    delete
    from shepard_data.ticket_roles
    where ticket_type_id = _index;
END
$$;


--
-- TOC entry 304 (class 1255 OID 16796)
-- Name: remove_ticket_type_by_keyword(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.remove_ticket_type_by_keyword(_guild_id character varying, _keyword character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _index integer;
BEGIN

    _index =
            (
                select index
                from shepard_data.ticket_types
                where keyword = lower(_keyword)
                  and guild_id = _guild_id::BIGINT
            );

    delete
    from shepard_data.ticket_types
    where index = _index;

    delete
    from shepard_data.ticket_channel
    where ticket_type_id = _index;

    delete
    from shepard_data.ticket_roles
    where ticket_type_id = _index;

END
$$;


--
-- TOC entry 306 (class 1255 OID 16797)
-- Name: set_changelog_channel(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_changelog_channel(_guild_id character varying, _channel_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.role_changelog(guild_id, channel_id)
    VALUES (_guild_id::BIGINT, _channel_id::BIGINT)
    on CONFLICT (guild_id)
        DO UPDATE SET channel_id = _channel_id::BIGINT;

END
$$;


--
-- TOC entry 307 (class 1255 OID 16798)
-- Name: set_context_admin(character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_admin(_context_name character varying, _state boolean DEFAULT false) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_admin
    set admin_only = _state
    where context_id = _context_id;

END
$$;


--
-- TOC entry 324 (class 1255 OID 16799)
-- Name: set_context_guild_check_active(character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_guild_check_active(_context_name character varying, _state boolean DEFAULT false) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_guild_check_state
    set state = _state
    where context_id = _context_id;

END
$$;


--
-- TOC entry 323 (class 1255 OID 16800)
-- Name: set_context_guild_list_type(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_guild_list_type(_context_name character varying,
                                                         _list_type character varying DEFAULT 'BLACKLIST'::character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    if _list_type = 'BLACKLIST' OR _list_type = 'WHITELIST' THEN
        update shepard_settings.context_guild_list_type
        set list_type = _list_type::shepard_settings.list_types
        where context_id = _context_id;
    END IF;

END
$$;


--
-- TOC entry 319 (class 1255 OID 16801)
-- Name: set_context_nsfw(character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_nsfw(_context_name character varying, _state boolean DEFAULT false) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_nsfw
    set nsfw = _state
    where context_id = _context_id;

END
$$;


--
-- TOC entry 313 (class 1255 OID 16802)
-- Name: set_context_user_check_active(character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_user_check_active(_context_name character varying, _state boolean DEFAULT false) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_user_check_state
    set state = _state
    where context_id = _context_id;

END
$$;


--
-- TOC entry 314 (class 1255 OID 16803)
-- Name: set_context_user_list_type(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_context_user_list_type(_context_name character varying,
                                                        _list_type character varying DEFAULT 'WHITELIST'::character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN
    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    IF _list_type = 'BLACKLIST' OR _list_type = 'WHITELIST' THEN
        update shepard_settings.context_user_list_type
        set list_type = _list_type::shepard_settings.list_types
        where context_id = _context_id;
    END IF;

END
$$;


--
-- TOC entry 320 (class 1255 OID 16804)
-- Name: set_creation_message(character varying, character varying, text); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_creation_message(_guild_id character varying, _keyword character varying,
                                                  _message text) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    update shepard_data.ticket_types
    set creation_message = _message
    where keyword = lower(_keyword)
      and guild_id = _guild_id::BIGINT;

END
$$;


--
-- TOC entry 315 (class 1255 OID 16805)
-- Name: set_greeting_channel(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_greeting_channel(_guild_id character varying, _channel_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.greetings(guild_id, channel_id)
    VALUES (_guild_id::BIGINT, _channel_id::BIGINT)
    on CONFLICT (guild_id)
        DO UPDATE SET channel_id = _channel_id::BIGINT;

END
$$;


--
-- TOC entry 316 (class 1255 OID 16806)
-- Name: set_greeting_text(character varying, text); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_greeting_text(_guild_id character varying, _message text) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.greetings(guild_id, message)
    VALUES (_guild_id::BIGINT, _message)
    on CONFLICT (guild_id)
        DO UPDATE SET message = _message;

END
$$;


--
-- TOC entry 387 (class 1255 OID 17381)
-- Name: set_guild_cooldown(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_guild_cooldown(_context_name character varying, _seconds integer DEFAULT 0) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_cooldown
    set guild_cooldown = _seconds
    where context_id = _context_id;

END
$$;


--
-- TOC entry 339 (class 1255 OID 17367)
-- Name: set_language(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_language(_guild_id character varying, _locale_code character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.language(guild_id, locale_code)
    VALUES (_guild_id::BIGINT, _locale_code)
    on CONFLICT (guild_id)
        DO UPDATE SET locale_code = _locale_code;

END
$$;


--
-- TOC entry 321 (class 1255 OID 16807)
-- Name: set_minecraft_link(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_minecraft_link(_user_id character varying, _uuid character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    uuid_save varchar;
BEGIN

    uuid_save = Replace(_uuid, '-', '');

    insert into shepard_data.minecraft_link(user_id, uuid)
    VALUES (_user_id::BIGINT, uuid_save)
    on CONFLICT (user_id)
        DO UPDATE SET uuid = uuid_save;
END
$$;


--
-- TOC entry 317 (class 1255 OID 16808)
-- Name: set_monitoring_channel(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_monitoring_channel(_guild_id character varying, _channel_id character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.monitoring_channel(guild_id, channel_id)
    VALUES (_guild_id::BIGINT, _channel_id::BIGINT)
    on CONFLICT (guild_id)
        DO UPDATE SET channel_id = _channel_id::BIGINT;

END
$$;


--
-- TOC entry 318 (class 1255 OID 16809)
-- Name: set_muted(character varying, character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_muted(_guild_id character varying, _user_id character varying,
                                       _duration character varying DEFAULT '1 day'::character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _interval INTERVAL;
BEGIN

    _interval = cast(_duration as INTERVAL);
exception
    when no_data_found then _interval = '1 day'::INTERVAL;

    insert into shepard_data.muted_users(guild_id, user_id, unmute_time)
    VALUES (_guild_id::BIGINT, user_id::BIGINT, now() + _interval)
    on CONFLICT (guild_id, user_id)
        DO UPDATE SET unmute_time =
                              (
                                  select unmute_time
                                  from shepard_data.muted_users
                                  where guild_id = _guild_id::BIGINT
                                    and user_id::BIGINT
                              )
                              + _interval;

END
$$;


--
-- TOC entry 388 (class 1255 OID 17389)
-- Name: set_permission_override(character varying, character varying, boolean); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_permission_override(_context_name character varying, _guild_id character varying,
                                                     _override boolean) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_settings.context_permission_override(context_id, guild_id, override)
    values (shepard_settings.get_or_create_context_id(_context_name), _guild_id::bigint, _override)
    on CONFLICT (context_id, guild_id)
        DO UPDATE SET override = _override;

END
$$;


--
-- TOC entry 322 (class 1255 OID 16810)
-- Name: set_prefix(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_prefix(_guild_id character varying, _prefix character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.prefix(guild_id, prefix)
    VALUES (_guild_id::BIGINT, _prefix)
    on CONFLICT (guild_id)
        DO UPDATE SET prefix = _prefix;

END
$$;


--
-- TOC entry 350 (class 1255 OID 16834)
-- Name: set_ticket_owner_roles(character varying, character varying, character varying[]); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_ticket_owner_roles(_guild_id character varying, _keyword character varying,
                                                    _role_ids character varying[]) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _index integer;
BEGIN

    _index =
            (
                select index
                from shepard_data.ticket_types
                where keyword = lower(_keyword)
                  and guild_id = _guild_id::BIGINT
            );

    insert into shepard_data.ticket_roles(guild_id, owner_roles, ticket_type_id)
    VALUES (_guild_id::BIGINT, _role_ids::BIGINT[], _index)
    on CONFLICT (ticket_type_id)
        DO UPDATE SET owner_roles = _role_ids::BIGINT[];

END
$$;


--
-- TOC entry 353 (class 1255 OID 16836)
-- Name: set_ticket_support_roles(character varying, character varying, character varying[]); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_ticket_support_roles(_guild_id character varying, _keyword character varying,
                                                      _role_ids character varying[]) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _index integer;
BEGIN

    _index =
            (
                select index
                from shepard_data.ticket_types
                where keyword = lower(_keyword)
                  and guild_id = _guild_id::BIGINT
            );

    insert into shepard_data.ticket_roles(guild_id, support_roles, ticket_type_id)
    VALUES (_guild_id::BIGINT, _role_ids::BIGINT[], _index)
    on CONFLICT (ticket_type_id)
        DO UPDATE SET support_roles = _role_ids::BIGINT[];

END
$$;


--
-- TOC entry 386 (class 1255 OID 17380)
-- Name: set_user_cooldown(character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.set_user_cooldown(_context_name character varying, _seconds integer DEFAULT 0) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    _context_id integer;
BEGIN

    _context_id = shepard_settings.get_or_create_context_id(_context_name);

    update shepard_settings.context_cooldown
    set user_cooldown = _seconds
    where context_id = _context_id;

END
$$;


--
-- TOC entry 391 (class 1255 OID 17391)
-- Name: try_take_complete_rubber_points(character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.try_take_complete_rubber_points(_guild_id character varying, _user_id character varying,
                                                             _points integer) RETURNS boolean
    LANGUAGE plpgsql
AS
$$
declare
    free_points  integer;
    guild_points integer;
BEGIN
    free_points = shepard_func.get_free_rubber_points(_guild_id, _user_id);

    if free_points >= _points then
        update shepard_data.free_rubber_points
        set score = free_points - _points
        where guild_id = _guild_id::bigint
          and user_id = _user_id::bigint;
        return true;
    end if;

    guild_points = shepard_func.get_rubber_points_user_score(_guild_id, _user_id);

    if free_points + guild_points > _points then
        update shepard_data.free_rubber_points
        set score = 0
        where guild_id = _guild_id::bigint and user_id = _user_id::bigint;
        update shepard_data.rubber_points
        set score = guild_points - (_points - free_points)
        where guild_id = _guild_id::bigint
          and user_id = _user_id::bigint;
        return true;
    end if;

    return false;
END
$$;


--
-- TOC entry 373 (class 1255 OID 16990)
-- Name: try_take_rubber_points(character varying, character varying, integer); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.try_take_rubber_points(_guild_id character varying, _user_id character varying,
                                                    _points integer) RETURNS boolean
    LANGUAGE plpgsql
AS
$$
declare
    pre_score integer;
BEGIN
    pre_score = shepard_func.get_rubber_points_user_score(_guild_id, _user_id);

    if pre_score - _points >= 0 then
        update shepard_data.rubber_points
        set score = pre_score - _points
        where guild_id = _guild_id::bigint
          and user_id = _user_id::bigint;
        return true;
    else
        return false;
    end if;

END
$$;


--
-- TOC entry 369 (class 1255 OID 16992)
-- Name: upcount_free_rubber_points(); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.upcount_free_rubber_points() RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    update shepard_data.free_rubber_points set score = least(score + 1, 100);
END
$$;


--
-- TOC entry 325 (class 1255 OID 16813)
-- Name: upcount_invite(character varying, character varying); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.upcount_invite(_guild_id character varying, _code character varying) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    update shepard_data.invites
    set count = count + 1
    where guild_id = _guild_id::BIGINT
      and code = _code;

END
$$;


--
-- TOC entry 326 (class 1255 OID 16814)
-- Name: update_invites(character varying, character varying[]); Type: FUNCTION; Schema: shepard_func; Owner: -
--

CREATE FUNCTION shepard_func.update_invites(_guild_id character varying, _codes character varying[]) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    delete
    from shepard_data.invites
    where guild_id = _guild_id::BIGINT
      AND code NOT IN (select unnest(_codes));


END
$$;


--
-- TOC entry 332 (class 1255 OID 16815)
-- Name: get_or_create_context_id(character varying); Type: FUNCTION; Schema: shepard_settings; Owner: -
--

CREATE FUNCTION shepard_settings.get_or_create_context_id(_context_name character varying) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    result integer;
BEGIN
    -- Check if a context with this name was created. If not create it
    if not (select exists(select 1 from shepard_settings.context_id where context_name = _context_name))
    then
        insert into shepard_settings.context_id (context_name) VALUES (_context_name);
    end if;

    SELECT id
    INTO result
    FROM shepard_settings.context_id c
    WHERE context_name = _context_name;

    -- Make sure, that a default setting for each table ws created.
    insert into shepard_settings.context_admin (context_id) values (result) on conflict (context_id) do nothing;
    insert into shepard_settings.context_user_check_state (context_id)
    values (result)
    on conflict (context_id) do nothing;
    insert into shepard_settings.context_user_list_type (context_id)
    values (result)
    on conflict (context_id) do nothing;
    insert into shepard_settings.context_nsfw (context_id) values (result) on conflict (context_id) do nothing;
    insert into shepard_settings.context_guild_check_state (context_id)
    values (result)
    on conflict (context_id) do nothing;
    insert into shepard_settings.context_guild_list_type (context_id)
    values (result)
    on conflict (context_id) do nothing;
    insert into shepard_settings.context_cooldown (context_id) values (result) on conflict (context_id) do nothing;

    RETURN result;
END
$$;


SET default_tablespace = '';

--
-- TOC entry 258 (class 1259 OID 18668)
-- Name: version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.version
(
    version character varying,
    patch   character varying
);


--
-- TOC entry 248 (class 1259 OID 16984)
-- Name: free_rubber_points; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.free_rubber_points
(
    guild_id bigint            NOT NULL,
    user_id  bigint            NOT NULL,
    score    integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 16588)
-- Name: greetings; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.greetings
(
    guild_id   bigint NOT NULL,
    channel_id bigint,
    message    text
);


--
-- TOC entry 243 (class 1259 OID 16903)
-- Name: hentai_or_not; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.hentai_or_not
(
    index         integer                 NOT NULL,
    cropped_image character varying(1000) NOT NULL,
    full_image    character varying(1000) NOT NULL,
    hentai        boolean                 NOT NULL
);


--
-- TOC entry 242 (class 1259 OID 16901)
-- Name: hentai_or_not_index_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.hentai_or_not_index_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3312 (class 0 OID 0)
-- Dependencies: 242
-- Name: hentai_or_not_index_seq; Type: SEQUENCE OWNED BY; Schema: shepard_data; Owner: -
--

ALTER SEQUENCE shepard_data.hentai_or_not_index_seq OWNED BY shepard_data.hentai_or_not.index;


--
-- TOC entry 244 (class 1259 OID 16922)
-- Name: hentai_or_not_scores; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.hentai_or_not_scores
(
    guild_id bigint            NOT NULL,
    user_id  bigint            NOT NULL,
    score    integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 16596)
-- Name: invites; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.invites
(
    guild_id bigint               NOT NULL,
    code     character varying(7) NOT NULL,
    count    integer,
    name     character varying(50)
);


--
-- TOC entry 257 (class 1259 OID 17397)
-- Name: kudo_gamble_jackpot; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.kudo_gamble_jackpot
(
    guild_id bigint           NOT NULL,
    jackpot  bigint DEFAULT 0 NOT NULL
);


--
-- TOC entry 254 (class 1259 OID 17362)
-- Name: language; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.language
(
    guild_id    bigint NOT NULL,
    locale_code character varying(5)
);


--
-- TOC entry 215 (class 1259 OID 16606)
-- Name: minecraft_link; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.minecraft_link
(
    user_id bigint                NOT NULL,
    uuid    character varying(32) NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 16611)
-- Name: minecraft_link_codes; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.minecraft_link_codes
(
    code       character varying(6)        NOT NULL,
    uuid       character varying(32)       NOT NULL,
    expired_at timestamp without time zone NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 16617)
-- Name: monitoring_channel; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.monitoring_channel
(
    guild_id   bigint NOT NULL,
    channel_id bigint NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 16582)
-- Name: monitoring_ips_index_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.monitoring_ips_index_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 245 (class 1259 OID 16944)
-- Name: monitoring_ips; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.monitoring_ips
(
    index    integer DEFAULT nextval('shepard_data.monitoring_ips_index_seq'::regclass) NOT NULL,
    guild_id bigint                                                                     NOT NULL,
    name     character varying(64)                                                      NOT NULL,
    address  character varying(200)                                                     NOT NULL,
    mcip     boolean                                                                    NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 16626)
-- Name: muted_users; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.muted_users
(
    guild_id    bigint                      NOT NULL,
    user_id     bigint                      NOT NULL,
    unmute_time timestamp without time zone NOT NULL
);


--
-- TOC entry 219 (class 1259 OID 16631)
-- Name: prefix; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.prefix
(
    guild_id bigint NOT NULL,
    prefix   character varying(2)
);


--
-- TOC entry 211 (class 1259 OID 16584)
-- Name: quotes_index_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.quotes_index_seq
    START WITH 19
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 220 (class 1259 OID 16636)
-- Name: quotes; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.quotes
(
    quote_id integer DEFAULT nextval('shepard_data.quotes_index_seq'::regclass) NOT NULL,
    quote    text                                                               NOT NULL,
    guild_id bigint                                                             NOT NULL
);


--
-- TOC entry 252 (class 1259 OID 17250)
-- Name: reminder; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.reminder
(
    index      integer                        NOT NULL,
    guild_id   bigint                         NOT NULL,
    user_id    bigint                         NOT NULL,
    channel_id bigint                         NOT NULL,
    message    text                           NOT NULL,
    "time"     timestamp(6) without time zone NOT NULL
);


--
-- TOC entry 251 (class 1259 OID 17248)
-- Name: reminder_index_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.reminder_index_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3313 (class 0 OID 0)
-- Dependencies: 251
-- Name: reminder_index_seq; Type: SEQUENCE OWNED BY; Schema: shepard_data; Owner: -
--

ALTER SEQUENCE shepard_data.reminder_index_seq OWNED BY shepard_data.reminder.index;


--
-- TOC entry 221 (class 1259 OID 16643)
-- Name: role_changelog; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.role_changelog
(
    guild_id        bigint NOT NULL,
    observing_roles bigint[],
    channel_id      bigint
);


--
-- TOC entry 247 (class 1259 OID 16963)
-- Name: rubber_points; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.rubber_points
(
    guild_id bigint           NOT NULL,
    user_id  bigint           NOT NULL,
    score    bigint DEFAULT 1 NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 16586)
-- Name: ticket_categories_index_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.ticket_categories_index_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999
    CACHE 1
    CYCLE;


--
-- TOC entry 240 (class 1259 OID 16839)
-- Name: ticket_channel; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.ticket_channel
(
    id             integer NOT NULL,
    guild_id       bigint  NOT NULL,
    ticket_owner   bigint  NOT NULL,
    channel_id     bigint  NOT NULL,
    ticket_type_id integer NOT NULL
);


--
-- TOC entry 239 (class 1259 OID 16837)
-- Name: ticket_channel_id_seq; Type: SEQUENCE; Schema: shepard_data; Owner: -
--

CREATE SEQUENCE shepard_data.ticket_channel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3314 (class 0 OID 0)
-- Dependencies: 239
-- Name: ticket_channel_id_seq; Type: SEQUENCE OWNED BY; Schema: shepard_data; Owner: -
--

ALTER SEQUENCE shepard_data.ticket_channel_id_seq OWNED BY shepard_data.ticket_channel.id;


--
-- TOC entry 241 (class 1259 OID 16844)
-- Name: ticket_count; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.ticket_count
(
    guild_id bigint            NOT NULL,
    counter  integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 16662)
-- Name: ticket_roles; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.ticket_roles
(
    guild_id       bigint  NOT NULL,
    owner_roles    bigint[],
    support_roles  bigint[],
    ticket_type_id integer NOT NULL
);


--
-- TOC entry 222 (class 1259 OID 16651)
-- Name: ticket_types; Type: TABLE; Schema: shepard_data; Owner: -
--

CREATE TABLE shepard_data.ticket_types
(
    index            integer DEFAULT nextval('shepard_data.ticket_categories_index_seq'::regclass) NOT NULL,
    guild_id         bigint                                                                        NOT NULL,
    category_id      bigint                                                                        NOT NULL,
    creation_message text    DEFAULT 'Thanks for creationg a ticket {ticket_owner}. Please describe your problem as precisely as possible.'::text,
    keyword          character varying(32)                                                         NOT NULL
);


--
-- TOC entry 246 (class 1259 OID 16954)
-- Name: ip_count; Type: SEQUENCE; Schema: shepard_func; Owner: -
--

CREATE SEQUENCE shepard_func.ip_count
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 238 (class 1259 OID 16819)
-- Name: quote_counter; Type: SEQUENCE; Schema: shepard_func; Owner: -
--

CREATE SEQUENCE shepard_func.quote_counter
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 253 (class 1259 OID 17257)
-- Name: reminder_count; Type: SEQUENCE; Schema: shepard_func; Owner: -
--

CREATE SEQUENCE shepard_func.reminder_count
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 237 (class 1259 OID 16817)
-- Name: type_counter; Type: SEQUENCE; Schema: shepard_func; Owner: -
--

CREATE SEQUENCE shepard_func.type_counter
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 225 (class 1259 OID 16673)
-- Name: context_admin; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_admin
(
    context_id integer NOT NULL,
    admin_only boolean DEFAULT false
);


--
-- TOC entry 255 (class 1259 OID 17373)
-- Name: context_cooldown; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_cooldown
(
    context_id     integer           NOT NULL,
    user_cooldown  integer DEFAULT 0 NOT NULL,
    guild_cooldown integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 16679)
-- Name: context_guild; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_guild
(
    context_id integer NOT NULL,
    guild_id   bigint  NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 16684)
-- Name: context_guild_check_state; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_guild_check_state
(
    context_id integer               NOT NULL,
    state      boolean DEFAULT false NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 16723)
-- Name: context_guild_list_type; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_guild_list_type
(
    context_id integer                                                                      NOT NULL,
    list_type  shepard_settings.list_types DEFAULT 'BLACKLIST'::shepard_settings.list_types NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16671)
-- Name: context_id_id_seq; Type: SEQUENCE; Schema: shepard_settings; Owner: -
--

CREATE SEQUENCE shepard_settings.context_id_id_seq
    START WITH 740
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 228 (class 1259 OID 16690)
-- Name: context_id; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_id
(
    id           integer DEFAULT nextval('shepard_settings.context_id_id_seq'::regclass) NOT NULL,
    context_name character varying(64)                                                   NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 16696)
-- Name: context_nsfw; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_nsfw
(
    context_id integer               NOT NULL,
    nsfw       boolean DEFAULT false NOT NULL
);


--
-- TOC entry 256 (class 1259 OID 17384)
-- Name: context_permission_override; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_permission_override
(
    context_id integer NOT NULL,
    guild_id   bigint  NOT NULL,
    override   boolean
);


--
-- TOC entry 230 (class 1259 OID 16702)
-- Name: context_role_permission; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_role_permission
(
    context_id integer NOT NULL,
    role_id    bigint  NOT NULL,
    guild_id   bigint  NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 16707)
-- Name: context_user; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_user
(
    context_id integer NOT NULL,
    user_id    bigint  NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 16712)
-- Name: context_user_check_state; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_user_check_state
(
    context_id integer               NOT NULL,
    state      boolean DEFAULT false NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 16729)
-- Name: context_user_list_type; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_user_list_type
(
    context_id integer                                                                      NOT NULL,
    list_type  shepard_settings.list_types DEFAULT 'BLACKLIST'::shepard_settings.list_types NOT NULL
);


--
-- TOC entry 235 (class 1259 OID 16735)
-- Name: context_user_permission; Type: TABLE; Schema: shepard_settings; Owner: -
--

CREATE TABLE shepard_settings.context_user_permission
(
    user_id    bigint  NOT NULL,
    context_id integer NOT NULL,
    guild_id   bigint  NOT NULL
);


--
-- TOC entry 3315 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE context_user_permission; Type: COMMENT; Schema: shepard_settings; Owner: -
--

COMMENT ON TABLE shepard_settings.context_user_permission IS 'If a context is admin only. A Admin can grant execution rights via a permission to a user.';


--
-- TOC entry 3104 (class 2604 OID 16906)
-- Name: hentai_or_not index; Type: DEFAULT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.hentai_or_not
    ALTER COLUMN index SET DEFAULT nextval('shepard_data.hentai_or_not_index_seq'::regclass);


--
-- TOC entry 3115 (class 2604 OID 17253)
-- Name: reminder index; Type: DEFAULT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.reminder
    ALTER COLUMN index SET DEFAULT nextval('shepard_data.reminder_index_seq'::regclass);


--
-- TOC entry 3102 (class 2604 OID 16842)
-- Name: ticket_channel id; Type: DEFAULT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.ticket_channel
    ALTER COLUMN id SET DEFAULT nextval('shepard_data.ticket_channel_id_seq'::regclass);


--
-- TOC entry 3172 (class 2606 OID 16989)
-- Name: free_rubber_points free_rubber_points_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.free_rubber_points
    ADD CONSTRAINT free_rubber_points_pkey PRIMARY KEY (guild_id, user_id);


--
-- TOC entry 3120 (class 2606 OID 16595)
-- Name: greetings greetings_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.greetings
    ADD CONSTRAINT greetings_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3136 (class 2606 OID 16650)
-- Name: role_changelog group_changelog_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.role_changelog
    ADD CONSTRAINT group_changelog_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3166 (class 2606 OID 16927)
-- Name: hentai_or_not_scores hentai_or_not_scores_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.hentai_or_not_scores
    ADD CONSTRAINT hentai_or_not_scores_pkey PRIMARY KEY (guild_id, user_id);


--
-- TOC entry 3122 (class 2606 OID 16600)
-- Name: invites invites_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.invites
    ADD CONSTRAINT invites_pkey PRIMARY KEY (code);


--
-- TOC entry 3182 (class 2606 OID 17402)
-- Name: kudo_gamble_jackpot kudo_gamble_jackpot_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.kudo_gamble_jackpot
    ADD CONSTRAINT kudo_gamble_jackpot_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3176 (class 2606 OID 17366)
-- Name: language language_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3126 (class 2606 OID 16615)
-- Name: minecraft_link_codes minecraft_link_codes_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.minecraft_link_codes
    ADD CONSTRAINT minecraft_link_codes_pkey PRIMARY KEY (code);


--
-- TOC entry 3124 (class 2606 OID 16610)
-- Name: minecraft_link minecraft_link_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.minecraft_link
    ADD CONSTRAINT minecraft_link_pkey PRIMARY KEY (user_id);


--
-- TOC entry 3128 (class 2606 OID 16621)
-- Name: monitoring_channel monitoring_channel_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.monitoring_channel
    ADD CONSTRAINT monitoring_channel_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3168 (class 2606 OID 16949)
-- Name: monitoring_ips monitoring_ips_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.monitoring_ips
    ADD CONSTRAINT monitoring_ips_pkey PRIMARY KEY (guild_id, address);


--
-- TOC entry 3130 (class 2606 OID 16630)
-- Name: muted_users muted_users_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.muted_users
    ADD CONSTRAINT muted_users_pkey PRIMARY KEY (guild_id, user_id);


--
-- TOC entry 3132 (class 2606 OID 16635)
-- Name: prefix prefix_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.prefix
    ADD CONSTRAINT prefix_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3134 (class 2606 OID 18677)
-- Name: quotes quotes_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.quotes
    ADD CONSTRAINT quotes_pkey PRIMARY KEY (quote_id, guild_id);


--
-- TOC entry 3170 (class 2606 OID 16968)
-- Name: rubber_points rubber_points_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.rubber_points
    ADD CONSTRAINT rubber_points_pkey PRIMARY KEY (guild_id, user_id);


--
-- TOC entry 3138 (class 2606 OID 16832)
-- Name: ticket_types ticket_categories_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.ticket_types
    ADD CONSTRAINT ticket_categories_pkey PRIMARY KEY (guild_id, keyword);


--
-- TOC entry 3164 (class 2606 OID 16849)
-- Name: ticket_count ticket_count_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.ticket_count
    ADD CONSTRAINT ticket_count_pkey PRIMARY KEY (guild_id);


--
-- TOC entry 3140 (class 2606 OID 16669)
-- Name: ticket_roles ticket_groups_pkey; Type: CONSTRAINT; Schema: shepard_data; Owner: -
--

ALTER TABLE ONLY shepard_data.ticket_roles
    ADD CONSTRAINT ticket_groups_pkey PRIMARY KEY (ticket_type_id);


--
-- TOC entry 3142 (class 2606 OID 16678)
-- Name: context_admin context_admin_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_admin
    ADD CONSTRAINT context_admin_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3156 (class 2606 OID 16717)
-- Name: context_user_check_state context_character_check_active_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_user_check_state
    ADD CONSTRAINT context_character_check_active_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3160 (class 2606 OID 16734)
-- Name: context_user_list_type context_character_list_type_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_user_list_type
    ADD CONSTRAINT context_character_list_type_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3154 (class 2606 OID 16711)
-- Name: context_user context_character_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_user
    ADD CONSTRAINT context_character_pkey PRIMARY KEY (context_id, user_id);


--
-- TOC entry 3178 (class 2606 OID 17379)
-- Name: context_cooldown context_cooldown_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_cooldown
    ADD CONSTRAINT context_cooldown_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3144 (class 2606 OID 16683)
-- Name: context_guild context_guilds_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_guild
    ADD CONSTRAINT context_guilds_pkey PRIMARY KEY (context_id, guild_id);


--
-- TOC entry 3148 (class 2606 OID 16695)
-- Name: context_id context_id_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_id
    ADD CONSTRAINT context_id_pkey PRIMARY KEY (context_name);


--
-- TOC entry 3150 (class 2606 OID 16701)
-- Name: context_nsfw context_nsfw_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_nsfw
    ADD CONSTRAINT context_nsfw_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3180 (class 2606 OID 17388)
-- Name: context_permission_override context_permission_override_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_permission_override
    ADD CONSTRAINT context_permission_override_pkey PRIMARY KEY (context_id, guild_id);


--
-- TOC entry 3152 (class 2606 OID 16883)
-- Name: context_role_permission context_role_permission_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_role_permission
    ADD CONSTRAINT context_role_permission_pkey PRIMARY KEY (context_id, role_id, guild_id);


--
-- TOC entry 3146 (class 2606 OID 16689)
-- Name: context_guild_check_state context_server_check_active_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_guild_check_state
    ADD CONSTRAINT context_server_check_active_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3158 (class 2606 OID 16728)
-- Name: context_guild_list_type context_server_list_type_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_guild_list_type
    ADD CONSTRAINT context_server_list_type_pkey PRIMARY KEY (context_id);


--
-- TOC entry 3162 (class 2606 OID 16881)
-- Name: context_user_permission context_user_permission_pkey; Type: CONSTRAINT; Schema: shepard_settings; Owner: -
--

ALTER TABLE ONLY shepard_settings.context_user_permission
    ADD CONSTRAINT context_user_permission_pkey PRIMARY KEY (user_id, context_id, guild_id);
