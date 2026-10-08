#!/bin/sh
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
	java_executable=$JAVA_HOME/bin/java
else
	java_executable=java
fi

java_version=$("$java_executable" -version 2>&1 | sed -n '1s/.*version "\([0-9][0-9]*\).*/\1/p')
if [ -z "$java_version" ]; then
	echo "Could not determine the Java version. Set JAVA_HOME to a JDK 21 or older." >&2
	exit 1
fi

if [ "$java_version" -gt 21 ]; then
	for candidate in "$HOME"/.sdkman/candidates/java/21* /usr/local/sdkman/candidates/java/21* /usr/lib/jvm/*21*; do
		if [ -x "$candidate/bin/java" ]; then
			JAVA_HOME=$candidate
			PATH=$JAVA_HOME/bin:$PATH
			export JAVA_HOME PATH
			java_executable=$JAVA_HOME/bin/java
			java_version=$("$java_executable" -version 2>&1 | sed -n '1s/.*version "\([0-9][0-9]*\).*/\1/p')
			break
		fi
	done
fi

if [ "$java_version" -gt 21 ]; then
	echo "TeaVM 0.9.2 requires JDK 21 or older. Set JAVA_HOME to a compatible JDK." >&2
	exit 1
fi

chmod +x gradlew
./gradlew generateJavascript