# DynamicXPDays

## English

**DynamicXPDays** is a lightweight and fully configurable Paper plugin that automatically activates XP multiplier events on selected weekdays.

Configure specific days such as Saturday and Sunday, choose your preferred XP multiplier and let the plugin handle everything automatically. The XP event activates at midnight on the configured days and ends automatically when the day changes.

DynamicXPDays also supports automatic Minecraft client-language detection. Every player receives notifications in their own supported language, while unsupported languages use the configured fallback language.

## Features

* Automatically activates and deactivates XP event days
* Select any combination of weekdays
* Configurable XP multiplier
* Configurable server timezone
* Automatic player client-language detection
* 21 included languages
* Configurable fallback language
* Join notification during active XP days
* Repeating chat notifications
* Configurable notification interval
* Fully customizable messages
* English and German configuration comments
* Permission-based XP bypass
* Reload command without restarting the server
* Lightweight and easy to configure

## Example Configuration

```yaml
settings:
  enabled: true
  multiplier: 2.0
  timezone: "Europe/Berlin"

  active-days:
    - SATURDAY
    - SUNDAY
```

In this example, players receive double XP every Saturday and Sunday.

Other multipliers are also possible:

```yaml
multiplier: 1.5
```

This provides 50% more XP.

```yaml
multiplier: 3.0
```

This provides triple XP.

## Notifications

DynamicXPDays can notify players when they join during an active XP event.

It can also send repeating chat messages at a configurable interval:

```yaml
notifications:
  join-message:
    enabled: true

  repeating-message:
    enabled: true
    interval-minutes: 30
    only-when-players-online: true
```

All notification texts can be customized separately for every included language.

## Languages

DynamicXPDays currently includes:

* German
* English
* French
* Spanish
* Italian
* Portuguese
* Dutch
* Polish
* Russian
* Ukrainian
* Turkish
* Czech
* Danish
* Swedish
* Norwegian
* Finnish
* Hungarian
* Romanian
* Japanese
* Korean
* Chinese

The plugin automatically detects the Minecraft client language of each player.

## Commands

```text
/dynamicxpdays status
/dynamicxpdays reload
```

Available aliases:

```text
/xpdays
/dynamicxp
/dxp
/doublexp
```

## Permissions

```text
dynamicxpdays.status
```

Allows players to check whether an XP event is currently active.

```text
dynamicxpdays.reload
```

Allows administrators to reload the configuration and messages.

```text
dynamicxpdays.bypass
```

Players with this permission do not receive the configured XP multiplier.

---

# DynamicXPDays

## Deutsch

**DynamicXPDays** ist ein leichtgewichtiges und vollständig konfigurierbares Paper-Plugin, das XP-Bonustage an ausgewählten Wochentagen automatisch aktiviert.

Du kannst beliebige Wochentage wie Samstag und Sonntag festlegen, den gewünschten XP-Multiplikator einstellen und den Rest automatisch vom Plugin übernehmen lassen. Der XP-Bonus wird an den eingestellten Tagen um Mitternacht aktiviert und beim nächsten Tageswechsel automatisch wieder deaktiviert.

DynamicXPDays erkennt außerdem automatisch die Minecraft-Clientsprache jedes Spielers. Jeder Spieler erhält die Benachrichtigungen in seiner unterstützten Sprache. Für nicht vorhandene Sprachen wird automatisch die eingestellte Ersatzsprache verwendet.

## Funktionen

* Automatische Aktivierung und Deaktivierung der XP-Tage
* Beliebige Wochentage auswählbar
* Frei einstellbarer XP-Multiplikator
* Einstellbare Server-Zeitzone
* Automatische Erkennung der Spielersprache
* 21 enthaltene Sprachen
* Einstellbare Ersatzsprache
* Benachrichtigung beim Betreten des Servers
* Regelmäßige Chat-Benachrichtigungen
* Frei einstellbarer Nachrichtenabstand
* Vollständig anpassbare Nachrichten
* Englische und deutsche Config-Beschreibungen
* Bypass-Berechtigung für einzelne Spieler
* Neuladen ohne Serverneustart
* Leichtgewichtig und einfach einzurichten

## Beispielkonfiguration

```yaml
settings:
  enabled: true
  multiplier: 2.0
  timezone: "Europe/Berlin"

  active-days:
    - SATURDAY
    - SUNDAY
```

In diesem Beispiel erhalten Spieler jeden Samstag und Sonntag doppelte XP.

Andere Multiplikatoren sind ebenfalls möglich:

```yaml
multiplier: 1.5
```

Damit erhalten Spieler 50 % mehr XP.

```yaml
multiplier: 3.0
```

Damit erhalten Spieler dreifache XP.

## Benachrichtigungen

DynamicXPDays kann Spieler beim Betreten des Servers über einen aktiven XP-Tag informieren.

Zusätzlich können in einem frei einstellbaren Abstand wiederholte Chat-Nachrichten gesendet werden:

```yaml
notifications:
  join-message:
    enabled: true

  repeating-message:
    enabled: true
    interval-minutes: 30
    only-when-players-online: true
```

Alle Benachrichtigungen können für jede enthaltene Sprache einzeln angepasst werden.

## Sprachen

DynamicXPDays enthält derzeit:

* Deutsch
* Englisch
* Französisch
* Spanisch
* Italienisch
* Portugiesisch
* Niederländisch
* Polnisch
* Russisch
* Ukrainisch
* Türkisch
* Tschechisch
* Dänisch
* Schwedisch
* Norwegisch
* Finnisch
* Ungarisch
* Rumänisch
* Japanisch
* Koreanisch
* Chinesisch

Das Plugin erkennt automatisch die Minecraft-Clientsprache jedes Spielers.

## Befehle

```text
/dynamicxpdays status
/dynamicxpdays reload
```

Verfügbare Aliase:

```text
/xpdays
/dynamicxp
/dxp
/doublexp
```

## Berechtigungen

```text
dynamicxpdays.status
```

Erlaubt Spielern zu prüfen, ob gerade ein XP-Tag aktiv ist.

```text
dynamicxpdays.reload
```

Erlaubt Administratoren, die Konfiguration und Nachrichten neu zu laden.

```text
dynamicxpdays.bypass
```

Spieler mit dieser Berechtigung erhalten den eingestellten XP-Multiplikator nicht.

## Paper compatibility / Paper-Kompatibilität

- Build target: Paper / Minecraft 26.3
- `api-version: 1.21` is intentionally retained for backward compatibility
- Plugin bytecode remains Java 21 compatible; the build itself uses JDK 25 for the Paper 26.3 API
