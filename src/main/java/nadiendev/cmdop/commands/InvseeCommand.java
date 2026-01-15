package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class InvseeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("invsee")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(InvseeCommand::openInventory)));
    }
    
    private static int openInventory(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_INVSEE.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /invsee está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            
            player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                    net.minecraft.world.inventory.MenuType.GENERIC_9x4, id, inv, target.getInventory(), 4),
                Component.literal("Inventario de " + target.getName().getString())));
            
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
}