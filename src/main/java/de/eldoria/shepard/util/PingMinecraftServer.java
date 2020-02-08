package de.eldoria.shepard.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class PingMinecraftServer {
    private static final String API = "https://api.mcsrvstat.us/2/";

    /**
     * Ping a minecraft server and get the result as ping object.
     *
     * @param address address of server to ping
     * @return ping object or null if api is unreachable.
     */
    public static MinecraftPing pingServer(String address) {
        URL url;
        HttpsURLConnection request;
        try {
            url = new URL(API + address);
            request = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            return null;
        }

        BufferedReader in;
        String response;
        try {
            in = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            response = content.toString();
        } catch (IOException e) {
            return null;
        }

        MinecraftPing pingResult = null;
        try {
            Gson gson = new Gson();
            pingResult = gson.fromJson(response, MinecraftPing.class);
        } catch (JsonSyntaxException e) {
            log.error("failed to parse ping response", e);
        }
        return pingResult;
    }

    public static class MinecraftPing {
        private boolean online;
        private String ip;
        private int port;
        private MotD motd;
        private Players players;
        private String version;
        private String hostname;
        private String icon;

        /**
         * Server status.
         *
         * @return true if online
         */
        public boolean isOnline() {
            return online;
        }

        /**
         * Server ip.
         *
         * @return the pinged server ip
         */
        public String getIp() {
            return ip;
        }

        /**
         * Get the pinged port on the ip.
         *
         * @return port of the pinged server
         */
        public int getPort() {
            return port;
        }

        /**
         * MotD object of the server.
         *
         * @return MotD object
         */
        public MotD getMotd() {
            return motd;
        }

        /**
         * Get the player of the server.
         *
         * @return get the players on the server
         */
        public Players getPlayers() {
            return players;
        }

        /**
         * Get the version of the server.
         *
         * @return the version as string.
         */
        public String getVersion() {
            return version;
        }

        /**
         * Get the Hostname of the server.
         *
         * @return hostname of server
         */
        public String getHostname() {
            return hostname;
        }

        /**
         * Get the Server icon as bytes.
         *
         * @return get the server icon
         */
        public String getIcon() {
            return icon;
        }

        public static class MotD {
            private String[] raw;
            private String[] clean;

            /**
             * Get the raw message.
             *
             * @return raw message as string
             */
            public String[] getRaw() {
                return raw;
            }

            /**
             * Get the clean message.
             *
             * @return stripped clean message
             */
            public String[] getClean() {
                return clean;
            }
        }

        public static class Players {
            private int online;
            private int max;
            private String[] list;

            /**
             * Get the amount of online players.
             *
             * @return amount of online players
             */
            public int getOnline() {
                return online;
            }

            /**
             * Get the max player amount.
             *
             * @return max players
             */
            public int getMax() {
                return max;
            }

            /**
             * Get a list of online player names.
             *
             * @return list of players. not always provided.
             */
            public String[] getList() {
                return list;
            }
        }
    }
}
