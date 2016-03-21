# Maven Plugin

## How it works
The maven plugin collects all dependencies of the project to build and saves them in a XML-file.
Now it starts a headless eclipse installation that is bundled with the maven plugin.
In this new headless eclipse instance the required build is done and the eclipse closes again.
Eclipse should log to System.out and System.err what is redirected to the maven logging.

Target Platform
no autobuild