package com.eshop.client.helper;

import lombok.Getter;

@Getter
public  class VoteCounters {
    private final int up;
    private final int down;
    public VoteCounters(int up, int down) { this.up = up; this.down = down; }
}