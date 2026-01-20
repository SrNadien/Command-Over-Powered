package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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

public class HomeCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
            .executes(ctx -> teleportHome(ctx, "home"))
            .then(Commands.argument("name", StringArgumentType.string())
                .executes(ctx -> teleportHome(ctx, StringArgumentType.getString(ctx, "name")))));
    }
    
    private static int teleportHome(CommandContext<CommandSourceStack> ctx, String homeName) {
        if (!cmdopConfig.ENABLE_HOME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /home está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        // Verificar cooldown
        long currentTime = System.currentTimeMillis();
        long lastUse = data.getLastHomeUse();
        int cooldown = cmdopConfig.HOME_COOLDOWN.get() * 1000;
        
        if (currentTime - lastUse < cooldown && !player.hasPermissions(4)) {
            long remaining = (cooldown - (currentTime - lastUse)) / 1000;
            player.sendSystemMessage(Component.literal("§cDebes esperar " + remaining + " segundos para usar /home de nuevo."));
            return 0;
        }
        
        if (!data.hasHome(homeName)) {
            player.sendSystemMessage(Component.literal("§cNo tienes un home llamado '" + homeName + "'."));
            return 0;
        }
        
        PlayerDataManager.HomeData homeData = data.getHomes().get(homeName);
        BlockPos pos = homeData.getPosition();
        String dimension = homeData.getDimension();
        
        ResourceKey<Level> dimensionKey = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            ResourceLocation.parse(dimension));
        ServerLevel level = player.getServer().getLevel(dimensionKey);
        
        if (level == null) {
            player.sendSystemMessage(Component.literal("§cDimensión no encontrada."));
            return 0;
        }
        
        // Guardar ubicación anterior antes de teletransportar
        BackCommand.savePreviousLocation(player);
        
        player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 
            player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("§aTeletransportado a '" + homeName + "'."));
        
        data.setLastHomeUse(currentTime);
        PlayerDataManager.savePlayerData(player.getUUID());
        
        return 1;
    }
}