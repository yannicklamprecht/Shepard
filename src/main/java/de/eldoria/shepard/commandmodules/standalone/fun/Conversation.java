package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Conversation extends Command implements Executable {
    private final Map<LocaleCode, Convos> conversations = new EnumMap<>(LocaleCode.class);
    private final LanguageHandler languageHandler = LanguageHandler.getInstance();

    public Conversation() {
        super("conversation", new String[]{"convo"},
                "command.conversation.description",
                SubCommand.builder("convo")
                        .addSubcommand("command.conversation.subCommand.starter",
                                Parameter.createCommand("starter"))
                        .addSubcommand("command.conversation.subCommand.questions",
                                Parameter.createCommand("question"))
                        .addSubcommand("command.conversation.subCommand.rather",
                                Parameter.createCommand("rather"))
                        .addSubcommand("command.conversation.subCommand.know",
                                Parameter.createCommand("know"))
                        .addSubcommand("command.conversation.subCommand.philosoph",
                                Parameter.createCommand("philosoph"))
                        .addSubcommand("command.conversation.subCommand.personal",
                                Parameter.createCommand("personal"))
                        .addSubcommand("command.conversation.subCommand.thisthat",
                                Parameter.createCommand("thisthat"))
                        .addSubcommand("command.conversation.subCommand.funny",
                                Parameter.createCommand("funny"))
                        .addSubcommand("command.conversation.subCommand.deep",
                                Parameter.createCommand("deep"))
                        .addSubcommand("command.conversation.subCommand.dating",
                                Parameter.createCommand("dating"))
                        .addSubcommand("command.conversation.subCommand.hypthese",
                                Parameter.createCommand("hypthese"))
                        .addSubcommand("command.conversation.subCommand.shortQuestions",
                                Parameter.createCommand("shortQuestions"))
                        .addSubcommand("command.conversation.subCommand.longQuestions",
                                Parameter.createCommand("longQuestions"))
                        .build(),
                "command.conversation.description",
                CommandCategory.FUN);

        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(ConvoList.class), representer);

        for (LocaleCode value : LocaleCode.values()) {
            ConvoList load = yaml.load(getClass().getResourceAsStream("/conversation/conversation_" + value.code + ".yml"));
            conversations.put(value, new Convos(load));
        }
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        LocaleCode guildLocale = languageHandler.getGuildLocale(wrapper.getGuild().orElse(null));
        Convos convo = conversations.get(guildLocale);
        if (args.length == 0) {
            conversation(convo.getAllConvo(), wrapper);
            return;
        }

        String cmd = args[0];
        if (isSubCommand("starter", cmd)) {
            conversation(convo.getStarterConvo(), wrapper);
            return;
        }
        if (isSubCommand("question", cmd)) {
            conversation(convo.getQuestionsConvo(), wrapper);
            return;
        }
        if (isSubCommand("rather", cmd)) {
            conversation(convo.getRatherConvo(), wrapper);
            return;
        }
        if (isSubCommand("know", cmd)) {
            conversation(convo.getKnowConvo(), wrapper);
            return;
        }
        if (isSubCommand("philosoph", cmd)) {
            conversation(convo.getPhilosophConvo(), wrapper);
            return;
        }
        if (isSubCommand("personal", cmd)) {
            conversation(convo.getPersonalConvo(), wrapper);
            return;
        }
        if (isSubCommand("thisThat", cmd)) {
            conversation(convo.getThisthatConvo(), wrapper);
            return;
        }
        if (isSubCommand("funny", cmd)) {
            conversation(convo.getFunnyConvo(), wrapper);
            return;
        }
        if (isSubCommand("deep", cmd)) {
            conversation(convo.getDeepConvo(), wrapper);
            return;
        }
        if (isSubCommand("dating", cmd)) {
            conversation(convo.getDatingConvo(), wrapper);
            return;
        }
        if (isSubCommand("hypthese", cmd)) {
            conversation(convo.getHyptheseConvo(), wrapper);
            return;
        }
        if (isSubCommand("shortQuestions", cmd)) {
            conversation(convo.getShortQuestionsConvo(), wrapper);
            return;
        }
        if (isSubCommand("longQuestions", cmd)) {
            conversation(convo.getLongQuestionsConvo(), wrapper);
            return;
        }
    }

    private void conversation(Convo convo, EventWrapper wrapper) {
        MessageEmbed build = new LocalizedEmbedBuilder(wrapper)
                .setTitle(convo.getTitle())
                .setDescription(convo.getQuestion())
                .setColor(Colors.Pastel.AQUA)
                .setFooter("https://conversationstartersworld.com")
                .build();
        wrapper.getMessageChannel().sendMessage(build).queue();
    }


    public static class Convos {
        private transient List<Convo> starterConvo;
        private transient List<Convo> questionsConvo;
        private transient List<Convo> ratherConvo;
        private transient List<Convo> knowConvo;
        private transient List<Convo> philosophConvo;
        private transient List<Convo> personalConvo;
        private transient List<Convo> thisthatConvo;
        private transient List<Convo> funnyConvo;
        private transient List<Convo> deepConvo;
        private transient List<Convo> datingConvo;
        private transient List<Convo> hyptheseConvo;
        private transient List<Convo> shortQuestionsConvo;
        private transient List<Convo> longQuestionsConvo;

        public Convos(ConvoList convoList) {
            starterConvo = toConvoList("command.conversation.title.starter", convoList.starter);
            questionsConvo = toConvoList("command.conversation.title.questions", convoList.questions);
            ratherConvo = toConvoList("command.conversation.title.rather", convoList.rather);
            knowConvo = toConvoList("command.conversation.title.know", convoList.know);
            philosophConvo = toConvoList("command.conversation.title.philosoph", convoList.philosophical);
            personalConvo = toConvoList("command.conversation.title.personal", convoList.personal);
            thisthatConvo = toConvoList("command.conversation.title.thisthat", convoList.thisthat);
            funnyConvo = toConvoList("command.conversation.title.funny", convoList.funny);
            deepConvo = toConvoList("command.conversation.title.deep", convoList.deep);
            datingConvo = toConvoList("command.conversation.title.dating", convoList.dating);
            hyptheseConvo = toConvoList("command.conversation.title.hypthese", convoList.hypothetical);
            shortQuestionsConvo = toConvoList("command.conversation.title.shortQuestions", convoList.shortQuestions);
            longQuestionsConvo = toConvoList("command.conversation.title.longQuestions", convoList.longQuestions);
        }

        public Convo getStarterConvo() {
            return getRandom(starterConvo);
        }

        public Convo getQuestionsConvo() {
            return getRandom(questionsConvo);
        }

        public Convo getRatherConvo() {
            return getRandom(ratherConvo);
        }

        public Convo getKnowConvo() {
            return getRandom(knowConvo);
        }

        public Convo getPhilosophConvo() {
            return getRandom(philosophConvo);
        }

        public Convo getPersonalConvo() {
            return getRandom(personalConvo);
        }

        public Convo getThisthatConvo() {
            return getRandom(thisthatConvo);
        }

        public Convo getFunnyConvo() {
            return getRandom(funnyConvo);
        }

        public Convo getDeepConvo() {
            return getRandom(deepConvo);
        }

        public Convo getDatingConvo() {
            return getRandom(datingConvo);
        }

        public Convo getHyptheseConvo() {
            return getRandom(hyptheseConvo);
        }

        public Convo getShortQuestionsConvo() {
            return getRandom(shortQuestionsConvo);
        }

        public Convo getLongQuestionsConvo() {
            return getRandom(longQuestionsConvo);
        }

        public Convo getAllConvo() {
            List<Convo> convos = new ArrayList<>();
            convos.add(getStarterConvo());
            convos.add(getQuestionsConvo());
            convos.add(getRatherConvo());
            convos.add(getKnowConvo());
            convos.add(getPhilosophConvo());
            convos.add(getPersonalConvo());
            convos.add(getThisthatConvo());
            convos.add(getFunnyConvo());
            convos.add(getDeepConvo());
            convos.add(getDatingConvo());
            convos.add(getHyptheseConvo());
            convos.add(getShortQuestionsConvo());
            convos.add(getLongQuestionsConvo());
            return getRandom(convos);
        }

        private List<Convo> toConvoList(String title, String[] messages) {
            return Arrays.stream(messages).map(s -> new Convo(title, s)).collect(Collectors.toList());
        }

        private Convo getRandom(List<Convo> convos) {
            return convos.get(ThreadLocalRandom.current().nextInt(convos.size()));
        }

    }

    @Data
    public static class ConvoList {
        private String[] starter = null;
        private String[] questions = null;
        private String[] rather = null;
        private String[] know = null;
        private String[] philosophical = null;
        private String[] personal = null;
        private String[] thisthat = null;
        private String[] funny = null;
        private String[] deep = null;
        private String[] dating = null;
        private String[] hypothetical = null;
        private String[] shortQuestions = null;
        private String[] longQuestions = null;
    }

    @Getter
    private static class Convo {
        private final String title;
        private final String question;

        public Convo(String title, String question) {
            this.title = title;
            this.question = question;
        }
    }
}
