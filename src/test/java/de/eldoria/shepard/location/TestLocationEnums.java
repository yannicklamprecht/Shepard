package de.eldoria.shepard.location;

import de.eldoria.shepard.database.ConnectionPool;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.util.TextLocalizer;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TestLocationEnums {

    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");
    private static LanguageHandler lh;

    public static Stream<Class<? extends Enum>> setup() throws ClassNotFoundException {
        // Prepare.
        String packageName = "de.eldoria.shepard.localization.enums";
        URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
        List<Class<? extends Enum>> classes = new ArrayList<>();
        // Filter .class files.
        assert root != null;
        File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
        // Find classes implementing ICommand.
        for (File file : files) {
            String className = file.getName().replaceAll(".class$", "");
            Class<?> cls = Class.forName(packageName + "." + className);
            if (Enum.class.isAssignableFrom(cls)) {
                classes.add((Class<? extends Enum>) cls);
            }
        }
        return classes.stream();
    }

    public static Stream<Enum> setupKeys() throws ClassNotFoundException {
        // Prepare.
        String packageName = "de.eldoria.shepard.localization.enums";
        URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
        List<Enum> classes = new ArrayList<>();
        // Filter .class files.
        assert root != null;
        File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
        // Find classes implementing ICommand.
        for (File file : files) {
            String className = file.getName().replaceAll(".class$", "");
            Class<?> cls = Class.forName(packageName + "." + className);
            if (Enum.class.isAssignableFrom(cls)) {
                Enum[] values = ((Class<? extends Enum>) cls).getEnumConstants();
                Collections.addAll(classes, values);

            }
        }

        return classes.stream();
    }

    @BeforeAll
    public static void setUpCore(){
        lh = new LanguageHandler();
        lh.addDataSource(new ConnectionPool().getSource());
        lh.init();
    }

    @ParameterizedTest
    @MethodSource("setup")
    public void testLocalizationEnumsTags(Class<? extends Enum> testClass){
        Enum value = testClass.getEnumConstants()[0];
        String tag = value.toString();
        assertTrue(testClass.getSimpleName()+" does not have valid tags",LOCALIZATION_CODE.matcher(tag).matches());
    }

    @ParameterizedTest
    @MethodSource("setupKeys")
    public void testLocalizationEnumsKeysInDefaultLanguage(Enum testEnum){
        String text = TextLocalizer.localizeAllAndReplace(testEnum.toString(), (Guild) null, "");
        assertNotEquals(
                "For " + testEnum.toString() + " in Class " + testEnum.getDeclaringClass().getSimpleName() + " is no Default Message defined",
                testEnum.toString(),
                text );

    }

}
