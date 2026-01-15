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
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(ctx -> teleportHome(ctx, StringArgumentType.getString(ctx, "name")))));
    }
    
    private static int teleportHome(CommandContext<CommandSourceStack> ctx, String name) {
        if (!cmdopConfig.ENABLE_HOME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /home está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        long cooldown = cmdopConfig.HOME_COOLDOWN.get() * 1000L;
        long timeSince = System.currentTimeMillis() - data.getLastHomeUse();
        if (cooldown > 0 && timeSince < cooldown && !player.hasPermissions(2)) {
            long remaining = (cooldown - timeSince) / 1000;
            player.sendSystemMessage(Component.literal("§cDebes esperar " + remaining + " segundos."));
            return 0;
        }
        
        if (name.equals("home") && data.getHomes().size() == 1) {
            name = data.getHomes().keySet().iterator().next();
        }
        
        PlayerDataManager.HomeData home = data.getHomes().get(name);
        if (home == null) {
            player.sendSystemMessage(Component.literal("§cHogar '" + name + "' no encontrado."));
            return 0;
        }
        
        ResourceKey<Level> dimension = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            ResourceLocation.parse(home.getDimension()));
        ServerLevel level = player.getServer().getLevel(dimension);
        
        if (level == null) {
            player.sendSystemMessage(Component.literal("§cDimensión no encontrada."));
            return 0;
        }
        
        BlockPos pos = home.getPosition();
        player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("§aTeletransportado a '" + name + "'."));
        data.setLastHomeUse(System.currentTimeMillis());
        
        return 1;
    }
}