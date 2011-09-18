package me.kalmanolah.extras.com.nijikokun.register.payment;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Methods.java Controls the getting / setting of methods & the method of
 * payment used.
 * 
 * @author: Nijikokun<nijikokun@gmail.com> (@nijikokun)
 * @copyright: Copyright (C) 2011
 * @license: GNUv3 Affero License <http://www.gnu.org/licenses/agpl-3.0.html>
 */
public class Methods {
	private boolean self = false;
	private Method Method = null;
	private String preferred = "";
	private Set<Method> Methods = new HashSet<Method>();
	private Set<String> Dependencies = new HashSet<String>();
	private Set<Method> Attachables = new HashSet<Method>();

	public Methods() {
		_init();
	}

	/**
	 * Allows you to set which economy plugin is most preferred.
	 * 
	 * @param preferred
	 */
	public Methods(String preferred) {
		_init();
		if (Dependencies.contains(preferred)) {
			this.preferred = preferred;
		}
	}

	private void _init() {
		addMethod("iConomy", new me.kalmanolah.extras.com.nijikokun.register.payment.methods.iCo4());
		addMethod("iConomy", new me.kalmanolah.extras.com.nijikokun.register.payment.methods.iCo5());
		addMethod("BOSEconomy", new me.kalmanolah.extras.com.nijikokun.register.payment.methods.BOSE());
		addMethod("Essentials", new me.kalmanolah.extras.com.nijikokun.register.payment.methods.EE17());
	}

	public Set<String> getDependencies() {
		return Dependencies;
	}

	public Method createMethod(Plugin plugin) {
		for (Method method : Methods) {
			if (method.isCompatible(plugin)) {
				method.setPlugin(plugin);
				return method;
			}
		}
		return null;
	}

	private void addMethod(String name, Method method) {
		Dependencies.add(name);
		Methods.add(method);
	}

	public boolean hasMethod() {
		return (Method != null);
	}

	public boolean setMethod(Plugin method) {
		if (hasMethod()) {
			return true;
		}
		if (self) {
			self = false;
			return false;
		}
		int count = 0;
		boolean match = false;
		Plugin plugin = null;
		PluginManager manager = method.getServer().getPluginManager();
		for (String name : getDependencies()) {
			if (hasMethod()) {
				break;
			}
			if (method.getDescription().getName().equals(name)) {
				plugin = method;
			} else {
				plugin = manager.getPlugin(name);
			}
			if (plugin == null) {
				continue;
			}
			Method current = createMethod(plugin);
			if (current == null) {
				continue;
			}
			if (preferred.isEmpty()) {
				Method = current;
			} else {
				Attachables.add(current);
			}
		}
		if (!preferred.isEmpty()) {
			do {
				if (hasMethod()) {
					match = true;
				} else {
					for (Method attached : Attachables) {
						if (attached == null) {
							continue;
						}
						if (hasMethod()) {
							match = true;
							break;
						}
						if (preferred.isEmpty()) {
							Method = attached;
						}
						if (count == 0) {
							if (preferred.equalsIgnoreCase(attached.getName())) {
								Method = attached;
							}
						} else {
							Method = attached;
						}
					}
					count++;
				}
			} while (!match);
		}
		return hasMethod();
	}

	public Method getMethod() {
		return Method;
	}

	public boolean checkDisabled(Plugin method) {
		if (!hasMethod()) {
			return true;
		}
		if (Method.isCompatible(method)) {
			Method = null;
		}
		return (Method == null);
	}
}
