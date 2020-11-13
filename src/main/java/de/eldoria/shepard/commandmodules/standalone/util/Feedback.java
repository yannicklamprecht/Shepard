package de.eldoria.shepard.commandmodules.standalone.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.repeatcommand.LatestCommandsCollection;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.localization.enums.commands.util.FeedbackLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.wrapper.EventWrapper;
import okhttp3.OkHttpClient;

public class Feedback extends Command implements ExecutableAsync, ReqLatestCommands, ReqConfig, ReqInit {
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().build();
    private WebhookClient webhookClient;
    private LatestCommandsCollection latestCommands;
    private Config config;

    /**
     * Create a new feedback command.
     */
    public Feedback() {
        super("feedback",
                new String[]{"bugreport"},
                "command.feedback.description",
                SubCommand.builder("feedback")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.message", null, true))
                        .build(),
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                .setAuthor(new WebhookEmbed.EmbedAuthor(
                        wrapper.getAuthor().getAsTag() + " (" + wrapper.getAuthor().getId() + ")",
                        wrapper.getAuthor().getEffectiveAvatarUrl(), ""))
                .setDescription(ArgumentParser.getMessage(args, 0));
        if (wrapper.isGuildEvent()) {
            builder.setTitle(new WebhookEmbed.EmbedTitle("New "
                    + (label.equalsIgnoreCase("bugreport") ? "Bugreport" : "Feedback") + " by "
                    + wrapper.getAuthor().getAsTag()
                    + " from Guild "
                    + wrapper.getGuild().get().getName(), ""));
        } else {
            builder.setTitle(new WebhookEmbed.EmbedTitle("New "
                    + (label.equalsIgnoreCase("bugreport") ? "Bugreport" : "Feedback") + " by "
                    + wrapper.getAuthor().getAsTag(), ""));
        }

        if (label.equalsIgnoreCase("bugreport")) {
            LatestCommandsCollection.SavedCommand latestCommand = latestCommands.getLatestCommand(wrapper);
            if (latestCommand != null) {
                builder.addField(new WebhookEmbed.EmbedField(false, "Last Command",
                        latestCommand.getLabel() + " " + String.join(" ", latestCommand.getArgs())));
            }
        }

        webhookClient.send(builder.build());

        MessageSender.sendMessage(FeedbackLocale.M_THANK_YOU.tag, wrapper.getMessageChannel());
    }

    @Override
    public void addLatestCommand(LatestCommandsCollection latestCommands) {
        this.latestCommands = latestCommands;
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void init() {
        webhookClient = new WebhookClientBuilder(config.getWebhooks().getFeedback())
                .setDaemon(true)
                .setHttpClient(OK_HTTP_CLIENT)
                .setWait(false)
                .build();
    }
}
