package nadiendev.cmdop.commands.init;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;


public class CommandSourceHelper {

    
    public static boolean isPlayer(CommandSourceStack source) {
        return source.getEntity() instanceof ServerPlayer;
    }

 
    public static boolean isConsole(CommandSourceStack source) {
        return !isPlayer(source);
    }

   
    public static ServerPlayer getPlayer(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

  
    public static UUID getPlayerUUID(CommandSourceStack source) {
        ServerPlayer player = getPlayer(source);
        return player != null ? player.getUUID() : null;
    }

  
    public static String getSenderName(CommandSourceStack source) {
        ServerPlayer player = getPlayer(source);
        return player != null ? player.getName().getString() : "Consola";
    }

    
    public static ServerPlayer requirePlayer(CommandSourceStack source) {
        return requirePlayer(source, "§cEste comando solo puede ser ejecutado por jugadores.");
    }

    
    public static ServerPlayer requirePlayer(CommandSourceStack source, String errorMessage) {
        ServerPlayer player = getPlayer(source);
        if (player == null) {
            source.sendFailure(MessageUtil.error(errorMessage));
        }
        return player;
    }
}