package de.eldoria.shepard.database.types;

public class Address {
    private final int id;
    private final String name;
    private final String address;
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
        this.address = address;
        this.minecraftIp = minecraftIp;
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
     * Indicates whether the ip is ip of a minecraft server or any other type.
     *
     * @return true if it is a minecraft server
     */
    public boolean isMinecraftIp() {
        return minecraftIp;
    }
}
