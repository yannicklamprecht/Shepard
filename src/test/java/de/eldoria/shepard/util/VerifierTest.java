package de.eldoria.shepard.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerifierTest {

    @Test
    void isValidId0() {
        assertTrue(Verifier.isValidId("123456789123456789")
                && Verifier.isValidId("<@123456789123456789>")
                && Verifier.isValidId("<#123456789123456789>")
                && Verifier.isValidId("<&123456789123456789>")
                && Verifier.isValidId("<@#123456789123456789>")
                && Verifier.isValidId("<@!123456789123456789>")
                && Verifier.isValidId("<!123456789123456789>"));
    }

    @Test
    void isValidId1() {
        assertFalse(Verifier.isValidId("1234567891234567891")
                && Verifier.isValidId("12345678912345678")
                && Verifier.isValidId("<&123456789123456789>")
                && Verifier.isValidId("<?123456789123456789>"));
    }

    @Test
    void checkAndGetBoolean0() {
        assertFalse(Verifier.checkAndGetBoolean("false").stateAsBoolean);
    }

    @Test
    void checkAndGetBoolean1() {
        assertFalse(Verifier.checkAndGetBoolean("tru").stateAsBoolean);
    }

    @Test
    void checkAndGetBoolean2() {
        assertTrue(Verifier.checkAndGetBoolean("true").stateAsBoolean);
    }

    @Test
    void getAddressType0() {
        AddressType addressType = Verifier.getAddressType("1.1.1.1");
        assertEquals(AddressType.IPV4, addressType);
    }

    @Test
    void getAddressType1() {
        AddressType addressType = Verifier.getAddressType("123.123.123.123:12345");
        assertEquals(AddressType.IPV4, addressType);
    }

    @Test
    void getAddressType2() {
        AddressType addressType = Verifier.getAddressType("1.1.1:1");
        assertEquals(AddressType.NONE, addressType);
    }

    @Test
    void getAddressType3() {
        AddressType addressType = Verifier.getAddressType("1.1.1.1:123456");
        assertEquals(AddressType.NONE, addressType);
    }

    @Test
    void getAddressType4() {
        AddressType addressType = Verifier.getAddressType("740:5e2d:6fcb:ec99:3b7c:fdd5:af77:d693");
        assertEquals(AddressType.IPV6, addressType);
    }

    @Test
    void getAddressType5() {
        AddressType addressType = Verifier.getAddressType("e7f1:4046:9c9c:6acd:2fcd:479c:3ff8:44f3");
        assertEquals(AddressType.IPV6, addressType);
    }

    @Test
    void getAddressType6() {
        AddressType addressType = Verifier.getAddressType("e7f1:4046:9c9c:6acd::::44f3");
        assertEquals(AddressType.IPV6, addressType);
    }

    @Test
    void getAddressType7() {
        AddressType addressType = Verifier.getAddressType("[e7f1:4046:9c9c:6acd::::44f3]:12345");
        assertEquals(AddressType.IPV6, addressType);
    }

    @Test
    void getAddressType8() {
        AddressType addressType = Verifier.getAddressType("e7f1:4046:9c9c:6acd::::44f3]:12345");
        assertEquals(AddressType.NONE, addressType);
    }

    @Test
    void getAddressType9() {
        AddressType addressType = Verifier.getAddressType("[e7f1:4046:9c9c:6acd::::44f3:12345");
        assertEquals(AddressType.NONE, addressType);
    }

    @Test
    void getAddressType10() {
        AddressType addressType = Verifier.getAddressType("[e7f1:4046:9c9c:6acd::::44f3]:123456");
        assertEquals(AddressType.NONE, addressType);
    }
}