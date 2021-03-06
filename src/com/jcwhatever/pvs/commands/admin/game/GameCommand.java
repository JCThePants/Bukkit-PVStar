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


package com.jcwhatever.pvs.commands.admin.game;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.commands.admin.game.autostart.AutostartCommand;

@CommandInfo(
        command="game",
        description="Manage game settings.")

public class GameCommand extends AbstractPVCommand {

    public GameCommand() {
        super();

        registerCommand(ArmorDamageSubCommand.class);
        registerCommand(AutoHealSubCommand.class);
        registerCommand(EndDelaySubCommand.class);
        registerCommand(AutostartCommand.class);
        registerCommand(FallSubCommand.class);
        registerCommand(HungerSubCommand.class);
        registerCommand(LivesBehaviorSubCommand.class);
        registerCommand(LivesSubCommand.class);
        registerCommand(MaxDeathTicksSubCommand.class);
        registerCommand(PointsSubCommand.class);
        registerCommand(PointsBehaviorSubCommand.class);
        registerCommand(PvpSubCommand.class);
        registerCommand(ReserveSpawnsSubCommand.class);
        registerCommand(SharingSubCommand.class);
        registerCommand(TeamPvpSubCommand.class);
        registerCommand(TeleportModeSubCommand.class);
        registerCommand(ToolDamageSubCommand.class);
        registerCommand(WeaponDamageSubCommand.class);
    }
}
