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


package com.jcwhatever.bukkit.pvs.commands.admin.spawns;


import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.Lang;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="spawns",
        command="list",
        staticParams={"page=1"},
        floatingParams={"type=$all"},
        usage="/{plugin-command} {command} list [page] [--type]",
        description="Shows a list of spawns for the selected arena.")

public class ListSubCommand extends AbstractPVCommand {

    @Localizable static final String _PAGINATOR_TITLE = "{0} Spawnpoints";
    @Localizable static final String _LABEL_TYPE = "Type";
    @Localizable static final String _LABEL_TEAM = "Team";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        int page = args.getInt("page");

        String typeName = args.getString("type");

        SpawnType type = PVStarAPI.getSpawnTypeManager().getType(typeName);

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, arena.getName()));

        List<SpawnType> spawnTypes = PVStarAPI.getSpawnTypeManager().getSpawnTypes();
        for (SpawnType spawnType : spawnTypes) {

            if (type == null || type == spawnType) {

                List<Spawnpoint> spawns = arena.getSpawnManager().getSpawns(spawnType);
                if (spawns == null)
                    continue;

                showSpawns(spawnType.getName(), pagin, spawns);
            }

        }

        pagin.show(sender, page, FormatTemplate.RAW);
    }

    private void showSpawns(String subHeader, ChatPaginator pagin, List<Spawnpoint> spawns) {
        pagin.addFormatted(FormatTemplate.SUB_HEADER, subHeader);

        String typeLabel = Lang.get(_LABEL_TYPE);
        String teamLabel = Lang.get(_LABEL_TEAM);

        for (Spawnpoint spawn : spawns) {

            String formatted = TextUtils.format("{YELLOW}{0}{GRAY} - {1}:{2}, {3}:{4}", spawn.getName(), typeLabel, spawn.getSpawnType().getName(), teamLabel, spawn.getTeam().name());

            pagin.addFormatted(FormatTemplate.RAW, formatted);
        }
    }

}
