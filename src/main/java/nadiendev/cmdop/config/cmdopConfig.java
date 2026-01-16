package nadiendev.cmdop.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class cmdopConfig {
    public static final ModConfigSpec SPEC;
    
    public static final ModConfigSpec.BooleanValue ENABLE_FLY;
    public static final ModConfigSpec.BooleanValue ENABLE_VANISH;
    public static final ModConfigSpec.BooleanValue ENABLE_HOME;
    public static final ModConfigSpec.IntValue HOME_COOLDOWN;
    public static final ModConfigSpec.IntValue MAX_HOMES;
    public static final ModConfigSpec.BooleanValue ENABLE_GAMEMODE;
    public static final ModConfigSpec.BooleanValue ENABLE_TPA;
    public static final ModConfigSpec.BooleanValue ENABLE_GOD;
    public static final ModConfigSpec.BooleanValue ENABLE_NICKNAME;
    public static final ModConfigSpec.BooleanValue ENABLE_ENDERCHEST;
    public static final ModConfigSpec.BooleanValue ENABLE_INVSEE;
    public static final ModConfigSpec.BooleanValue ENABLE_SKULL;
    public static final ModConfigSpec.BooleanValue ENABLE_MORE;
    public static final ModConfigSpec.IntValue MORE_MAX_STACK;
    public static final ModConfigSpec.BooleanValue ENABLE_TPS;
    public static final ModConfigSpec.BooleanValue ENABLE_TIME_WEATHER;
    public static final ModConfigSpec.BooleanValue ENABLE_AFK;
    public static final ModConfigSpec.BooleanValue ENABLE_WORKBENCH;
    public static final ModConfigSpec.BooleanValue ENABLE_SIZE;
    public static final ModConfigSpec.IntValue SIZE_MAX_SCALE;
    public static final ModConfigSpec.BooleanValue ENABLE_SPEED;
    public static final ModConfigSpec.BooleanValue ENABLE_HEAL;
    public static final ModConfigSpec.BooleanValue ENABLE_FEED;
    public static final ModConfigSpec.BooleanValue ENABLE_SUDO;
    public static final ModConfigSpec.ConfigValue<String> CHAT_FORMAT;
    public static final ModConfigSpec.ConfigValue<String> SAY_FORMAT;
    
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        
        builder.comment("Command toggles").push("commands");
        ENABLE_FLY = builder.define("enableFly", true);
        ENABLE_VANISH = builder.define("enableVanish", true);
        ENABLE_GAMEMODE = builder.define("enableGamemode", true);
        ENABLE_TPA = builder.define("enableTpa", true);
        ENABLE_GOD = builder.define("enableGod", true);
        ENABLE_NICKNAME = builder.define("enableNickname", true);
        ENABLE_ENDERCHEST = builder.define("enableEnderchest", true);
        ENABLE_INVSEE = builder.define("enableInvsee", true);
        ENABLE_SKULL = builder.define("enableSkull", true);
        ENABLE_MORE = builder.define("enableMore", true);
        ENABLE_TPS = builder.define("enableTps", true);
        ENABLE_TIME_WEATHER = builder.define("enableTimeWeather", true);
        ENABLE_AFK = builder.define("enableAfk", true);
        ENABLE_WORKBENCH = builder.define("enableWorkbench", true);
        ENABLE_SIZE = builder.define("enableSize", true);
        ENABLE_SPEED = builder.define("enableSpeed", true);
        ENABLE_HEAL = builder.define("enableHeal", true);
        ENABLE_FEED = builder.define("enableFeed", true);
        ENABLE_SUDO = builder.define("enableSudo", true);
        builder.pop();
        
        builder.comment("Home settings").push("home");
        ENABLE_HOME = builder.define("enableHome", true);
        HOME_COOLDOWN = builder.defineInRange("homeCooldown", 0, 0, 3600);
        MAX_HOMES = builder.defineInRange("maxHomes", 5, 1, 100);
        builder.pop();
        
        builder.comment("Size command settings").push("size");
        SIZE_MAX_SCALE = builder.comment("Maximum scale size (default: 4, max: 10)")
            .defineInRange("maxScale", 4, 1, 10);
        builder.pop();
        
        builder.comment("More command settings").push("more");
        MORE_MAX_STACK = builder.comment("Maximum stack size (default: 64, max: 100)")
            .defineInRange("maxStackSize", 64, 1, 100);
        builder.pop();
        
        builder.comment("Chat formatting").push("chat");
        CHAT_FORMAT = builder.define("chatFormat", "&6&l✎ &r{DISPLAYNAME}&7 ➤ &r&o {MESSAGE}");
        SAY_FORMAT = builder.define("sayFormat", "[Server]");
        builder.pop();
        
        SPEC = builder.build();
    }
}