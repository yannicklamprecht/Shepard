ALTER TABLE shepard_data.reminder
    ADD COLUMN delivered boolean NOT NULL DEFAULT false;

ALTER TABLE shepard_data.reminder
    ADD COLUMN deleted boolean NOT NULL DEFAULT false;

ALTER TABLE shepard_data.reminder
    ADD COLUMN snooze_count integer NOT NULL DEFAULT 0;

ALTER TABLE shepard_data.reminder
    ALTER COLUMN index TYPE bigint;

Alter sequence shepard_data.reminder_index_seq as bigint;

DROP FUNCTION IF EXISTS shepard_func.add_reminder_date(character varying, character varying, character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_func.add_reminder_date(_guild_id bigint, _user_id bigint,
                                                          _channel_id bigint, _message character varying,
                                                          _date character varying,
                                                          _time character varying) RETURNS bigint
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
    VALUES (_guild_id, _user_id, _channel_id, _message, date)
    RETURNING index;

END
$$;


DROP FUNCTION IF EXISTS shepard_func.add_reminder_interval(character varying, character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_func.add_reminder_interval(_guild_id bigint, _user_id bigint,
                                                              _channel_id bigint, _message character varying,
                                                              _time character varying) RETURNS bigint
    LANGUAGE plpgsql
AS
$$
BEGIN

    insert into shepard_data.reminder(guild_id, user_id, channel_id, message, time)
    VALUES (_guild_id, _user_id, _channel_id, _message, now() + _time::interval);
    RETURN CURRVAL('shepard_data.reminder_index_seq'::regclass);

END
$$;


DROP FUNCTION IF EXISTS shepard_func.get_expired_reminder;

CREATE OR REPLACE FUNCTION shepard_func.get_expired_reminder()
    RETURNS TABLE
            (
                id           bigint,
                guild_id     bigint,
                channel_id   bigint,
                user_id      bigint,
                message      character varying,
                snooze_count integer
            )
    LANGUAGE plpgsql
    ROWS 100
AS
$$
DECLARE
    _temp_result_name text;
BEGIN

    _temp_result_name = 'tmp_reminder_' || floor(random() * 1000000000);

    execute format($ex$
        create temporary table %1$s
            (
                    id         bigint,
                    guild_id   bigint,
                    channel_id bigint,
                    user_id    bigint,
                    message    character varying,
                    snooze_count integer
            )
            on commit drop;

        Insert into %1$s
        select r.index, r.guild_id, r.channel_id, r.user_id, r.message, r.snooze_count
        from shepard_data.reminder r
        where r.time < now()
          and not delivered
          and not deleted;


        update shepard_data.reminder r
        set delivered = true
        where (select exists(select 1 from %1$s t where r.index = t.id));
        $ex$, _temp_result_name);

    return query
    execute format($ex$
        select * from %1$s;
    $ex$, _temp_result_name);

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.snooze_reminder(_guild_id bigint, _user_id bigint, _reminder_id bigint,
                                                        _interval varchar)
    RETURNS timestamp
    LANGUAGE plpgsql
AS
$$
BEGIN

    update shepard_data.reminder
    set time         = now() + _interval::interval,
        snooze_count = snooze_count + 1,
        delivered    = false
    where index = _reminder_id
      and guild_id = _guild_id
      and user_id = _user_id;

    return (select time from shepard_data.reminder where index = _reminder_id);

END
$$;

DROP FUNCTION IF EXISTS shepard_func.get_reminder(_guild_id varchar, _user_id varchar, _reminder_id integer);

CREATE OR REPLACE FUNCTION shepard_func.get_reminder(_guild_id bigint, _user_id bigint, _reminder_id bigint)
    RETURNS TABLE
            (
                id            bigint,
                guild_id      bigint,
                channel_id    bigint,
                user_id       bigint,
                message       text,
                reminder_time timestamp without time zone,
                snooze_count  integer
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    return query
        select r.index as id,
               r.guild_id,
               r.channel_id,
               r.user_id,
               r.message,
               r.time,
               r.snooze_count
        from shepard_data.reminder r
        where r.user_id = _user_id
          and r.guild_id = _guild_id
          and r.index = _reminder_id;

END
$$;


CREATE OR REPLACE FUNCTION shepard_func.get_user_reminder(_guild_id bigint, _user_id bigint)
    RETURNS TABLE
            (
                id            bigint,
                message       text,
                reminder_time timestamp without time zone
            )
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        select r.index as id,
               r.message,
               r.time
        from shepard_data.reminder r
        where guild_id = _guild_id::BIGINT
          and user_id = _user_id::bigint
          and not deleted
          and not delivered
        order by r.time asc;

END
$$;

DROP FUNCTION IF EXISTS shepard_func.remove_reminder(_guild_id varchar, _user_id varchar, _reminder_id integer);

CREATE OR REPLACE FUNCTION shepard_func.remove_reminder(_guild_id varchar, _user_id varchar, _reminder_id bigint)
    RETURNS boolean
    LANGUAGE plpgsql
AS
$$
BEGIN

    Update shepard_data.reminder
    set deleted = true
    where index = _reminder_id
      and guild_id = _guild_id
      and user_id = _user_id;

    return (select EXISTS(select 1
                          from shepard_data.reminder
                          where index = _reminder_id
                            and guild_id = _guild_id
                            and user_id = _user_id));

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.restore_reminder(_guild_id varchar, _user_id varchar, _reminder_id integer) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN

    Update shepard_data.reminder
    set deleted = false
    where index = _reminder_id
      and guild_id = _guild_id
      and user_id = _user_id;

END
$$;

CREATE OR REPLACE FUNCTION shepard_func.remove_expired_reminder() RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    delete from shepard_data.reminder r where r.time < now() - '1 week'::interval and (deleted or delivered);
END
$$;
