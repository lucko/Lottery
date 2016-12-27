package net.erbros.lottery;

import lombok.RequiredArgsConstructor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerJoinReminderListener implements Listener {
    private final LotteryGame lGame;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Send the player some info about time until lottery draw?
        lGame.sendMessage(event.getPlayer(), "Welcome");
    }
}
