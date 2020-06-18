package de.eldoria.shepard.util;

import de.eldoria.shepard.DummyClasses.DummyMember;
import de.eldoria.shepard.DummyClasses.DummyUser;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.core.configuration.Loader;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestUtils {

    private static JDAImpl jda;

    @BeforeAll
    public static void setup() throws IOException {
        Config config = Loader.loadConfig();
        AuthorizationConfig authconfig = new AuthorizationConfig(config.getGeneralSettings().getToken());
        jda = new JDAImpl(authconfig);
    }

    @Test
    public void ReplacerTest(){
        assertThat(
                Replacer.applyUserPlaceholder(new DummyMember(jda).getUser(),
                        "User:{user_name} Tag:{user_tag} Mention:{user_mention}"),
                Is.is("User:Test User Tag:@Test User Mention:@Test User"));
    }

}
