package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand implements Command {
    private final String helpMessage = "GW2-Raid-Bot Hilfe:\n" +
            "Commands:\n" +
            "**!setRaidLeaderRole [role]** - Setzt die Raidleiter Rolle. Dies kann nur von Personen mit der 'Server verwalten' Berechtigung ausgeführt werden.\n" +
            "**!createRaid** - Startet den Raiderstellungsprozess. Kann nur von Raidleitern genutzt werden.\n" +
            "**!removeFromRaid [raid id] [name]** - Entfernt eine Person aus dem Raid. Kann nur von Raidleitern genutzt werden.\n" +
            "**!endRaid [raid id] [log link 1] [log link 2] ...** - Beendet einen Raid (Entfernt die Nachricht und sendet die logs an die Teilnehmer per privater Nachricht) (Das senden der Logs ist optional.)\n" +
            "**!help** - Du befindest dich bereits in der Hilfe\n" +
            "**!info** - Informationen zum Bot und seinem Autor\n" +
            "\n\n" +
            "Anleitung:\n" +
            "Um diesen Bot zu nutzen, setze die Raidleiter Rolle. Jeder mit dieser Rolle kann dann über !createRaid eine Raidankündigung erstellen. Dabei werden Sie durch" +
            " den Raiderstellungsprozess geführt bei dem der Bot Sie zu Details des Raids befragt. Anschließend wird der Bot eine Raidankündigung im festgelegten Channel posten" +
            " Sobald die Ankündigung gemacht wurde, können Personen sich für den Raid registrieren in dem sie auf das entsprechende Icon für die gewünschte Spezialisierung klicken und dann dem Bot antworten für welche Rolle Sie sich registieren wollen.";
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        channel.sendMessage(helpMessage).queue();
    }
}
