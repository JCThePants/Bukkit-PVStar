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


package com.jcwhatever.bukkit.pvs.modules;

import com.jcwhatever.generic.utils.DependencyRunner.IDependantRunnable;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for an unloaded module with utilities
 * to check if dependencies are loaded.
 */
public class UnloadedModuleContainer implements IDependantRunnable {

    private final PVStarModule _module;
    private final PVModuleInfo _moduleInfo;
    private final ModuleLoader _loader;

    /*
     * Constructor.
     */
    public UnloadedModuleContainer(ModuleLoader loader, PVStarModule module, PVModuleInfo moduleInfo) {
        PreCon.notNull(loader);
        PreCon.notNull(module);
        PreCon.notNull(moduleInfo);

        _loader = loader;
        _module = module;
        _moduleInfo = moduleInfo;
    }

    /*
     * Get the name of the module.
     */
    public String getName() {
        return _moduleInfo.getName();
    }

    /*
     * Get the name of the module in lowercase.
     */
    public String getSearchName() {
        return _moduleInfo.getSearchName();
    }

    /*
     * Get the module.
     */
    public PVStarModule getModule() {
        return _module;
    }

    /*
     * Get the names of required Bukkit dependencies that are not
     * loaded yet.
     */
    public List<String> getMissingBukkitDepends() {

        List<String> result = new ArrayList<>(_moduleInfo.getBukkitDepends().size());

        for (String depend : _moduleInfo.getBukkitDepends()) {

            Plugin plugin = Bukkit.getPluginManager().getPlugin(depend);
            if (plugin == null || !plugin.isEnabled()) {
                result.add(depend);
            }
        }

        return result;
    }

    /*
     * Get the names of required PV-Star module dependencies that
     * are not loaded yet.
     */
    public List<String> getMissingModuleDepends() {

        List<String> result = new ArrayList<>(_moduleInfo.getModuleDepends().size());

        for (String depend : _moduleInfo.getModuleDepends()) {

            PVStarModule module = _loader.getModule(depend);
            if (module == null || !module.isEnabled()) {
                result.add(depend);
            }
        }

        return result;
    }

    /*
     * Determine if Bukkit dependencies are loaded.
     */
    public boolean isBukkitDependsLoaded() {
        for (String depend : _moduleInfo.getBukkitDepends()) {

            Plugin plugin = Bukkit.getPluginManager().getPlugin(depend);
            if (plugin == null || !plugin.isEnabled())
                return false;
        }

        for (String depend : _moduleInfo.getBukkitSoftDepends()) {

            Plugin plugin = Bukkit.getPluginManager().getPlugin(depend);
            if (plugin != null && !plugin.isEnabled())
                return false;
        }

        return true;
    }

    /*
     * Determine if PV-Star module dependencies are loaded.
     */
    public boolean isModuleDependsLoaded() {
        for (String depend : _moduleInfo.getModuleDepends()) {

            PVStarModule module = _loader.getModule(depend);
            if (module == null || !module.isEnabled())
                return false;
        }

        for (String depend : _moduleInfo.getModuleSoftDepends()) {

            PVStarModule module = _loader.getModule(depend);
            if (module != null && !module.isEnabled())
                return false;
        }

        return true;
    }

    @Override
    public boolean isDependencyReady() {
        return isBukkitDependsLoaded() && isModuleDependsLoaded();
    }

    @Override
    public void run() {
        _module.preEnable();
    }
}
