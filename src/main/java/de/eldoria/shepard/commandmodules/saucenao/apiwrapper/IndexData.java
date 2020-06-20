package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import lombok.Getter;

@Getter
public class IndexData {
    /**
     * status fields are 0 if success,
     * >0 for server side errors (failed descriptor gen, failed query, etc),
     * <0 for client side errors (bad image, out of searches, etc).
     * Not all errors are properly tagged yet, and some may exit the api.
     */
    private int status;
    /**
     * Id of the parent index.
     */
    private int parentId;
    /**
     * Id of the index.
     */
    private int id;
    /**
     * Count of results.
     */
    private int results = 0;
}
