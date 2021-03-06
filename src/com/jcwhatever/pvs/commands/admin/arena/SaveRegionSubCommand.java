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
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.io.IOException;

@CommandInfo(
        parent="arena",
        command="saveregion",
        description="Save the selected arenas region to disk.")

public class SaveRegionSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SAVING =
            "The region for arena '{0: arena name}' is being saved...";

    @Localizable static final String _CANCELLED =
            "Save cancelled.";

    @Localizable static final String _FAILED =
            "There was an error that prevented the region from being saved.";

    @Localizable static final String _SUCCESS =
            "The region for arena '{0: arena name}' has been saved.";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNING);
        if (arena == null)
            return; // finish

        tell(sender, Lang.get(_SAVING, arena.getName()));

        IFuture future;

        try {
            future = arena.getRegion().saveData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(Lang.get(_FAILED));
        }

        future.onError(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                tellError(sender, Lang.get(_FAILED, message));
            }
        })
        .onCancel(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                tell(sender, Lang.get(_CANCELLED, message));
            }
        })
        .onSuccess(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                tellSuccess(sender, Lang.get(_SUCCESS, arena.getName()));
            }
        });
    }
}
