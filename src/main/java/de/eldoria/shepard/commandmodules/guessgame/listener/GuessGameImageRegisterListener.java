package de.eldoria.shepard.commandmodules.guessgame.listener;

import de.eldoria.shepard.commandmodules.guessgame.util.ConfigurationState;
import de.eldoria.shepard.commandmodules.guessgame.util.ImageConfiguration;
import de.eldoria.shepard.commandmodules.guessgame.util.ImageRegister;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;

import static de.eldoria.shepard.localization.enums.listener.GuessGameImageRegisterListenerLocale.M_ADDED_NSFW;
import static de.eldoria.shepard.localization.enums.listener.GuessGameImageRegisterListenerLocale.M_ADDED_SFW;
import static de.eldoria.shepard.localization.enums.listener.GuessGameImageRegisterListenerLocale.M_COPPED_REGISTERED;
import static de.eldoria.shepard.localization.enums.listener.GuessGameImageRegisterListenerLocale.M_SET_REGISTERED;

public class GuessGameImageRegisterListener extends ListenerAdapter implements ReqStatistics {

    private final ImageRegister register;
    private Statistics statistics;

    /**
     * Create a new guess game image register listener.
     *
     * @param register image register instance
     */
    public GuessGameImageRegisterListener(ImageRegister register) {
        this.register = register;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        statistics.eventDispatched(event.getJDA());


        EventWrapper wrapper = EventWrapper.wrap(event);
        ConfigurationState configurationState = register.getConfigurationState(wrapper);
        if (configurationState != ConfigurationState.NONE) {
            if (event.getMessage().getAttachments().size() == 1) {
                if (configurationState == ConfigurationState.CROPPED) {
                    MessageSender.sendMessage(M_COPPED_REGISTERED.tag, event.getChannel());
                }
                register.addImage(wrapper,
                        event.getMessage().getAttachments().get(0).getUrl());

            }
        }

        if (register.getConfigurationState(wrapper) == ConfigurationState.CONFIGURED) {
            ImageConfiguration configuration = register.getConfiguration(wrapper);

            if (register.registerConfiguration(wrapper)) {
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(event.getGuild())
                        .setTitle(configuration.isNsfw() ? M_ADDED_NSFW.tag : M_ADDED_SFW.tag)
                        .setThumbnail(configuration.getCroppedImage())
                        .setImage(configuration.getFullImage())
                        .setDescription(M_SET_REGISTERED.tag)
                        .setColor(Color.green);

                event.getChannel().sendMessage(builder.build()).queue();

            }
        }
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}