package net.minekingdom.snaipe.Thieves;

import org.bukkit.configuration.file.FileConfiguration;

public class Language {

    public static String cannotRobThisPlayer;
    public static String disabled;
    public static String enabled;
    public static String thiefSpotted;
    public static String victimIsTooFar;
    public static String youHaveBeenDiscovered;
    public static String youCannotStealInNonPvPArea;
	public static String cooldownNotFinished;
	public static String levelUp;
	public static String cannotStealMore;
    
    public static void init(FileConfiguration config)
    {
        enabled = config.getString("language.enabled", "Theft have been enabled on the server.");
        disabled = config.getString("language.disabled", "Theft have been disabled on the server.");
        
        youHaveBeenDiscovered = config.getString("language.you-have-been-discovered", "You have been discovered, and the victim stunned you with anger.");
        victimIsTooFar = config.getString("language.victim-is-too-far", "The victim is too far from you.");
        thiefSpotted = config.getString("language.thief-spotted", "You feel that something is moving inside your pocket.");
        cannotRobThisPlayer = config.getString("language.cannot-rob-player", "You cannot rob this player.");
        youCannotStealInNonPvPArea = config.getString("language.cannot-steal-in-non-pvp-area", "You cannot steal in non-PvP area.");
        cooldownNotFinished = config.getString("language.cooldown-not-finished", "Your cooldown is not finished.");
        levelUp = config.getString("language.level-up", "You just leveled up !");
        cannotStealMore = config.getString("language.cannot-steal-more", "You have exceeded the maximum number of items you can steal for your level !");
    }
    
}