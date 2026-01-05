package co.lemee.servui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServUiMod {
    public static final String MOD_ID = "servui";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static void initialize() {
        LOGGER.info("ServUI loaded!");
    }
}
