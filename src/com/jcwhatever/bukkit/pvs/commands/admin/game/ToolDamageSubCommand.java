package com.jcwhatever.bukkit.pvs.commands.admin.game;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="game",
        command="tooldamage",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} {command} tooldamage [on|off]",
        description="Allow or prevent player tool damage/durability in the selected arena game.")

public class ToolDamageSubCommand extends AbstractPVCommand {

    @Localizable static final String _TOOL_DAMAGE_ENABLED = "Arena '{0}' game tool damage is enabled.";
    @Localizable static final String _TOOL_DAMAGE_DISABLED = "Arena '{0}' game tool damage is {RED}disabled.";
    @Localizable static final String _TOOL_DAMAGE_CHANGE_ENABLED = "Arena '{0}' game tool damage changed to enabled.";
    @Localizable static final String _TOOL_DAMAGE_CHANGE_DISABLED = "Arena '{0}' game tool damage changed to {RED}disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = arena.getGameManager().getSettings().isToolsDamageable();

            if (isEnabled) {
                tell(sender, Lang.get(_TOOL_DAMAGE_ENABLED, arena.getName()));
            }
            else {
                tell(sender, Lang.get(_TOOL_DAMAGE_DISABLED, arena.getName()));
            }
        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            arena.getGameManager().getSettings().setToolsDamageable(isEnabled);

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_TOOL_DAMAGE_CHANGE_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_TOOL_DAMAGE_CHANGE_DISABLED, arena.getName()));
            }
        }
    }
}

