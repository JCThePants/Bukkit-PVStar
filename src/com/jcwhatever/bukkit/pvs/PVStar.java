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


package com.jcwhatever.bukkit.pvs;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.commands.CommandDispatcher;
import com.jcwhatever.nucleus.events.manager.NucleusEventManager;
import com.jcwhatever.nucleus.kits.KitManager;
import com.jcwhatever.nucleus.permissions.Permissions;
import com.jcwhatever.nucleus.signs.SignManager;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.IPVStar;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ExtensionTypeManager;
import com.jcwhatever.bukkit.pvs.api.arena.managers.ArenaManager;
import com.jcwhatever.bukkit.pvs.api.commands.CommandHelper;
import com.jcwhatever.bukkit.pvs.api.events.PVStarLoadedEvent;
import com.jcwhatever.bukkit.pvs.api.modules.ModuleInfo;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.points.PointsManager;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnTypeManager;
import com.jcwhatever.bukkit.pvs.api.stats.StatsManager;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.arenas.PVArena;
import com.jcwhatever.bukkit.pvs.commands.PVCommandDispatcher;
import com.jcwhatever.bukkit.pvs.commands.PVCommandHelper;
import com.jcwhatever.bukkit.pvs.listeners.BukkitEventForwarder;
import com.jcwhatever.bukkit.pvs.listeners.MobEventListener;
import com.jcwhatever.bukkit.pvs.listeners.PlayerEventListener;
import com.jcwhatever.bukkit.pvs.listeners.PvpListener;
import com.jcwhatever.bukkit.pvs.listeners.SharingListener;
import com.jcwhatever.bukkit.pvs.listeners.WorldEventListener;
import com.jcwhatever.bukkit.pvs.modules.ModuleLoader;
import com.jcwhatever.bukkit.pvs.points.PVPointsManager;
import com.jcwhatever.bukkit.pvs.scripting.PVStarScriptApi;
import com.jcwhatever.bukkit.pvs.signs.PVSignManager;
import com.jcwhatever.bukkit.pvs.stats.PVStatsManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * PV-Star plugin implementation.
 */
public class PVStar extends NucleusPlugin implements IPVStar {

    private ModuleLoader _moduleLoader;
    private PVArenaManager _arenaManager;
    private PVCommandDispatcher _commandHandler;
    private NucleusEventManager _eventManager;
    private StatsManager _statsManager;
    private PointsManager _pointsManager;
    private SignManager _signManager;
    private PVExtensionTypeManager _extensionManager;
    private PVSpawnTypeManager _spawnTypeManager;
    private PVCommandHelper _commandHelper;
    private BukkitEventForwarder _eventForwarder;
    private boolean _isLoaded;

    @Override
    public boolean isLoaded() {
        return _isLoaded;
    }

    @Override
    public String getChatPrefix() {
        return ChatColor.AQUA + "[PV*] " + ChatColor.WHITE;
    }

    @Override
    public String getConsolePrefix() {
        return "[PV*] ";
    }

    @Override
    public ArenaPlayer getArenaPlayer(Object player) {
        PreCon.notNull(player);

        if (player instanceof ArenaPlayer)
            return (ArenaPlayer) player;

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return PVArenaPlayer.get(p);
    }

    @Override
    public ArenaManager getArenaManager() {
        return _arenaManager;
    }

    @Override
    public SpawnTypeManager getSpawnTypeManager() {
        return _spawnTypeManager;
    }

    @Override
    public StatsManager getStatsManager() {
        return _statsManager;
    }

    @Override
    public ExtensionTypeManager getExtensionManager() {
        return _extensionManager;
    }

    @Override
    public PointsManager getPointsManager() {
        return _pointsManager;
    }

    @Override
    public KitManager getKitManager() {
        return Nucleus.getKitManager();
    }

    @Override
    public SignManager getSignManager() {
        return _signManager;
    }

    @Override
    public CommandDispatcher getCommandHandler() {
        return _commandHandler;
    }

    @Override
    public CommandHelper getCommandHelper() {
        return _commandHelper;
    }

    @Override
    public NucleusEventManager getEventManager() {
        return _eventManager;
    }

    @Override
    @Nullable
    public PVStarModule getModule(String name) {
        PreCon.notNullOrEmpty(name);

        return _moduleLoader.getModule(name);
    }

    @Override
    public List<PVStarModule> getModules() {
        return _moduleLoader.getModules();
    }

    @Override
    @Nullable
    public ModuleInfo getModuleInfo(PVStarModule module) {
        return _moduleLoader.getModuleInfo(module);
    }

    @Override
    protected void onEnablePlugin() {
        PVStarAPI.setImplementation(this);

        _signManager = new PVSignManager(this, getDataNode().getNode("signs"));
        _pointsManager = new PVPointsManager();
        _statsManager = new PVStatsManager();
        _eventManager = new NucleusEventManager();
        _extensionManager = new PVExtensionTypeManager();
        _spawnTypeManager = new PVSpawnTypeManager();
        _commandHelper = new PVCommandHelper();

        // enable command
        _commandHandler = new PVCommandDispatcher(this);
        registerCommands(_commandHandler);

        Msg.info("Loading modules...");

        // load modules
        _moduleLoader = new ModuleLoader(this);
        _moduleLoader.loadModules();

        // enable loaded modules
        _moduleLoader.enable(new Runnable() {

            // called after modules are finished loading
            @Override
            public void run() {

                // load arenas
                _arenaManager = new PVArenaManager(getDataNode().getNode("arenas"));

                // register built in arenas
                _arenaManager.registerType(PVArena.class);

                // load arenas, permissions batch for performance
                Permissions.runBatchOperation(true, new Runnable() {

                    @Override
                    public void run() {
                        _arenaManager.loadArenas();
                    }
                });

                // register event listeners
                registerEventListeners(
                        new MobEventListener(),
                        new PlayerEventListener(),
                        new PvpListener(),
                        new SharingListener(),
                        new WorldEventListener());

                // forward global Bukkit events to the appropriate
                // arena event manager.
                _eventForwarder = new BukkitEventForwarder(Nucleus.getEventManager());

                // register script api
                Nucleus.getScriptApiRepo().registerApiType(PVStarAPI.getPlugin(), PVStarScriptApi.class);

                Msg.info("Modules loaded.");
                _isLoaded = true;

                _eventManager.call(new PVStarLoadedEvent());
            }
        });
    }

    @Override
    protected void onDisablePlugin() {

        _isLoaded = false;

        // end arenas
        List<Arena> arenas = _arenaManager.getArenas();
        for (Arena arena : arenas) {
            arena.getGameManager().end();
        }

        Collection<PVStarModule> modules = _moduleLoader.getModules();

        for (PVStarModule module : modules) {
            module.dispose();
        }

        if (_eventForwarder != null) {
            _eventForwarder.dispose();
            _eventForwarder = null;
        }
    }
}
