# dbLearn* - Installation

## Building the web application from source



### 0. Get the source:

```bash
git clone https://github.com/ajanovski/dbLearnStar.git
```



### 1. Setup a CAS instance for authentication.

The web application is configured to use CAS for authentication. If you don't have access to a production/development CAS server to use for testing purposes, you can run your own development CAS instance in a container by following these instructions.

First, go into the directory for a CAS container example.

```bash
cd dbLearnStar-other/container_cas_example/
```

Then, edit cas.properties and configure a list of experimental users that you will use to test authentication in the web application.

```bash
$EDITOR config/cas.properties 
```

Next, setup the podman/docker command for starting the container - modify the container name, modify the port and the local directories that are mapped to the container.

```bash
$EDITOR webstart.sh
```

Finally, run the CAS container. Tested with podman, but the command should be 100% compatibile with docker too.

```bash
./webstart.sh
```

Run podman ps or docker ps to view the list of running containers, to check if the startup was successful.

```bash
podman ps
```



### 2. Setup a DBMS instance for the main database.

The web application is built and tested with PostgreSQL for the main database. It should be possible to run against another DBMS, but some source code modifications will be required. If you don't have access to a production/development PostgreSQL server, you can run your own PostgreSQL instance in a container by following these instructions. 

First, go into the directory for the DB container example.

```bash
cd dbLearnStar-other/container_maindb_example/
```

Next, setup the docker/podman command for starting the container - modify the port and the local directories that are mapped to the container if you need to do so.

```bash
$EDITOR dbstart.sh
```

Next, run the DB container. Tested with podman, but the command should be 100% compatibile with docker too.

```bash
./dbstart.sh
```

Run podman ps or docker ps to view the list of running containers, to check if the startup was successful.

```bash
podman ps
```

Finally, enter console within the container to initialize the MAIN DB.

```bash
podman exec -u postgres -it dblearnstar_maindb bash
```

```sql
psql
CREATE USER dblearnstar_owner ENCRYPTED PASSWORD 'DBLEARNSTAR_OWNER_PASSWORD';
CREATE DATABASE dblearnstar ENCODING 'UTF-8' OWNER DBLEARNSTAR_OWNER;
\c dblearnstar
CREATE SCHEMA dblearnstar AUTHORIZATION dblearnstar_owner;
```



### 3. Build and run from source

First, configure a maven build profile.

Edit the local maven settings file:

```bash
$EDITOR ~/.m2/settings.xml
```

If you don't have this file, setup an initial file, like documented here: https://maven.apache.org/settings.html

Under the *profiles* section add a new build profile called development-dbLearnStar, setting up for your local needs.


