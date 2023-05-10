package space.hwahyang.ticketing;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * @author HwaHyang
 */
public final class Ticketing extends JavaPlugin implements Listener {

    private final FileConfiguration config = getConfig();

    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        this.saveDefaultConfig();

        logger.info("Ticketing Enabled.");
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("ticketing.bypass")) return;

        long now = System.currentTimeMillis() / 1000L;

        if (config.getLong("startTimestamp") <= now && now <= config.getLong("endTimestamp")) {

        } else {
            String zoneId = ZoneId.systemDefault().getId();
            String nowTimezone = convertUnixTimestampToString(now);
            String startTimezone = convertUnixTimestampToString(getConfig().getLong("startTimestamp"));
            String endTimezone = convertUnixTimestampToString(getConfig().getLong("endTimestamp"));

            String message = getConfig().getString("deniedMessage").replace("&", "§");
            message = message.replace("{NOWTIME}", nowTimezone);
            message = message.replace("{STARTTIME}", startTimezone);
            message = message.replace("{ENDTIME}", endTimezone);
            message = message.replace("{TIMEZONE}", zoneId);

            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
        }
    }

    @Override
    public void onDisable() {
        logger.info("Ticketing Disabled.");
    }

    public String convertUnixTimestampToString(long unixTimestamp) {
        Date date = new Date(unixTimestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        return sdf.format(date);
    }
}
