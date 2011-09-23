package me.kalmanolah.okb3;

import me.kalmanolah.okb3extras.com.nijikokun.register.payment.Methods;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class OKServerListener extends ServerListener {
	private OKmain plugin;
	@SuppressWarnings("unused")
	private static Methods Methods = null;

	public OKServerListener(OKmain plugin) {
		this.plugin = plugin;
		Methods = new Methods();
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (!me.kalmanolah.okb3extras.com.nijikokun.register.payment.Methods.hasMethod()) {
			if (me.kalmanolah.okb3extras.com.nijikokun.register.payment.Methods.setMethod(plugin.getServer().getPluginManager())) {
				OKmain.Method = me.kalmanolah.okb3extras.com.nijikokun.register.payment.Methods.getMethod();
				OKLogger.info("Hooked into " + OKmain.Method.getName() + " version: " + OKmain.Method.getVersion() + "...");
			}
		}
	}
}