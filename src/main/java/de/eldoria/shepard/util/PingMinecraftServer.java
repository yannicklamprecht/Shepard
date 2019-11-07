package de.eldoria.shepard.util;

import com.google.gson.Gson;
import de.eldoria.shepard.ShepardBot;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PingMinecraftServer {
    private static final String API = "https://api.mcsrvstat.us/2/";

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
        } catch (Exception e) {
            ShepardBot.getLogger().error(e);
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

        public boolean isOnline() {
            return online;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public MotD getMotd() {
            return motd;
        }

        public Players getPlayers() {
            return players;
        }

        public String getVersion() {
            return version;
        }

        public String getHostname() {
            return hostname;
        }

        public String getIcon() {
            return icon;
        }

        public static class MotD {
            private String[] raw;
            private String[] clean;
            private String[] html;

            public String[] getRaw() {
                return raw;
            }

            public String[] getClean() {
                return clean;
            }
        }

        public static class Players {
            private int online;
            private int max;
            private String[] list;

            public int getOnline() {
                return online;
            }

            public int getMax() {
                return max;
            }

            public String[] getList() {
                return list;
            }
        }

        public static class Info {
            private String raw;
            private String clean;
            private String html;

            public String getRaw() {
                return raw;
            }

            public void setRaw(String raw) {
                this.raw = raw;
            }
        }
    }
}
