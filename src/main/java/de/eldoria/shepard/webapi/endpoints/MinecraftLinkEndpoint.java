package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import de.eldoria.shepard.database.queries.commands.MinecraftLinkData;

import static spark.Spark.path;
import static spark.Spark.post;

public class MinecraftLinkEndpoint {
    public MinecraftLinkEndpoint() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> {
            post("/minecraftlink/", (request, response) -> {
                MinecraftLinkData.addLinkCode("", "", null);
                return HttpStatusCodes.STATUS_CODE_OK;
            });

        });
    }
}