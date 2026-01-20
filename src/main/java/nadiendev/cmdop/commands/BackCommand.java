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
            .requires(source -> source.hasPermission(2))
            .executes(BackCommand::teleportBack));
        
        // Alias
        dispatcher.register(Commands.literal("return")
            .requires(source -> source.hasPermission(2))
            .executes(BackCommand::teleportBack));
        
        dispatcher.register(Commands.literal("eback")
            .requires(source -> source.hasPermission(2))
            .executes(BackCommand::teleportBack));
    }
    
    private static int teleportBack(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        BlockPos targetPos = null;
        String targetDim = null;
        String message = null;
        
        // Prioridad 1: Ubicación anterior (de teletransporte)
        if (data.getPreviousLocation() != null && data.getPreviousDimension() != null) {
            targetPos = data.getPreviousLocation();
            targetDim = data.getPreviousDimension();
            message = "§aTeletransportado a tu ubicación anterior.";
        }
        // Prioridad 2: Ubicación de muerte
        else if (data.getLastDeathLocation() != null && data.getLastDeathDimension() != null) {
            targetPos = data.getLastDeathLocation();
            targetDim = data.getLastDeathDimension();
            message = "§aTeletransportado a tu último punto de muerte.";
        }
        
        if (targetPos == null || targetDim == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes un punto anterior o de muerte reciente."));
            return 0;
        }
        
        ResourceKey<Level> dimension = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            ResourceLocation.parse(targetDim));
        ServerLevel level = player.getServer().getLevel(dimension);
        
        if (level == null) {
            player.sendSystemMessage(Component.literal("§cDimensión no encontrada."));
            return 0;
        }
        
        // Guardar la ubicación actual antes de teletransportar
        savePreviousLocation(player);
        
        player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 
            player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal(message));
        
        return 1;
    }
    
    /**
     * Guarda la ubicación actual del jugador como ubicación anterior
     */
    public static void savePreviousLocation(ServerPlayer player) {
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        BlockPos currentPos = player.blockPosition();
        String currentDim = player.level().dimension().location().toString();
        data.setPreviousLocation(currentPos, currentDim);
        PlayerDataManager.savePlayerData(player.getUUID());
    }
}