/*
 * This file is part of PV-Star for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.jcwhatever.bukkit.pvs.commands.admin.spectator;

import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.Lang;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="spectator",
        command="weapondamage",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} {command} weapondamage [on|off]",
        description="Allow or prevent spectator weapon damage/durability in the selected arena.")

public class WeaponDamageSubCommand extends AbstractPVCommand {

    @Localizable static final String _WEAPON_DAMAGE_ENABLED = "Arena '{0}' spectator Weapon Damage is enabled.";
    @Localizable static final String _WEAPON_DAMAGE_DISABLED = "Arena '{0}' spectator Weapon Damage is {RED}disabled.";
    @Localizable static final String _WEAPON_DAMAGE_CHANGE_ENABLED = "Arena '{0}' spectator Weapon Damage changed to enabled.";
    @Localizable static final String _WEAPON_DAMAGE_CHANGE_DISABLED = "Arena '{0}' spectator Weapon Damage changed to {RED}disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = arena.getSpectatorManager().getSettings().isWeaponsDamageable();

            if (isEnabled) {
                tell(sender, Lang.get(_WEAPON_DAMAGE_ENABLED, arena.getName()));
            }
            else {
                tell(sender, Lang.get(_WEAPON_DAMAGE_DISABLED, arena.getName()));
            }
        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            arena.getSpectatorManager().getSettings().setWeaponsDamageable(isEnabled);

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_WEAPON_DAMAGE_CHANGE_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_WEAPON_DAMAGE_CHANGE_DISABLED, arena.getName()));
            }
        }
    }
}
