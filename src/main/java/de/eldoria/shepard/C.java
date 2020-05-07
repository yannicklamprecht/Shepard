package de.eldoria.shepard;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@UtilityClass
public class C {

    /**
     * Will be send to error-log channel.
     */
    public static final Marker NOTIFY_ADMIN = createMarker("NOTIFY_ADMIN");
    /**
     * Will be send to command-log channel.
     */
    public static final Marker COMMAND = createMarker("COMMAND");
    /**
     * Will be send to status-log.
     */
    public static final Marker STATUS = createMarker("STATUS");
    /**
     * Currently unused.
     */
    public static final Marker DISCORD = createMarker("DISCORD");

    private static Marker createMarker(@NonNull String name, @NonNull Marker... children) {
        Marker marker = MarkerFactory.getMarker(name);
        for (Marker child : children) {
            marker.add(child);
        }
        return marker;
    }
}
