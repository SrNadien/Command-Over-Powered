package nadiendev.cmdop.event;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class ChatFormatHandler {
    
    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String message = event.getMessage().getString();
        
        String format = cmdopConfig.CHAT_FORMAT.get();
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        String displayName = data.getNickname() != null ? data.getNickname() : player.getName().getString();
        
        String formatted = format
            .replace("{MESSAGE}", message)
            .replace("{USERNAME}", player.getName().getString())
            .replace("{DISPLAYNAME}", displayName)
            .replace("{NICKNAME}", data.getNickname() != null ? data.getNickname() : player.getName().getString())
            .replace("{PREFIX}", "")
            .replace("{SUFFIX}", "")
            .replace("{GROUP}", "")
            .replace("{WORLD}", player.level().dimension().location().getPath())
            .replace("{WORLDNAME}", player.level().dimension().location().toString())
            .replace("{SHORTWORLDNAME}", String.valueOf(player.level().dimension().location().getPath().charAt(0)))
            .replace("&", "§");
        
        event.setCanceled(true);
        
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            PlayerDataManager.PlayerData pData = PlayerDataManager.getData(p.getUUID());
            if (!pData.isVanished() || p.hasPermissions(2)) {
                p.sendSystemMessage(Component.literal(formatted));
            }
        });
    }
}