package de.eldoria.shepard.basemodules.commanddispatching.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import javax.sql.DataSource;

public class ExecutionValidator implements ReqDataSource {

    private CommandData commandData;

    /**
     * Create a new execution validator.
     */
    public ExecutionValidator() {
    }

    /**
     * Checks if a user can access a command globally.
     *
     * @param command        command to check
     * @param messageContext event wrapper
     * @return true if the user can access this command
     */
    public boolean canAccess(Command command, MessageEventDataWrapper messageContext) {
        return commandData.canAccess(command, messageContext.getMember());
        //return isContextValid(command, messageContext);
    }

    /**
     * Checks if a user can use a command based on guild permission settings.
     *
     * @param command        command to check
     * @param messageContext event wrapper
     * @return true if the user can use this command
     */
    public boolean canUse(Command command, MessageEventDataWrapper messageContext) {
        Member member = messageContext.getMember();
        //Checks if a command is not admin only and override is inactive or if the member is a administrator
        if ((member != null && member.hasPermission(Permission.ADMINISTRATOR))) {
            return true;
        }

        return commandData.canUse(command, messageContext.getMember());
        //return hasPermission(command, messageContext);
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }
}
