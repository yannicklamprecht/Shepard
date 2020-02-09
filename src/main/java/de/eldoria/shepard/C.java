package de.eldoria.shepard;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@UtilityClass
public class C {
	
	public static final Marker NOTIFY_ADMIN = createMarker("NOTIFY_ADMIN");
	public static final Marker COMMAND = createMarker("COMMAND");
	public static final Marker STATUS = createMarker("STATUS");
	public static final Marker DISCORD = createMarker("DISCORD");
	
	private static Marker createMarker(@NonNull String name, @NonNull Marker... children) {
		Marker marker = MarkerFactory.getMarker(name);
		for (Marker child : children) {
			marker.add(child);
		}
		return marker;
	}
}
