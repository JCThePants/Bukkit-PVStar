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


package com.jcwhatever.pvs.arenas.managers;

import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.ArenaTeam;
import com.jcwhatever.pvs.api.arena.ArenaTeam.TeamDistributor;
import com.jcwhatever.pvs.api.arena.managers.ITeamManager;
import com.jcwhatever.pvs.api.arena.options.AddToContextReason;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.arena.options.RemoveFromContextReason;
import com.jcwhatever.pvs.api.arena.options.TeamChangeReason;
import com.jcwhatever.pvs.api.events.players.PlayerPreAddToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerRemovedFromContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerTeamChangedEvent;
import com.jcwhatever.pvs.api.events.spawns.SpawnAddedEvent;
import com.jcwhatever.pvs.api.events.spawns.SpawnRemovedEvent;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Team manager implementation.
 */
public class TeamManager implements ITeamManager, IEventListener {

    private final IArena _arena;
    private final ElementCounter<ArenaTeam> _teams = new ElementCounter<>(RemovalPolicy.REMOVE);
    private final ElementCounter<ArenaTeam> _currentTeams = new ElementCounter<>(RemovalPolicy.REMOVE);

    private TeamDistributor _teamDistributor;

    /*
     * Constructor.
     */
    public TeamManager(IArena arena) {
        _arena = arena;

        arena.getEventManager().register(this);

        loadSettings();
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public IArena getArena() {
        return _arena;
    }

    @Override
    public Set<ArenaTeam> getAvailable() {
        return _teams.getElements();
    }

    @Override
    public Set<ArenaTeam> getCurrentTeams() {
        return _currentTeams.getElements();
    }

    @Override
    public int totalTeams() {
        return _teams.size();
    }

    @Override
    public int totalCurrentTeams() {
        return _currentTeams.size();
    }

    /**
     * Get the next available team from the team distributor.
     */
    @Nullable
    protected ArenaTeam nextTeam() {
        return getTeamDistributor().next();
    }

    /**
     * Place a team back into circulation. Use when a player leaves the arena
     * to prevent issues with the distribution of teams.
     */
    protected void recycleTeam(ArenaTeam team) {
        getTeamDistributor().recycle(team);
    }

    /**
     * Get the distributor responsible for distributing teams to players
     * as they join.
     */
    protected TeamDistributor getTeamDistributor() {
        if (_teamDistributor == null)
            _teamDistributor = new TeamDistributor(getAvailable());

        return _teamDistributor;
    }

    /*
     * Set a players team when they are added to an arena.
     */
    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onPlayerAdd(PlayerPreAddToContextEvent event) {

        if (event.getReason() != AddToContextReason.FORWARDING &&
                event.getReason() != AddToContextReason.CONTEXT_CHANGE) {

            ArenaTeam team = nextTeam();
            if (team != null) {
                _currentTeams.add(team);
            }
            else {
                team = ArenaTeam.NONE;
            }

            event.getPlayer().setTeam(team, TeamChangeReason.JOIN_ARENA);
        }
    }

    /*
     * Recycle the a players team when they are removed from the arena.
     * Ensures an even distribution of teams.
     */
    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onPlayerRemove(PlayerRemovedFromContextEvent event) {

        if (event.getReason() != RemoveFromContextReason.FORWARDING &&
                event.getReason() != RemoveFromContextReason.CONTEXT_CHANGE) {

            ArenaTeam team = event.getPlayer().getTeam();

            _currentTeams.subtract(team);

            recycleTeam(team);
        }
    }

    /*
     * Make sure teams updated if players team is changed.
     */
    @EventMethod(priority = EventSubscriberPriority.LAST)
    private void onTeamChange(PlayerTeamChangedEvent event) {

        if (event.getReason() == TeamChangeReason.JOIN_ARENA)
            return;

        ArenaTeam previous = event.getPreviousTeam();
        ArenaTeam current = event.getTeam();

        if (previous != ArenaTeam.NONE &&
                previous != null) {

            _currentTeams.subtract(previous);
        }

        if (current != ArenaTeam.NONE &&
                current != null) {

            _currentTeams.add(current);
        }
    }

    /*
     * Add spawn team.
     */
    @EventMethod(priority = EventSubscriberPriority.LAST)
    private void onAddSpawn(SpawnAddedEvent event) {

        if (event.getSpawnpoint().getTeam() == ArenaTeam.NONE)
            return;

        _teams.add(event.getSpawnpoint().getTeam());
    }

    /*
     * Remove team when spawn is removed.
     */
    @EventMethod(priority = EventSubscriberPriority.LAST)
    private void onRemoveSpawn(SpawnRemovedEvent event) {

        if (event.getSpawnpoint().getTeam() == ArenaTeam.NONE)
            return;

        _teams.subtract(event.getSpawnpoint().getTeam());
    }

    // load settings
    private void loadSettings() {

        List<Spawnpoint> spawnpoints = getArena().getSpawns().getAll(ArenaContext.GAME);

        for (Spawnpoint spawn : spawnpoints) {
            if (spawn.getTeam() == ArenaTeam.NONE)
                continue;

            _teams.add(spawn.getTeam());
        }
    }
}
