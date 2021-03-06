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


package com.jcwhatever.pvs.commands.admin.arena;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="arena",
        command="mobspawn",
        staticParams={"on|off|info=info"},
        description="Set or view the settings for allowing natural mob spawns in the " +
                "selected arena.",

        paramDescriptions = {
                "on|off|info= 'on' to turn on, 'off' to turn off, 'info' or " +
                        "leave blank to see current setting."})

public class MobSpawnSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SPAWNING_ENABLED =
            "{WHITE}Natural mob spawning in arena '{0: arena name}' is {GREEN}Enabled{WHITE}.";

    @Localizable static final String _SPAWNING_DISABLED =
            "{WHITE}Natural mob spawning in arena '{0: arena name}' is {RED}Disabled{WHITE}.";

    @Localizable static final String _SPAWNING_CHANGED_ENABLED =
            "Natural mob spawning in arena '{0:arena name}' changed to Enabled.";

    @Localizable static final String _SPAWNING_CHANGED_DISABLED =
            "Natural mob spawning in arena '{0: arena name}' changed to {RED}Disabled.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = arena.getSettings().isMobSpawnEnabled();

            if (isEnabled) {
                tell(sender, Lang.get(_SPAWNING_ENABLED, arena.getName()));
            }
            else {
                tell(sender, Lang.get(_SPAWNING_DISABLED, arena.getName()));
            }
        }
        else {
            boolean isEnabled = args.getBoolean("on|off|info");

            arena.getSettings().setMobSpawnEnabled(isEnabled);

            if (isEnabled) {
                tellSuccess(sender, Lang.get(_SPAWNING_CHANGED_ENABLED, arena.getName()));
            }
            else {
                tellSuccess(sender, Lang.get(_SPAWNING_CHANGED_DISABLED, arena.getName()));
            }
        }
    }
}

