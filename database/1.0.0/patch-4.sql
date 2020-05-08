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

    RETURN coalesce(result, ARRAY[]::bigint[]);

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

    RETURN coalesce(result, ARRAY[]::bigint[]);

END
$BODY$;

