package net.dodogang.splatcraftserver.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager
                .literal("discord")
                    .executes(context -> {
                            context.getSource().getPlayer().sendMessage(new LiteralText("Click here to join the Splatcraft Discord!").styled((style) -> style.withColor(TextColor.fromRgb(0x7289DA)).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.splatcraft.net")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("https://discord.splatcraft.net")))), false);
                            return 1;
                        }
                    )
        );
    }
}
