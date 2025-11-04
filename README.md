# LibLog

A basic application logger.

- Multiple log files
- Roll log files daily
- String localization
- Send messages to log servers
- Additional plugin log options
- And more...

## Maven Dependency

Include the library in your project by adding the following dependency to your pom.xml

```
<dependency>
	<groupId>com.mclarkdev.tools</groupId>
	<artifactId>liblog</artifactId>
	<version>1.6.5</version>
</dependency>
```

## Configuration

Default log configuration can be modified by setting environment variables prior to launching the application.

```
# console:/[?debug]
# file:/[?debug]
# file:/data/logs/[?debug]
# tcp://127.0.0.1:1234[/][?debug]
# udp://127.0.0.1:1234[/][?debug]

# Console and files on disk
LOG_STREAMS=console:/;file:/

# Log to disk, and send to a log server bound on :1234
LOG_STREAMS=file:/;tcp://127.0.0.1:1234
```

## Example

Invoke any of the LibLog methods anywhere your application should produce a log message.

```
LibLog._log("This is a log message.");
LibLog._logF("Hello, %s", "world");
```

### Using Localized Strings

Load localized strings based on the users default language.

```
String lang = LibArgs.instance().getString(//
		"language", Locale.getDefault().toString());

// Load localized message codes
LibLog._logF("Loading Language Pack: %s", lang);
LibLog.loadStrings(ServerLauncher.class.getResourceAsStream(//
		String.format("/strings/codes.%s.properties", lang)));
```

Use _logc_ methods to resolve logger codes to localized messages.

```
LibLog._clog("I0001");
```

# License

Open source & free for all. ❤
