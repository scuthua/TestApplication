package com.flower.test.model;

import java.util.List;

/**
 * Created by flower on 2016/2/27.
 */
public class Lrc {
    private List<String> lrc;
    private List<Integer> lrcTime;

    public List<String> getLrc() {
        return lrc;
    }

    public void setLrc(List<String> lrc) {
        this.lrc = lrc;
    }

    public List<Integer> getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(List<Integer> lrcTime) {
        this.lrcTime = lrcTime;
    }
}
