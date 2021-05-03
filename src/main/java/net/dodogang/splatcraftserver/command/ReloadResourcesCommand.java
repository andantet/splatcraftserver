package net.dodogang.splatcraftserver.command;

import com.cibernet.splatcraft.Splatcraft;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.dodogang.splatcraftserver.SplatcraftServer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ReloadResourcesCommand {
    public static final Identifier ID = new Identifier(SplatcraftServer.MOD_ID, "reloadresources");

    private static final SimpleCommandExceptionType NOT_DEDI_SERVER_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("This is not a dedicated server! As such, no server resource pack is present."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager
                .literal(ID.toString())
                    .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
                        .executes(ReloadResourcesCommand::execute)
                        .then(CommandManager.literal("force")
                            .executes(ReloadResourcesCommand::executeForce)
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        MinecraftServer server = Splatcraft.SERVER_INSTANCE;
        if (server instanceof MinecraftDedicatedServer) {
            ServerPropertiesLoader loader = ((MinecraftDedicatedServer) server).propertiesLoader;
            if (loader != null) {
                ServerPropertiesHandler handler = loader.getPropertiesHandler();
                if (handler != null) {
                    Properties properties = new Properties();
                    try {
                        InputStream propertiesStream = Files.newInputStream(Paths.get("server.properties"));
                        properties.load(propertiesStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ctx.getSource().sendFeedback(new LiteralText("Reloaded server resource pack from server.properties!"), true);
                    server.setResourcePack(properties.getProperty("resource-pack"), properties.getProperty("resource-pack-sha1"));
                }
            }

            return 1;
        } else {
            throw NOT_DEDI_SERVER_EXCEPTION.create();
        }
    }
    private static int executeForce(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int result = ReloadResourcesCommand.execute(ctx);

        MinecraftServer server = Splatcraft.SERVER_INSTANCE;
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            if (!server.getResourcePackUrl().isEmpty()) {
                player.sendResourcePackUrl(server.getResourcePackUrl(), server.getResourcePackHash());
            }
        }

        return result;
    }
}
