package de.eldoria.shepard.database.types;

import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.Verifier;

public class Address {
    private final int id;
    private final String name;
    private final String address;
    private final int port;
    private final AddressType type;
    private final boolean minecraftIp;

    /**
     * Creates new address object.
     *
     * @param id          id of the address -> auto increment from database
     * @param name        name of the address
     * @param address     the address to ping
     * @param minecraftIp true if the address is a minecraft ip
     */
    public Address(int id, String name, String address, boolean minecraftIp) {
        this.id = id;
        this.name = name;
        this.minecraftIp = minecraftIp;
        this.type = Verifier.getAddressType(address);
        String portString = "";
        String[] split;
        switch (type) {
            case DOMAIN:
            case IPV4:
                split = address.split(":");
                if (split.length == 2) {
                    portString = split[split.length - 1];
                }

                break;
            case IPV6:
                split = address.split("]:");
                if (split.length == 2) {
                    portString = split[split.length - 1];
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        int port;
        try {
            if (!portString.isBlank()) {
                port = Integer.parseInt(portString);
            } else {
                port = -1;
            }
        } catch (NumberFormatException e) {
            port = -1;
        }

        this.port = port;

        if (port != -1) {
            switch (type) {
                case IPV4:
                case DOMAIN:
                    this.address = address.replace(":" + port, "");
                    break;
                case IPV6:
                    this.address = address.replace("]:" + port, "").replace("[", "");
                    break;
                default:
                    this.address = "";
            }
        } else {
            this.address = address;
        }
    }

    /**
     * Index of the address object in the guild context.
     *
     * @return index of address
     */
    public int getId() {
        return id;
    }

    /**
     * Name of the address.
     *
     * @return string
     */
    public String getName() {
        return name;
    }

    /**
     * Address as domain, ipv4 or ipv6.
     *
     * @return Address as string
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the full address with port.
     *
     * @return Address as string
     */
    public String getFullAddress() {
        switch (type) {
            case DOMAIN:
            case IPV4:
                return address + (port != -1 ? ":" + port : "");
            case IPV6:
                if (port != -1) {
                    return "[" + address + "]:" + port;
                }
                return address;
            default:
                return "";
        }
    }

    /**
     * Indicates whether the ip is ip of a minecraft server or any other type.
     *
     * @return true if it is a minecraft server
     */
    public boolean isMinecraftIp() {
        return minecraftIp;
    }

    /**
     * Get the type of the address.
     *
     * @return Type of the address
     */
    public AddressType getType() {
        return type;
    }

    public int getPort() {
        return port;
    }
}
