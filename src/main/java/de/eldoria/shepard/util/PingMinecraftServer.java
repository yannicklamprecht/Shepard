package de.eldoria.shepard.util;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.eldoria.shepard.ShepardBot;
import okhttp3.Request;
import okhttp3.Request.Builder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PingMinecraftServer {
    private static final String API = "https://api.mcsrvstat.us/2/";

    public static MinecraftPing pingServer(String address) {
        URL url;
        InputStream con;
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
            //System.out.println(response);

        } catch (IOException e) {
            return null;
        }


        ObjectMapper mapper = new ObjectMapper();
        MinecraftPing pingResult = null;
        try {

            pingResult = mapper.readValue(response, MinecraftPing.class);
        } catch (IOException e) {
            ShepardBot.getLogger().error(e);
        }
        return pingResult;
    }

    public static class MinecraftPing {
        private boolean online;
        private String ip;
        private int port;
        @JsonIgnore
        private Debug debug;
        private Motd motd;
        private Players players;
        private String version;
        private int protocol;
        private String hostname;
        private String icon;
        private String software;
        private String map;
        private Plugins plugins;
        private Mods mods;
        private Info info;

        private static class Debug {
            boolean ping;
            boolean query;
            boolean srv;
            boolean querymismatch;
            boolean ipinsrv;
            boolean animatedmotd;
            boolean proxypipe;
            int cachetime;
            int apiVersion;
            Dns dns;

            public boolean isPing() {
                return ping;
            }

            public void setPing(boolean ping) {
                this.ping = ping;
            }

            public boolean isQuery() {
                return query;
            }

            public void setQuery(boolean query) {
                this.query = query;
            }

            public boolean isSrv() {
                return srv;
            }

            public void setSrv(boolean srv) {
                this.srv = srv;
            }

            public boolean isQuerymismatch() {
                return querymismatch;
            }

            public void setQuerymismatch(boolean querymismatch) {
                this.querymismatch = querymismatch;
            }

            public boolean isIpinsrv() {
                return ipinsrv;
            }

            public void setIpinsrv(boolean ipinsrv) {
                this.ipinsrv = ipinsrv;
            }

            public boolean isAnimatedmotd() {
                return animatedmotd;
            }

            public void setAnimatedmotd(boolean animatedmotd) {
                this.animatedmotd = animatedmotd;
            }

            public boolean isProxypipe() {
                return proxypipe;
            }

            public void setProxypipe(boolean proxypipe) {
                this.proxypipe = proxypipe;
            }

            public int getCachetime() {
                return cachetime;
            }

            public void setCachetime(int cachetime) {
                this.cachetime = cachetime;
            }

            public int getApiVersion() {
                return apiVersion;
            }

            @JsonSetter("api_version")
            public void setApiVersion(int apiVersion) {
                this.apiVersion = apiVersion;
            }

            public Dns getDns() {
                return dns;
            }

            public void setDns(Dns dns) {
                this.dns = dns;
            }

            private static class Dns {
                Record[] a;

                public Record[] getA() {
                    return a;
                }

                public void setA(Record[] a) {
                    this.a = a;
                }

                private static class Record {
                    String host;
                    String recordClass;
                    int ttl;
                    String type;
                    String ip;

                    public String getHost() {
                        return host;
                    }

                    public void setHost(String host) {
                        this.host = host;
                    }

                    public String getRecordClass() {
                        return recordClass;
                    }

                    @JsonSetter("class")
                    public void setRecordClass(String recordClass) {
                        this.recordClass = recordClass;
                    }

                    public int getTtl() {
                        return ttl;
                    }

                    public void setTtl(int ttl) {
                        this.ttl = ttl;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getIp() {
                        return ip;
                    }

                    public void setIp(String ip) {
                        this.ip = ip;
                    }
                }
            }
        }


        private static class Motd {
            private String[] raw;
            private String[] clean;
            private String[] html;

            public String[] getRaw() {
                return raw;
            }

            public void setRaw(String[] raw) {
                this.raw = raw;
            }

            public String[] getClean() {
                return clean;
            }

            public void setClean(String[] clean) {
                this.clean = clean;
            }

            public String[] getHtml() {
                return html;
            }

            public void setHtml(String[] html) {
                this.html = html;
            }
        }

        private static class Players {
            int online;
            int max;
            String[] list;

            public int getOnline() {
                return online;
            }

            public void setOnline(int online) {
                this.online = online;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public String[] getList() {
                return list;
            }

            public void setList(String[] list) {
                this.list = list;
            }
        }

        private static class Plugins {
            String[] names;
            String[] raw;

            public String[] getNames() {
                return names;
            }

            public void setNames(String[] names) {
                this.names = names;
            }

            public String[] getRaw() {
                return raw;
            }

            public void setRaw(String[] raw) {
                this.raw = raw;
            }
        }

        private static class Mods {
            String[] names;
            String[] raw;

            public String[] getNames() {
                return names;
            }

            public void setNames(String[] names) {
                this.names = names;
            }

            public String[] getRaw() {
                return raw;
            }

            public void setRaw(String[] raw) {
                this.raw = raw;
            }
        }

        private static class Info {
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

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public Debug getDebug() {
            return debug;
        }

        public void setDebug(Debug debug) {
            this.debug = debug;
        }

        public Motd getMotd() {
            return motd;
        }

        public void setMotd(Motd motd) {
            this.motd = motd;
        }

        public Players getPlayers() {
            return players;
        }

        public void setPlayers(Players players) {
            this.players = players;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getProtocol() {
            return protocol;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSoftware() {
            return software;
        }

        public void setSoftware(String software) {
            this.software = software;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public Plugins getPlugins() {
            return plugins;
        }

        public void setPlugins(Plugins plugins) {
            this.plugins = plugins;
        }

        public Mods getMods() {
            return mods;
        }

        public void setMods(Mods mods) {
            this.mods = mods;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }
    }
}
