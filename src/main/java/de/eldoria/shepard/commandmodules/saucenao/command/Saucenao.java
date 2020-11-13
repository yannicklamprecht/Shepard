package de.eldoria.shepard.commandmodules.saucenao.command;

import de.eldoria.shepard.C;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.ResultEntry;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.SauceIndex;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.SauceResponse;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.SaucenaoApiWrapper;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Anime;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Danbooru;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.DeviantArt;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.E621Net;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Gelbooru;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.HMisc;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.IdolComplex;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Imdb;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Konachan;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Madokami;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.MangaDex;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.NicoNicoSeiga;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.NijieImages;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PawooNet;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PixivImages;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Sankaku;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.TwoDMarket;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.YandeRe;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageboardMeta;
import de.eldoria.shepard.commandmodules.saucenao.data.SauceData;
import de.eldoria.shepard.commandmodules.saucenao.data.SauceRequests;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.core.configuration.configdata.SaucenaoConfig;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.AD_LINK;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.A_LINK;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.C_BASE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.C_STANDALONE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_ANIME_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_ARTIST;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_AUTHOR;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_CHARACTER;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_CREATOR;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_ENG_TITLE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_ENTRY_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_GUILD_LIMIT;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_HENTAI_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_IMAGE_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_IMAGE_OR_LINK;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_JP_TITLE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_MATCH;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_MOVIE_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_NOT_A_IMAGE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_PART;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_POWERED;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_SIMILARITY;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_SOURCE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_TAGS;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_TIME;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_TITLE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_TOTAL_LIMIT;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_TYPE;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_UPLOADER;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_USER;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_USER_LIMIT;
import static de.eldoria.shepard.localization.enums.commands.util.SaucenaoLocale.M_YEAR;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

@Slf4j
public class Saucenao extends Command implements ExecutableAsync, ReqConfig, ReqDataSource {
    private SaucenaoConfig config;
    private SaucenaoApiWrapper saucenaoWrapper;
    private SauceData sauceData;

