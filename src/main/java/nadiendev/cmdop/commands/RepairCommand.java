package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.commands.init.MessageUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RepairCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!cmdopConfig.ENABLE_REPAIR.get()) return;
        
        dispatcher.register(Commands.literal("repair")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayer();
                if (player == null) return 0;
                
                return repairItem(player);
            })
        );
        
        dispatcher.register(Commands.literal("fix")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayer();
                if (player == null) return 0;
                
                return repairItem(player);
            })
        );
    }
    
    private static int repairItem(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        
        if (stack.isEmpty()) {
            player.sendSystemMessage(MessageUtil.error("No tienes ningún item en la mano."));
            return 0;
        }
        
        if (!stack.isDamageableItem() && stack.getMaxDamage() == 0) {
            player.sendSystemMessage(MessageUtil.error("Este item no se puede reparar."));
            return 0;
        }
        
        if (stack.getDamageValue() == 0) {
            player.sendSystemMessage(MessageUtil.info("Este item ya está reparado."));
            return 0;
        }
        
        stack.setDamageValue(0);
        player.sendSystemMessage(MessageUtil.success("Item reparado exitosamente."));
        
        return 1;
    }
}