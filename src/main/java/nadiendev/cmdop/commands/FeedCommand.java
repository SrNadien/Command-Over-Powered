package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class FeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("feed")
            .executes(FeedCommand::feedSelf)
            .then(Commands.argument("player", EntityArgument.players())
                .requires(source -> source.hasPermission(2))
                .executes(FeedCommand::feedOthers)));
    }
    
    private static int feedSelf(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_FEED.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /feed está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            feedPlayer(player);
            player.sendSystemMessage(Component.literal("§a¡Has sido alimentado!"));
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cSolo los jugadores pueden usar este comando."));
            return 0;
        }
    }
    
    private static int feedOthers(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_FEED.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /feed está deshabilitado."));
            return 0;
        }
        
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "player");
            for (ServerPlayer player : players) {
                feedPlayer(player);
                player.sendSystemMessage(Component.literal("§a¡Has sido alimentado!"));
            }
            ctx.getSource().sendSuccess(() -> Component.literal("§aJugador(es) alimentado(s) exitosamente."), true);
            return players.size();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cNo se pudo encontrar al jugador."));
            return 0;
        }
    }
    
    private static void feedPlayer(ServerPlayer player) {
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
    }
}