package com.jcwhatever.bukkit.pvs.commands.admin.spectator;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="spectator",
        command="hunger",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} {command} hunger [on|off]",
        description="Allow or prevent player hunger in the selected arena spectator area.")

public class HungerSubCommand extends AbstractPVCommand {

    @Localizable static final String _HUNGER_ENABLED = "Arena '{0}' spectator Hunger is enabled.";
    @Localizable static final String _HUNGER_DISABLED = "Arena '{0}' spectator Hunger is {RED}disabled.";
    @Localizable static final String _HUNGER_CHANGE_ENABLED = "Arena '{0}' spectator Hunger changed to enabled.";
    @Localizable static final String _HUNGER_CHANGE_DISABLED = "Arena '{0}' spectator Hunger changed to {RED}disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = arena.getSpectatorManager().getSettings().isHungerEnabled();

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_HUNGER_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_HUNGER_DISABLED, arena.getName()));
            }
        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            arena.getSpectatorManager().getSettings().setHungerEnabled(isEnabled);

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_HUNGER_CHANGE_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_HUNGER_CHANGE_DISABLED, arena.getName()));
            }
        }
    }
}

