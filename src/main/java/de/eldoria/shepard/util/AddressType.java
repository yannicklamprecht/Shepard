package de.eldoria.shepard.util;

public enum AddressType {
    /**
     * Used when an address does not match any pattern.
     */
    NONE,
    /**
     * Used when an address match the ipv4 pattern.
     */
    IPV4,
    /**
     * Used when an address match the ipv6 pattern.
     */
    IPV6,
    /**
     * Used when an address match the domain pattern.
     */
    DOMAIN;
}
