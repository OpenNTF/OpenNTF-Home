# OpenNTF Apps


### Jakarta Technologies Used

The app is structured around Jakarta EE 8 and related technologies, and expects to run in a web app container using Java 11. The structure
is largely similar to what I described in [my XPages to Jakarta EE series](https://frostillic.us/blog/posts/2019/1/17/122236e1b44e3de285258385008366f4)
a while back. The structure of the Maven projects is similar to [this post](https://frostillic.us/blog/posts/d262b683-af1c-40ed-b603-ecf7ebd76934),
except I don't plan to target Domino's servlet container.

Specifically, the JEE components in use are:

- [CDI 2.0](https://www.slideshare.net/vjbug/introduction-to-cdi-42356193) -  . This is the "managed bean" layer and is used for normal bean-type purposes and also to inject context (for example, some classes in `core` expect the environment to provide an ODA `Session` instance for the current user, which a bean in `webapp-home` does)
- [JAX-RS 2.x](https://frostillic.us/blog/posts/2015/12/3/1CD1740C77996C8B85257F10006FEB21) . This is a newer revision of the same thing we've had in Domino thanks to the ExtLib for a while. All of the API endpoints are defined this way, as is the structure of the user-visible site
- [MVC 1.1](https://frostillic.us/blog/posts/2019/2/11/20140f04a50636d98525839e006e5f07) . This is a framework that sits on top of JAX-RS to render pages for JAX-RS endpoints. It's a pretty thin layer and is focused on letting you interpret the request, provide some context, and pass it off to a "view" of one sort or another.
- JSP. MVC supports a couple front-end frameworks, but JSP is built-in everywhere and works well with CDI. The syntax and concepts end up pretty similar to XPages, except that the page doesn't persist beyond the request - there's no partial refresh built in.

During development, I'll likely switch over to Jakarta EE 9, which switches the packages from `javax.*` to `jakarta.*`, but the technical
concepts are unchanged compared to JEE 8.

### Other Misc. Technologies

Currently, the app uses the OpenNTF Domino API and thereby Notes.jar. Hopefully, this will change down the line

### Developing

I've been developing using an Open Liberty 21.0.0.1 server using Eclipse's tooling on a Mac. Beyond just setting up the server, I've
created a `server.env` file with these settings (in addition to the keystore password that was added automatically):

```properties
Notes_ExecDirectory=/Applications/HCL Notes.app/Contents/MacOS
LD_LIBRARY_PATH=/Applications/HCL Notes.app/Contents/MacOS
DYLD_LIBRARY_PATH=/Applications/HCL Notes.app/Contents/MacOS
```

My `server.xml` looks like:

```xml
<server description="new server">
    <featureManager>
        <feature>localConnector-1.0</feature>
		<feature>jsp-2.3</feature>
		<feature>cdi-2.0</feature>
		<feature>jaxrs-2.1</feature>
		<feature>jsonb-1.0</feature>
		<feature>jsonp-1.1</feature>
		<feature>mpConfig-1.4</feature>
		<feature>beanValidation-2.0</feature>
	</featureManager>

    <httpEndpoint host="*" httpPort="21080" httpsPort="21443" id="defaultHttpEndpoint"/>

    <applicationManager autoExpand="true"/>

    <ssl id="defaultSSLConfig" trustDefaultCerts="true"/>

    <applicationMonitor updateTrigger="mbean"/>

    <webApplication contextRoot="openntfhome" id="webapp-home" location="openntfhome.war" name="webapp-home"/>
</server>
```

Additionally, to get to proprietary dependencies, open your `~/.m2/settings.xml` and add a server entry for `openntf-internal`, like:

```xml
<settings>
	<servers>
		<server>
			<id>openntf-internal</id>
			<username>(your OpenNTF username)</username>
			<password>(your OpenNTF password)</password>
		</server>
	</servers>
</settings>
```

### Deployment

My plan for deployment is to use the [Domino Open Liberty Runtime](https://openntf.org/main.nsf/project.xsp?r=project/Domino%20Open%20Liberty%20Runtime)
to run it attached to the normal Domino servers, so it will use the active server ID for local access. The runtime should be configured to use Java 11.