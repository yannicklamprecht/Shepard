package de.eldoria.shepard.localization.enums.commands;

public interface LocaleEnum {
    default String tag() {
        try {
            return (String) this.getClass().getField("tag").get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
