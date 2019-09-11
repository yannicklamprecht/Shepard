package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.listener.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class MonitoringData {

    private MonitoringData() {
    }

    /**
     * Adds a address for monitoring.
     *
     * @param guild   Guild object for lookup
     * @param address address to add
     * @param name    name of the address
     * @param event   event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addMonitoringAddress(Guild guild, String address, String name, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_monitoring_adress(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, address);
            statement.setString(3, name);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Removes a monitoring address by index.
     *
     * @param guild Guild object for lookup
     * @param index address index
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeMonitoringAddressByIndex(Guild guild, int index, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_monitoring_adress_by_index(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, index);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Sets the monitoring channel of the guild.
     *
     * @param guild   Guild object for which the channel should be set
     * @param channel iod of the channel
     * @param event   event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setMonitoringChannel(Guild guild, TextChannel channel, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_monitoring_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Remove monitoring channel from a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeMonitoringChannel(Guild guild, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Get monitoring addresses for a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return list of address object
     */
    public static List<Address> getMonitoringAddresses(Guild guild, MessageEventDataWrapper event) {
        List<Address> addresses = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_monitoring_adresses(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                addresses.add(new Address(result.getInt("adress_id"),
                        result.getString("name"),
                        result.getString("adress")));
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
        }
        return addresses;
    }

    /**
     * Get the monitoring channel of a guild.
     *
     * @param guild Id of the guild
     * @param event event from command sending for error handling. Can be null.
     * @return Channel id as string
     */
    public static String getMonitoringChannel(Guild guild, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_monitoring_channel(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
        }
        return null;
    }


}

