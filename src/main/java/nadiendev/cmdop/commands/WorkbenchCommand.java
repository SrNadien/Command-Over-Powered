package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.util.DummyCraftingMenu;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class WorkbenchCommand {
    
    private static final Component WORKBENCH_TITLE = Component.translatable("container.crafting");
    
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
            (id, inventory, p) -> new DummyCraftingMenu(id, inventory, 
                ContainerLevelAccess.create(player.level(), player.blockPosition())), 
            WORKBENCH_TITLE));
        
        return 1;
    }
}