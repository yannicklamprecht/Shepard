package de.eldoria.shepard.commandmodules.chatcontrol.rules.settings;

import de.eldoria.shepard.commandmodules.chatcontrol.rules.Rules;
import lombok.Builder;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public class RuleSettings {
    private final RuleSetting<Void> normal;
    private final RuleSetting<List<String>> link;
    private final RuleSetting<Integer> mentions;
    private final RuleSetting<List<String>> attachments;
    private final RuleSetting<Integer> caps;
    private final RuleSetting<Integer> duplicated;
    private final RuleSetting<List<String>> badword;
    private final RuleSetting<String> regex;

    /**
     * Check if a message violates a rule set in this rule settings object.
     *
     * @param message message to check.
     * @return returns the rule which was violated by the message or null if no rule was violated.
     */
    public Rules checkRules(Message message) {
        if (badword.getState() != RuleState.DISABLED) {
            if (checkBadword(message)) {
                return Rules.BADWORD_MESSAGES;
            }
        }

        if (mentions.getState() != RuleState.DISABLED) {
            if (checkMentions(message)) {
                return Rules.MAX_MENTIONS;
            }
        }


        if (caps.getState() != RuleState.DISABLED) {
            if (checkCaps(message)) {
                return Rules.CAPS_MESSAGES;
            }
        }

        if (link.getState() != RuleState.DISABLED) {
            if (checkLink(message)) {
                return Rules.LINK_MESSAGE;
            } else {
                return null;
            }
        }

        if (duplicated.getState() != RuleState.DISABLED) {
            if (checkDuplicated(message)) {
                return Rules.DUPLICATED_MESSAGES;
            }
        }

        if (attachments.getState() != RuleState.DISABLED) {
            if (checkAttachments(message)) {
                return Rules.ATTACHMENT_MESSAGE;
            } else {
                return null;
            }
        }

        if (regex.getState() != RuleState.DISABLED) {
            if (checkRegex(message)) {
                return Rules.REGEX_MESSAGES;
            } else {
                return null;
            }
        }

        if (normal.getState() != RuleState.DISABLED) {

            if (checkNormal(message)) {
                return Rules.NORMAL_MESSAGE;
            } else {
                return null;
            }
        }

        return null;
    }

    /**
     * Check if a message is not allowed to be send.
     *
     * @param message message to check
     * @return true if the message is not allowed.
     */
    private boolean checkNormal(Message message) {
        switch (normal.getState()) {
            case ALLOW:
                return false;
            case DENY:
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + normal.getState());
        }
    }

    public boolean checkLink(Message message) {
        String contentStripped = message.getContentStripped();

        boolean contains = false;
        //TODO: Search for urls in message.
        if (link.getState() == RuleState.ALLOW) {
            for (String s : link.getValue()) {
                if (contentStripped.contains(s)) {
                    contains = true;
                    break;
                }
            }
        }

        return normal.getState() == RuleState.ALLOW ? !contains : contains;
    }

    private boolean checkMentions(Message message) {
        int mentions = message.getMentions().size();
        return mentions > this.mentions.getValue();
    }

    private boolean checkAttachments(Message message) {
        List<String> extensions = message.getAttachments()
                .stream()
                .map(Message.Attachment::getFileExtension)
                .collect(Collectors.toList());

        for (String extension : extensions) {
            if (attachments.getValue().contains(extension)) return true;
        }
        return false;
    }

    private boolean checkCaps(Message message) {
        return true;
    }

    private boolean checkDuplicated(Message message) {
        return true;
    }

    private boolean checkBadword(Message message) {
        return true;
    }

    private boolean checkRegex(Message message) {
        return true;
    }
}
