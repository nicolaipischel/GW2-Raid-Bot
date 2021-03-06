package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Role setup step for the raid.
 * This one should take multiple inputs and as a result it doesn't finish until the user
 * types 'done'.
 * @author Christopher Bitler
 */
public class RunRoleSetupStep implements CreationStep {

    /**
     * Handle user input - should be in the format [number]:[role] unless it is 'done'.
     * @param e The direct message event
     * @return True if the user entered 'done', false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());

        if(e.getMessage().getRawContent().equalsIgnoreCase("done")) {
            if(raid.getRolesWithNumbers().size() > 0) {
                return true;
            } else {
                e.getChannel().sendMessage("Du musst mindestens eine Rolle hinzufügen!").queue();
                return false;
            }
        } else {
            String[] parts = e.getMessage().getRawContent().split(":");
            if(parts.length < 2) {
                e.getChannel().sendMessage("Du musst die Rolle im folgenden Format eingeben! [Anzahl]:[Rolle]").queue();
            } else {
                try {
                    int amnt = Integer.parseInt(parts[0]);
                    String roleName = parts[1].trim();
                    raid.getRolesWithNumbers().add(new RaidRole(amnt, roleName));
                    e.getChannel().sendMessage("Role added").queue();
                } catch (Exception ex) {
                    e.getChannel().sendMessage("Üngültige Eingabe! Bitte stelle sicher, dass die Eingabe im folgenden Format ist [number]:[role], Beispiel 5:DPS").queue();
                }
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the roles for raid run (format: [amount]:[Role name]). Type done to finish entering roles:";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return null;
    }
}
