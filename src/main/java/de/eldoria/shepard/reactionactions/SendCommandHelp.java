package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.util.reactions.EmojiCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class SendCommandHelp extends Action {

    private final Command command;
    private final MessageEventDataWrapper messageContext;

    public SendCommandHelp(Command command, MessageEventDataWrapper messageContext) {
        super(EmojiCollection.QUESTION_MARK, null, 60,false);
        this.command = command;
        this.messageContext = messageContext;
    }

    @Override
    protected void internalExecute(GuildMessageReactionAddEvent event) {
        messageContext.getAuthor().openPrivateChannel().queue(command::sendCommandUsage);
    }
}
