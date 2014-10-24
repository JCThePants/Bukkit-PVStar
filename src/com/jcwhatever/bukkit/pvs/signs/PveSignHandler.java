package com.jcwhatever.bukkit.pvs.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.PVArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.arena.options.NameMatchMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.regex.Matcher;

public class PveSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "PVE";
    }

    @Override
    public String getDescription() {
        return "An arena join sign. Player joins an arena by clicking on the sign.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "PVE",
                "<arenaName>",
                "--anything--",
                "--anything--"
        };
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {
        String rawName = sign.getRawLine(1);

        Matcher matcher = TextUtils.PATTERN_SPACE.matcher(rawName);

        String arenaName = matcher.replaceAll("_");

        List<Arena> arenas = PVStarAPI.getArenaManager().getArena(arenaName, NameMatchMode.CASE_INSENSITIVE);
        return arenas.size() == 1;
    }

    @Override
    protected boolean onSignClick(final Player p, SignContainer sign) {
        String rawName = sign.getRawLine(1);
        Matcher matcher = TextUtils.PATTERN_SPACE.matcher(rawName);

        final String arenaName = matcher.replaceAll("_");

        List<Arena> arenas = PVStarAPI.getArenaManager().getArena(arenaName, NameMatchMode.CASE_INSENSITIVE);
        if (arenas.size() != 1)
            return false;

        final Arena arena =  arenas.get(0);

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), new Runnable() {

            @Override
            public void run() {

                ArenaPlayer player = PVArenaPlayer.get(p);

                // Add player to arena
                arena.join(player, AddPlayerReason.PLAYER_JOIN);
            }
        });

        return true;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {
        // allow
        return true;
    }
}
