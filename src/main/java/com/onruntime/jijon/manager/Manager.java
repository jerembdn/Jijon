package com.onruntime.jijon.manager;

public interface Manager {
    void init();
    default void stop() {}
}
