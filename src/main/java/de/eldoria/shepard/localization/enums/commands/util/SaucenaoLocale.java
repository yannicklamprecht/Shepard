package de.eldoria.shepard.localization.enums.commands.util;

public enum SaucenaoLocale {
    DESCRIPTION("command.sauce.description"),
    C_STANDALONE("command.sauce.subCommand.standalone"),
    C_BASE("command.sauce.subCommand.base"),
    A_LINK("command.sauce.argument"),
    AD_LINK("command.sauce.argumentDescription"),
    M_GUILD_LIMIT("command.sauce.message.guildLimit"),
    M_USER_LIMIT("command.sauce.message.userLimit"),
    M_TOTAL_LIMIT("command.sauce.message.totalLimit"),
    M_IMAGE_OR_LINK("command.sauce.message.imageOrLink"),
    M_NOT_A_IMAGE("command.sauce.message.notAImage"),
    M_NOT_FOUND("command.sauce.message.notFound"),
    M_MATCH("command.sauce.message.match"),
    M_SIMILARITY("command.sauce.message.similarity"),
    M_IMAGE_INFO("command.sauce.message.imageInfo"),
    M_MOVIE_INFO("command.sauce.message.movieInfo"),
    M_HENTAI_INFO("command.sauce.message.hentaiInfo"),
    M_ANIME_INFO("command.sauce.message.animeInfo"),
    M_MANGA_INFO("command.sauce.message.mangaInfo"),
    M_ENTRY_INFO("command.sauce.message.entryInfo"),
    M_TITLE("command.sauce.message.title"),
    M_ARTIST("command.sauce.message.artist"),
    M_UPLOADER("command.sauce.message.uploader"),
    M_ENG_TITLE("command.sauce.message.engTitle"),
    M_JP_TITLE("command.sauce.message.jpTitle"),
    M_SOURCE("command.sauce.message.source"),
    M_PART("command.sauce.message.part"),
    M_YEAR("command.sauce.message.year"),
    M_TIME("command.sauce.message.time"),
    M_USER("command.sauce.message.user"),
    M_TYPE("command.sauce.message.type"),
    M_AUTHOR("command.sauce.message.author"),
    M_CHARACTER("command.sauce.message.character"),
    M_CREATOR("command.sauce.message.creator"),
    M_TAGS("command.sauce.message.tags"),
    M_POWERED("command.sauce.message.powered");
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    SaucenaoLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
