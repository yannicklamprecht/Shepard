package de.eldoria.shepard.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextFormattingTest {

    @Test
    void fillString() {
        String test = TextFormatting.fillString("test", 5);
        assertEquals(5, test.length());
    }

    @Test
    void getRangeAsString0() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, 1, 8);
        assertEquals(String.join(" ", new String[] {"is", "a", "simple", "test", "text", "as", "array"}), rangeAsString);
    }

    @Test
    void getRangeAsString1() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, 1, 0);
        assertEquals(String.join(" ", new String[] {"is", "a", "simple", "test", "text", "as", "array"}), rangeAsString);
    }

    @Test
    void getRangeAsString2() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, 1, -1);
        assertEquals(String.join(" ", new String[] {"is", "a", "simple", "test", "text", "as"}), rangeAsString);
    }

    @Test
    void getRangeAsString3() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, -3, 0);
        assertEquals(String.join(" ", new String[] {"text", "as", "array"}), rangeAsString);
    }

    @Test
    void getRangeAsString4() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, 3, 4);
        assertEquals("simple", rangeAsString);
    }

    @Test
    void getRangeAsString5() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, -3, -2);
        assertEquals("text", rangeAsString);
    }

    @Test
    void getRangeAsString6() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, -10, -11);
        assertEquals("", rangeAsString);
    }

    @Test
    void getRangeAsString7() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, 30, 35);
        assertEquals("", rangeAsString);
    }

    @Test
    void getRangeAsString8() {
        String[] array = new String[] {"this", "is", "a", "simple", "test", "text", "as", "array"};
        String rangeAsString = TextFormatting.getRangeAsString(" ", array, -1, 0);
        assertEquals("array", rangeAsString);
    }

    @Test
    void trimText0() {
        String text = "This is a simple text to test text cropping";
        String cropText = TextFormatting.cropText(text, "...", text.length(), false);
        assertEquals(text, cropText);
    }

    @Test
    void trimText1() {
        String text = "This is a simple text to test text cropping";
        String cropText = TextFormatting.cropText(text, "...", text.length(), false);
        assertEquals(text, cropText);
    }

    @Test
    void trimText2() {
        String text = "This is a simple text to test text cropping";
        String cropText = TextFormatting.cropText(text, "...", text.length() - 1, false);
        assertEquals("This is a simple text to test text crop...", cropText);
    }

    @Test
    void trimText3() {
        String text = "This is a simple text to test text cropping";
        String cropText = TextFormatting.cropText(text, "...", text.length() - 1, true);
        assertEquals("This is a simple text to test text...", cropText);
    }
}