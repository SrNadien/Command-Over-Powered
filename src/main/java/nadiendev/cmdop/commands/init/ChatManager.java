package nadiendev.cmdop.commands.init;


public class ChatManager {
    
    private boolean afkEnabled = true;
    
    public ChatManager() {
        
    }
    
 
    public boolean isAfkEnabled() {
        return afkEnabled;
    }
    
   
    public void setAfkEnabled(boolean enabled) {
        this.afkEnabled = enabled;
    }
}