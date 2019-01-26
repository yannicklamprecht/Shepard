package de.chojo.shepard.collections;

import de.chojo.shepard.modules.keywords.KeyWordArgs;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KeyWordCollection {
    private static KeyWordCollection instance;

    private ArrayList<Keyword> keywordsArrayList = new ArrayList<>();
    private int lastKeyWordArraySize = 0;

    private Map<String, Keyword> keywords = new HashMap<>();

    public static KeyWordCollection getInstance() {
        if (instance == null) {
            synchronized (KeyWordCollection.class) {
                if (instance == null) {
                    instance = new KeyWordCollection();
                }
            }
        }
        return instance;
    }

    private KeyWordCollection(){}

    public void addKeyword(Keyword keyword) {
        keywordsArrayList.add(keyword);
    }

    public KeyWordArgs getKeyword(MessageReceivedEvent event) {
        //if (keywords.size() == 0)
        //    rebuildHasMap();
        //if (lastKeyWordArraySize == 0 || lastKeyWordArraySize < keywordsArrayList.size()){
        //    rebuildHasMap();
        //}
        //    return keywords.get(key.toLowerCase());
        for (Keyword keyword : keywordsArrayList) {
            String key = keyword.hasKeyword(event);
            if (key != null) {
                return new KeyWordArgs(key, keyword);
            }
        }
        return null;
    }

    private void rebuildHasMap() {
        lastKeyWordArraySize = keywordsArrayList.size();
        for (Keyword keyword : keywordsArrayList) {
            for (String key : keyword.getKeywords()) {
                keywords.put(key.toLowerCase(), keyword);
            }
        }
    }


}
