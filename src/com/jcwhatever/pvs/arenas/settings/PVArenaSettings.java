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


package com.jcwhatever.pvs.arenas.settings;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.options.DropsCleanup;
import com.jcwhatever.pvs.api.arena.settings.IArenaSettings;
import com.jcwhatever.pvs.api.events.ArenaDisabledEvent;
import com.jcwhatever.pvs.api.events.ArenaEnabledEvent;
import com.jcwhatever.pvs.api.events.ArenaPreEnableEvent;
import com.jcwhatever.pvs.arenas.AbstractArena;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Arena settings implementation.
 */
public class PVArenaSettings implements IArenaSettings {

    private static final boolean DEFAULT_ENABLED_STATE = true;

    private final AbstractArena _arena;
    private final IDataNode _dataNode;

    private int _minPlayers = 2;
    private int _maxPlayers = 10;
    private boolean _isVisible = true;
    private boolean _isEnabled;
    private boolean _isMobSpawnEnabled = false;
    private Location _removeLocation;
    private DropsCleanup _dropsCleanup = DropsCleanup.OFF;
    private String _typeDisplayName;


    /*
     * Constructor.
     */
    public PVArenaSettings(AbstractArena arena) {
        PreCon.notNull(arena);

        _arena = arena;
        _dataNode = arena.getDataNode("settings.arena");

        _isEnabled = _dataNode.getBoolean("enabled", DEFAULT_ENABLED_STATE);
        _minPlayers = _dataNode.getInteger("min-players", _minPlayers);
        _maxPlayers = _dataNode.getInteger("max-players", _maxPlayers);
        _isVisible = _dataNode.getBoolean("visible", _isVisible);
        _isMobSpawnEnabled = _dataNode.getBoolean("mob-spawn", _isMobSpawnEnabled);
        _removeLocation = _dataNode.getLocation("remove-location", _removeLocation);
        _dropsCleanup = _dataNode.getEnum("drops-cleanup", _dropsCleanup, DropsCleanup.class);
        _typeDisplayName = _dataNode.getString("type-display");
    }

    @Override
    public boolean isEnabled() {
        return _isEnabled && _arena.getRegion().isDefined() &&
                _arena.getRegion().isWorldLoaded();
    }

    @Override
    public boolean isConfigEnabled() {
        return _dataNode.getBoolean("enabled", DEFAULT_ENABLED_STATE);
    }

    @Override
    public void setEnabled(boolean isEnabled) {

        if (_isEnabled == isEnabled)
            return;

        setTransientEnabled(isEnabled);

        save("enabled", _isEnabled);
    }

    @Override
    public void setTransientEnabled(boolean isEnabled) {

        // Note: The setting is not checked to see if it is the same
        // because the method may be called simply to run the events.
        // (i.e. The arenas world is loaded or unloaded and events need)

        // prevent enabling if world is not loaded
        if (_arena.getRegion().isDefined() &&
                !_arena.getRegion().isWorldLoaded()) {

            if (isEnabled)
                return;
        }

        _isEnabled = isEnabled;
        if (isEnabled) {

            if (_arena.getEventManager().call(this, new ArenaPreEnableEvent(_arena)).isCancelled())
                return;

            _isEnabled = true;

            _arena.getExtensions().enable();
            _arena.getEventManager().call(this, new ArenaEnabledEvent(_arena));
        }
        else {
            _isEnabled = false;

            _arena.getExtensions().disable();
            _arena.getEventManager().call(this, new ArenaDisabledEvent(_arena));
        }
    }

    @Override
    public final boolean isVisible() {
        return _isVisible;
    }

    @Override
    public final void setVisible(boolean isVisible) {
        save("visible", _isVisible = isVisible);
    }

    @Override
    public final String getTypeDisplayName() {
        return _typeDisplayName != null
                ? _typeDisplayName
                : "Arena";
    }

    @Override
    public final void setTypeDisplayName(@Nullable String typeDisplayName) {
        save("type-display", _typeDisplayName = typeDisplayName);
    }

    @Override
    public int getMinPlayers() {
        return _minPlayers;
    }

    @Override
    public void setMinPlayers(int minPlayers) {
        PreCon.positiveNumber(minPlayers);

        save("min-players", _minPlayers = minPlayers);
    }

    @Override
    public int getMaxPlayers() {
        return _maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        PreCon.greaterThanZero(maxPlayers);

        save("max-players", _maxPlayers = maxPlayers);
    }

    @Override
    public boolean isMobSpawnEnabled() {
        return _isMobSpawnEnabled;
    }

    @Override
    public void setMobSpawnEnabled(boolean isEnabled) {
        save("mob-spawn", _isMobSpawnEnabled = isEnabled);
    }

    @Override
    @Nullable
    public Location getRemoveLocation() {

        if (_removeLocation != null)
            return _removeLocation;

        if (_arena.getRegion().getWorld() != null)
            return _arena.getRegion().getWorld().getSpawnLocation();

        return null;
    }

    @Override
    public void setRemoveLocation(@Nullable Location location) {
        save("remove-location", _removeLocation = location);
    }

    @Override
    public DropsCleanup getDropsCleanup() {
        return _dropsCleanup;
    }

    @Override
    public void setDropsCleanup(DropsCleanup cleanup) {
        PreCon.notNull(cleanup);

        save("drops-cleanup", _dropsCleanup = cleanup);
    }

    /**
     * Save a setting.
     */
    protected void save(String nodeName, @Nullable Object value) {
        _dataNode.set(nodeName, value);
        _dataNode.save();
    }
}
