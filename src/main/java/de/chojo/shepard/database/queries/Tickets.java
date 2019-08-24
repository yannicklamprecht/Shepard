package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.w3c.dom.Text;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.chojo.shepard.database.DbUtil.handleException;

class Tickets {

    private Tickets(){}

    public static boolean addType(Guild guild, String categoryId, String creationMessage,
                                  String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_ticket_type(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(1, categoryId);
            statement.setString(1, creationMessage);
            statement.setString(1, keyword);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return false;
    }

    public static void removeTypeByIndex(Guild guild, int index, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_ticket_type_by_index(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, index);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeTypeByKeyword(Guild guild, String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_ticket_type_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static TicketType getTypeByKeyword(Guild guild, String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_type_by_keyword(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new TicketType(result.getString("category_id")
                        , result.getString("creation_message")
                        , result.getString("keyword"));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    public static List<TicketType> getTypes(Guild guild, MessageReceivedEvent event) {
        List<TicketType> types = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_types(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                types.add(new TicketType(result.getInt("id")
                        , result.getString("category_id")
                        , result.getString("creation_message")
                        , result.getString("keyword")));
            }

        } catch (SQLException e) {
            handleException(e, event);
        }
        return types;
    }

    public static void setCreationMessage(Guild guild, String keyword, String message, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_creation_message(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            statement.setString(3, message);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void createChannel(Guild guild, TextChannel channel,
                                           String ticketOwnerId, String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.create_ticket_channel(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.setString(3, ticketOwnerId);
            statement.setString(4, keyword);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static List<String> getChannelIdsByOwner(Guild guild, String userId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_channel_ids_by_owner(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, userId);
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    public static String getChannelOwnerId(Guild guild, TextChannel channel, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_channel_owner(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    public static List<String> getChannelOwnerRoles(Guild guild, TextChannel channel, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_channel_owner_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    public static void removeChannel(Guild guild, TextChannel channel, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_ticket_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeChannelByUser(Guild guild, String userId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_ticket_channel_by_user(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, userId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void setTypeOwnerRoles(Guild guild, String keyword, String[] roleIds, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_ticket_owner_role(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar", roleIds);
            statement.setArray(3, ids);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void setTypeSupportRoles(Guild guild, String keyword, String[] roleIds, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_ticket_support_role(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar", roleIds);
            statement.setArray(3, ids);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static List<String> getTypeOwnerRoles(Guild guild, String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_owner_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    public static List<String> getTypeSupportRoles(Guild guild, String keyword, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_ticket_support_roles(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }




}
