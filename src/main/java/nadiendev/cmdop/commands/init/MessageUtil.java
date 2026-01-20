package nadiendev.cmdop.commands.init;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;


public class MessageUtil {
    
 
    public static Component success(String message) {
        return Component.literal(message)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)));
    }
    
   
    public static Component error(String message) {
        return Component.literal(message)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)));
    }
    
    
    public static Component warning(String message) {
        return Component.literal(message)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00)));
    }
    
  
    public static Component info(String message) {
        return Component.literal(message)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FFFF)));
    }
    
  
    public static Component component(String message) {
        return Component.literal(message);
    }
}