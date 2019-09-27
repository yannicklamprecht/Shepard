package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.HentaiOrNotData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.guessgame.ImageRegister;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static java.lang.System.lineSeparator;

public class GuessGameConfig extends Command {

    public GuessGameConfig() {
        commandName = "guessGameConfig";
        commandAliases = new String[] {"ggc", "hentaiconfig"};
        commandDesc = "Manage Hentai Images";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__ddImage** -> Add image to database" + lineSeparator()
                                + "**__r__emoveImage** -> Remove image from database" + lineSeparator()
                                + "**__c__ancelRegistration** -> Cancel registration of image", true),
                new CommandArg("values",
                        "**addImage** -> [`true` or `nsfw` if hentai | `false` or `sfw` if not]"  + lineSeparator()
                                + "**removeImage** -> [url of cropped or full image]" + lineSeparator()
                                + "**cancelRegistration** -> leave empty", false)
        };
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("addImage") || cmd.equalsIgnoreCase("a")) {
            addImage(args, messageContext);
            return;
        }

        if (cmd.equalsIgnoreCase("removeImage") || cmd.equalsIgnoreCase("r")) {
            removeImage(args, messageContext);
            return;
        }
        if (cmd.equalsIgnoreCase("cancelRegistration") || cmd.equalsIgnoreCase("c")) {
            ImageRegister.getInstance().cancelConfiguration(messageContext.getAuthor());
            MessageSender.sendMessage("Registration canceled.", messageContext.getChannel());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void removeImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }
        HentaiOrNotData.removeHentaiImage(args[1], messageContext);
    }

    private void addImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }
        String replace = args[1].replace("sfw", "false").replace("nsfw", "true");
        BooleanState booleanState = Verifier.checkAndGetBoolean(replace);
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }
        ImageRegister.getInstance().startConfiguration(messageContext.getAuthor(),
                booleanState.stateABoolean);
        MessageSender.sendMessage("Started registration of new image set." + lineSeparator()
                + "Please send the cropped image.", messageContext.getChannel());
    }
}
