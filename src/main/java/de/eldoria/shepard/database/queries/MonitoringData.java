package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class MonitoringData {

    private static Map<Long, List<Address>> addresses = new HashMap<>();
    private static Map<Long, Boolean> addressesDirty = new HashMap<>();

    private MonitoringData() {
    }

    /**
     * Adds a address for monitoring.
     *
     * @param guild          Guild object for lookup
     * @param address        address to add
     * @param name           name of the address
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @param minecraftIp    true when the ip is the ip of a  minecraft server
     * @return true if the query execution was successful
     */
    public static boolean addMonitoringAddress(Guild guild, String address, String name, boolean minecraftIp,
                                               MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_monitoring_address(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, address);
            statement.setString(3, name);
            statement.setBoolean(4, minecraftIp);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        addressesDirty.put(guild.getIdLong(), true);
        return true;
    }

    /**
     * Removes a monitoring address by index.
     *
     * @param guild          Guild object for lookup
     * @param index          address index
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeMonitoringAddressByIndex(Guild guild, int index,
                                                         MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_monitoring_address_by_index(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, index);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        addressesDirty.put(guild.getIdLong(), true);
        return true;
    }

    /**
     * Sets the monitoring channel of the guild.
     *
     * @param guild          Guild object for which the channel should be set
     * @param channel        iod of the channel
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setMonitoringChannel(Guild guild, TextChannel channel,
                                               MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_monitoring_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Remove monitoring channel from a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeMonitoringChannel(Guild guild, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get monitoring addresses for a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return list of address object
     */
    public static List<Address> getMonitoringAddressesForGuild(Guild guild, MessageEventDataWrapper messageContext) {
        return getMonitoringAddressesForGuild(guild.getIdLong(), messageContext);
    }

    private static List<Address> getMonitoringAddressesForGuild(long guild, MessageEventDataWrapper messageContext) {
        if (addressesDirty.getOrDefault(guild, true)) {
            try (PreparedStatement statement = DatabaseConnector.getConn()
                    .prepareStatement("SELECT * from shepard_func.get_monitoring_addresses_for_guild(?)")) {
                statement.setString(1, guild + "");
                ResultSet result = statement.executeQuery();

                addresses.put(guild, new ArrayList<>());
                while (result.next()) {
                    addresses.get(guild)
                            .add(new Address(result.getInt("address_id"),
                                    result.getString("name"),
                                    result.getString("address"),
                                    result.getBoolean("mcip")));
                }
                addressesDirty.put(guild, false);
            } catch (SQLException e) {
                handleExceptionAndIgnore(e, messageContext);
            }
        }
        return addresses.get(guild);

    }

    /**
     * Get monitoring addresses for all guilds.
     *
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return map of lists with a list for each guild
     */
    public static Map<Long, List<Address>> getMonitoringAddresses(MessageEventDataWrapper messageContext) {
        for (Map.Entry<Long, Boolean> set : addressesDirty.entrySet()) {
            if (set.getValue()) {
                getMonitoringAddressesForGuild(set.getKey(), null);
            }
        }

        return addresses;
    }

    /**
     * Get the monitoring channel of a guild.
     *
     * @param guild          Id of the guild
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Channel id as string
     */
    public static String getMonitoringChannel(Guild guild, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return null;
    }


}

