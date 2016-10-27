# Registration room

The registration room is a Game On! room which allows developers to register their
rooms for various competitions, hack days etc. In order to use this room, you simply
either checkout or clone this repository, supply some configuration via environment variables
and then deploy it to Bluemix or some other publicly available end point. When deployed,
the room auto-registers with Game On! (hence why you need a public end point), after which
developers can either navigate to the room or teleport there directly from first room.

## Room configuration

The following environment variables need to be set

```
GAMEON_ID=<your Game On! ID>
GAMEON_SECRET=<your Game On! secret>

EVENT_ID=<a short unique description for your room>
EVENT_DESC=<a description of the event for which this room was created>

LOCAL_ENDPOINT=<this is where Game On! will connect to your room>
```

The registration room is a normal Game On! room, which means that you will need some Game On!
credentials in order to register it (the GAMEON_ID and GAMEON_SECRET values). If you haven't already
done so, then you can sign up for Game On at https://game-on.org, or if you are already registered then
your ID and SECRET can be found at https://game-on.org/#/play/myrooms.

### Room configuration (optional)

There are some optional environment variables that you can set in order to override some of the
default locations used by registration room.

```
MAP_SERVICE_URL=<location of the Game On! map service>
REGISTRATION_SERVICE_URL=<location of the Game On! registration service>
```

The default locations point to the live instance of Game On! i.e. the one hosted at game-on.org. However, if you are
running your own copy, then overriding these values will allow you to register with that version of the game.

### Setting environment variables

There are a couple of ways of setting environment variables, some of which depend on how you are going to
deploy the room.

* **server.env** : This is a Liberty file that can be used to specify environment variables. Create the file in the
```
regroom-wlpcfg/servers/regroom
```
directory next to the server.xml file.
* **docker-compose** : if deploying to a docker container, then you can set the environment variables in the docker-compose.yml
file.

## Customising the notices

It is likely that your hack or competition will have a set of rules that a developer should read before registering their
room. There are two places that you can customise

* **regroom-app/src/webapp/WEB-INF/classes/notices.txt** : this is a text file which can contain 'safe' markdown (Game On! uses Angular to sanitise it's output which can remove some markdown such as hyperlinks). It will be displayed if a player types **'/notices'** in the room.
* **regroom-app/src/webapp/index.html** : a normal HTML file that will be displayed in the browser if someone navigates directly
to the hosted room i.e. not via Game On!. This is potentially useful to describe things in more detail, rather than presenting a
wall of text in the notices.txt file.

## Room commands

There are a number of commands that developers (i.e. players) can type in when in the registration room.

* **/notices** : display the specific rules for registering a room (can also use **/examine notice board**).
* **/register <siteID>** : this will register the specified room for the event (the site id can be found on the rooms page https://game-on.org/#/play/myrooms).
* **/rooms** : show a list of rooms registered for this event.

## Viewing registered rooms

Rooms that have been registered for events can be viewed outside of actual game environment via the registration service. Full instructions can be found at https://github.com/gameontext/registration-service/blob/master/README.md.
