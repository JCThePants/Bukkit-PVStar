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

import com.jcwhatever.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@CommandInfo(
        command="spectator",
        description="Manage spectator settings.")

public class SpectatorCommand extends AbstractPVCommand {

    public SpectatorCommand() {
        super();

        registerCommand(ArmorDamageSubCommand.class);
        registerCommand(FallSubCommand.class);
        registerCommand(HungerSubCommand.class);
        registerCommand(PvpSubCommand.class);
        registerCommand(SharingSubCommand.class);
        registerCommand(ToolDamageSubCommand.class);
        registerCommand(WeaponDamageSubCommand.class);
    }
}