# ServUI (Server UI)
This is a fork of the SGui Library that is a small, jij-able library that allows creation of server side guis.

Based on the original [Patbox/sgui](https://github.com/Patbox/sgui) library, ServUI supports both Fabric and NeoForge.

## Usage (for mod developers):
Add it to your dependencies like this:

- create a `lib` directory and put the jar in it.
- Add the local repository to your build.gradle:

```
repositories {
    flatDir {
        dir 'libs'
    }
}

dependencies {
	modImplementation include("co.lemee:servui:1.9.1+1.21.10-neoforge") // Adapt it, it should match the jar you are using
}
```

Example of a mod using it: [AuctionHouse](https://github.com/jblemee/AuctionHouse/)

After that you are ready to go! You can use SimpleGUI and other classes directly for simple ones or extend
them for more complex guis.
