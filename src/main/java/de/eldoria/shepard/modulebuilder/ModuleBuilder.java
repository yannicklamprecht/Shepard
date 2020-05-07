package de.eldoria.shepard.modulebuilder;


import de.eldoria.shepard.commandmodules.SharedResources;

public interface ModuleBuilder extends ReqAssigner {
    /**
     * Method which is calles in every module builder when a new module is build.
     *
     * @param resources resources for initializing module parts
     */
    void buildModule(SharedResources resources);
}
