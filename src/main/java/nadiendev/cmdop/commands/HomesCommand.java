package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class HomesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("homes")
            .executes(ctx -> listHomes(ctx, null))
            .then(Commands.argument("player", EntityArgument.player())
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> listHomes(ctx, EntityArgument.getPlayer(ctx, "player")))));
    }
    
    private static int listHomes(CommandContext<CommandSourceStack> ctx, ServerPlayer targetPlayer) {
        if (!cmdopConfig.ENABLE_HOME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /homes está deshabilitado."));
            return 0;
        }
        
        ServerPlayer viewer = ctx.getSource().getPlayer();
        if (viewer == null) return 0;
        
        // Si no se especifica un jugador, mostrar los propios homes
        ServerPlayer target = targetPlayer != null ? targetPlayer : viewer;
        boolean isOwnHomes = target.getUUID().equals(viewer.getUUID());
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(target.getUUID());
        Map<String, PlayerDataManager.HomeData> homes = data.getHomes();
        
        if (homes.isEmpty()) {
            String message = isOwnHomes ? 
                "§eNo tienes hogares guardados." : 
                "§e" + target.getName().getString() + " no tiene hogares guardados.";
            viewer.sendSystemMessage(Component.literal(message));
            return 0;
        }
        
        // Título
        String title = isOwnHomes ? 
            "§6§l=== Tus Hogares (" + homes.size() + ") ===" :
            "§6§l=== Hogares de " + target.getName().getString() + " (" + homes.size() + ") ===";
        viewer.sendSystemMessage(Component.literal(title));
        
        // Listar cada home con clickable
        for (Map.Entry<String, PlayerDataManager.HomeData> entry : homes.entrySet()) {
            String name = entry.getKey();
            PlayerDataManager.HomeData home = entry.getValue();
            
            String dimensionName = formatDimension(home.getDimension());
            String coords = String.format("X: %d, Y: %d, Z: %d", 
                home.getPosition().getX(), 
                home.getPosition().getY(), 
                home.getPosition().getZ());
            
            // Crear componente clickable
            Component homeName = Component.literal("§a• §f" + name)
                .setStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND, 
                        "/home " + name
                    ))
                    .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Component.literal("§aClick para teletransportarte a " + name)
                    ))
                    .withUnderlined(true)
                );
            
            Component info = Component.literal(" §7(" + dimensionName + ") §8- §7" + coords);
            
            viewer.sendSystemMessage(homeName.copy().append(info));
        }
        
        // Mensaje de ayuda
        if (isOwnHomes) {
            viewer.sendSystemMessage(Component.literal("§7Haz click en el nombre de un home para teletransportarte."));
        }
        
        return homes.size();
    }
    
    private static String formatDimension(String dimension) {
        if (dimension.contains("overworld")) return "Mundo";
        if (dimension.contains("the_nether")) return "Nether";
        if (dimension.contains("the_end")) return "End";
        return dimension;
    }
}