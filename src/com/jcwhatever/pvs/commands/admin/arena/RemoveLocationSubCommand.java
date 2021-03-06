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
import com.jcwhatever.nucleus.managed.commands.arguments.ILocationHandler;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="arena",
        command="removelocation",
        staticParams={"current|select|info=info"},
        description="Set or view the location that players are teleported to when they " +
                "are removed from the selected arena.",

        paramDescriptions = {
                "current|select|info= Leave blank to see current setting. {LOCATION}"})

public class RemoveLocationSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _VIEW_LOCATION =
            "Remove location for arena '{0: arena name}' is:";

    @Localizable static final String _SET_LOCATION =
            "Remove location for arena '{0: arena name}' set to:\n{WHITE}{1: location}";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final IArena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "current|select|info"));
        if (arena == null)
            return; // finish

        if (args.getString("current|select|info").equals("info")) {

            tell(sender, Lang.get(_VIEW_LOCATION, arena.getName()));
            tell(sender, LocationUtils.serialize(arena.getSettings().getRemoveLocation()));
        }
        else {

            args.getLocation(sender, "current|select|info", new ILocationHandler() {

                @Override
                public void onLocationRetrieved(Player player, Location result) {

                    arena.getSettings().setRemoveLocation(result);

                    tellSuccess(sender, Lang.get(_SET_LOCATION,
                            arena.getName(),
                            TextUtils.formatLocation(arena.getSettings().getRemoveLocation(), true)));
                }
            });
        }
    }
}
