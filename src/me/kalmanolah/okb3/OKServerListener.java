package me.kalmanolah.okb3;

import me.kalmanolah.extras.com.nijikokun.register.payment.Methods;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class OKServerListener extends ServerListener {
	@SuppressWarnings("unused")
	private OKmain plugin;
	private Methods Methods = null;

	public OKServerListener(OKmain plugin) {
		this.plugin = plugin;
		Methods = new Methods();
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (!Methods.hasMethod()) {
			if (Methods.setMethod(event.getPlugin())) {
				OKmain.Method = Methods.getMethod();
				OKLogger.info("Hooked into " + OKmain.Method.getName() + " version: " + OKmain.Method.getVersion() + "...");
			}
		}
	}
}