package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;

public interface ReqExecutionValidator {
    /**
     * Add a execution validator to a object.
     *
     * @param validator execution validator isntance
     */
    void addExecutionValidator(ExecutionValidator validator);
}
