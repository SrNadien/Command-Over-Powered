package nadiendev.cmdop.commands;

import nadiendev.cmdop.commands.init.CommandProperties;
import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FlyCommand {
    
    private static final CommandProperties PROPERTIES = CommandProperties.create("fly", 2);
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(PROPERTIES.literal())
            .requires(source -> source.hasPermission(PROPERTIES.defaultRequiredLevel()))
            .executes(FlyCommand::toggleSelf)
            .then(Commands.argument("player", EntityArgument.player())
                .executes(FlyCommand::toggleOther)));
    }
    
    private static int toggleSelf(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_FLY.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /fly está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        toggleFly(player);
        return 1;
    }
    
    private static int toggleOther(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_FLY.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /fly está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            toggleFly(target);
            ctx.getSource().sendSuccess(() -> Component.literal("§aVuelo de " + target.getName().getString() + " cambiado."), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
    
    private static void toggleFly(ServerPlayer player) {
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        boolean flying = !data.isFlying();
        data.setFlying(flying);
        
        player.getAbilities().mayfly = flying;
        if (!flying) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
        
        player.sendSystemMessage(Component.literal(flying ? "§aVuelo activado." : "§cVuelo desactivado."));
    }
}