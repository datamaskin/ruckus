#!/bin/bash
PS3="Choose(1-5):   "
echo "Select mode to start the applicaiton:"
select name in "development" "development (full refresh)" "optimized" "optimized (full refresh)" "simulator (dev)" "simulator (optimized/build)"  "exit"
do
        break
done


if [[ $name == "optimized" ]]
then
	node public/r.js -o public/app.build.js
	cp -R target/web/public/main/dusttemplates public/build/
	activator -Dconfig.file=conf/application.build.conf
	run
fi

if [[ $name == "optimized (full refresh)" ]]
then
	mysql -u ruckus -pruckus -e "DROP DATABASE ruckus;"
	mysql -u ruckus -pruckus -e "CREATE DATABASE ruckus;"
	node public/r.js -o public/app.build.js
	cp -R target/web/public/main/dusttemplates public/build/
	sbt clean update compile
	activator -Dconfig.file=conf/application.build.conf
	run
fi

if [[ $name == "development" ]]
then
	activator -Dconfig.file=conf/application.dev.conf
	run
fi

if [[ $name == "development (full refresh)" ]]
then
	echo "Removing database."
	mysql -u ruckus -pruckus -e "DROP DATABASE ruckus;"
	echo "Creating new database."
	mysql -u ruckus -pruckus -e "CREATE DATABASE ruckus;"
	sbt clean update compile
	echo "Starting Play."
	activator -Dconfig.file=conf/application.dev.conf
	run
fi

if [[ $name == "simulator (dev)" ]]
then
	activator -Dconfig.file=conf/application.dev.conf -Dsimulator=true
	run
fi

if [[ $name == "simulator (optimized/build)" ]]
then
	#echo "Removing database."
	#mysql -u ruckus -pruckus -e "DROP DATABASE ruckus;"
	#echo "Creating new database."
	#mysql -u ruckus -pruckus -e "CREATE DATABASE ruckus;"

	node public/r.js -o public/app.build.js
	cp -R target/web/public/main/dusttemplates public/build/
	sbt clean update compile
	echo "Starting Play."
	activator -Dconfig.file=conf/application.build.conf -Dsimulator=true
	run
fi
