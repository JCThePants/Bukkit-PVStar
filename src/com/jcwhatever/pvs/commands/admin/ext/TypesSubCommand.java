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


package com.jcwhatever.pvs.commands.admin.ext;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.pvs.Lang;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="ext",
        command="types",
        staticParams = { "page=1" },
        floatingParams = { "search=" },
        description="List available arena extensions.",

        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional. Specify a search filter."
        })

public class TypesSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable
    static final String _PAGINATOR_TITLE =
            "Available Arena Extensions";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ChatPaginator pagin = createPagin(args, 7, Lang.get(_PAGINATOR_TITLE));

        List<Class<? extends ArenaExtension>> classes = PVStarAPI.getExtensionManager().getExtensionClasses();
        for (Class<? extends ArenaExtension> extClass : classes) {
            ArenaExtensionInfo info = extClass.getAnnotation(ArenaExtensionInfo.class);
            if (info == null)
                continue;

            pagin.add(info.name(), info.description());
        }

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm("search");

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
