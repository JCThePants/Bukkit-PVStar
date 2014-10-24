package com.jcwhatever.bukkit.pvs.commands.admin.lobby;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="lobby",
        command="tooldamage",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} {command} tooldamage [on|off]",
        description="Allow or prevent player tool damage/durability in the selected arena lobby.")

public class ToolDamageSubCommand extends AbstractPVCommand {

    @Localizable static final String _TOOL_DAMAGE_ENABLED = "Arena '{0}' lobby Tool Damage is enabled.";
    @Localizable static final String _TOOL_DAMAGE_DISABLED = "Arena '{0}' lobby Tool Damage is {RED}disabled.";
    @Localizable static final String _TOOL_DAMAGE_CHANGE_ENABLED = "Arena '{0}' lobby Tool Damage changed to enabled.";
    @Localizable static final String _TOOL_DAMAGE_CHANGE_DISABLED = "Arena '{0}' lobby Tool Damage changed to {RED}disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = arena.getLobbyManager().getSettings().isToolsDamageable();

            if (isEnabled) {
                tell(sender, Lang.get(_TOOL_DAMAGE_ENABLED, arena.getName()));
            }
            else {
                tell(sender, Lang.get(_TOOL_DAMAGE_DISABLED, arena.getName()));
            }
        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            arena.getLobbyManager().getSettings().setToolsDamageable(isEnabled);

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_TOOL_DAMAGE_CHANGE_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_TOOL_DAMAGE_CHANGE_DISABLED, arena.getName()));
            }
        }
    }
}
