package nadiendev.cmdop.commands.init;

public class CommandProperties {
    private final String literal;
    private final int defaultRequiredLevel;
    
    private CommandProperties(String literal, int defaultRequiredLevel) {
        this.literal = literal;
        this.defaultRequiredLevel = defaultRequiredLevel;
    }
    
    public static CommandProperties create(String literal, int defaultRequiredLevel) {
        return new CommandProperties(literal, defaultRequiredLevel);
    }
    
    public String literal() {
        return literal;
    }
    
    public int defaultRequiredLevel() {
        return defaultRequiredLevel;
    }
    
    public String getName() {
        return literal;
    }
    
    public int getPermissionLevel() {
        return defaultRequiredLevel;
    }
}