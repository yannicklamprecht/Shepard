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
    VALUES (_guild_id, _user_id, _channel_id, _message, date);
    RETURN CURRVAL('shepard_data.reminder_index_seq'::regclass);

END
$$;
