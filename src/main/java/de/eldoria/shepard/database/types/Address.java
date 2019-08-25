package de.eldoria.shepard.database.types;

public class Address {
    private final int id;
    private final String name;
    private final String address;

    /**
     * Creates new address object.
     * @param id id of the address -> auto increment from database
     * @param name name of the address
     * @param address the address to ping
     */
    public Address(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    /**
     * Index of the address object in the guild context.
     * @return index of address
     */
    public int getId() {
        return id;
    }

    /**
     * Name of the address.
     * @return string
     */
    public String getName() {
        return name;
    }

    /**
     * Address as domain, ipv4 or ipv6.
     * @return Address as string
     */
    public String getAddress() {
        return address;
    }
}
