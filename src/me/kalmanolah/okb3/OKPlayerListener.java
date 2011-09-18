package me.kalmanolah.okb3;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OKPlayerListener extends org.bukkit.event.player.PlayerListener {
	private static OKmain plugin;

	public OKPlayerListener(OKmain instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if ((event.getFrom().getWorld().getName() != event.getTo().getWorld().getName()) || (OKmain.portals.contains(event.getPlayer()))) {
			new Thread(new OKRunnable(plugin, event)).start();
			OKmain.portals.remove(event.getPlayer());
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		OKmain.cachedjoinmsgs.put(event.getPlayer(), event.getJoinMessage());
		event.setJoinMessage(null);
		new Thread(new OKRunnable(plugin, event)).start();
	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		OKmain.portals.add(event.getPlayer());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (OKmain.kicks.contains(event.getPlayer())) {
			event.setQuitMessage(null);
			OKmain.kicks.remove(event.getPlayer());
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if (OKmain.kicks.contains(event.getPlayer())) {
			event.setLeaveMessage(null);
			OKmain.kicks.remove(event.getPlayer());
		}
	}
}
