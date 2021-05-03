package net.dodogang.splatcraftserver;

import net.dodogang.splatcraftserver.command.DiscordCommand;
import net.dodogang.splatcraftserver.command.ReloadResourcesCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.example.GeckoLibMod;

public class SplatcraftServer implements DedicatedServerModInitializer {
    public static final String MOD_ID = "splatcraftserver";
    public static final String MOD_NAME = "Splatcraft (Dodo Gang Server)";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        log("Initializing");

        GeckoLibMod.DISABLE_IN_DEV = true;

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            DiscordCommand.register(dispatcher);
            ReloadResourcesCommand.register(dispatcher);
        });

        log("Initialized");
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static void log(String message) {
        log(Level.INFO, message);
    }
}
