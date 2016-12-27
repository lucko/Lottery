package net.erbros.lottery.events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LotteryBuyTicketEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    private Player player;

    @Getter
    private int amount;

    @Getter
    @Setter
    private boolean cancelled = false;

    public LotteryBuyTicketEvent(Player player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