    public Saucenao() {
        super("sauce",
                new String[] {"saucenao", "saucepls", "source", "sourcepls"},
                "command.sauce.description",
                SubCommand.builder("sauce")
                        .addSubcommand("command.sauce.subCommand.base",
                                Parameter.createInput("command.sauce.argument", "command.sauce.argumentDescription", false))
                        .build(),
                "command.sauce.subCommand.standalone",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        SauceRequests requests = sauceData.getSauceRequests(config.getLongDuration(), config.getShortDuration(), wrapper);
        if (requests == null) return;

        if (wrapper.isGuildEvent()) {
            if (requests.guildShortLimitExceeded(config)) {
                MessageSender.sendMessage(
                        localizeAllAndReplace(M_GUILD_LIMIT.tag, wrapper,
                                requests.getWaitGuildShort() + ""),
                        wrapper.getMessageChannel());
                return;
            }

            if (requests.guildLongLimitExceeded(config)) {
                MessageSender.sendMessage(
                        localizeAllAndReplace(M_GUILD_LIMIT.tag, wrapper,
                                requests.getWaitGuildLong() + ""),
                        wrapper.getMessageChannel());
                return;
            }
        }

        if (requests.userShortLimitExceeded(config)) {
            MessageSender.sendMessage(
                    localizeAllAndReplace(M_USER_LIMIT.tag, wrapper,
                            requests.getWaitUserShort() + ""),
                    wrapper.getMessageChannel());
            return;
        }

        if (requests.userLongLimitExceeded(config)) {
            MessageSender.sendMessage(
                    localizeAllAndReplace(M_USER_LIMIT.tag, wrapper,
                            requests.getWaitUserLong() + ""),
                    wrapper.getMessageChannel());
            return;
        }

        if (requests.totalShortLimitExceeded(config)) {
            MessageSender.sendMessage(
                    localizeAllAndReplace(M_TOTAL_LIMIT.tag, wrapper,
                            requests.getWaitTotalShort() + ""),
                    wrapper.getMessageChannel());
            return;
        }

        if (requests.totalLongLimitExceeded(config)) {
            MessageSender.sendMessage(
                    localizeAllAndReplace(M_TOTAL_LIMIT.tag, wrapper,
                            requests.getWaitTotalLong() + ""),
                    wrapper.getMessageChannel());
            log.warn(C.NOTIFY_ADMIN, "Global limit for Saucenao API reached. Consider upgrading.");
            return;
        }

        sauceData.logSauceRequest(wrapper);

        String url;
        if (args.length == 0) {
            List<Message.Attachment> attachments = wrapper.getMessage().get().getAttachments();
            if (attachments.isEmpty()) {
                //TODO: Image is missing;
                MessageSender.sendMessage(M_IMAGE_OR_LINK.tag, wrapper.getMessageChannel());
                return;
            }
            if (!attachments.get(0).isImage()) {
                MessageSender.sendMessage(M_NOT_A_IMAGE.tag, wrapper.getMessageChannel());
                return;
            }
            url = attachments.get(0).getUrl();
        } else {
            url = args[0];
        }

        wrapper.getMessageChannel().sendTyping().queue();
        SauceResponse sauceResponse = saucenaoWrapper.requestImage(url);

        if (sauceResponse == null || sauceResponse.getResponseMeta().getResultsReturned() == 0) {
            MessageSender.sendMessage(M_NOT_FOUND.tag, wrapper.getMessageChannel());
            return;
        }

        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(wrapper);

        ResultEntry resultEntry = sauceResponse.getResults().get(0);
        // Only include image if it is a NSFW channel. Better safe than sorry.
        boolean nsfw = (wrapper.isPrivateEvent()
                || (wrapper.isGuildEvent() && wrapper.getTextChannel().get().isNSFW()));

        if (nsfw) {
            embedBuilder.setThumbnail(resultEntry.getResultMeta().getThumbnail());
            /*if (resultEntry.getData() instanceof ExternalUrlMeta) {
                ExternalUrlMeta data = (ExternalUrlMeta) resultEntry.getData();
                if (data.getExternalUrls().length != 0) {
                    embedBuilder.setImage(data.getExternalUrls()[0]);
                }
            }*/
        }

        embedBuilder.setFooter(M_POWERED.tag);
        embedBuilder.setTitle(localizeAllAndReplace(M_MATCH.tag, wrapper,
                resultEntry.getResultMeta().getStrippedIndexName()));
        embedBuilder.setDescription(M_SIMILARITY + ": " + resultEntry.getResultMeta().getSimilarity());

        StringBuilder sBuilder = new StringBuilder();

        switch (resultEntry.getResultMeta().getIndex()) {
            case PIXIV_IMAGES:
                PixivImages pixivImages = resultEntry.getData().getImageData();
                if (!pixivImages.getTitle().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(pixivImages.getTitle()).append("\n");
                }
                if (!pixivImages.getMemberName().isBlank()) {
                    sBuilder.append(M_ARTIST.tag).append(": [").append(pixivImages.getMemberName()).append("](")
                            .append("https://www.pixiv.net/en/users/")
                            .append(pixivImages.getMemberId()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, pixivImages);
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            case NICO_NICO_SEIGA:
                NicoNicoSeiga nicoNicoSeiga = resultEntry.getData().getImageData();
                if (!nicoNicoSeiga.getTitle().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(nicoNicoSeiga.getTitle()).append("\n");
                }
                if (!nicoNicoSeiga.getMemberName().isBlank()) {
                    sBuilder.append(M_UPLOADER.tag).append(": ").append(nicoNicoSeiga.getMemberName()).append("\n");
                }
                addExternalUrls(sBuilder, nicoNicoSeiga);
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            // Start of booru pages. They have all the same base class.
            case DANBOORU:
                Danbooru danbooru = resultEntry.getData().getImageData();
                addBooruMeta(danbooru.getId(), danbooru, embedBuilder);
                break;
            case E621_NET:
                E621Net e621Net = resultEntry.getData().getImageData();
                addBooruMeta(e621Net.getId(), e621Net, embedBuilder);
                break;
            case GELBOORU:
                Gelbooru gelbooru = resultEntry.getData().getImageData();
                addBooruMeta(gelbooru.getId(), gelbooru, embedBuilder);
                break;
            case IDOL_COMPLEX:
                IdolComplex idolComplex = resultEntry.getData().getImageData();
                addBooruMeta(idolComplex.getId(), idolComplex, embedBuilder);
                break;
            case KONACHAN:
                Konachan konachan = resultEntry.getData().getImageData();
                addBooruMeta(konachan.getId(), konachan, embedBuilder);
                break;
            case SANKAKU:
                Sankaku sankaku = resultEntry.getData().getImageData();
                addBooruMeta(sankaku.getId(), sankaku, embedBuilder);
                break;
            case YANDE_RE:
                YandeRe yandeRe = resultEntry.getData().getImageData();
                addBooruMeta(yandeRe.getId(), yandeRe, embedBuilder);
                break;
            // End of booru pages
            case NIJIE_IMAGES:
                NijieImages nijieImages = resultEntry.getData().getImageData();
                if (!nijieImages.getTitle().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(nijieImages.getTitle()).append("\n");
                }
                if (!nijieImages.getMemberName().isBlank()) {
                    sBuilder.append(M_ARTIST.tag).append(": [").append(nijieImages.getMemberName()).append("](")
                            .append("https://nijie.info/members.php?id=")
                            .append(nijieImages.getMemberId()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, nijieImages);
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            case H_MISC:
                HMisc hMisc = resultEntry.getData().getImageData();
                if (!hMisc.getEngName().isBlank()) {
                    sBuilder.append(M_ENG_TITLE.tag).append(": ").append(hMisc.getEngName()).append("\n");
                }
                if (!hMisc.getJpName().isBlank()) {
                    sBuilder.append(M_JP_TITLE.tag).append(": ").append(hMisc.getJpName()).append("\n");
                }
                if (!hMisc.getSource().isBlank()) {
                    sBuilder.append(M_SOURCE.tag).append(": ").append(hMisc.getSource()).append("\n");
                }
                if (hMisc.getCreator().length != 0) {
                    sBuilder.append(M_ARTIST.tag).append(": ").append(String.join(", ", hMisc.getCreator()));
                }
                embedBuilder.addField(M_HENTAI_INFO.tag, sBuilder.toString(), false);
                break;
            case TWO_D_MARKET:
                TwoDMarket twoDMarket = resultEntry.getData().getImageData();
                if (!twoDMarket.getSource().isBlank()) {
                    sBuilder.append(M_SOURCE.tag).append(": ").append(twoDMarket.getSource()).append("\n");
                }
                if (!twoDMarket.getCreator().isEmpty()) {
                    sBuilder.append(M_ARTIST.tag).append(": ").append(twoDMarket.getCreator());
                }
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            case ANIME:
                Anime anime = resultEntry.getData().getImageData();
                if (!anime.getSource().isBlank()) {
                    sBuilder.append(M_SOURCE.tag).append(": ").append(anime.getSource()).append("\n");
                }
                if (anime.getPart() != null && !anime.getPart().isBlank()) {
                    sBuilder.append(M_PART.tag).append(": ").append(anime.getPart()).append("\n");
                }
                if (!anime.getYear().isBlank()) {
                    sBuilder.append(M_YEAR.tag).append(": ").append(anime.getYear()).append("\n");
                }
                if (!anime.getEstimatedTime().isBlank()) {
                    sBuilder.append(M_TIME.tag).append(": ").append(anime.getEstimatedTime()).append("\n");
                }
                addExternalUrls(sBuilder, anime);
                embedBuilder.addField(M_ANIME_INFO.tag, sBuilder.toString(), false);
                break;
            case H_ANIME:
                Anime hAnime = resultEntry.getData().getImageData();
                if (!hAnime.getSource().isBlank()) {
                    sBuilder.append(M_SOURCE.tag).append(": ").append(hAnime.getSource()).append("\n");
                }
                if (!hAnime.getPart().isBlank()) {
                    sBuilder.append(M_PART.tag).append(": ").append(hAnime.getPart()).append("\n");
                }
                if (!hAnime.getYear().isBlank()) {
                    sBuilder.append(M_YEAR.tag).append(": ").append(hAnime.getYear()).append("\n");
                }
                if (!hAnime.getEstimatedTime().isBlank()) {
                    sBuilder.append(M_TIME.tag).append(": ").append(hAnime.getEstimatedTime()).append("\n");
                }
                addExternalUrls(sBuilder, hAnime);
                embedBuilder.addField(M_HENTAI_INFO.tag, sBuilder.toString(), false);
                break;
            case DEVIANT_ART:
                DeviantArt deviantArt = resultEntry.getData().getImageData();
                if (!deviantArt.getTitle().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(deviantArt.getTitle()).append("\n");
                }
                if (!deviantArt.getAuthorName().isBlank()) {
                    sBuilder.append(M_ARTIST.tag).append(": [").append(deviantArt.getAuthorName()).append("](")
                            .append(deviantArt.getAuthorUrl()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, deviantArt);
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            case PAWOO_NET:
                PawooNet pawooNet = resultEntry.getData().getImageData();
                if (!pawooNet.getPawooUser().isBlank()) {
                    sBuilder.append(M_USER.tag).append(": ").append(pawooNet.getPawooUser()).append("\n");
                }
                addExternalUrls(sBuilder, pawooNet);
                embedBuilder.addField(M_ENTRY_INFO.tag, sBuilder.toString(), false);
                break;
            case MADOKAMI:
                Madokami madokami = resultEntry.getData().getImageData();
                if (!madokami.getSource().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(madokami.getSource()).append("\n");
                }
                if (!madokami.getPart().isBlank()) {
                    sBuilder.append(M_PART.tag).append(": ").append(madokami.getType()).append("\n");
                }
                if (!madokami.getType().isBlank()) {
                    sBuilder.append(M_TYPE.tag).append(": ").append(madokami.getType()).append("\n");
                }
                embedBuilder.addField(M_IMAGE_INFO.tag, sBuilder.toString(), false);
                break;
            case MANGA_DEX:
                MangaDex mangaDex = resultEntry.getData().getImageData();
                if (!mangaDex.getSource().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(mangaDex.getSource()).append("\n");
                }
                if (!mangaDex.getPart().isBlank()) {
                    sBuilder.append(M_PART.tag).append(": ").append(mangaDex.getPart()).append("\n");
                }
                if (!mangaDex.getAuthor().isBlank()) {
                    sBuilder.append(M_AUTHOR.tag).append(": ").append(mangaDex.getAuthor()).append("\n");
                }
                if (!mangaDex.getArtist().isBlank()) {
                    sBuilder.append(M_ARTIST.tag).append(": ").append(mangaDex.getArtist()).append("\n");
                }
                embedBuilder.addField("Manga Info", sBuilder.toString(), false);
                break;
            case MOVIES:
            case SHOWS:
                Imdb imdb = resultEntry.getData().getImageData();
                if (!imdb.getSource().isBlank()) {
                    sBuilder.append(M_TITLE.tag).append(": ").append(imdb.getSource()).append("\n");
                }
                if (!imdb.getPart().isBlank()) {
                    sBuilder.append(M_PART.tag).append(": ").append(imdb.getPart()).append("\n");
                }
                if (!imdb.getYear().isBlank()) {
                    sBuilder.append(M_YEAR.tag).append(": ").append(imdb.getYear()).append("\n");
                }
                if (!imdb.getEstimatedTime().isBlank()) {
                    sBuilder.append(M_TIME.tag).append(": ").append(imdb.getEstimatedTime());
                }
                addExternalUrls(sBuilder, imdb);
                embedBuilder.addField(M_MOVIE_INFO.tag, sBuilder.toString(), false);
                break;
        }
        wrapper.getMessageChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addBooruMeta(int id, ImageboardMeta meta, LocalizedEmbedBuilder builder) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!meta.getCharacters().isBlank()) {
            stringBuilder.append(M_CHARACTER.tag).append(": ").append(meta.getCharacters()).append("\n");
        }
        if (!meta.getCreator().isBlank()) {
            stringBuilder.append(M_CREATOR.tag).append(": ").append(meta.getCreator()).append("\n");
        }
        if (!meta.getSource().isBlank()) {
            stringBuilder.append(M_SOURCE.tag).append(": ").append(meta.getSource()).append("\n");
        }
        if (!meta.getMaterial().isBlank()) {
            stringBuilder.append(M_TAGS.tag).append(": ").append(meta.getMaterial()).append("\n");
        }

        addExternalUrls(stringBuilder, meta);

        builder.addField(M_IMAGE_INFO.tag, stringBuilder.toString(), false);
    }

    private void addExternalUrls(StringBuilder builder, ExternalUrlMeta meta) {
        if (meta.getExternalUrls() != null && meta.getExternalUrls().length != 0) {
            String urls = Arrays.stream(meta.getExternalUrls())
                    .map(url -> "[" + M_SOURCE + "](" + url + ")")
                    .collect(Collectors.joining(", "));
            builder.append(urls);
        }
    }

    @Override
    public void addConfig(Config config) {
        this.config = config.getThirdPartyApis().getSaucenaoConfig();
        saucenaoWrapper = SaucenaoApiWrapper
                .builder(this.config.getToken())
                .withIndices(SauceIndex.PIXIV_IMAGES, SauceIndex.NICO_NICO_SEIGA, SauceIndex.DANBOORU,
                        SauceIndex.NIJIE_IMAGES, SauceIndex.YANDE_RE,
                        SauceIndex.H_MISC, SauceIndex.ANIME, SauceIndex.H_ANIME,
                        SauceIndex.GELBOORU, SauceIndex.KONACHAN, SauceIndex.E621_NET,
                        SauceIndex.IDOL_COMPLEX, SauceIndex.PAWOO_NET, SauceIndex.DEVIANT_ART,
                        SauceIndex.MADOKAMI, SauceIndex.MANGA_DEX, SauceIndex.MOVIES,
                        SauceIndex.SHOWS, SauceIndex.SANKAKU)
                .withResultCount(5)
                .build();
    }

    @Override
    public void addDataSource(DataSource source) {
        sauceData = new SauceData(source);
    }
}
