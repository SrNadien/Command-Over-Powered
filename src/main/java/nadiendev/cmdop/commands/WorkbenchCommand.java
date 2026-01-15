package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class WorkbenchCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wb")
            .executes(WorkbenchCommand::openWorkbench));
        
        dispatcher.register(Commands.literal("workbench")
            .executes(WorkbenchCommand::openWorkbench));
        
        dispatcher.register(Commands.literal("craft")
            .executes(WorkbenchCommand::openWorkbench));
    }
    
    private static int openWorkbench(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_WORKBENCH.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /wb está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> new CraftingMenu(id, inv, 
                ContainerLevelAccess.create(player.level(), player.blockPosition())),
            Component.translatable("container.crafting")));
        
        return 1;
    }
}