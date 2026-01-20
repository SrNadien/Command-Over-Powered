package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class GamemodeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Comando principal /gm con números
        dispatcher.register(Commands.literal("gm")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
                .executes(ctx -> changeGamemode(ctx, IntegerArgumentType.getInteger(ctx, "mode"), null))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> changeGamemode(ctx, IntegerArgumentType.getInteger(ctx, "mode"), 
                        EntityArgument.getPlayer(ctx, "player"))))));
        
        // /gmc - Cambiar a modo creativo
        dispatcher.register(Commands.literal("gmc")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> changeGamemode(ctx, 1, null))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> changeGamemode(ctx, 1, EntityArgument.getPlayer(ctx, "player")))));
        
        // /gms - Cambiar a modo supervivencia
        dispatcher.register(Commands.literal("gms")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> changeGamemode(ctx, 0, null))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> changeGamemode(ctx, 0, EntityArgument.getPlayer(ctx, "player")))));
        
        // /gma - Cambiar a modo aventura
        dispatcher.register(Commands.literal("gma")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> changeGamemode(ctx, 2, null))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> changeGamemode(ctx, 2, EntityArgument.getPlayer(ctx, "player")))));
        
        // /gmsp - Cambiar a modo espectador
        dispatcher.register(Commands.literal("gmsp")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> changeGamemode(ctx, 3, null))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> changeGamemode(ctx, 3, EntityArgument.getPlayer(ctx, "player")))));
    }
    
    private static int changeGamemode(CommandContext<CommandSourceStack> ctx, int mode, ServerPlayer target) {
        if (!cmdopConfig.ENABLE_GAMEMODE.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando de gamemode está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = target != null ? target : ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        GameType gameType = switch (mode) {
            case 0 -> GameType.SURVIVAL;
            case 1 -> GameType.CREATIVE;
            case 2 -> GameType.ADVENTURE;
            case 3 -> GameType.SPECTATOR;
            default -> null;
        };
        
        if (gameType == null) return 0;
        
        player.setGameMode(gameType);
        String modeName = switch (mode) {
            case 0 -> "Supervivencia";
            case 1 -> "Creativo";
            case 2 -> "Aventura";
            case 3 -> "Espectador";
            default -> "";
        };
        
        if (target != null) {
            ctx.getSource().sendSuccess(() -> Component.literal("§aGamemode de " + target.getName().getString() + 
                " cambiado a " + modeName + "."), true);
        }
        player.sendSystemMessage(Component.literal("§aModo de juego cambiado a " + modeName + "."));
        
        return 1;
    }
}