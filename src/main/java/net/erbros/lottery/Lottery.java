package net.erbros.lottery;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Lottery extends JavaPlugin {

    @Getter
    private Economy economy;

    public boolean timerStarted = false;

    @Getter
    private LotteryConfig lotteryConfig;

    @Getter
    private LotteryGame lotteryGame;

    @Override
    public void onEnable() {

        FileConfiguration config;
        lotteryConfig = new LotteryConfig(this);
        lotteryGame = new LotteryGame(this);
        // Lets find some configs
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        lotteryConfig.loadConfig();

        final PluginManager pm = getServer().getPluginManager();

        if (lotteryConfig.isWelcomeMessage()) {
            pm.registerEvents(new PlayerJoinReminderListener(lotteryGame), this);
        }

        getCommand("lottery").setExecutor(new MainCommandExecutor(this));

        // Is the date we are going to draw the lottery set? If not, we should
        // do it.
        if (getNextexec() == 0) {
            // Set first time to be config hours later? Millisecs, * 1000.
            setNextexec(System.currentTimeMillis() + extendTime());
        }
        setupEconomy();

        // Start the timer for the first time.
        startTimerSchedule(false);
    }

    @Override
    public void onDisable() {
        // Disable all running timers.
        Bukkit.getServer().getScheduler().cancelTasks(this);

        lotteryConfig.debugMsg("[Lottery]: has been disabled (including timers).");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    private long getNextexec() {
        return lotteryConfig.getNextexec();
    }

    private void setNextexec(long aNextexec) {
        lotteryConfig.setNextexec(aNextexec);
    }

    public boolean isLotteryDue() {
        return getNextexec() > 0 && System.currentTimeMillis() + 1000 >= getNextexec();
    }

    public void startTimerSchedule(boolean drawAtOnce) {
        long extendtime;
        // Cancel any existing timers.
        if (timerStarted) {
            // Let's try and stop any running threads.
            try {
                Bukkit.getServer().getScheduler().cancelTasks(this);
            } catch (ClassCastException ignored) {}

            extendtime = extendTime();
        } else {
            // Get time until lottery drawing.
            extendtime = getNextexec() - System.currentTimeMillis();
        }
        // What if the admin changed the config to a shorter time? lets check,
        // and if
        // that is the case, lets use the new time.
        if (System.currentTimeMillis() + extendTime() < getNextexec()) {
            setNextexec(System.currentTimeMillis() + extendTime());
        }

        // If the time is passed (perhaps the server was offline?), draw lottery
        // at once.
        if (extendtime <= 0) {
            extendtime = 1000;
            lotteryConfig.debugMsg("Seems we need to make a draw at once!");
        }

        // Is the drawAtOnce boolean set to true? In that case, do drawing in a
        // few secs.
        if (drawAtOnce) {
            extendtime = 100;
            setNextexec(System.currentTimeMillis() + 100);
            lotteryConfig.debugMsg("DRAW NOW");
        }

        // Delay in server ticks. 20 ticks = 1 second.
        extendtime = extendtime / 1000 * 20;
        runDrawTimer(extendtime);

        // Timer is now started, let it know.
        timerStarted = true;
    }

    public void lotteryDraw() {
        lotteryConfig.debugMsg("Doing a lottery draw");

        if (getNextexec() > 0 && System.currentTimeMillis() + 1000 >= getNextexec()) {
            // Get the winner, if any. And remove file so we are ready for
            // new round.
            lotteryConfig.debugMsg("Getting winner.");
            if (!lotteryGame.getWinner()) {
                lotteryConfig.debugMsg("Failed getting winner");
            }
            setNextexec(System.currentTimeMillis() + extendTime());
        }
        // Call a new timer.
        startTimerSchedule(false);
    }

    public void extendLotteryDraw() {
        // Cancel timer.
        try {
            Bukkit.getServer().getScheduler().cancelTasks(this);
        } catch (ClassCastException exception) {}

        long extendtime;

        // How much time left? Below 0?
        if (getNextexec() < System.currentTimeMillis()) {
            extendtime = 3000;
        } else {
            extendtime = getNextexec() - System.currentTimeMillis();
        }
        // Delay in server ticks. 20 ticks = 1 second.
        extendtime = extendtime / 1000 * 20;
        runDrawTimer(extendtime);
    }

    private void runDrawTimer(final long extendtime) {
        // Is this very long until? On servers with lag and long between
        // restarts there might be a very long time between when server
        // should have drawn winner and when it will draw. Perhaps help the
        // server a bit by only scheduling for half the length at a time?
        // But only if its more than 5 seconds left.
        if (extendtime < 5 * 20) {
            getServer().getScheduler().runTaskLaterAsynchronously(this, new LotteryDraw(this, true), extendtime);
            lotteryConfig.debugMsg("LotteryDraw() " + extendtime + 100);
        } else {
            final long newtime = extendtime / 10;
            getServer().getScheduler().runTaskLaterAsynchronously(this, new LotteryDraw(this, false), newtime);
            lotteryConfig.debugMsg("extendLotteryDraw() " + newtime);
        }
    }

    private long extendTime() {
        final double exacttime = lotteryConfig.getHours() * 60 * 60 * 1000;
        final long extendTime = (long) exacttime;
        lotteryConfig.debugMsg("extendTime: " + extendTime);
        return extendTime;
    }
}