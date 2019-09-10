package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.TicketType;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class TicketData {

    private TicketData() {
    }

    /**
     * Creates a new ticket type.
     *
     * @param guild           Guild on which the type should be added
     * @param category        channel category id
     * @param creationMessage creation message
     * @param keyword         type keyword
     * @param event           event from command sending for error handling. Can be null.
     */
    public static void addType(Guild guild, Category category, String creationMessage,
                               String keyword, MessageReceivedEvent event) throws SQLException {
        String categoryId = null;
        if (category != null) {
            categoryId = category.getId();
        }
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_ticket_type(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, categoryId);
            statement.setString(3, creationMessage);
            statement.setString(4, keyword);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a type by id.
     *
     * @param guild Guild object for lookup
     * @param id    id of the type
     * @param event event from command sending for error handling. Can be null.
     */
    public static void removeTypeByIndex(Guild guild, int id, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_ticket_type_by_index(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, id);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a type by keyword.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword of the type
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void removeTypeByKeyword(Guild guild, String keyword, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_ticket_type_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Gets a type by keyword.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword
     * @param event   event from command sending for error handling. Can be null.
     * @return Ticket type object or null if no type was found for keyword.
     */
    public static TicketType getTypeByKeyword(Guild guild, String keyword, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_ticket_type_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new TicketType(
                        guild.getCategoryById(result.getString("category_id")),
                        result.getString("creation_message"),
                        result.getString("keyword"));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    /**
     * Gets a type by channel.
     *
     * @param guild   Guild object for lookup
     * @param channel channel for lookup
     * @param event   event from command sending for error handling. Can be null.
     * @return Ticket type object or null if no type was found for channel.
     */
    public static TicketType getTypeByChannel(Guild guild, TextChannel channel, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_ticket_type_by_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new TicketType(
                        guild.getCategoryById(result.getString("category_id")),
                        result.getString("creation_message"),
                        result.getString("keyword"));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }


    /**
     * Get all types of one guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return List of ticket types.
     */
    public static List<TicketType> getTypes(Guild guild, MessageReceivedEvent event) throws SQLException {
        List<TicketType> types = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_ticket_types(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                types.add(new TicketType(
                        result.getInt("id"),
                        guild.getCategoryById(result.getString("category_id")),
                        result.getString("creation_message"),
                        result.getString("keyword")));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return types;
    }

    /**
     * Set creation message for a ticket type.
     *
     * @param guild   Guild for which the message should be set
     * @param keyword keyword of type
     * @param message new message
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void setCreationMessage(Guild guild, String keyword, String message, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_creation_message(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            statement.setString(3, message);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Creates a channel.
     *
     * @param guild       Guild object for lookup
     * @param channel     chanel object
     * @param ticketOwner user object of the ticket owner
     * @param keyword     keyword of the ticket type.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void createChannel(Guild guild, TextChannel channel,
                                     User ticketOwner, String keyword, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.create_ticket_channel(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.setString(3, ticketOwner.getId());
            statement.setString(4, keyword);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Get all channel ids by owner on a guild.
     *
     * @param guild        Guild object for lookup
     * @param channelOwner owner of the channel.
     * @param event        event from command sending for error handling. Can be null.
     * @return list of channel ids
     */
    public static List<String> getChannelIdsByOwner(Guild guild, User channelOwner, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_channel_ids_by_owner(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channelOwner.getId());
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    /**
     * Get all channel ids by type on a guild.
     *
     * @param guild Guild object for lookup
     * @param type  owner of the channel.
     * @param event event from command sending for error handling. Can be null.
     * @return list of channel ids
     */
    public static List<String> getChannelIdsByType(Guild guild, String type, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_ticket_channel_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, type);
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    /**
     * get user id of the channel owner of a channel.
     *
     * @param guild   Guild object for lookup
     * @param channel channel object
     * @param event   event from command sending for error handling. Can be null.
     * @return id of the user.
     */
    public static String getChannelOwnerId(Guild guild, TextChannel channel, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_ticket_channel_owner(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }


    /**
     * Get the roles for the channel owner of a channel.
     *
     * @param guild   Guild object for lookup
     * @param channel channel id
     * @param event   event from command sending for error handling. Can be null.
     * @return List of role ids
     */
    public static List<String> getChannelOwnerRoles(Guild guild, TextChannel channel, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_ticket_channel_owner_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return Collections.emptyList();
    }

    /**
     * Remove a channel.
     *
     * @param guild   Guild object for lookup
     * @param channel channel object
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void removeChannel(Guild guild, TextChannel channel, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_ticket_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Remove all channels where user is ticket owner.
     *
     * @param guild       Guild object for lookup
     * @param ticketOwner ticketOwner as user.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void removeChannelByUser(Guild guild, User ticketOwner, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_ticket_channel_by_user(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, ticketOwner.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * set owner roles for a ticket type.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword of the type
     * @param roles   one or more role ids.
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void setTypeOwnerRoles(Guild guild, String keyword, List<Role> roles, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_ticket_owner_roles(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar",
                    roles.stream().map(ISnowflake::getId).toArray());
            statement.setArray(3, ids);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Set roles for ticket support.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword of ticket type
     * @param roles   one or more role ids
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void setTypeSupportRoles(Guild guild, String keyword, List<Role> roles, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_ticket_support_roles(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar",
                    roles.stream().map(ISnowflake::getId).toArray());
            statement.setArray(3, ids);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * get owner roles for one type.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword of type.
     * @param event   event from command sending for error handling. Can be null.
     * @return Return list of role ids
     */
    public static List<String> getTypeOwnerRoles(Guild guild, String keyword, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_ticket_owner_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    /**
     * Get support roles for ticket type.
     *
     * @param guild   Guild object for lookup
     * @param keyword keyword of ticket type
     * @param event   event from command sending for error handling. Can be null.
     * @return list of role ids
     */
    public static List<String> getTypeSupportRoles(Guild guild, String keyword, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_ticket_support_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    /**
     * Get the auto increment. Increment is per guild and goes from 1 to 999. After that starts at 1 again.
     * After usage of the method the number is used.
     *
     * @param guild guild object
     * @param event event from command sending for error handling. Can be null.
     * @return integer auto increment.
     */
    public static int getNextTicketCount(Guild guild, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_next_ticket_count(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return 1;
    }

}
