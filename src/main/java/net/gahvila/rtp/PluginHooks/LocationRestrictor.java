package net.gahvila.rtp.PluginHooks;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public interface LocationRestrictor {

    public Plugin supporterPlugin();

    public boolean denyLandingAtLocation(Location location);

}
