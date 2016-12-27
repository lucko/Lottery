package net.erbros.lottery;

import lombok.AllArgsConstructor;

import java.util.TimerTask;

@AllArgsConstructor
public class LotteryDraw extends TimerTask {

    private final Lottery plugin;
    private final boolean draw;

    public void run() {
        if (draw && plugin.isLotteryDue()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, plugin::lotteryDraw);
        } else {
            plugin.extendLotteryDraw();
        }
    }
}