CREATE TABLE IF NOT EXISTS shepard_settings.command_state
(
    guild_id   bigint  NOT NULL,
    command_id integer NOT NULL,
    active     boolean NOT NULL,
    CONSTRAINT command_state_pkey PRIMARY KEY (guild_id, command_id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

CREATE TABLE IF NOT EXISTS shepard_settings.command_channel_list
(
    guild_id   bigint  NOT NULL,
    channel_id bigint  NOT NULL,
    command_id integer NOT NULL,
    CONSTRAINT command_channel_pkey PRIMARY KEY (command_id, guild_id, channel_id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;


CREATE TABLE IF NOT EXISTS shepard_settings.command_channel_list_type
(
    guild_id   bigint                      NOT NULL,
    command_id integer                     NOT NULL,
    list_type  shepard_settings.list_types NOT NULL,
    CONSTRAINT command_channel_list_type_pkey PRIMARY KEY (command_id, guild_id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

CREATE TABLE shepard_settings.command_channel_check_state
(
    guild_id bigint NOT NULL,
    command_id integer NOT NULL,
    state boolean NOT NULL,
    CONSTRAINT command_channel_check_state_pkey PRIMARY KEY (guild_id, command_id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

CREATE OR REPLACE FUNCTION shepard_settings.add_command_channel(_command_name character varying,
                                                                _guild_id bigint,
                                                                _channel_id bigint)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    insert into shepard_settings.command_channel_list(guild_id, channel_id, command_id)
    VALUES (_guild_id, _channel_id, _command_id)
    on conflict (guild_id, channel_id, command_id)
        do nothing;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.remove_command_channel(_command_name character varying,
                                                                   _guild_id bigint,
                                                                   _channel_id bigint)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    delete
    from shepard_settings.command_channel_list
    where command_id = _command_id
      and guild_id = _guild_id
      and channel_id = _channel_id;

END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.get_channel_list_type(_command_name character varying,
                                                                  _guild_id bigint)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      varchar;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    select list_type::varchar
    into result
    from shepard_settings.command_channel_list_type
    where guild_id = _guild_id
      and command_id = _command_id;

    return result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_channel_check_active(_command_name character varying,
                                                                    _guild_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      boolean;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    select state
    into result
    from shepard_settings.command_channel_check_state
    where guild_id = _guild_id
      and command_id = _command_id;

    RETURN coalesce(result, false);

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_channel_on_list(_command_name character varying,
                                                               _channel_id bigint,
                                                               _guild_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      boolean;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    SELECT EXISTS(
                   SELECT 1
                   from shepard_settings.command_channel_list
                   where command_id = _command_id
                     and channel_id = _channel_id
                     and guild_id = _guild_id)
    into result;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.can_use(_command_name character varying, _guild_id bigint, _user_id bigint,
                                                    _role_ids bigint[])
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    requires_permission boolean;
BEGIN
    -- check permission override
    if (select shepard_settings.get_permission_override(_command_name, _guild_id)) then
        requires_permission = (select shepard_settings.get_permission_override(_command_name, _guild_id));
    else
        requires_permission = (select shepard_settings.is_admin_command(_command_name));
    end if;

    -- if override is false then no permission is required
    if not requires_permission then
        return true;
    end if;

    -- check user permission
    if (select shepard_settings.has_user_permission(_command_name, _guild_id, _user_id)) then
        return true;
    end if;

    return (select shepard_settings.has_user_permission_role(_command_name, _guild_id, _role_ids));


END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.can_use_here(_command_name character varying, _guild_id bigint,
                                                         _channel_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
BEGIN
    -- check if command is usable in channel;
    if coalesce((select shepard_settings.is_channel_check_active(_command_name, _guild_id)), false) then
        if (select shepard_settings.is_channel_on_list(_command_name, _channel_id, _guild_id)) then
            return coalesce((select shepard_settings.get_channel_list_type(_command_name, _guild_id)), 'WHITELIST') =
                   'WHITELIST';
        else
            return coalesce((select shepard_settings.get_channel_list_type(_command_name, _guild_id)), 'WHITELIST') !=
                   'WHITELIST';
        end if;
    else
        return True;
    end if;
END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.set_command_state(_command_name character varying, _guild_id bigint,
                                                              _state boolean)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    insert into shepard_settings.command_state(command_id, guild_id, active)
    VALUES (_command_id, _guild_id, _state)
    on CONFLICT (guild_id, command_id)
        DO UPDATE
        SET active = _state;
END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.set_command_channel_check_active(_command_name character varying,
                                                                             _guild_id bigint,
                                                                             _state boolean DEFAULT false)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    insert into shepard_settings.command_channel_check_state(command_id, guild_id, state)
    VALUES (_command_id, _guild_id, _state)
    on CONFLICT (guild_id, command_id)
        DO UPDATE
        SET state = _state;
END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.set_command_channel_list_type(_command_name character varying,
                                                                          _guild_id bigint,
                                                                          _list_type character varying DEFAULT 'BLACKLIST'::character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    if _list_type = 'BLACKLIST' OR _list_type = 'WHITELIST' THEN
        insert into shepard_settings.command_channel_list_type(command_id, guild_id, list_type)
        VALUES (_command_id, _guild_id, _list_type::shepard_settings.list_types)
        on CONFLICT (guild_id, command_id)
            DO UPDATE
            SET list_type = _list_type::shepard_settings.list_types;
    END IF;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_command_state(_command_name character varying,
                                                              _guild_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    result      boolean;
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    select active
    into result
    from shepard_settings.command_state
    where guild_id = _guild_id
      and command_id = _command_id;

    RETURN coalesce(result, true);

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_command_channel_list(_command_name character varying, _guild_id bigint)
    RETURNS bigint[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      bigint[];
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    SELECT array_agg(channel_id)
    from shepard_settings.command_channel_list
    where command_id = _command_id
      and guild_id = _guild_id
    into result;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_user_permission_list(_command_name character varying, _guild_id bigint)
    RETURNS bigint[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      bigint[];
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    SELECT array_agg(user_id)
    from shepard_settings.command_user_permission
    where command_id = _command_id
      and guild_id = _guild_id
    into result;

    RETURN coalesce(result, ARRAY []::bigint);

END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.get_role_permission_list(_command_name character varying, _guild_id bigint)
    RETURNS bigint[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      bigint[];
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    SELECT array_agg(role_id)
    from shepard_settings.command_role_permission
    where command_id = _command_id
      and guild_id = _guild_id
    into result;

    RETURN coalesce(result, ARRAY []::bigint);

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.can_use(_command_name character varying,
                                                    _guild_id bigint,
                                                    _user_id bigint,
                                                    _role_ids bigint[])
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    command_parts text[];
    subcommand    boolean;
BEGIN
    command_parts = regexp_split_to_array(_command_name, '\.');

    subcommand = array_length(command_parts, 3) = 2;

    -- if override is false then no permission is required
    if not (select shepard_settings.requires_permission(_command_name, _guild_id)) then
        Raise notice 'This command does not require a permission';
        return true;
    end if;

    -- check user permission
    if subcommand and (select shepard_settings.has_user_permission(_command_name, _guild_id, _user_id)) then
        Raise Notice 'Subcommand permission granted by user';
        return true;
    end if;

    if (select shepard_settings.has_user_permission(command_parts[1] || '.*', _guild_id, _user_id)) then
        Raise Notice 'General permission granted by user';
        return true;
    end if;
    if subcommand and (select shepard_settings.has_user_permission_role(_command_name, _guild_id, _role_ids)) then
        Raise Notice 'Subcommand permission granted by role';
        return true;
    end if;

    if (select shepard_settings.has_user_permission_role(command_parts[1] || '.*', _guild_id, _role_ids)) then
        Raise Notice 'General permission granted by role';
        return true;
    end if;

    Raise notice 'User has not the permission to execute command %', _command_name;
    return false;
END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.requires_permission(_command_name character varying,
                                                                _guild_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    requires_permission boolean;
    command_parts       text[];
BEGIN
    command_parts = regexp_split_to_array(_command_name, '\.');
    Raise Notice 'checking permission for %', _command_name;

    -- when a wildcard is requests a permission is always needed.
    if (_command_name ~* '\*') then
        Raise Notice 'Command is wildcard. A wildcard always requires a permission';
        return True;
    end if;

    -- check permission override
    -- check if the permission override for the command is set
    if ((select shepard_settings.get_permission_override(command_parts[1], _guild_id))) is not null then
        Raise NOTICE 'found permission override for %', command_parts[1];
        requires_permission = (select shepard_settings.get_permission_override(command_parts[1], _guild_id));
    else
        -- if the permission override for the wildcard is not set, the default value is used.
        Raise NOTICE 'No permission override for %', command_parts[1];
        requires_permission = (select shepard_settings.is_admin_command(command_parts[1]));
    end if;

    Raise NOTICE 'permission required: %', requires_permission;
    -- if the current command does not require a permission, the subcommand determines the permission, if a permission override is set.
    if not requires_permission and
       ((select shepard_settings.get_permission_override(_command_name, _guild_id))) is not null then
        Raise NOTICE 'No permission required. checking if subcommand needs permission %', requires_permission;
        return (select shepard_settings.get_permission_override(_command_name, _guild_id));
    end if;

    return requires_permission;
END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.can_use_in_channel(_command_name character varying,
                                                               _guild_id bigint,
                                                               _user_id bigint,
                                                               _channel_id bigint,
                                                               _role_ids bigint[])
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
BEGIN
    if not (select shepard_settings.can_use_here(_command_name, _guild_id, _channel_id)) then
        Raise NOTICE 'command is not allowed in this channel';
        return false;
    end if;

    if not (select * from shepard_settings.get_command_state(_command_name, _guild_id)) then
        Raise NOTICE 'command is disabled on this guild';
        return false;
    end if;

    return shepard_settings.can_use(_command_name, _guild_id, _user_id, _role_ids);
END

$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.help_display_check(_command_name character varying,
                                                               _guild_id bigint,
                                                               _user_id bigint,
                                                               _channel_id bigint,
                                                               _role_ids bigint[],
                                                               _is_admin boolean)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
BEGIN
    if not (select shepard_settings.can_access(_command_name, _guild_id, _user_id)) then
        Raise notice 'Command is not accessable for this user or guild';
        return false;
    end if;

    if not (select shepard_settings.can_use_here(_command_name, _guild_id, _channel_id)) then
        Raise NOTICE 'Command is not allowed in this channel';
        return false;
    end if;


    if not (select shepard_settings.get_command_state(_command_name, _guild_id)) then
        Raise NOTICE 'Command is disabled on this guild';
        return false;
    end if;

    if _is_admin then
        Raise notice 'Command is allowed. User is admin';
        return true;
    end if;

    if not (select shepard_settings.can_use(_command_name, _guild_id, _user_id, _role_ids)) then
        Raise Notice 'User is not allowed to use this command.';
        return false;
    end if;

    Raise notice 'User is allowed to user this command';
    return true;
END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_guild_cooldown(
    _command_name character varying)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      integer;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    select guild_cooldown
    into result
    from shepard_settings.command_cooldown
    where command_id = _command_id;

    RETURN coalesce(result, 1);

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_user_cooldown(
    _command_name character varying)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
    result      integer;
BEGIN
    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    select user_cooldown
    into result
    from shepard_settings.command_cooldown
    where command_id = _command_id;

    RETURN coalesce(result, 5);

END
$BODY$;
