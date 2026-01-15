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

public class EnderChestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ec")
            .executes(EnderChestCommand::openSelf)
            .then(Commands.argument("player", EntityArgument.player())
                .requires(source -> source.hasPermission(2))
                .executes(EnderChestCommand::openOther)));
        
        dispatcher.register(Commands.literal("enderchest")
            .executes(EnderChestCommand::openSelf)
            .then(Commands.argument("player", EntityArgument.player())
                .requires(source -> source.hasPermission(2))
                .executes(EnderChestCommand::openOther)));
    }
    
    private static int openSelf(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_ENDERCHEST.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /ec está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, player.getEnderChestInventory()),
            Component.literal("Ender Chest")));
        
        return 1;
    }
    
    private static int openOther(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_ENDERCHEST.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /ec está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            
            player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, target.getEnderChestInventory()),
                Component.literal("Ender Chest de " + target.getName().getString())));
            
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
}