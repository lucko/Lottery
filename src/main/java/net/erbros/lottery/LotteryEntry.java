package net.erbros.lottery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class LotteryEntry {

    private final UUID uuid;
    private final String name;
    private int tickets;

}
