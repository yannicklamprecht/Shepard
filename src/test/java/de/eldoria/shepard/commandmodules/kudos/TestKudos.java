package de.eldoria.shepard.commandmodules.kudos;

import de.eldoria.shepard.DummyClasses.DummyGuild;
import de.eldoria.shepard.DummyClasses.DummyMember;
import de.eldoria.shepard.DummyClasses.DummyTextChannel;
import de.eldoria.shepard.DummyClasses.DummyUser;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.kudos.commands.Kudos;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.core.configuration.Loader;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class TestKudos {

    private static JDAImpl jda;

    @Mock
    static
    KudoData kudoData;

    @Mock
    static
    ArgumentParser parser;

    @Mock
    static
    EventWrapper wrapper;

    @Mock
    static
    DummyTextChannel channel;

    @Mock
    static
    MessageAction message;
    
    @InjectMocks
    private Kudos kudos;

    @BeforeAll
    public static void setUp() throws IOException {
        Config config = Loader.loadConfig();
        AuthorizationConfig authconfig = new AuthorizationConfig(config.getGeneralSettings().getToken());
        jda = new JDAImpl(authconfig);
        new LanguageHandler().init();
    }

    @BeforeEach
    public void beforeEach(){
        when(wrapper.getGuild()).thenReturn(Optional.of(new DummyGuild(jda)));
        when(wrapper.getMessageChannel()).thenReturn(channel);
    }


    @Test
    public void testGive(){
        when(parser.getGuildMember(any(Guild.class), anyString())).thenReturn(new DummyMember(jda));
        when(kudoData.tryTakeCompletePoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class))).thenReturn(true);
        when(kudoData.addRubberPoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class))).thenReturn(true);
        when(channel.sendMessage(anyString())).thenReturn(message);
        when(wrapper.getAuthor()).thenReturn(new DummyUser(jda));
        when(wrapper.getMember()).thenReturn(Optional.of(new DummyMember(jda)));

        String command = "g @Chojo 50";
        String[] args = command.split(" ");


        kudos.execute(args[0], args, wrapper);

        verify(kudoData).tryTakeCompletePoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class));
        verify(kudoData).addRubberPoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class));
    }

    @Test
    public void testTop(){
        when(channel.sendMessage(any(MessageEmbed.class))).thenReturn(message);

        String command = "top";
        String[] args = command.split(" ");

        kudos.execute(args[0], args, wrapper);

    }
}
