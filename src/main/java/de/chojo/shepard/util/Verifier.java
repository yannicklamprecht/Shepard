package de.chojo.shepard.util;

import de.chojo.shepard.database.DbUtil;
import de.chojo.shepard.messagehandler.Messages;

public class Verifier {
    public static boolean isValidId(String id){
        return DbUtil.getIdRaw(id).length() == 18;
    }

    public static Boolean checkAndGetBoolean(String string){
        Boolean state = null;
        if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
            state = string.equalsIgnoreCase("true");
        } else {
            return;
        }

    }
}