```xml
<profiles>
	<profile>
		<id>development-dbLearnStar-example</id>
		<activation>
			<property>
				<name>env</name>
				<value>development-dbLearnStar-example</value>
			</property>
		</activation>
		<properties>
			<!-- CAS (Apereo Central Authentication Service) SETUP -->

			<!-- CAS URL, without the /cas suffix from step 1. -->
			<cas.server>CAS URL</cas.server>
			<!-- the address of this webapp, where CAS will redirect after a successful login. -->
			<app.server>APPLICATION_URL_AFTER_LOGIN</app.server>
			<logout.redirectToServer>APPLICATION_URL_AFTER_LOGOUT</logout.redirectToServer>

			<!-- MAIN DATABASE SETUP -->

			<!-- This is the database containing the main tables used by the web application, from step 2. -->
			<!-- Tested and built against PostgreSQL. 
			Could work with another DBMS with few changes in the sources.-->

			<jdbc.url>jdbc:postgresql://address:port/MAINDB_NAME</jdbc.url>
			<jdbc.username>MAINDB_USERNAME</jdbc.username>
			<jdbc.password>MAINDB_PASSWORD</jdbc.password>
			<jdbc.default_schema>dbLearnStar</jdbc.default_schema>

			<hib.hbm2ddlauto>update</hib.hbm2ddlauto>

			<tapestry.hmac-passphrase>CHANGE_IT!</tapestry.hmac-passphrase>

			<production.mode>false</production.mode>

			<logging.path>/tmp</logging.path>
			<logging.rootCategory>INFO,Console</logging.rootCategory>

			<!-- SETUP DIRECTORIES CONTAINING ADDITIONAL FILES-->
			
			<!-- Main directory-->
			<additionalFiles.path>/.../.../dbLearnStar_files</additionalFiles.path>
			<!-- Subdirectories of the main directory, setup as relative paths-->
			<xls.path>/excel</xls.path>
			<fonts.path>/fonts</fonts.path>
			<data.path>/data</data.path>
			<upload.path>/upload</upload.path>
			<upload.path.submissions>/submissions</upload.path.submissions>



			<!-- STUDENT DATABASES SETUP -->

			<!-- Here we setup how the system connects to databases developed by students, as part of an automated assessments.-->
			<!-- Tested and built against PostgreSQL. 
			Could work with another DBMS with few changes in the sources.-->

			<studentdbs.jdbc.driver>org.postgresql.Driver</studentdbs.jdbc.driver>
			<studentdbs.jdbc.url>jdbc:postgresql://address:port</studentdbs.jdbc.url>
			<studentdbs.jdbc.username></studentdbs.jdbc.username>
			<studentdbs.jdbc.password></studentdbs.jdbc.password>
			<studentdbs.jdbc.default_schema>tables</studentdbs.jdbc.default_schema>



			<!-- EPRMS DATABASE SETUP -->

			<!-- Here we setup how the system connects to the EPRMS database, if we with to integrate EPRMS. -->
			<!-- Tested and built against PostgreSQL. 
			Could work with another DBMS with few changes in the sources.-->

			<eprms.jdbc.driver>org.postgresql.Driver</eprms.jdbc.driver>
			<eprms.jdbc.url>jdbc:postgresql://address:port/EPRMSDB_NAME</eprms.jdbc.url>
			<eprms.jdbc.username>EPRMSDB_USERNAME</eprms.jdbc.username>
			<eprms.jdbc.password>EPRMSDB_PASS</eprms.jdbc.password>
			<eprms.jdbc.default_schema>eprms_main</eprms.jdbc.default_schema>
		</properties>
	</profile>
</profiles>
```

The latest version is tested with Java 21 and Maven 3.9.9.

Build and install the model sub-project first. The jar will be installed in the local maven repo.

```bash
cd dbLearnStar-model/
mvn -P development-dbLearnStar-example clean install
cd ..
```

After the model is successfully built, one can build and run the web application.

Setup the port on which the app runs here:

```bash
cd dbLearnStar-webApp/
$EDITOR src/main/resources/application.yml
```

```bash
mvn -P development-dbLearnStar-example clean spring-boot:run
```

If the build is successful, the web application will be running on the configured port under localhost.

You can test it at: [http://localhost:8081](http://localhost:8081) (change to configured port)

Before first run, you need to register the account of the admin user and username in the person table in the MAIN DB.

If all is setup properly the browser will automatically redirect you to the CAS instance at [http://localhost:8080](http://localhost:8080) (or configured address and port).

Before authentication you need an initial set of users in the MAIN DB.

```sql
insert into dblearnstar.roles (name) values ('ADMINISTRATOR'),('INSTRUCTOR');
insert into dblearnstar.person (user_name, first_name, last_name) values ('admin', 'Administrator', 'Administrator');
insert into dblearnstar.person_role (person_id, role_id) values ((select person_id from dblearnstar.person where user_name='admin'), (select role_id from dblearnstar.roles where name='ADMINISTRATOR'));
```
Then you can authenticate. Enter a user/password configured in your CAS instance in step 1.

If authentication is successful you will be redirected back to the web aplication at [http://localpost:8081](http://localpost:8081) (or configured address and port).

The webaplication main screen will welcome you.
