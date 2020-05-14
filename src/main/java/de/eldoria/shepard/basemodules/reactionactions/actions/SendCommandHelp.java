package de.eldoria.shepard.basemodules.reactionactions.actions;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.CommandUtil;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class SendCommandHelp extends Action {

    private final Command command;
    private final String prefix;

    /**
     * Creates a new send command help action.
     *
     * @param prefix prefix of guild.
     * @param command command for which the help should be send.
     */
    public SendCommandHelp(Command command, String prefix) {
        super(Emoji.QUESTION_MARK, null, 60, true);
        this.command = command;
        this.prefix = prefix;
    }

    @Override
    protected void internalExecute(EventWrapper wrapper) {
        wrapper.getMessageChannel().sendMessage(
                CommandUtil.getCommandHelpEmbed(command, wrapper, prefix)).queue();
    }
}
