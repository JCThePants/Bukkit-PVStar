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


package com.jcwhatever.pvs.commands;

import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.options.NameMatchMode;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand.ArenaReturned;
import com.jcwhatever.pvs.api.commands.ICommandHelper;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.exceptions.MissingExtensionAnnotation;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class PVCommandHelper implements ICommandHelper {

    @Localizable static final String _ARENA_NOT_SELECTED =
            "No arena selected. Use '/{plugin-command} arena select <arenaName>' first.";

    @Localizable static final String _WAIT_TILL_FINISHED =
            "Please wait until the arena is finished.";

    @Localizable static final String _ARENA_NOT_EXISTS =
            "Specified arena '{0: arena name}' doesn't exist. Type '/{plugin-command} list' for a list.";

    @Localizable static final String _MULTIPLE_ARENAS =
            "Multiple arenas found for '{0: arena search name}'. Please be more specific:";

    @Localizable static final String _EXTENSION_NOT_FOUND =
            "{0: extension name} is not installed in arena '{1: arena name}'.";

    /**
     * Get the command senders currently selected arena.
     *
     * @param sender    The command sender to check and display error messages to.
     * @param returned  Specify return conditions.
     */
    @Nullable
    @Override
    public IArena getSelectedArena(CommandSender sender, ArenaReturned returned) {
        IArena arena;

        if ((arena = PVStarAPI.getArenaManager().getSelectedArena(sender)) == null) {
            Msg.tellError(sender, Lang.get(_ARENA_NOT_SELECTED));
            return null;
        }

        if (returned == ArenaReturned.NOT_RUNNING && arena.getGame().isRunning()) {
            Msg.tellError(sender, Lang.get(_WAIT_TILL_FINISHED));
            return null;
        }

        return arena;
    }

    /**
     * Get an extension instance from an arena.
     *
     * @param sender  The command sender to display error messages to.
     * @param arena   The arena to get the extension instance from.
     * @param clazz   The extension type.
     * @param <T>     The extension type.
     */
    @Override
    @Nullable
    public <T extends ArenaExtension> T getExtension(CommandSender sender, IArena arena, Class<T> clazz) {

        T extension = arena.getExtensions().get(clazz);
        if (extension == null) {

            ArenaExtensionInfo info = clazz.getAnnotation(ArenaExtensionInfo.class);
            if (info == null)
                throw new MissingExtensionAnnotation(clazz);

            Msg.tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, info.name(), arena.getName()));
            return null; // finish
        }

        return extension;
    }

    /**
     * Get an arena by name.
     *
     * @param sender     The command sender to display error messages to.
     * @param arenaName  The name or partial name of the arena to find.
     */
    @Override
    @Nullable
    public IArena getArena(CommandSender sender, String arenaName) {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(arenaName);

        List<IArena> results = PVStarAPI.getArenaManager().getArena(arenaName, NameMatchMode.CASE_INSENSITIVE);

        if (results.size() == 0) {
            results = PVStarAPI.getArenaManager().getArena(arenaName, NameMatchMode.BEGINS_WITH);
        }

        if (results.size() == 0) {
            Msg.tellError(sender, Lang.get(_ARENA_NOT_EXISTS, arenaName));
        }
        else if (results.size() == 1) {
            return results.get(0);
        }
        else {
            Msg.tellError(sender, Lang.get(_MULTIPLE_ARENAS, "{YELLOW}" + arenaName));
            Msg.tell(sender, TextUtils.concat(results, ", "));
        }

        return null;
    }

    /**
     * Get an arena by exact name. (non-case sensitive)
     *
     * @param arenaName  The name of the arena.
     */
    @Override
    @Nullable
    public IArena getArena(String arenaName) {
        PreCon.notNullOrEmpty(arenaName);

        List<IArena> results = PVStarAPI.getArenaManager().getArena(arenaName, NameMatchMode.CASE_INSENSITIVE);

        if (results.size() == 1) {
            return results.get(0);
        }

        return null;
    }


    /**
     * Get a list of arena ids from a comma delimited string of arena names.
     *
     * @param sender      The command sender to display error messages to.
     * @param arenaNames  The names of the arenas.
     *
     * @return  Null if any arena in the list could not be found.
     */
    @Override
    @Nullable
    public List<UUID> getArenaIds(CommandSender sender, String arenaNames) {
        PreCon.notNullOrEmpty(arenaNames);

        String[] arenaNamesArray = TextUtils.PATTERN_COMMA.split(arenaNames);
        List<UUID> result = new ArrayList<>(arenaNamesArray.length);

        for (String arenaName : arenaNamesArray) {

            IArena arena = getArena(sender, arenaName);
            if (arena == null)
                return null;

            result.add(arena.getId());
        }
        return result;
    }
}
