package de.eldoria.shepard.commandmodules.saucenao;

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
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Saucenao extends Command implements ExecutableAsync, ReqConfig, ReqInit {
    private Config config;
    private SaucenaoApiWrapper saucenaoWrapper;


    public Saucenao() {
        super("sauce",
                new String[] {"saucenao", "saucepls"},
                "",
                SubCommand.builder("sauce")
                        .addSubcommand("link",
                                Parameter.createInput("link", "link of image", false))
                        .build(),
                "Attach a image.",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {

        // TODO enforce rate limit before requesting.

        String url;
        if (args.length == 0) {
            List<Message.Attachment> attachments = wrapper.getMessage().get().getAttachments();
            if (attachments.isEmpty()) {
                //TODO: Image is missing;
                MessageSender.sendMessage("please provide a link or a image.", wrapper.getMessageChannel());
                return;
            }
            if (!attachments.get(0).isImage()) {
                MessageSender.sendMessage("File is not a image.", wrapper.getMessageChannel());
                return;
            }
            url = attachments.get(0).getUrl();
        } else {
            url = args[0];
        }

        wrapper.getMessageChannel().sendTyping().queue();
        SauceResponse sauceResponse = saucenaoWrapper.requestImage(url);

        if (sauceResponse == null || sauceResponse.getResponseMeta().getResultsReturned() == 0) {
            MessageSender.sendMessage("No source was found.", wrapper.getMessageChannel());
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

        embedBuilder.setFooter("Powered by Saucenao.net");
        embedBuilder.setTitle("Match found on " + resultEntry.getResultMeta().getStrippedIndexName());
        embedBuilder.setDescription("Similarity:" + resultEntry.getResultMeta().getSimilarity());

        StringBuilder sBuilder = new StringBuilder();

        switch (resultEntry.getResultMeta().getIndex()) {
            case PIXIV_IMAGES:
                PixivImages pixivImages = resultEntry.getData().getImageData();
                if (!pixivImages.getTitle().isBlank()) {
                    sBuilder.append("Title: ").append(pixivImages.getTitle()).append("\n");
                }
                if (!pixivImages.getMemberName().isBlank()) {
                    sBuilder.append("Artist: [").append(pixivImages.getMemberName()).append("](")
                            .append("https://www.pixiv.net/en/users/")
                            .append(pixivImages.getMemberId()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, pixivImages);
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            case NICO_NICO_SEIGA:
                NicoNicoSeiga nicoNicoSeiga = resultEntry.getData().getImageData();
                if (!nicoNicoSeiga.getTitle().isBlank()) {
                    sBuilder.append("Title: ").append(nicoNicoSeiga.getTitle()).append("\n");
                }
                if (!nicoNicoSeiga.getMemberName().isBlank()) {
                    sBuilder.append("Uploader: ").append(nicoNicoSeiga.getMemberName()).append("\n");
                }
                addExternalUrls(sBuilder, nicoNicoSeiga);
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            // Start of booru pages. They have all the same base class.
            case DANBOORU:
                Danbooru danbooru = resultEntry.getData().getImageData();
                addBooruMeta(danbooru.getId(), danbooru, embedBuilder, nsfw);
                break;
            case E621_NET:
                E621Net e621Net = resultEntry.getData().getImageData();
                addBooruMeta(e621Net.getId(), e621Net, embedBuilder, nsfw);
                break;
            case GELBOORU:
                Gelbooru gelbooru = resultEntry.getData().getImageData();
                addBooruMeta(gelbooru.getId(), gelbooru, embedBuilder, nsfw);
                break;
            case IDOL_COMPLEX:
                IdolComplex idolComplex = resultEntry.getData().getImageData();
                addBooruMeta(idolComplex.getId(), idolComplex, embedBuilder, nsfw);
                break;
            case KONACHAN:
                Konachan konachan = resultEntry.getData().getImageData();
                addBooruMeta(konachan.getId(), konachan, embedBuilder, nsfw);
                break;
            case SANKAKU:
                Sankaku sankaku = resultEntry.getData().getImageData();
                addBooruMeta(sankaku.getId(), sankaku, embedBuilder, nsfw);
                break;
            case YANDE_RE:
                YandeRe yandeRe = resultEntry.getData().getImageData();
                addBooruMeta(yandeRe.getId(), yandeRe, embedBuilder, nsfw);
                break;
            // End of booru pages
            case NIJIE_IMAGES:
                NijieImages nijieImages = resultEntry.getData().getImageData();
                if (!nijieImages.getTitle().isBlank()) {
                    sBuilder.append("Title: ").append(nijieImages.getTitle()).append("\n");
                }
                if (!nijieImages.getMemberName().isBlank()) {
                    sBuilder.append("Artist: [").append(nijieImages.getMemberName()).append("](")
                            .append("https://nijie.info/members.php?id=")
                            .append(nijieImages.getMemberId()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, nijieImages);
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            case H_MISC:
                HMisc hMisc = resultEntry.getData().getImageData();
                if (!hMisc.getEngName().isBlank()) {
                    sBuilder.append("English Title: ").append(hMisc.getEngName()).append("\n");
                }
                if (!hMisc.getJpName().isBlank()) {
                    sBuilder.append("Japanese Title: ").append(hMisc.getJpName()).append("\n");
                }
                if (!hMisc.getSource().isBlank()) {
                    sBuilder.append("Source: ").append(hMisc.getSource()).append("\n");
                }
                if (hMisc.getCreator().length != 0) {
                    sBuilder.append("Artist: ").append(String.join(", ", hMisc.getCreator()));
                }
                embedBuilder.addField("Hentai Info", sBuilder.toString(), false);
                break;
            case TWO_D_MARKET:
                TwoDMarket twoDMarket = resultEntry.getData().getImageData();
                if (!twoDMarket.getSource().isBlank()) {
                    sBuilder.append("Source: ").append(twoDMarket.getSource()).append("\n");
                }
                if (!twoDMarket.getCreator().isEmpty()) {
                    sBuilder.append("Artist: ").append(twoDMarket.getCreator());
                }
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            case ANIME:
                Anime anime = resultEntry.getData().getImageData();
                if (!anime.getSource().isBlank()) {
                    sBuilder.append("Source: ").append(anime.getSource()).append("\n");
                }
                if (!anime.getPart().isBlank()) {
                    sBuilder.append("Part: ").append(anime.getPart()).append("\n");
                }
                if (!anime.getYear().isBlank()) {
                    sBuilder.append("Year: ").append(anime.getYear()).append("\n");
                }
                if (!anime.getEstimatedTime().isBlank()) {
                    sBuilder.append("Time: ").append(anime.getEstimatedTime()).append("\n");
                }
                addExternalUrls(sBuilder, anime);
                embedBuilder.addField("Anime Info", sBuilder.toString(), false);
                break;
            case H_ANIME:
                Anime hAnime = resultEntry.getData().getImageData();
                if (!hAnime.getSource().isBlank()) {
                    sBuilder.append("Source: ").append(hAnime.getSource()).append("\n");
                }
                if (!hAnime.getPart().isBlank()) {
                    sBuilder.append("Part: ").append(hAnime.getPart()).append("\n");
                }
                if (!hAnime.getYear().isBlank()) {
                    sBuilder.append("Year: ").append(hAnime.getYear()).append("\n");
                }
                if (!hAnime.getEstimatedTime().isBlank()) {
                    sBuilder.append("Time: ").append(hAnime.getEstimatedTime()).append("\n");
                }
                addExternalUrls(sBuilder, hAnime);
                embedBuilder.addField("Hentai Info", sBuilder.toString(), false);

            case DEVIANT_ART:
                DeviantArt deviantArt = resultEntry.getData().getImageData();
                if (!deviantArt.getTitle().isBlank()) {
                    sBuilder.append("Title: ").append(deviantArt.getTitle()).append("\n");
                }
                if (!deviantArt.getAuthorName().isBlank()) {
                    sBuilder.append("Artist: [").append(deviantArt.getAuthorName()).append("](")
                            .append(deviantArt.getAuthorUrl()).append(")").append("\n");
                }
                addExternalUrls(sBuilder, deviantArt);
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            case PAWOO_NET:
                PawooNet pawooNet = resultEntry.getData().getImageData();
                if (!pawooNet.getPawooUser().isBlank()) {
                    sBuilder.append("User: ").append(pawooNet.getPawooUser()).append("\n");
                }
                addExternalUrls(sBuilder, pawooNet);
                embedBuilder.addField("Entry Info", sBuilder.toString(), false);
                break;
            case MADOKAMI:
                Madokami madokami = resultEntry.getData().getImageData();
                if (!madokami.getSource().isBlank()) {
                    sBuilder.append("Title: ").append(madokami.getSource()).append("\n");
                }
                if (!madokami.getPart().isBlank()) {
                    sBuilder.append("Part: ").append(madokami.getType()).append("\n");
                }
                if (!madokami.getType().isBlank()) {
                    sBuilder.append("Type: ").append(madokami.getType()).append("\n");
                }
                embedBuilder.addField("Image Info", sBuilder.toString(), false);
                break;
            case MANGA_DEX:
                MangaDex mangaDex = resultEntry.getData().getImageData();
                if (!mangaDex.getSource().isBlank()) {
                    sBuilder.append("Title: ").append(mangaDex.getSource()).append("\n");
                }
                if (!mangaDex.getPart().isBlank()) {
                    sBuilder.append("Part: ").append(mangaDex.getPart()).append("\n");
                }
                if (!mangaDex.getAuthor().isBlank()) {
                    sBuilder.append("Author: ").append(mangaDex.getAuthor()).append("\n");
                }
                if (!mangaDex.getArtist().isBlank()) {
                    sBuilder.append("Artist: ").append(mangaDex.getArtist()).append("\n");
                }
                embedBuilder.addField("Manga Info", sBuilder.toString(), false);
                break;
            case MOVIES:
            case SHOWS:
                Imdb imdb = resultEntry.getData().getImageData();
                if (!imdb.getSource().isBlank()) {
                    sBuilder.append("Title: ").append(imdb.getSource()).append("\n");
                }
                if (!imdb.getPart().isBlank()) {
                    sBuilder.append("Part: ").append(imdb.getPart()).append("\n");
                }
                if (!imdb.getYear().isBlank()) {
                    sBuilder.append("Year: ").append(imdb.getYear()).append("\n");
                }
                if (!imdb.getEstimatedTime().isBlank()) {
                    sBuilder.append("Time: ").append(imdb.getEstimatedTime());
                }
                addExternalUrls(sBuilder, imdb);
                embedBuilder.addField("Movie Info", sBuilder.toString(), false);
                break;
        }
        wrapper.getMessageChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addBooruMeta(int id, ImageboardMeta meta, LocalizedEmbedBuilder builder, boolean isNsfw) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!meta.getCharacters().isBlank()) {
            stringBuilder.append("Character: ").append(meta.getCharacters()).append("\n");
        }
        if (!meta.getCreator().isBlank()) {
            stringBuilder.append("Creator: ").append(meta.getCreator()).append("\n");
        }
        if (!meta.getSource().isBlank()) {
            stringBuilder.append("Source: ").append(meta.getSource()).append("\n");
        }
        if (!meta.getMaterial().isBlank()) {
            stringBuilder.append("Tags: ").append(meta.getMaterial()).append("\n");
        }

        addExternalUrls(stringBuilder, meta);

        builder.addField("Image Info", stringBuilder.toString(), false);
    }

    private void addExternalUrls(StringBuilder builder, ExternalUrlMeta meta) {
        if (meta.getExternalUrls() != null && meta.getExternalUrls().length != 0) {
            String urls = Arrays.stream(meta.getExternalUrls())
                    .map(url -> "[Source](" + url + ")")
                    .collect(Collectors.joining(", "));
            builder.append(urls);
        }
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void init() {
        saucenaoWrapper = SaucenaoApiWrapper
                .builder(config.getThirdPartyApis().getSaucenao())
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
}
