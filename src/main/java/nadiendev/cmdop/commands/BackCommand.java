package nadiendev.cmdop.commands;

import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class BackCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("back")
            .executes(BackCommand::teleportBack));
    }
    
    private static int teleportBack(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        BlockPos deathPos = data.getLastDeathLocation();
        String deathDim = data.getLastDeathDimension();
        
        if (deathPos == null || deathDim == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes un punto de muerte reciente."));
            return 0;
        }
        
        ResourceKey<Level> dimension = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            ResourceLocation.parse(deathDim));
        ServerLevel level = player.getServer().getLevel(dimension);
        
        if (level == null) {
            player.sendSystemMessage(Component.literal("§cDimensión no encontrada."));
            return 0;
        }
        
        player.teleportTo(level, deathPos.getX() + 0.5, deathPos.getY(), deathPos.getZ() + 0.5, 
            player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("§aTeletransportado a tu último punto de muerte."));
        
        return 1;
    }
}