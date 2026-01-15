package nadiendev.cmdop.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class ThorCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("thor")
            .requires(source -> source.hasPermission(2))
            .executes(ThorCommand::strikeNearest)
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ThorCommand::strikePlayer)));
    }
    
    private static int strikePlayer(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            strikeLightning(target.serverLevel(), target.blockPosition());
            ctx.getSource().sendSuccess(() -> Component.literal("§aRayo lanzado a " + 
                target.getName().getString() + "."), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
    
    private static int strikeNearest(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer executor = ctx.getSource().getPlayer();
        if (executor == null) return 0;
        
        ServerPlayer nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (ServerPlayer player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
            if (player != executor) {
                double dist = player.distanceToSqr(executor);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = player;
                }
            }
        }
        
        if (nearest == null) {
            executor.sendSystemMessage(Component.literal("§cNo hay jugadores cerca."));
            return 0;
        }
        
        final ServerPlayer finalNearest = nearest;
        strikeLightning(finalNearest.serverLevel(), finalNearest.blockPosition());
        ctx.getSource().sendSuccess(() -> Component.literal("§aRayo lanzado a " + 
            finalNearest.getName().getString() + "."), true);
        
        return 1;
    }
    
    private static void strikeLightning(ServerLevel level, BlockPos pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            bolt.setVisualOnly(false);
            level.addFreshEntity(bolt);
        }
    }
}