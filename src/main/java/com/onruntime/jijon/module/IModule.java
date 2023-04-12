package com.onruntime.jijon.module;

import org.bukkit.plugin.Plugin;

public interface IModule {
	void init(Plugin plugin);
	default void stop() {}
}
