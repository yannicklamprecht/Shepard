package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.PixivImages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SaucenaoConfigApiWrapperTest {
    static SaucenaoApiWrapper wrapper;

    @BeforeAll
    static void createWrapper() {
    }

    @Test
    void generalTest() {
        wrapper = SaucenaoApiWrapper.builder().withIndices(SauceIndex.PIXIV_IMAGES).build();
        SauceResponse sauceResponse = wrapper.requestImage("http://chojo.u.catgirlsare.sexy/6YWwRE_v.png");
        List<ResultEntry> results = sauceResponse.getResults();

        Assertions.assertFalse(results.isEmpty());

        ResultEntry resultEntry = results.get(0);
        SauceIndex index = resultEntry.getResultMeta().getIndex();
        switch (index) {
            case PIXIV_IMAGES: {
                PixivImages imageMeta = resultEntry.getData().getImageData();
                System.out.println(imageMeta.toString() + " urls: " + String.join(", ",imageMeta.getExternalUrls()));
            }
        }
    }
}
