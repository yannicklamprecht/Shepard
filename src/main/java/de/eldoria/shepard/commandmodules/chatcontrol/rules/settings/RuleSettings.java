package de.eldoria.shepard.commandmodules.chatcontrol.rules.settings;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.chatcontrol.rules.Rules;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
public class RuleSettings {
    private static final Pattern linkPattern = Pattern.compile("\\shttp(s)?:.+?\\..+?\\s");

    private final RuleSetting<Void> normal;
    private final RuleSetting<List<String>> link;
    private final RuleSetting<Integer> mentions;
    private final RuleSetting<List<String>> attachments;
    private final RuleSetting<CapsSettings> caps;
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
            if (mentionIsNotAllowed(message)) {
                return Rules.MAX_MENTIONS;
            }
        }


        if (caps.getState() != RuleState.DISABLED) {
            if (capsNotAllowed(message)) {
                return Rules.CAPS_MESSAGES;
            }
        }

        if (link.getState() != RuleState.DISABLED) {
            if (linkIsNotAllowed(message)) {
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
            if (attachmentIsNotAllowed(message)) {
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

            if (normalIsNotAllowed(message)) {
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
    private boolean normalIsNotAllowed(Message message) {
        switch (normal.getState()) {
            case ALLOW:
                return false;
            case DENY:
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + normal.getState());
        }
    }

    public boolean linkIsNotAllowed(Message message) {
        String contentStripped = message.getContentStripped().toLowerCase();
        Matcher matcher = linkPattern.matcher(contentStripped);

        List<String> urls = new ArrayList<>();

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        boolean contains = true;
        if (link.getState() == RuleState.ALLOW) {
            for (String url : urls) {
                boolean allowed = false;
                for (String tld : link.getValue()) {
                    if (url.contains(tld)) {
                        allowed = true;
                        break;
                    }
                }
                // Check if message contains a unallowed url;
                if (!allowed) return true;
            }
            return false;
        }

        if (link.getState() == RuleState.DENY) {
            for (String url : urls) {
                boolean denied = false;
                for (String tld : link.getValue()) {
                    if (url.contains(tld)) {
                        denied = true;
                        break;
                    }
                }
                // Check if message contains a unallowed url;
                if (!denied) return true;
            }
            return false;
        }

        return false;
    }

    private boolean mentionIsNotAllowed(Message message) {
        int mentions = message.getMentions().size();
        return mentions > this.mentions.getValue();
    }

    private boolean attachmentIsNotAllowed(Message message) {
        List<String> extensions = message.getAttachments()
                .stream()
                .map(Message.Attachment::getFileExtension)
                .collect(Collectors.toList());

        if (attachments.getState() == RuleState.ALLOW) {
            for (String extension : extensions) {
                if (!attachments.getValue().contains(extension)) return true;
            }
        }

        if (attachments.getState() == RuleState.DENY) {
            for (String extension : extensions) {
                if (attachments.getValue().contains(extension)) return true;
            }
        }
        return false;
    }

    private boolean capsNotAllowed(Message message) {
        String contentStripped = message.getContentStripped();
        int upperCount = 0;
        int lowerCount = 0;
        for (char c : contentStripped.toCharArray()) {
            if (Character.isWhitespace(c)) continue;
            if (Character.isUpperCase(c)) {
                upperCount++;
            } else {
                lowerCount++;
            }
        }

        if (upperCount + lowerCount < caps.getValue().getMinMessageLength()) return false;

        return (upperCount + lowerCount) / upperCount * 100 > caps.getValue().getCapsRatio();
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

    @Data
    public static class CapsSettings {
        private final int minMessageLength;
        private final int capsRatio;
    }
}
