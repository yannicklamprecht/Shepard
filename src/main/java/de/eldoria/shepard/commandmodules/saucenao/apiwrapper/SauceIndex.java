package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Anime;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.BcyNet;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Danbooru;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.DeviantArt;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.DrawrImages;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.E621Net;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Fakku;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Gelbooru;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.HGameGc;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.HMagazines;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.HMisc;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.IdolComplex;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Imdb;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Konachan;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Madokami;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.MangaDex;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.MediBang;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.NicoNicoSeiga;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.NijieImages;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PawooNet;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PixivImages;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PortalGraphicsNet;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.Sankaku;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.TwoDMarket;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.YandeRe;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;

public enum SauceIndex {
    /**
     * Replaced by index 18.
     */
    @Deprecated
    H_MAGAZINES(0, HMagazines.class),
    /**
     * Replaced by index 18.
     */
    @Deprecated
    H_GAME_GC(2, HGameGc.class),
    /**
     * Replaced by index 18.
     * Will probably return always null.
     */
    @Deprecated
    DOUJINSHI_DB(3, null),
    PIXIV_IMAGES(5, PixivImages.class),
    NICO_NICO_SEIGA(8, NicoNicoSeiga.class),
    DANBOORU(9, Danbooru.class),
    /**
     * Website down. 12.2019
     */
    @Deprecated
    DRAWR_IMAGE(10, DrawrImages.class),
    NIJIE_IMAGES(11, NijieImages.class),
    YANDE_RE(12, YandeRe.class),
    /**
     * Inactive as of 08.2015.
     * Will probably return always null.
     */
    @Deprecated
    SHUTTERSTOCK(15, null),

    /**
     * Incomplete as of 19.06.2020
     */
    @Deprecated
    FAKKU(16, Fakku.class),
    H_MISC(18, HMisc.class),
    TWO_D_MARKET(19, TwoDMarket.class),
    /**
     * Updater broken as of late 2019.
     */
    @Deprecated
    MEDI_BANG(20, MediBang.class),
    ANIME(21, Anime.class),
    H_ANIME(22, Anime.class),
    MOVIES(23, Imdb.class),
    SHOWS(24, Imdb.class),
    GELBOORU(25, Gelbooru.class),
    KONACHAN(26, Konachan.class),

    /**
     * API broken as of 19.06.2020
     */
    @Deprecated
    SANKAKU(27, Sankaku.class),
    /**
     * Broken as of 20.06.2020.
     * Result is always null
     */
    @Deprecated
    ANIME_PICURES_NET(28, null),
    E621_NET(29, E621Net.class),
    IDOL_COMPLEX(30, IdolComplex.class),

    /**
     * Incomplete as of 19.06.2020.
     */
    @Deprecated
    BCY_NET_ILLUST(31, BcyNet.class),
    /**
     * Incomplete as of 19.06.2020.
     */
    @Deprecated
    BCY_NET_COSPLAY(32, BcyNet.class),
    PORTAL_GRAPHICS_NET(33, PortalGraphicsNet.class),
    /**
     * Last updated 07.2017.
     */
    @Deprecated
    DEVIANT_ART(34, DeviantArt.class),
    PAWOO_NET(35, PawooNet.class),
    MADOKAMI(36, Madokami.class),
    MANGA_DEX(37, MangaDex.class),
    ALL(999, null),
    UNKOWN(-1, null);

    public final long bitmask;
    private final Class<? extends ImageMeta> dataClass;
    public final int index;

    /**
     * Create a new index with a bitmask.
     *
     * @param index bitmask of index number
     * @param <T>   class which holds the data for this index
     */
    <T extends ImageMeta> SauceIndex(int index, Class<T> dataClass) {
        this.index = index;
        this.bitmask = indexToBitmask(index);
        this.dataClass = dataClass;
    }

    private static long indexToBitmask(long index) {
        long res = 1;
        long sq = 2L;
        while (index > 0) {
            if (index % 2 == 1) {
                res *= sq;
            }
            sq = sq * sq;
            index /= 2;
        }
        return res;
    }

    public static SauceIndex getIndex(int index) {
        for (SauceIndex value : values()) {
            if (value.index == index) return value;
        }
        return UNKOWN;
    }

    @SuppressWarnings("unchecked")
    public <T extends ImageMeta> Class<T> getDataClass() {
        return (Class<T>) dataClass;
    }
}
