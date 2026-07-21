\
package de.nexusgaming.dynamicxpdays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public final class DynamicXPDaysPlugin extends JavaPlugin implements Listener {

    private boolean pluginEnabled;
    private double multiplier;
    private ZoneId zoneId;
    private Set<DayOfWeek> activeDays;
    private boolean respectBypassPermission;

    private boolean usePlayerClientLanguage;
    private String fallbackLanguage;

    private boolean joinMessageEnabled;
    private boolean repeatingMessageEnabled;
    private int repeatingIntervalMinutes;
    private boolean onlyWhenPlayersOnline;

    private FileConfiguration messages;
    private BukkitTask repeatingTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        loadSettings();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DynamicXPDays has been enabled.");
    }

    @Override
    public void onDisable() {
        cancelRepeatingTask();
        getLogger().info("DynamicXPDays has been disabled.");
    }

    private void loadSettings() {
        reloadConfig();
        reloadMessages();

        FileConfiguration config = getConfig();

        pluginEnabled = config.getBoolean("settings.enabled", true);
        multiplier = config.getDouble("settings.multiplier", 2.0);
        respectBypassPermission = config.getBoolean("settings.respect-bypass-permission", true);

        usePlayerClientLanguage = config.getBoolean("language.use-player-client-language", true);
        fallbackLanguage = normalizeLanguage(config.getString("language.fallback-language", "de"));

        joinMessageEnabled = config.getBoolean("notifications.join-message.enabled", true);
        repeatingMessageEnabled = config.getBoolean("notifications.repeating-message.enabled", true);
        repeatingIntervalMinutes = Math.max(
                1,
                config.getInt("notifications.repeating-message.interval-minutes", 30)
        );
        onlyWhenPlayersOnline = config.getBoolean(
                "notifications.repeating-message.only-when-players-online",
                true
        );

        if (multiplier < 0.0) {
            getLogger().warning("The XP multiplier cannot be negative. Falling back to 2.0.");
            multiplier = 2.0;
        }

        String timezone = config.getString("settings.timezone", "Europe/Berlin");
        try {
            zoneId = ZoneId.of(timezone);
        } catch (Exception exception) {
            getLogger().log(
                    Level.WARNING,
                    "Invalid timezone '" + timezone + "'. Falling back to Europe/Berlin.",
                    exception
            );
            zoneId = ZoneId.of("Europe/Berlin");
        }

        activeDays = EnumSet.noneOf(DayOfWeek.class);
        List<String> configuredDays = config.getStringList("settings.active-days");

        for (String configuredDay : configuredDays) {
            try {
                activeDays.add(DayOfWeek.valueOf(configuredDay.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException exception) {
                getLogger().warning("Ignoring invalid weekday in config: " + configuredDay);
            }
        }

        if (activeDays.isEmpty()) {
            getLogger().warning("No valid active days configured. Double XP will never activate.");
        }

        restartRepeatingTask();
    }

    private void reloadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void restartRepeatingTask() {
        cancelRepeatingTask();

        if (!repeatingMessageEnabled) {
            return;
        }

        long intervalTicks = repeatingIntervalMinutes * 60L * 20L;

        repeatingTask = getServer().getScheduler().runTaskTimer(
                this,
                this::sendRepeatingNotification,
                intervalTicks,
                intervalTicks
        );
    }

    private void cancelRepeatingTask() {
        if (repeatingTask != null) {
            repeatingTask.cancel();
            repeatingTask = null;
        }
    }

    private void sendRepeatingNotification() {
        if (!isDoubleXpActive()) {
            return;
        }

        if (onlyWhenPlayersOnline && getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage(message(player, "repeating", null));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExperienceChange(PlayerExpChangeEvent event) {
        if (!isDoubleXpActive()) {
            return;
        }

        Player player = event.getPlayer();

        if (respectBypassPermission && player.hasPermission("dynamicxpdays.bypass")) {
            return;
        }

        int originalAmount = event.getAmount();
        if (originalAmount <= 0) {
            return;
        }

        long calculatedAmount = Math.round(originalAmount * multiplier);
        int safeAmount = calculatedAmount > Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : (int) calculatedAmount;

        event.setAmount(safeAmount);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!joinMessageEnabled || !isDoubleXpActive()) {
            return;
        }

        event.getPlayer().sendMessage(message(event.getPlayer(), "join", null));
    }

    private boolean isDoubleXpActive() {
        if (!pluginEnabled) {
            return false;
        }

        DayOfWeek currentDay = ZonedDateTime.now(zoneId).getDayOfWeek();
        return activeDays.contains(currentDay);
    }

    private String message(Player player, String key, String label) {
        String language = resolveLanguage(player);
        String path = language + "." + key;

        String text = messages.getString(path);
        if (text == null) {
            text = messages.getString(fallbackLanguage + "." + key);
        }
        if (text == null) {
            text = messages.getString("en." + key, key);
        }

        String prefix = messages.getString(language + ".prefix");
        if (prefix == null) {
            prefix = messages.getString(fallbackLanguage + ".prefix");
        }
        if (prefix == null) {
            prefix = messages.getString("en.prefix", "");
        }

        return color(
                prefix + text
                        .replace("{multiplier}", formatMultiplier(multiplier))
                        .replace("{day}", ZonedDateTime.now(zoneId).getDayOfWeek().name())
                        .replace("{label}", label == null ? "dynamicxpdays" : label)
        );
    }

    private String message(CommandSender sender, String key, String label) {
        return message(sender instanceof Player player ? player : null, key, label);
    }

    private String resolveLanguage(Player player) {
        if (!usePlayerClientLanguage || player == null) {
            return fallbackLanguage;
        }

        String locale = player.getLocale();
        String normalized = normalizeLanguage(locale);

        if (messages.isConfigurationSection(normalized)) {
            return normalized;
        }

        return fallbackLanguage;
    }

    private String normalizeLanguage(String locale) {
        if (locale == null || locale.isBlank()) {
            return "de";
        }

        String normalized = locale.toLowerCase(Locale.ROOT).replace('-', '_');
        int separator = normalized.indexOf('_');

        if (separator > 0) {
            normalized = normalized.substring(0, separator);
        }

        return normalized;
    }

    private String formatMultiplier(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }

        return String.valueOf(value);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            if (!sender.hasPermission("dynamicxpdays.status")) {
                sender.sendMessage(message(sender, "no-permission", label));
                return true;
            }

            if (!pluginEnabled) {
                sender.sendMessage(message(sender, "disabled", label));
                return true;
            }

            sender.sendMessage(message(
                    sender,
                    isDoubleXpActive() ? "active" : "inactive",
                    label
            ));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dynamicxpdays.reload")) {
                sender.sendMessage(message(sender, "no-permission", label));
                return true;
            }

            loadSettings();
            sender.sendMessage(message(sender, "reload", label));
            return true;
        }

        sender.sendMessage(message(sender, "usage", label));
        return true;
    }
}
