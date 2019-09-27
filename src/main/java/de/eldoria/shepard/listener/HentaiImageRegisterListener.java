package de.eldoria.shepard.listener;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.guessgame.ConfigurationType;
import de.eldoria.shepard.minigames.guessgame.ImageConfiguration;
import de.eldoria.shepard.minigames.guessgame.ImageRegister;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;

public class HentaiImageRegisterListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        ImageRegister register = ImageRegister.getInstance();
        ConfigurationType configurationState = register.getConfigurationState(event.getAuthor());
        if (configurationState != ConfigurationType.NONE) {
            if (event.getMessage().getAttachments().size() == 1) {
                if (configurationState == ConfigurationType.CROPPED) {
                    MessageSender.sendMessage("Cropped Image Registered." + System.lineSeparator()
                            + "Please send the full image", event.getChannel());
                }
                register.addImage(event.getAuthor(),
                        event.getMessage().getAttachments().get(0).getUrl());

            }
        }

        if (register.getConfigurationState(event.getAuthor()) == ConfigurationType.CONFIGURED) {
            ImageConfiguration configuration = register.getConfiguration(event.getAuthor());

            if (register.registerConfiguration(event.getAuthor())) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("Added new " + (configuration.isHentai() ? "hentai" : "non hentai")
                                + " image set.")
                        .setThumbnail(configuration.getCroppedImage())
                        .setImage(configuration.getFullImage())
                        .setDescription("Registered with thumbnail and full image.")
                        .setColor(Color.green);

                event.getChannel().sendMessage(builder.build()).queue();

            }
        }
    }
}