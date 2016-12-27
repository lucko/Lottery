package net.erbros.lottery.events;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class LotteryDrawEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    private UUID winner;

    @Getter
    private int ticketsBought;

    @Getter
    private double winnings;

    @Getter
    private int material;

    @Getter
    private String winnerName;

    public LotteryDrawEvent(UUID winner, String winnerName, int ticketsBought, double winnings, int material) {
        this.winner = winner;
        this.winnerName = winnerName;
        this.ticketsBought = ticketsBought;
        this.winnings = winnings;
        this.material = material;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
