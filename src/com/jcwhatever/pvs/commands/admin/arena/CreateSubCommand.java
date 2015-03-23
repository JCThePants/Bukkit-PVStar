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

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="arena",
        command={"create"},
        staticParams={ "arenaName" },
        description="Create a new arena. Type is for display purposes and is optional.",

        paramDescriptions = {
                "arenaName= The name of the arena. {NAME16}"})

public class CreateSubCommand extends AbstractPVCommand {

    @Localizable static final String _ARENA_ALREADY_EXISTS =
            "An arena with the name '{0: arena name}' already exists.";

    @Localizable static final String _FAILED =
            "Failed to create arena.";

    @Localizable static final String _SUCCESS =
            "A new arena with the name '{0: arena name}' of type '{1: arena type}' has been created.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        String name = args.getName("arenaName");
        String type = "Arena";

        Arena arena = getArena(name);

        if (arena != null) {
            tellError(sender, Lang.get(_ARENA_ALREADY_EXISTS, name));
            return; // finish
        }

        arena = PVStarAPI.getArenaManager().addArena(name, type);
        if (arena == null) {
            tellError(sender, Lang.get(_FAILED));
        }
        else {
            PVStarAPI.getArenaManager().setSelectedArena(sender, arena);
            tellSuccess(sender, Lang.get(_SUCCESS, name, type));
        }
    }
}


