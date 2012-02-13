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
    
    public static void init(FileConfiguration config)
    {
        enabled = config.getString("language.enabled", "Theft have been enabled on the server.");
        disabled = config.getString("language.disabled", "Theft have been disabled on the server.");
        
        youHaveBeenDiscovered = config.getString("language.you-have-been-discovered", "You have been discovered, and the victim stunned you with anger.");
        victimIsTooFar = config.getString("language.victim-is-too-far", "The victim is too far from you.");
        thiefSpotted = config.getString("language.thief-spotted", "You feel that something is moving inside your pocket.");
        cannotRobThisPlayer = config.getString("language.cannot-rob-player", "You cannot rob this player.");
        youCannotStealInNonPvPArea = config.getString("language.cannot-steal-in-non-pvp-area", "You cannot steal in non-PvP area.");
    }
    
}