package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SpeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
  
        dispatcher.register(Commands.literal("speed")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("speed", FloatArgumentType.floatArg(0, 10))
                .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), null, true))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), 
                        EntityArgument.getPlayer(ctx, "player"), true)))));
        
       
        dispatcher.register(Commands.literal("flyspeed")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("speed", FloatArgumentType.floatArg(0, 10))
                .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), null, true))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), 
                        EntityArgument.getPlayer(ctx, "player"), true)))));
        
       
        dispatcher.register(Commands.literal("walkspeed")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("speed", FloatArgumentType.floatArg(0, 10))
                .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), null, false))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> setSpeed(ctx, FloatArgumentType.getFloat(ctx, "speed"), 
                        EntityArgument.getPlayer(ctx, "player"), false)))));
    }
    
    private static int setSpeed(CommandContext<CommandSourceStack> ctx, float speed, ServerPlayer target, boolean isFlySpeed) {
        if (!cmdopConfig.ENABLE_SPEED.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /speed está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = target != null ? target : ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        
        
        if (isFlySpeed) {
            float flySpeed = speed * 0.05f; // 1 = normal, 10 = muy rápido
            player.getAbilities().setFlyingSpeed(flySpeed);
            player.onUpdateAbilities();
            
            if (target != null) {
                ctx.getSource().sendSuccess(() -> Component.literal("§aVelocidad de vuelo de " + 
                    target.getName().getString() + " establecida a " + speed + "."), true);
            }
            player.sendSystemMessage(Component.literal("§aVelocidad de vuelo establecida a " + speed + "."));
        } else {
            float walkSpeed = speed * 0.1f; // 1 = normal, 10 = muy rápido
            player.getAbilities().setWalkingSpeed(walkSpeed);
            player.onUpdateAbilities();
            
            if (target != null) {
                ctx.getSource().sendSuccess(() -> Component.literal("§aVelocidad de caminar de " + 
                    target.getName().getString() + " establecida a " + speed + "."), true);
            }
            player.sendSystemMessage(Component.literal("§aVelocidad de caminar establecida a " + speed + "."));
        }
        
        return 1;
    }
}