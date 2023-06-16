#!/bin/sh

# build need Apache Maven
MAVEN_EXISTS=$(which mvn)
if [ -z "$MAVEN_EXISTS" ]; then
    echo "Please install apache maven"
    exit 1
fi
JAVA_EXISTS=$(which java)
if [ -z "$JAVA_EXISTS" ]; then
    echo "Please install at least OpenJDK 8"
    exit 1
fi

mvn clean
mvn verify $@

if [ $? -eq 0 ]; then
  JAR="compiler/target/wnf-compiler.jar"
  if [[ -e ${JAR} ]]; then
    echo "Build successful"
  else
    echo "Build failed, jar not found"
  fi
else
  echo "Build failed."
  exit 1
fi
