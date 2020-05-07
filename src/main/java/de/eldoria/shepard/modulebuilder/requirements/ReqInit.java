package de.eldoria.shepard.modulebuilder.requirements;

public interface ReqInit {
    /**
     * The requires init interface will be called after all other requirements were delivered.
     */
    void init();
}
