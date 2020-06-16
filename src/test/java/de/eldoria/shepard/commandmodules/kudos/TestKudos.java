package de.eldoria.shepard.commandmodules.kudos;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.kudos.commands.Kudos;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.util.DummyMember;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestKudos {

    @Mock
    KudoData kudoData;

    @Mock
    ArgumentParser parser;
    
    @InjectMocks
    private Kudos kudos;


    @BeforeEach
    public void setUp(){
        when(parser.getGuildMember(any(Guild.class), anyString())).thenReturn(new DummyMember());
    }

    @Test
    public void testGive(){
        String command = "g @Chojo 50";
        String[] args = command.split(" ");

        kudos.execute(args[0], args, EventWrapper.fakeEmpty());

        verify(kudoData).tryTakeCompletePoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class));
        verify(kudoData).addRubberPoints(any(Guild.class), any(User.class), anyInt(), any(EventWrapper.class));
    }

    @Test
    public void testTop(){
        String command = "top";
        String[] args = command.split(" ");

        kudos.execute(args[0], args, EventWrapper.fakeEmpty());

    }
}
