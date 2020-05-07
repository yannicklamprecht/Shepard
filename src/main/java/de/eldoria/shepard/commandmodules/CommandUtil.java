package de.eldoria.shepard.commandmodules;

import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.CommandLocale.BASE_COMMAND;
import static java.lang.System.lineSeparator;

public class CommandUtil {
    /**
     * Get the command help as a localized embed.
     *
     * @param command command to generate the help
     * @param guild   guild for locale settings
     * @param prefix  prefix of guild
     * @return localized embed.
     */
    public static MessageEmbed getCommandHelpEmbed(Command command, Guild guild, String prefix) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(guild);

        builder.setTitle("__**" + HelpLocale.M_HELP_FOR_COMMAND + " " + command.getCommandName() + "**__")
                .setColor(Color.green);

        builder.setDescription(command.getCommandDesc());

        // Build alias field
        if (command.getCommandAliases() != null && command.getCommandAliases().length != 0) {
            builder.appendDescription(lineSeparator() + "__**" + HelpLocale.W_ALIASES + ":**__ "
                    + String.join(", ", command.getCommandAliases()));
        }

        // Build main command field. Only present when command has a standalone function and subcommands.
        if (command.isStandalone() && command.getSubCommands().length != 0) {
            builder.addField("**__" + BASE_COMMAND.tag + "__**:",
                    "**" + prefix + command.getCommandName() + "**\n" + command.getStandaloneDescription(), false);
        }

        // Build subcommand field.
        if (command.getSubCommands().length != 0) {
            List<String> subcommandHelp = getSubcommandHelp(command.getSubCommands()).stream()
                    .map(s -> TextLocalizer.localizeAll(s, guild).replace("\n", "\n> "))
                    .collect(Collectors.toList());
            List<String> chunks = new ArrayList<>();

            StringBuilder sBuilder = new StringBuilder();
            for (var s : subcommandHelp) {
                if (sBuilder.length() + s.length() > 1024) {
                    chunks.add(sBuilder.toString());
                    sBuilder.setLength(0);
                }
                sBuilder.append(s).append("\n\n");
            }
            if (builder.length() != 0) {
                chunks.add(sBuilder.toString());
            }

            for (var c : chunks) {
                builder.addField("",
                        c.replace("{prefix}", prefix), false);
            }
        }
        return builder.build();

    }

    /**
     * Get the subcommand help.
     *
     * @return subcommand help as preformatted string.
     */
    private static List<String> getSubcommandHelp(SubCommand[] subCommands) {
        List<String> subCommandsHelp = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            subCommandsHelp.add(subCommand.getCommandPattern());
        }
        return subCommandsHelp;
    }

    /**
     * Generate the lazy command depending on all commands in an array.
     *
     * @param subCommands subcommands for lazy command generation
     */
    protected static void generateLazySubCommands(SubCommand... subCommands) {
        // Lazy sub commands are unique in their parameter stage. but not in all sub commands
        // Iterate though each stage
        List<Parameter> parameters = new ArrayList<>();

        int maxParams = 0;
        for (var subCommand : subCommands) {
            maxParams = Math.max(maxParams, subCommand.getParameters().length);
        }

        for (int parameterStage = 0; parameterStage < maxParams; parameterStage++) {
            // Search commands in this parameter stage
            for (SubCommand s : subCommands) {
                Parameter[] p = s.getParameters();
                if (p.length > parameterStage) {
                    if (p[parameterStage].isCommand()) {
                        parameters.add(p[parameterStage]);
                    }
                }
            }

            // generate lazy commands
            int i = 0;
            do {
                for (Parameter p : parameters) {
                    p.setShortCommand(p.generateShortCommand(i));
                }
                i++;
                parameters = new ArrayList<>(getNotUniqueCommands(parameters));
            } while (parameters.size() != 0);
        }
    }

    private static Set<Parameter> getNotUniqueCommands(List<Parameter> parameters) {
        Set<Parameter> p = new HashSet<>();

        for (Parameter param1 : parameters) {
            for (Parameter param2 : parameters) {
                if (param1.getCommandName().equals(param2.getCommandName())) continue;
                if (param1.getShortCommand().equals(param2.getShortCommand())) {
                    p.add(param2);
                }
            }
        }
        return p;
    }

}
