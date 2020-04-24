-- REDEFINE TABLE NAMES / REPLACE CONTEXT WITH COMMAND DUE TO REMOVEAL OF CONTEXTS
ALTER TABLE IF EXISTS shepard_settings.context_id
    RENAME TO command_id;

ALTER TABLE IF EXISTS shepard_settings.context_admin
    RENAME TO command_admin;

ALTER TABLE IF EXISTS shepard_settings.context_cooldown
    RENAME TO command_cooldown;

ALTER TABLE IF EXISTS shepard_settings.context_guild
    RENAME TO command_guild_list;

ALTER TABLE IF EXISTS shepard_settings.context_guild_check_state
    RENAME TO command_guild_check_state;

ALTER TABLE IF EXISTS shepard_settings.context_guild_list_type
    RENAME TO command_guild_list_type;

ALTER TABLE IF EXISTS shepard_settings.context_nsfw
    RENAME TO command_nsfw;

ALTER TABLE IF EXISTS shepard_settings.context_permission_override
    RENAME TO command_permission_override;

ALTER TABLE IF EXISTS shepard_settings.context_role_permission
    RENAME TO command_role_permission;

ALTER TABLE IF EXISTS shepard_settings.context_user
    RENAME TO command_user_list;

ALTER TABLE IF EXISTS shepard_settings.context_user_check_state
    RENAME TO command_user_check_state;

ALTER TABLE IF EXISTS shepard_settings.context_user_list_type
    RENAME TO command_user_list_type;

ALTER TABLE IF EXISTS shepard_settings.context_user_permission
    RENAME TO command_user_permission;

-- REDEFINE COLUMN NAMES. RENAME CONTEXT TO COMMAND
-- WARNING: THIS PART CAN ONLY BE EXECUTED ONCE!
ALTER TABLE IF EXISTS shepard_settings.command_id
    RENAME context_name to command_name;

ALTER TABLE IF EXISTS shepard_settings.command_admin
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_cooldown
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_guild_list
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_guild_check_state
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_guild_list_type
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_nsfw
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_permission_override
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_role_permission
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_user_list
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_user_check_state
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_user_list_type
    RENAME context_id to command_id;

ALTER TABLE IF EXISTS shepard_settings.command_user_permission
    RENAME context_id to command_id;


-- REDEFINE SHEPARD SETTINGS FUNCTIONS


DROP FUNCTION IF EXISTS shepard_settings.get_or_create_context_id(character varying);

CREATE OR REPLACE FUNCTION shepard_settings.get_or_create_command_id(
    _command_name character varying)
    RETURNS integer
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    result integer;
BEGIN
    -- Check if a command with this name was created. If not create it
    if not (select exists(select 1 from shepard_settings.command_id where command_name = _command_name))
    then
        insert into shepard_settings.command_id (command_name) VALUES (_command_name);
    end if;

    SELECT id
    INTO result
    FROM shepard_settings.command_id c
    WHERE command_name = _command_name;

    -- Make sure, that a default setting for each table ws created.
    insert into shepard_settings.command_admin (command_id) values (result) on conflict (command_id) do nothing;
    insert into shepard_settings.command_user_check_state (command_id)
    values (result)
    on conflict (command_id) do nothing;
    insert into shepard_settings.command_user_list_type (command_id)
    values (result)
    on conflict (command_id) do nothing;
    insert into shepard_settings.command_nsfw (command_id) values (result) on conflict (command_id) do nothing;
    insert into shepard_settings.command_guild_check_state (command_id)
    values (result)
    on conflict (command_id) do nothing;
    insert into shepard_settings.command_guild_list_type (command_id)
    values (result)
    on conflict (command_id) do nothing;
    insert into shepard_settings.command_cooldown (command_id) values (result) on conflict (command_id) do nothing;

    RETURN result;
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

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_guild_list_type(
    _command_name character varying)
    RETURNS varchar
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
    from shepard_settings.command_guild_list_type
    where command_id = _command_id;

    return result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_permission_override(_command_name character varying, _guild_id bigint)
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

    SELECT override
    INTO result
    FROM shepard_settings.command_permission_override
    WHERE guild_id = _guild_id
      and command_id = _command_id;

    RETURN result;

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

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.get_user_list_type(
    _command_name character varying)
    RETURNS varchar
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
    from shepard_settings.command_user_list_type
    where command_id = _command_id;

    return result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_cooldown(
    _command_name character varying)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
