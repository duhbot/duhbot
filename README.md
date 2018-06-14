# DuhBot
Extensible bot implemented in Java using [PircBotX](https://github.com/pircbotx/pircbotx)


## Building
The project is intended to be built with Maven and will build properly with `maven package`

After building, install to any given directory with the following structure:
```
## assuming (directory) as root of install folder
(directory)/DuhBot*.jar < target/DuhBot*.jar
(directory)/libs < target/libs
(directory)/plugins < any plugins you want to use
(directory)/config.xml < config.xml.example
```
