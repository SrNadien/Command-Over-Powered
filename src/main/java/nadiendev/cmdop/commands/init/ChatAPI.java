package nadiendev.cmdop.commands.init;

import net.minecraft.server.level.ServerPlayer;


public class ChatAPI {
    
    private static ChatManager chatManager;
    
   
    public static void setChatManager(ChatManager manager) {
        chatManager = manager;
    }
    
   
    public static ChatManager getChatManager() {
        if (chatManager == null) {
            chatManager = new ChatManager();
        }
        return chatManager;
    }
    
    
    public static boolean isMutedOrIgnored(ServerPlayer sender, ServerPlayer target) {
      
        return false;
    }
    
   
    public static void broadcastSocialSpy(ServerPlayer sender, ServerPlayer target, String message) {
     
    }
}