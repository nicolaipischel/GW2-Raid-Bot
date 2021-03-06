package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] specs = {
            "dragonhunter", //387295988282556417
            "firebrand", //387296167958151169
            "herald", //387296053659172869
            "renegade", //387296192381321226
            "berserker", //387296013947502592
            "spellbreaker", //387296212421836800
            "scrapper", //387296081823662081
            "holosmith", //387296176770121738
            "druid", // 387296044716916738
            "soulbeast", //387296205488521216
            "daredevil", //387296029533274113
            "deadeye", //387296159716081664
            "weaver", //387296219988361218
            "tempest", //387296089340117002
            "chronomancer", //387296021710897152
            "mirage", //387296184114610185
            "reaper", //387296061997318146
            "scourge" //387296198928891905
    };

    static Emote[] reactions = {
            getEmoji("612749569096417300"), // Dragonhunter
            getEmoji("612749569951924224"), // Firebrand
            getEmoji("612749569700397056"), // Herald
            getEmoji("612749569242955777"), // Renegade
            getEmoji("612749569599733800"), // Berserker
            getEmoji("612749569054212102"), // Spellbreaker
            getEmoji("612749569465385127"), // Scrapper
            getEmoji("612749569436024891"), // Holosmith
            getEmoji("612749569448607805"), // Druid
            getEmoji("612749569893203968"), // Soulbeast
            getEmoji("612749569717043200"), // Daredevil
            getEmoji("612749570052456498"), // Deadeye
            getEmoji("612749567317901343"), // Weaver
            getEmoji("612749567116705832"), // Tempest
            getEmoji("612749569566179359"), // Chronomancer
            getEmoji("612749569456996573"), // Mirage
            getEmoji("612749569310064674"), // Reaper
            getEmoji("612749570627076126"), // Scourge
            getEmoji("616769294901968946") // X_
    };

    /**
     * Get an emoji from it's emote ID via JDA
     * @param id The ID of the emoji
     * @return The emote object representing that emoji
     */
    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    /**
     * Get the list of reaction names as a list
     * @return The list of reactions as a list
     */
    public static List<String> getSpecs() {
        return new ArrayList<>(Arrays.asList(specs));
    }

    /**
     * Get the list of emote objects
     * @return The emotes
     */
    public static List<Emote> getEmotes() {
        return new ArrayList<>(Arrays.asList(reactions));
    }


}
