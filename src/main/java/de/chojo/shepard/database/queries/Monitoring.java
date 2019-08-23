package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.Address;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Monitoring {
    public static void addMonitoringAdress(String guildId, String address, String name, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_monitoring_adress(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, address);
            statement.setString(3, name);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeMonitoringAdressByIndex(String guildId, int index, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_monitoring_adress_by_index(?,?)")) {
            statement.setString(1, guildId);
            statement.setInt(2, index);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void setMonitoringChannel(String guildId, String address, String name, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_monitoring_channel(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, address);
            statement.setString(3, name);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeMonitoringChannel(String guildId, String address, String name, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_monitoring_channel(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, address);
            statement.setString(3, name);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static List<Address> getMonitoringChannel(String guildId, String address, String name, MessageReceivedEvent event) {
        List<Address> addresses = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_monitoring_adresses(?)")) {
            statement.setString(1, guildId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                addresses.add(new Address(result.getInt("adress_id")
                        , result.getString("name")
                        , result.getString("adress")));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return addresses;
    }
}

