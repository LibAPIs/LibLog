# LibLog

A basic application logger.

- Multiple log files
- Roll log files daily
- String localization
- And more...

## Maven Dependency

Include the library in your project by adding the following dependency to your pom.xml

```
<dependency>
	<groupId>com.mclarkdev.tools</groupId>
	<artifactId>liblog</artifactId>
	<version>1.5.1</version>
</dependency>
```

## Configuration

Default configuraion values can be modified by setting environment variables prior to launching the application.

```
LOG_DIR    Set the path where log files will be created.	(logs)
LOG_NAME   Set the base name of the default log file.		(server)
LOG_DEBUG  Set any value to enable debug logging.
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