BEGIN
    RETURN (shepard_settings.has_guild_cooldown(_command_name) OR shepard_settings.has_user_cooldown(_command_name));

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_guild_cooldown(
    _command_name character varying)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
BEGIN

    RETURN shepard_settings.get_guild_cooldown(_command_name) != 0;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_permission_override(_command_name character varying, _guild_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
BEGIN
    RETURN shepard_settings.get_permission_override(_command_name, _guild_id);

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_user_cooldown(
    _command_name character varying)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
BEGIN
    RETURN shepard_settings.get_user_cooldown(_command_name) != 0;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_user_permission_role(_command_name character varying, _guild_id bigint,
                                                                     _role_ids bigint[])
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

    with role_ids AS
             (
                 SELECT array_agg(role_id) as roles
                 FROM shepard_settings.command_role_permission
                 WHERE guild_id = _guild_id
                   and command_id = _command_id
             )
    SELECT _role_ids && COALESCE(role_ids.roles, ARRAY []::bigint[])
    INTO result
    FROM role_ids;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.has_user_permission(_command_name character varying, _guild_id bigint,
                                                                _user_id bigint)
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

    SELECT EXISTS(SELECT 1
                  from shepard_settings.command_user_permission
                  where _guild_id = _guild_id
                    AND command_id = _command_id
                    AND user_id = _user_id)
    INTO result;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_admin_command(
    _command_name character varying)
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

    select admin_only
    into result
    from shepard_settings.command_admin
    where command_id = _command_id;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_guild_check_active(
    _command_name character varying)
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
    from shepard_settings.command_guild_check_state
    where command_id = _command_id;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_guild_on_list(_command_name character varying, _guild_id bigint)
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

    SELECT EXISTS(SELECT 1
                  from shepard_settings.command_guild_list
                  where command_id = _command_id
                    and guild_id = _guild_id)
    into result;

    RETURN result;

END
$BODY$;

CREATE OR REPLACE FUNCTION shepard_settings.is_nsfw(
    _command_name character varying)
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

    select nsfw
    into result
    from shepard_settings.command_nsfw
    where command_id = _command_id;

    RETURN result;

END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.is_user_check_active(
    _command_name character varying)
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
    from shepard_settings.command_user_check_state
    where command_id = _command_id;

    RETURN result;

END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.is_user_on_list(_command_name character varying, _user_id bigint)
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
                   from shepard_settings.command_user_list
                   where command_id = _command_id
                     and user_id = _user_id)
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

    RETURN result;

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

    RETURN result;

END
$BODY$;


CREATE OR REPLACE FUNCTION shepard_settings.can_access(_command_name character varying, _guild_id bigint, _user_id bigint)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    can_be_executed_by_user  boolean;
    can_be_executed_on_guild boolean;
BEGIN

    if (select shepard_settings.is_user_check_active(_command_name)) then
        if (select shepard_settings.is_user_on_list(_command_name, _user_id)) then
            can_be_executed_by_user = (select shepard_settings.get_user_list_type(_command_name)) = 'WHITELIST';
        else
            can_be_executed_by_user = (select shepard_settings.get_user_list_type(_command_name)) != 'WHITELIST';
        end if;
    else
        can_be_executed_by_user = True;
    end if;

    if not can_be_executed_by_user then
        return false;
    end if;

    if (select shepard_settings.is_guild_check_active(_command_name)) then
        if (select shepard_settings.is_guild_on_list(_command_name, _guild_id)) then
            can_be_executed_on_guild = (select shepard_settings.get_guild_list_type(_command_name)) = 'WHITELIST';
        else
            can_be_executed_on_guild = (select shepard_settings.get_guild_list_type(_command_name)) != 'WHITELIST';
        end if;
    else
        can_be_executed_on_guild = True;
    end if;

    return can_be_executed_on_guild;

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
    if (select shepard_settings.get_permission_override(_command_name, _guild_id)) then
        requires_permission = (select shepard_settings.get_permission_override(_command_name, _guild_id));
    else
        requires_permission = (select shepard_settings.is_admin_command(_command_name));
    end if;

    if not requires_permission then
        return true;
    end if;

    if (select shepard_settings.has_user_permission(_command_name, _guild_id, _user_id)) then
        return true;
    end if;

    return (select shepard_settings.has_user_permission_role(_command_name, _guild_id, _role_ids));
