package co.lemee.servui.fabric;

import co.lemee.servui.ServUiMod;
import net.fabricmc.api.ModInitializer;

public class SGuiFabricMod implements ModInitializer {

    @Override
    public void onInitialize(){
        ServUiMod.initialize();
    }
}