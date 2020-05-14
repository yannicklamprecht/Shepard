package de.eldoria.shepard.commandmodules.monitoring.data;

import de.eldoria.shepard.commandmodules.monitoring.util.Address;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class MonitoringData extends QueryObject {

    /**
     * Create a new monitoring data object.
     *
     * @param source data source for information retrieval
     */
    public MonitoringData(DataSource source) {
        super(source);
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
    public boolean addMonitoringAddress(Guild guild, String address, String name, boolean minecraftIp,
                                        EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_monitoring_address(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, address);
            statement.setString(3, name);
            statement.setBoolean(4, minecraftIp);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
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
    public boolean removeMonitoringAddressByIndex(Guild guild, int index,
                                                  EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_monitoring_address_by_index(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, index);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
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
    public boolean setMonitoringChannel(Guild guild, TextChannel channel,
                                        EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_monitoring_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public boolean removeMonitoringChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public List<Address> getMonitoringAddressesForGuild(Guild guild, EventWrapper messageContext) {
        return getMonitoringAddressesForGuild(guild.getIdLong(), messageContext);
    }

    private List<Address> getMonitoringAddressesForGuild(long guild, EventWrapper messageContext) {
        List<Address> addresses = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_monitoring_addresses_for_guild(?)")) {
            statement.setString(1, guild + "");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                addresses.add(new Address(result.getInt("address_id"),
                        result.getString("name"),
                        result.getString("address"),
                        result.getBoolean("mcip")));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public String getMonitoringChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }


}

