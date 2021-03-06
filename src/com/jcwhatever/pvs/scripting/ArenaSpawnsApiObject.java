/*
 * This file is part of PV-Star for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.pvs.scripting;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.ArenaTeam;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.spawns.SpawnType;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Arena spawns sub API.
 */
public class ArenaSpawnsApiObject implements IDisposable {

    private final IArena _arena;

    /**
     * Constructor.
     */
    ArenaSpawnsApiObject(IArena arena) {
        _arena = arena;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    /**
     * Reset the api and release resources.
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /**
     * Determine if there are lobby spawns available.
     */
    public boolean hasLobbySpawns() {
        return _arena.getSpawns().hasLobbySpawns();
    }

    /**
     * Determine if there are game spawns available.
     */
    public boolean hasGameSpawns() {
        return _arena.getSpawns().hasGameSpawns();
    }

    /**
     * Determine if there are spectator spawns available.
     */
    public boolean hasSpectatorSpawns() {
        return _arena.getSpawns().hasSpectatorSpawns();
    }

    /**
     * Get all game spawn points.
     */
    public List<Spawnpoint> getGameSpawns() {
        return _arena.getSpawns().getAll(ArenaContext.GAME);
    }

    /**
     * Get all lobby spawn points.
     */
    public List<Spawnpoint> getLobbySpawns() {
        return _arena.getSpawns().getAll(ArenaContext.LOBBY);
    }

    /**
     * Get all spectator spawn points.
     */
    public List<Spawnpoint> getSpectatorSpawns() {
        return _arena.getSpawns().getAll(ArenaContext.SPECTATOR);
    }

    /**
     * Get a random spawn for a player. The spawn returned correlates
     * to the players current arena relation. (i.e player in lobby gets a lobby spawn)
     *
     * @param player  The player to get a random spawn for.
     *
     * @return  Null if the player is not in an arena.
     */
    @Nullable
    public Spawnpoint getRandomSpawn(IArenaPlayer player) {
        PreCon.notNull(player);

        return Rand.get(_arena.getSpawns().getAll(player.getContext()));
    }

    /**
     * Get a random lobby spawn.
     *
     * @param teamName  The name of the team the spawn is for.
     */
    @Nullable
    public Spawnpoint getRandomLobbySpawn(String teamName) {
        PreCon.notNullOrEmpty(teamName);

        ArenaTeam team = getEnum(teamName, ArenaTeam.class);

        return Rand.get(_arena.getSpawns().getAll(team, ArenaContext.LOBBY));
    }

    /**
     * Get a random game spawn.
     *
     * @param teamName  The team the spawn is for.
     */
    @Nullable
    public Spawnpoint getRandomGameSpawn(String teamName) {
        PreCon.notNullOrEmpty(teamName);

        ArenaTeam team = getEnum(teamName, ArenaTeam.class);

        return Rand.get(_arena.getSpawns().getAll(team, ArenaContext.GAME));
    }

    /**
     * Get a random spectator spawn.
     *
     * @param teamName  The team the spawn is for.
     */
    @Nullable
    public Spawnpoint getRandomSpectatorSpawn(String teamName) {
        PreCon.notNullOrEmpty(teamName);

        ArenaTeam team = getEnum(teamName, ArenaTeam.class);

        return Rand.get(_arena.getSpawns().getAll(team, ArenaContext.SPECTATOR));
    }

    /**
     * Get a spawn by it's name.
     *
     * @param name  The name of the spawn.
     */
    @Nullable
    public Spawnpoint getSpawn(String name) {
        PreCon.notNullOrEmpty(name);

        return _arena.getSpawns().get(name);
    }

    /**
     * Get all spawnpoints
     */
    public List<Spawnpoint> getSpawns() {
        return _arena.getSpawns().getAll();
    }

    /**
     * Get all spawnpoints from a comma delimited string of spawn names.
     *
     * @param spawnNames  The names of the spawns to retrieve.
     */
    public List<Spawnpoint> getSpawnsByNames(String spawnNames) {
        PreCon.notNullOrEmpty(spawnNames);

        return _arena.getSpawns().getAll(spawnNames);
    }

    /**
     * Get all spawns of the specified type.
     *
     * @param typeName  The spawn type.
     */
    public List<Spawnpoint> getSpawnsByType(String typeName) {
        PreCon.notNullOrEmpty(typeName);

        SpawnType type = PVStarAPI.getSpawnTypeManager().getType(typeName);
        if (type == null)
            return new ArrayList<>(0);

        return _arena.getSpawns().getAll(type);
    }

    /**
     * Get all spawns for the specified team.
     *
     * @param teamName  The arena team.
     */
    public List<Spawnpoint> getSpawnsByTeam(String teamName) {
        PreCon.notNullOrEmpty(teamName);

        ArenaTeam team = getEnum(teamName, ArenaTeam.class);

        return _arena.getSpawns().getAll(team);
    }

    /**
     * Get all spawns for the specified team and type.
     *
     * @param typeName  The spawn type.
     * @param teamName  The arena team.
     */
    public List<Spawnpoint> getSpawnsByTypeAndTeam(String typeName, String teamName) {
        PreCon.notNullOrEmpty(typeName);
        PreCon.notNullOrEmpty(teamName);

        SpawnType type = PVStarAPI.getSpawnTypeManager().getType(typeName);
        if (type == null)
            return new ArrayList<>(0);

        ArenaTeam team = getEnum(teamName, ArenaTeam.class);

        return _arena.getSpawns().getAll(type, team);
    }

    /**
     * Reserves a spawn point for a player by removing it as a candidate
     * for the managers getter methods (getRandomSpawn, getSpawns, etc).
     *
     * @param player  The player to reserve the spawn for.
     * @param spawn   The spawnpoint to reserve.
     */
    public void reserveSpawn(Object player, Spawnpoint spawn) {
        PreCon.notNull(player);
        PreCon.notNull(spawn);

        IArenaPlayer p = PVStarAPI.getArenaPlayer(player);

        _arena.getSpawns().reserve(p, spawn);
    }

    /**
     * Removes the reserved status of the spawnpoint reserved for a player
     * and makes it available via the managers spawnpoint getter methods.
     *
     * @param player  The player the spawn was reserved for.
     */
    public void unreserveSpawn(Object player) {
        PreCon.notNull(player);

        IArenaPlayer p = PVStarAPI.getArenaPlayer(player);

        _arena.getSpawns().unreserve(p);
    }

    /**
     * Clear all reserved spawns and make them available via the managers
     * spawnpoint getter methods.
     */
    public void clearReserved() {
        _arena.getSpawns().clearReserved();
    }

    // convert enum constant name into enum constant
    private <T extends Enum<T>> T getEnum(String constantName, Class<T> enumClass) {

        T e = EnumUtils.searchEnum(constantName, enumClass);
        if (e == null) {
            throw new RuntimeException(
                    "Could not find enum constant named '" + constantName +
                            "' in enum type " + enumClass.getSimpleName());
        }

        return e;
    }
}
