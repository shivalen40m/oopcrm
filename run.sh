#!/bin/bash
# Compile and run DMS application

cd src
javac -cp ".:../lib/postgresql-42.7.10.jar" com/dms/**/*.java com/dms/*.java
java -cp ".:../lib/postgresql-42.7.10.jar" com.dms.Main

javac -cp "src:lib/postgresql-42.7.10.jar" -d src src/com/dms/**/*.java src/com/dms/*.java 2>&1
java -cp "src:lib/postgresql-42.7.10.jar" com.dms.Main