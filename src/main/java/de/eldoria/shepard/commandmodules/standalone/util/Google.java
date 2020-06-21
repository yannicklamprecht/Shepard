package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.util.GoogleLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Google extends Command implements Executable {
    public Google() {
        super("google",
                new String[] {"ask", "yahoo", "bing", "startpage",
                        "aol", "duckduckgo", "duck", "quant",
                        "wikipedia", "lmgtfy", "mcseu", "reddit"},
                GoogleLocale.DESCRIPTION.tag,
                SubCommand.builder("query")
                        .addSubcommand(GoogleLocale.C_SEARCH.tag,
                                Parameter.createInput(GoogleLocale.A_SEARCH.tag, GoogleLocale.AD_SEARCH.tag, true))
                        .build(),
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        SearchEngine engine = SearchEngine.getSearchEngine(label);
        String search = engine.getSearch(String.join(" ", args));

        if (search == null) {
            MessageSender.sendMessage(GoogleLocale.M_INVALID.tag, wrapper.getMessageChannel());
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper);
        builder.setTitle(engine.caption, search);
        builder.setAuthor(engine.name, engine.base, engine.icon);
        builder.setColor(engine.color);
        builder.setDescription(search);
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    private enum SearchEngine {
        GOOGLE("Google","https://www.google.com",
                "https://www.google.com/search?q={search}",
                new Color(67, 135, 249), GoogleLocale.M_GOOGLE.tag,
                "https://www.google.com/images/branding/googleg/1x/googleg_standard_color_128dp.png"),
        ASK("Ask.com","https://www.ask.com",
                "https://www.ask.com/web?q={search}",
                new Color(207, 0, 0), GoogleLocale.M_ASK.tag,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Ask.com_Logo.svg/1200px-Ask.com_Logo.svg.png"),
        YAHOO("yahoo!","https://search.yahoo.com",
                "https://search.yahoo.com/search?p={search}",
                new Color(96, 1, 210), GoogleLocale.M_YAHOO.tag,
                "https://s.yimg.com/pv/static/img/y_icon_iphone_76.min.png"),
        BING("Bing","https://www.bing.com/",
                "https://www.bing.com/search?q={search}",
                new Color(12, 132, 132), GoogleLocale.M_BING.tag,
                "https://www.bing.com/sa/simg/bing_p_rr_teal_min.ico"),
        STARTPAGE("Startpage","https://www.startpage.com",
                "https://www.startpage.com/do/search?q={search}",
                new Color(55, 70, 119), GoogleLocale.M_STARTPAGE.tag,
                "https://www.startpage.com/sp/spstatic/favicons/favicon-144x144.png"),
        AOL("Aol.","https://search.aol.com",
                "https://search.aol.com/aol/search?q={search}",
                new Color(23,150,196), GoogleLocale.M_AOL.tag,
                "https://s.yimg.com/pv/static/img/aol_touch_icon_dkblue_114.png"),
        DUCK_DUCK_GO("DuckDuckGo","https://duckduckgo.com",
                "https://duckduckgo.com/?q={search}",
                new Color(222, 88, 51), GoogleLocale.M_DUCK.tag,
                "https://duckduckgo.com/assets/icons/meta/DDG-iOS-icon_152x152.png", "duck"),
        QUANT("Quant","https://www.qwant.com",
                "https://www.qwant.com/?q={search}",
                new Color(255, 218, 18), GoogleLocale.M_QUANT.tag,
                "https://www.qwant.com/favicon-64.png"),
        WIKIPEDIA("Wikipedia","https://en.wikipedia.org",
                "https://en.wikipedia.org/w/index.php?sort=relevance&search={search}&fulltext=1",
                new Color(246, 246, 246), GoogleLocale.M_WIKIPEDIA.tag,
                "https://en.wikipedia.org/static/apple-touch/wikipedia.png"),
        LMGTFY("LMGTFY!","https://lmgtfy.com",
                "https://lmgtfy.com/?q={search}",
                Color.WHITE, GoogleLocale.M_LMGTFY.tag,
                "https://lmgtfy.com/apple-icon-114x114.png"),
        MCSEU("MCSEU","https://minecraft-server.eu/",
                "https://minecraft-server.eu/serverlist/search/?search={search}",
                new Color(45, 131, 219), GoogleLocale.M_MCSEU.tag,
                "http://chojo.u.catgirlsare.sexy/h4HqX2kj.png"),
        REDDIT("Reddit", "https://www.reddit.com",
                "https://www.reddit.com/search/?q={search}",
                new Color(255, 69, 0), GoogleLocale.M_REDDIT.tag,
                "https://www.redditstatic.com/desktop2x/img/favicon/favicon-96x96.png");

        private final String name;
        private final String base;
        private final String search;
        private final Color color;
        private final String caption;
        private final String icon;
        private final String[] alias;

        SearchEngine(String name, String base, String search, Color color, String caption, String icon, String... alias) {
            this.name = name;
            this.base = base;
            this.search = search;
            this.color = color;
            this.caption = caption;
            this.icon = icon;
            this.alias = Objects.requireNonNullElse(alias, new String[0]);
        }

        public static SearchEngine getSearchEngine(String id) {
            for (SearchEngine engine : values()) {
                if (engine.name().equalsIgnoreCase(id)) return engine;
                for (String alias : engine.alias) {
                    if (alias.equalsIgnoreCase(id)) return engine;
                }
            }
            throw new IllegalArgumentException("Search engine " + id + " is invalid but requested");
        }

        public String getSearch(String string) {
            //String noSpaces = string.replace(" ", "+");
            String searchTerm;
            try {
                searchTerm = URLEncoder.encode(string, StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                return null;
            }

            return this.search.replace("{search}", searchTerm);
        }
    }
}
