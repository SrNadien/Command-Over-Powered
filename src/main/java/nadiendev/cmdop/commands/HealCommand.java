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

public class HealCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
            .executes(HealCommand::healSelf)
            .then(Commands.argument("player", EntityArgument.players())
                .requires(source -> source.hasPermission(2))
                .executes(HealCommand::healOthers)));
    }
    
    private static int healSelf(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_HEAL.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /heal está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            healPlayer(player);
            player.sendSystemMessage(Component.literal("§a¡Has sido curado!"));
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cSolo los jugadores pueden usar este comando."));
            return 0;
        }
    }
    
    private static int healOthers(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_HEAL.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /heal está deshabilitado."));
            return 0;
        }
        
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "player");
            for (ServerPlayer player : players) {
                healPlayer(player);
                player.sendSystemMessage(Component.literal("§a¡Has sido curado!"));
            }
            ctx.getSource().sendSuccess(() -> Component.literal("§aJugador(es) curado(s) exitosamente."), true);
            return players.size();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cNo se pudo encontrar al jugador."));
            return 0;
        }
    }
    
    private static void healPlayer(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.removeAllEffects();
        player.setAirSupply(player.getMaxAirSupply());
        player.clearFire();
    }
}