END
$BODY$;


DROP FUNCTION IF EXISTS shepard_func.add_context_guild(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.add_command_guild(_command_name character varying,
                                                              _guild_id character varying)
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

    insert into shepard_settings.command_guild_list(command_id, guild_id)
    VALUES (_command_id, _guild_id::BIGINT)
    on conflict (command_id, guild_id)
        do nothing;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.add_context_role_permission(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.add_command_role_permission(_command_name character varying,
                                                                        _guild_id character varying,
                                                                        _role_id character varying)
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

    insert into shepard_settings.command_role_permission(command_id, role_id, guild_id)
    VALUES (_command_id, _role_id::BIGINT, _guild_id::BIGINT)
    on conflict (role_id, command_id, guild_id)
        do nothing;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.add_context_user(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.add_command_user(_command_name character varying,
                                                             _user_id character varying)
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

    insert into shepard_settings.command_user_list(command_id, user_id)
    VALUES (_command_id, _user_id::BIGINT)
    ON CONFLICT
        DO NOTHING;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.add_context_user_permission(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.add_command_user_permission(_command_name character varying,
                                                                        _guild_id character varying,
                                                                        _user_id character varying)
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

    insert into shepard_settings.command_user_permission(command_id, user_id, guild_id)
    VALUES (_command_id, _user_id::BIGINT, _guild_id::BIGINT)
    on conflict (user_id, command_id, guild_id)
        do nothing;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.get_context_data(character varying);

CREATE OR REPLACE FUNCTION shepard_settings.get_command_data(
    _command_name character varying)
    RETURNS TABLE
            (
                admin_only        boolean,
                nsfw              boolean,
                user_check_state  boolean,
                user_list_type    shepard_settings.list_types,
                user_list         varchar[],
                guild_check_state boolean,
                guild_list_type   shepard_settings.list_types,
                guild_list        varchar[],
                user_cooldown     integer,
                guild_cooldown    integer
            )
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    RETURN QUERY
        SELECT (select t.admin_only
                from shepard_settings.command_admin t
                where command_id = _command_id) as admin_only,

               (select t.nsfw
                from shepard_settings.command_nsfw t
                where command_id = _command_id) as nsfw,

               (select t.state
                from shepard_settings.command_user_check_state t
                where command_id = _command_id) as user_check_state,

               (select t.list_type
                from shepard_settings.command_user_list_type t
                where command_id = _command_id) as user_list_type,

               (select array_agg(t.user_id::varchar)
                from shepard_settings.command_user_list t
                where command_id = _command_id) as user_list,

               (select t.state
                from shepard_settings.command_guild_check_state t
                where command_id = _command_id) as guild_check_state,

               (select t.list_type
                from shepard_settings.command_guild_list_type t
                where command_id = _command_id) as guild_list_type,

               (select array_agg(t.guild_id::varchar)
                from shepard_settings.command_guild_list t
                where command_id = _command_id) as guild_list,

               (select t.user_cooldown
                from shepard_settings.command_cooldown t
                where command_id = _command_id) as user_cooldown,

               (select t.guild_cooldown
                from shepard_settings.command_cooldown t
                where command_id = _command_id) as guild_cooldown;
END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.get_context_role_permissions(character varying);

CREATE OR REPLACE FUNCTION shepard_settings.get_command_role_permissions(
    _command_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                role_id  character varying
            )
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
    ROWS 1000
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    RETURN QUERY SELECT r.guild_id::varchar, r.role_id::varchar
                 FROM shepard_settings.command_role_permission r
                 WHERE command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.get_context_user_permissions(character varying);

CREATE OR REPLACE FUNCTION shepard_settings.get_context_user_permissions(
    _command_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                user_id  character varying
            )
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
    ROWS 1000
AS
$BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    RETURN QUERY SELECT u.guild_id::varchar, u.user_id::varchar
                 FROM shepard_settings.command_user_permission u
                 WHERE command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.get_permission_overrides(character varying);

CREATE OR REPLACE FUNCTION shepard_settings.get_permission_overrides(
    _command_name character varying)
    RETURNS TABLE
            (
                guild_id character varying,
                override boolean
            )
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
    ROWS 1000
AS
$BODY$
BEGIN

    return query
        select p.guild_id::varchar,
               p.override
        from shepard_settings.command_permission_override p
        where command_id = shepard_settings.get_or_create_command_id(_command_name);

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.remove_context_guild(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.remove_command_guild(_command_name character varying,
                                                                 _guild_id character varying)
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
    from shepard_settings.context_guild
    where command_id = _command_id
      and guild_id = _guild_id::BIGINT;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.remove_context_role_permission(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.remove_command_role_permission(_command_name character varying,
                                                                           _guild_id character varying,
                                                                           _role_id character varying)
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
    from shepard_settings.command_role_permission
    where command_id = _command_id
      and role_id = _role_id::BIGINT
      and guild_id = _guild_id::BIGINT;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.remove_context_user(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.remove_command_user(_command_name character varying,
                                                                _user_id character varying)
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
    from shepard_settings.command_user_list
    where command_id = _command_id
      and user_id = _user_id::BIGINT;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.remove_context_user_permission(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.remove_command_user_permission(_command_name character varying,
                                                                           _guild_id character varying,
                                                                           _user_id character varying)
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
    from shepard_settings.command_user_permission
    where command_id = _command_id
      and user_id = _user_id::BIGINT
      and guild_id = _guild_id::BIGINT;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_admin(character varying, boolean);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_admin(_command_name character varying,
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

    update shepard_settings.command_admin
    set admin_only = _state
    where command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_guild_check_active(character varying, boolean);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_guild_check_active(_command_name character varying,
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

    update shepard_settings.command_guild_check_state
    set state = _state
    where command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_guild_list_type(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_guild_list_type(_command_name character varying,
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
        update shepard_settings.command_guild_list_type
        set list_type = _list_type::shepard_settings.list_types
        where command_id = _command_id;
    END IF;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_nsfw(character varying, boolean);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_nsfw(_command_name character varying,
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

    update shepard_settings.command_nsfw
    set nsfw = _state
    where command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_user_check_active(character varying, boolean);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_user_check_active(_command_name character varying,
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

    update shepard_settings.command_user_check_state
    set state = _state
    where command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_context_user_list_type(character varying, character varying);

CREATE OR REPLACE FUNCTION shepard_settings.set_command_user_list_type(_command_name character varying,
                                                                       _list_type character varying DEFAULT 'WHITELIST'::character varying)
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

    IF _list_type = 'BLACKLIST' OR _list_type = 'WHITELIST' THEN
        update shepard_settings.command_user_list_type
        set list_type = _list_type::shepard_settings.list_types
        where command_id = _command_id;
    END IF;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_permission_override(character varying, character varying, boolean);

CREATE OR REPLACE FUNCTION shepard_settings.set_permission_override(_command_name character varying,
                                                                    _guild_id character varying,
                                                                    _override boolean)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS
$BODY$
BEGIN

    insert into shepard_settings.command_permission_override(command_id, guild_id, override)
    values (shepard_settings.get_or_create_command_id(_command_name), _guild_id::bigint, _override)
    on CONFLICT (command_id, guild_id)
        DO UPDATE SET override = _override;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_guild_cooldown(character varying, integer);

CREATE OR REPLACE FUNCTION shepard_settings.set_guild_cooldown(
    _command_name character varying,
    _seconds integer DEFAULT 0)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    update shepard_settings.command_cooldown
    set guild_cooldown = _seconds
    where command_id = _command_id;

END
$BODY$;

DROP FUNCTION IF EXISTS shepard_func.set_user_cooldown(character varying, integer);

CREATE OR REPLACE FUNCTION shepard_settings.set_user_cooldown(
    _command_name character varying,
    _seconds integer DEFAULT 0)
    RETURNS void
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
DECLARE
    _command_id integer;
BEGIN

    _command_id = shepard_settings.get_or_create_command_id(_command_name);

    update shepard_settings.command_cooldown
    set user_cooldown = _seconds
    where command_id = _command_id;

END
$BODY$;
