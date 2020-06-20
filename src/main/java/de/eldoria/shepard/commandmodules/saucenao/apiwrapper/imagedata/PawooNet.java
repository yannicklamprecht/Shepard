package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Getter;

@Getter
public class PawooNet extends ExternalUrlMeta {
    private String createdAt;
    private int id;
    /**
     * Accountname of user.
     */
    @SerializedName("pawoo_user_acct")
    private String pawooUser;
    /**
     * Username of user.
     */
    @SerializedName("pawoo_user_username")
    private String pawooUsername;
    /**
     * Displayname of user.
     */
    @SerializedName("pawoo_user_display_name")
    private String pawooDisplayname;

}
