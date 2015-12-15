import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This module is based off of the Lab09 server to connect the shuffleboard database to the app.
 *
 * @author kvlinden, kjc38
 * @version 12/1/2015
 */
@Path("/shuffle")
public class Shuffleboard {
    /**
     * Constants for a local Postgresql server with the shuffleboard database
     */
    private static final String DB_URI = "jdbc:postgresql://localhost:9998/shuffleboard";
    private static final String DB_LOGIN_ID = "postgres";
    private static final String DB_PASSWORD = "login";

    /**
     * Gets the user that has the parameter id
     *
     * @param id a user id in the shuffleboard database
     * @return a string version of the user record, if any, with the given id
     */
    @GET
    @Path("/user/{id}")
    @Produces("text/plain")
    public String getPlayer(@PathParam("id") int id) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE id=" + id);
            if (resultSet.next()) {
                result = resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3);
            } else {
                result = "nothing found...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * Gets the user that has the parameter name
     *
     * @param username a user name in the shuffleboard database
     * @return a string version of the user record, if any, with the given name
     */
    @GET
    @Path("/userName/{username}")
    @Produces("text/plain")
    public String getPlayerByName(@PathParam("username") String username) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM Users WHERE name='" + username + "'");
            if (resultSet.next()) {
                result = resultSet.getInt(1)+"";
            } else {
                result = "nothing found...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * Gets all the data in Users
     * @return a string representation of the user records in the Users table
     */
    @GET
    @Path("/users")
    @Produces("text/plain")
    public String getPlayers() {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");
            while (resultSet.next()) {
                result += resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + "\n";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * Gets the users that have shared their calendar with the specified user
     *
     * @param id a user id in the shuffleboard database
     * @return a string version of the user's friends
     */
    @GET
    @Path("/user/{id}/friended")
    @Produces("text/plain")
    public String getFriended(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            // Get the users that this user has listed as a friend
            ResultSet outFriends = statement.executeQuery("SELECT name FROM Friends, Users WHERE userID=" + id + " AND friendID = ID");
            ArrayList<String> outs = new ArrayList<String>(1);
            while (outFriends.next()) {
                outs.add(outFriends.getString(1));
            }
            outFriends.close();
            // Get the users that have this user listed as a friend
            ResultSet resultSet = statement.executeQuery("SELECT name FROM Friends, Users WHERE friendID=" + id + " AND userID = ID");
            while (resultSet.next()) {
                if (!outs.contains(resultSet.getString(1)))
                    result += resultSet.getString(1) + ",";
            }
            if (result == "") result = "This user has no friends";
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * Gets the friends of the user that has the parameter id
     *
     * @param id a user id in the shuffleboard database
     * @return a string version of the user's friends
     */
    @GET
    @Path("/user/{id}/friends")
    @Produces("text/plain")
    public String getFriends(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM Friends, Users WHERE userID=" + id + " AND friendID = ID");
            while (resultSet.next()) {
                result += resultSet.getString(1) + ",";
            } if (result == ""){
                result = "This user has no friends.";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * Gets the static events for the user that has the parameter id
     *
     * @param id a user id in the shuffleboard database
     * @return a string version of the static event
     */
    @GET
    @Path("/user/{id}/events/static")
    @Produces("text/plain")
    public String getStaticEvents(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT StaticEvents.ID, label, startTime, stopTime, days " +
                    "FROM Users, StaticEvents WHERE userID=" + id + " ORDER BY StaticEvents.ID");
            while (resultSet.next()) {
                result += resultSet.getInt(1) + "__" + resultSet.getString(2) + "__" + resultSet.getInt(3)
                        + "__" + resultSet.getInt(4) + "__" + resultSet.getString(5) + "___";
            } if (result == ""){
                result = "This user has no static events.";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    //TODO: Get static events for 2 users if they are friends

    /**
     * Gets the dynamic events for the user that has the parameter id
     *
     * @param id a user id in the shuffleboard database
     * @return a string version of the dynamic event
     */
    @GET
    @Path("/user/{id}/events/dynamic")
    @Produces("text/plain")
    public String getDynamicEvents(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT DynamicEvents.ID, label, timesPerWeek, length, days " +
                    "FROM Users, DynamicEvents WHERE userID=" + id + " ORDER BY DynamicEvents.ID");
            while (resultSet.next()) {
                result += resultSet.getInt(1) + "__" + resultSet.getString(2) + "__" + resultSet.getInt(3)
                        + "__" + resultSet.getString(4) + "__" + resultSet.getString(5) + "___";
            } if (result == ""){
                result = "This user has no dynamic events.";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    //TODO: Get dynamic events for 2 users if they are friends

    /**
     * PUT method for creating an instance of Person with a given ID - If the
     * player already exists, replace them with the new player field values.
     *
     * @param id         the ID for the new user, assumed to be unique
     * @param UserLine a string representation of the player in the format: name passwordHash
     * @return status message
     */
    @PUT
    @Path("/user/{id}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putUser(@PathParam("id") int id, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        String name = st.nextToken(), passwordHash = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE id=" + id);
            //I had to break the updates up to work
            if (resultSet.next()) {
                statement.executeUpdate("UPDATE Users SET name='" + name + "' WHERE id=" + id);
                statement.executeUpdate("UPDATE Users SET passwordHash='" + passwordHash + "' WHERE id=" + id);
                result = "User " + id + " updated...";
            } else {
                statement.executeUpdate("INSERT INTO Users VALUES (" + id + ", '" + name + "', '" + passwordHash + "')");
                result = "User " + id + " added...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * PUT method for creating a friendship between the user and another user - If the
     * friendship already exists, nothing happens. (Even if a user changes their name, their ID will stay the same
     * therefore, an option to update the friendship doesn't make sense.)
     *
     * @param id         the ID for user, assumed to be unique
     * @param friendName  the ID of the user for the user to become friends with
     * @return status message
     */
    @PUT
    @Path("/user/{id}/friendName/add/{friendName}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putFriends(@PathParam("id") int id, @PathParam("friendName") String friendName) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE name='" + friendName + "'");
            if (resultSet.next()) {
                int friendID = resultSet.getInt(1);
                ResultSet resultSet1 = statement.executeQuery("SELECT * FROM Friends WHERE userID=" + id + "AND friendID=" + friendID);
                if (resultSet1.next()) {
                    result = "Friendship already exists.";
                } else {
                    statement.executeUpdate("INSERT INTO Friends VALUES (" + id + ", '" + friendID + "')");
                    result = "Friendship between the users with IDs of " + id + " and " + friendID + " added!";
                }
                resultSet1.close();
            } else {
                result = "User does not exist";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * PUT method for creating a friendship between the user and another user - If the
     * friendship already exists, nothing happens. (Even if a user changes their name, their ID will stay the same
     * therefore, an option to update the friendship doesn't make sense.)
     *
     * @param id         the ID for user, assumed to be unique
     * @param friendID  the ID of the user for the user to become friends with
     * @return status message
     */
    @PUT
    @Path("/user/{id}/friend/add/{friendID}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putFriends(@PathParam("id") int id, @PathParam("friendID") int friendID) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Friends WHERE userID=" + id + "AND friendID=" + friendID);
            if (resultSet.next()) {
                result = "Friendship already exists.";
            } else {
                statement.executeUpdate("INSERT INTO Friends VALUES (" + id + ", '" + friendID + "')");
                result = "Friendship between the users with IDs of " + id + " and " + friendID + " added!";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * PUT method for creating a static event for the user - If the
     * event already exists, replace it with the new field values.
     *
     * @param id         the ID for the user
     * @param staticID  the ID of the static event
     * @param UserLine a string representation of the player in the format: startTime stopTime days label
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/static/{staticID}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putStaticEvent(@PathParam("id") int id, @PathParam("staticID") int staticID, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        String startTime = st.nextToken(), stopTime = st.nextToken(), days = st.nextToken(), label = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM StaticEvents WHERE userID=" + id + " AND ID=" + staticID);
            //Unfortunately, I had to break up the updates for them to work
            if (resultSet.next()) {
                statement.executeUpdate("UPDATE StaticEvents SET startTime='" + startTime + "' WHERE ID=" + staticID);
                statement.executeUpdate("UPDATE StaticEvents SET stopTime='" + stopTime + "' WHERE ID=" + staticID);
                statement.executeUpdate("UPDATE StaticEvents SET days='" + days + "' WHERE ID=" + staticID);
                statement.executeUpdate("UPDATE StaticEvents SET label='" + label + "' WHERE ID=" + staticID);
                result = "Static event " + staticID + " updated...";
            } else {
                statement.executeUpdate("INSERT INTO StaticEvents VALUES (" + id + ", '" + startTime + "', '" + stopTime +
                        "', '" + days + "', '" + label + "', '" + staticID + "')");
                result = "Static event " + staticID + " for the user with ID " + id + " added...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    //TODO make this look for duplicates and stuff
    /**
     * POST method for creating a static event, with a new, unique id, for the User whose userid is the path parameter
     * number.
     * <p/>
     * The method creates a new, unique ID by querying the StaticEvents table for the
     * largest ID and adding 1 to that.
     *
     * @param id        the ID for the user
     * @param id        the ID for the event being edited
     * @param data      The data for the event in string format separated by "__"
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/static/edit/{eventID}/{data}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String editStaticEvent(@PathParam("id") int id, @PathParam("eventID") int eventID, @PathParam("data") String data) {
        String result;
        String[] dataVals = data.split("__");
        dataVals[3] = dataVals[3].replace("%20", " ");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE StaticEvents SET startTime=" + dataVals[0] + ", stopTime=" + dataVals[1] +
                    ", days='" + dataVals[2] + "', label='" + dataVals[3] + "' WHERE userID=" + id + " AND id=" + eventID);
            System.out.println("UPDATE StaticEvents SET startTime=" + dataVals[0] + ", stopTime=" + dataVals[1] +
                    ", days='" + dataVals[2] + "', '" + dataVals[3] + "'");
            statement.close();
            connection.close();
            result = "Static event " + eventID + " for the user with ID " + id + " edited...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * POST method for creating a static event, with a new, unique id, for the User whose userid is the path parameter
     * number.
     * <p/>
     * The method creates a new, unique ID by querying the StaticEvents table for the
     * largest ID and adding 1 to that.
     *
     * @param id        the ID for the user
     * @param data      The data for the event in string format separated by "__"
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/static/addLong/{data}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putStaticEventLong(@PathParam("id") int id, @PathParam("data") String data) {
        String result;
        int staticID = -1;
        String[] dataVals = data.split("__");
        dataVals[3] = dataVals[3].replace("%20", " ");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) FROM StaticEvents");
            if (resultSet.next()) {
                staticID = resultSet.getInt(1) + 1;
            }
            statement.executeUpdate("INSERT INTO StaticEvents VALUES (" + id + ", " + dataVals[0] + ", " + dataVals[1] +
                    ", '" + dataVals[2] + "', '" + dataVals[3] + "')");
            resultSet.close();
            statement.close();
            connection.close();
            result = "Static event " + staticID + " for the user with ID " + id + " added...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * PUT method for creating a dynamic event for the user - If the
     * event already exists, replace it with the new field values.
     *
     * @param id         the ID for the user
     * @param dynamicID  the ID of the dynamic event
     * @param UserLine a string representation of the player in the format:
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/dynamic/{dynamicID}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putDynamicEvent(@PathParam("id") int id, @PathParam("dynamicID") int dynamicID, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        String timesPerWeek = st.nextToken(), length = st.nextToken(), days = st.nextToken(), label = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DynamicEvents WHERE userID=" + id + " AND ID=" + dynamicID);
            //Unfortunately, I had to break up the updates for them to work
            if (resultSet.next()) {
                statement.executeUpdate("UPDATE DynamicEvents SET timesPerWeek='" + timesPerWeek + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET length='" + length + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET days='" + days + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET label='" + label + "' WHERE ID=" + dynamicID);
                result = "Dynamic event " + dynamicID + " updated...";
            } else {
                statement.executeUpdate("INSERT INTO DynamicEvents VALUES (" + id + ", '" + timesPerWeek + "', '" + length +
                        "', '" + days + "', '" + label + "', '" + dynamicID + "')");
                result = "Static event " + dynamicID + " for the user with ID " + id + " added...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    //TODO make this look for duplicates and stuff
    /**
     * PUT method for creating a dynamic event for the user - If the
     * event already exists, replace it with the new field values.
     *
     * @param id        the ID for the user
     * @param data      The data for the event in string format separated by "__"
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/dynamic/addLong/{data}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putDynamicEventLong(@PathParam("id") int id, @PathParam("data") String data) {
        String result = "";
        String[] dataVals = data.split("__");
        dataVals[2] = dataVals[2].replace("_", ".");
        dataVals[3] = dataVals[3].replace("%20", " ");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
/*            ResultSet resultSet = statement.executeQuery("SELECT * FROM DynamicEvents WHERE userID=" + id + " AND ID=" + dynamicID);
            //Unfortunately, I had to break up the updates for them to work
            if (resultSet.next()) {
                statement.executeUpdate("UPDATE DynamicEvents SET timesPerWeek='" + timesPerWeek + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET length='" + length + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET days='" + days + "' WHERE ID=" + dynamicID);
                statement.executeUpdate("UPDATE DynamicEvents SET label='" + label + "' WHERE ID=" + dynamicID);
                result = "Dynamic event " + dynamicID + " updated...";
            } else {*/
                statement.executeUpdate("INSERT INTO DynamicEvents VALUES (" + id + ", '" + dataVals[0] + "', '" + dataVals[1] +
                        "', '" + dataVals[2] + "', '" + dataVals[3] + "')");
                result = "Static event " + dataVals[3] + " for the user with ID " + id + " added...";
//            }
//            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * PUT method for creating a dynamic event for the user - If the
     * event already exists, replace it with the new field values.
     *
     * @param id        the ID for the user
     * @param eventID   the ID for the event being changed
     * @param data      The data for the event in string format separated by "__"
     * @return status message
     */
    @PUT
    @Path("/user/{id}/events/dynamic/edit/{eventID}/{data}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String editDynamicEvent(@PathParam("id") int id, @PathParam("eventID") int eventID, @PathParam("data") String data) {
        String result = "";
        String[] dataVals = data.split("__");
        dataVals[3] = dataVals[3].replace("%20", " ");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
                statement.executeUpdate("UPDATE DynamicEvents SET timesPerWeek='" + dataVals[0] + "' WHERE ID=" + eventID + " AND userID=" + id);
                statement.executeUpdate("UPDATE DynamicEvents SET length='" + dataVals[1] + "' WHERE ID=" + eventID + " AND userID=" + id);
                statement.executeUpdate("UPDATE DynamicEvents SET days='" + dataVals[2] + "' WHERE ID=" + eventID + " AND userID=" + id);
                statement.executeUpdate("UPDATE DynamicEvents SET label='" + dataVals[3] + "' WHERE ID=" + eventID + " AND userID=" + id);
                result = "Dynamic event " + eventID + " updated...";
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }


    /**
     * POST method for creating an instance of User with a new, unique ID
     * number.
     * <p/>
     * The method creates a new, unique ID by querying the player table for the
     * largest ID and adding 1 to that.
     *
     * @param UserLine a string representation of the player in the format: name passwordHash
     * @return status message
     */
    @POST
    @Path("/user")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postUser(String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        int id = -1;
        String name = st.nextToken(), passwordHash = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) FROM Users");
            if (resultSet.next()) {
                id = resultSet.getInt(1) + 1;
            }
            statement.executeUpdate("INSERT INTO Users VALUES (" + id + ", '" + name + "', '" + passwordHash + "')");
            resultSet.close();
            statement.close();
            connection.close();
            result = "User " + id + " added...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * POST method for creating a friendship with two already existing users.
     *
     * @param UserLine a string representation of the other user in the format: friendID
     * @return status message
     */
    @POST
    @Path("/user/{id}/friend")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postFriend(@PathParam("id") int id, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        String friendID = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO Friends VALUES (" + id + ", '" + friendID + "')");
            statement.close();
            connection.close();
            result = "Friendship between the users with IDs of " + id + " and " + friendID + " added!";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * POST method for creating a static event, with a new, unique id, for the User whose userid is the path parameter
     * number.
     * <p/>
     * The method creates a new, unique ID by querying the StaticEvents table for the
     * largest ID and adding 1 to that.
     *
     * @param UserLine a string representation of the player in the format: startTime stopTime days label
     * @return status message
     */
    @POST
    @Path("/user/{id}/events/static/add/")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postStaticEvent(@PathParam("id") int id, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        int staticID = -1;
        String startTime = st.nextToken(), stopTime = st.nextToken(), days = st.nextToken(), label = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) FROM StaticEvents");
            if (resultSet.next()) {
                staticID = resultSet.getInt(1) + 1;
            }
            statement.executeUpdate("INSERT INTO StaticEvents VALUES (" + id + ", '" + startTime + "', '" + stopTime +
                    "', '" + days + "', '" + label + "', '" + staticID + "')");
            resultSet.close();
            statement.close();
            connection.close();
            result = "Static event " + staticID + " for the user with ID " + id + " added...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * POST method for creating a dynamic event, with a new, unique id, for the User whose userid is the path parameter
     * number.
     * <p/>
     * The method creates a new, unique ID by querying the DynamicEvents table for the
     * largest ID and adding 1 to that.
     *
     * @param UserLine a string representation of the player in the format: timesPerWeek, length, days, label
     * @return status message
     */
    @POST
    @Path("/user/{id}/events/dynamic/add")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postDynamicEvent(@PathParam("id") int id, String UserLine) {
        String result;
        StringTokenizer st = new StringTokenizer(UserLine);
        int dynamicID = -1;
        String timesPerWeek = st.nextToken(), length = st.nextToken(), days = st.nextToken(), label = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) FROM DynamicEvents");
            if (resultSet.next()) {
                dynamicID = resultSet.getInt(1) + 1;
            }
            statement.executeUpdate("INSERT INTO DynamicEvents VALUES (" + id + ", '" + timesPerWeek + "', '" + length +
                    "', '" + days + "', '" + label + "', '" + dynamicID + "')");
            resultSet.close();
            statement.close();
            connection.close();
            result = "Dynamic event " + dynamicID + " for the user with ID " + id + " added...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * DELETE method for deleting an instance of user with the given ID. If
     * the player doesn't exist, then don't delete anything.
     *
     * @param id the ID of the player to be returned
     * @return a simple text confirmation message
     */
    @DELETE
    @Path("/user/{id}")
    @Produces("text/plain")
    public String deleteUser(@PathParam("id") int id) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Users WHERE id=" + id);
            statement.close();
            connection.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "User " + id + " deleted...";
    }

    /**
     * DELETE method for deleting the friendship with another user with the given ID. If
     * the friendship doesn't exist, then don't delete anything.
     *
     * @param id the ID of the player to be returned
     * @param friendID the ID of the user's friend to be deleted
     * @return a simple text confirmation message
     */
    @DELETE
    @Path("/user/{id}/friend/delete/{friendID}")
    @Produces("text/plain")
    public String deleteFriend(@PathParam("id") int id, @PathParam("friendID") int friendID) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Friends WHERE friendID=" + friendID + " AND userID=" + id);
            statement.close();
            connection.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Friendship between the users with IDs of " + id + " and " + friendID + " deleted...";
    }

    /**
     * DELETE method for deleting the user's static event with the given ID. If
     * the event doesn't exist, then don't delete anything.
     *
     * @param id the ID of the player to be returned
     * @param staticID the ID of the user's event to be deleted
     * @return a simple text confirmation message
     */
    @DELETE
    @Path("/user/{id}/events/static/{staticID}")
    @Produces("text/plain")
    public String deleteStaticEvent(@PathParam("id") int id, @PathParam("staticID") int staticID) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM StaticEvents WHERE ID=" + staticID + " AND userID=" + id);
            statement.close();
            connection.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "The static event (" + staticID + ") for user with the ID " + id + " has been deleted...";
    }

    /**
     * DELETE method for deleting the user's dynamic event with the given ID. If
     * the event doesn't exist, then don't delete anything.
     *
     * @param id the ID of the player to be returned
     * @param dynamicID the ID of the user's event to be deleted
     * @return a simple text confirmation message
     */
    @DELETE
    @Path("/user/{id}/events/dynamic/{dynamicID}")
    @Produces("text/plain")
    public String deleteDynamicEvent(@PathParam("id") int id, @PathParam("dynamicID") int dynamicID) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGIN_ID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM DynamicEvents WHERE ID=" + dynamicID + " AND userID=" + id);
            statement.close();
            connection.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "The static event (" + dynamicID + ") for user with the ID " + id + " has been deleted...";
    }

    /**
     * Run this main method to fire up the service.
     *
     * @param args command-line arguments (ignored)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://10.0.0.31:9999/");
        server.start();

        System.out.println("Server running...");
        System.out.println("Visit: http://localhost:9999/shuffle");
        System.out.println("Hit return to stop...");
        //noinspection ResultOfMethodCallIgnored
        System.in.read();
        System.out.println("Stopping server...");
        server.stop(0);
        System.out.println("Server stopped...");
    }
}