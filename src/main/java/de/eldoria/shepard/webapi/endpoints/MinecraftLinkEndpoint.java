package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import de.eldoria.shepard.database.queries.MinecraftLinkData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;

import javax.sql.DataSource;

import static spark.Spark.path;
import static spark.Spark.post;

public class MinecraftLinkEndpoint implements ReqDataSource {
    private MinecraftLinkData minecraftLinkData;

    /**
     * Create a new minecraft link endpoint.
     */
    public MinecraftLinkEndpoint() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> post("/minecraftlink/", (request, response) -> {
            minecraftLinkData.addLinkCode("", "", null);
            return HttpStatusCodes.STATUS_CODE_OK;
        }));
    }

    @Override
    public void addDataSource(DataSource source) {
        minecraftLinkData = new MinecraftLinkData(source);
    }
}