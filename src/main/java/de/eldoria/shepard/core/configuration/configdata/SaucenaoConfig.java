package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

@Data
public class SaucenaoConfig {
    private String token;
    private int longDuration;
    private int shortDuration;
    private int longLimit;
    private int shortLimit;
    private int longUserLimit;
    private int shortUserLimit;
    private int longGuildLimit;
    private int shortGuildLimit;
}
