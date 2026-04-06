#!/usr/bin/env sh
# -----------------------------------------------------------------------------
# Gradle start up script for UN*X
# -----------------------------------------------------------------------------

##############################################################################
# Bootstrap the Gradle runtime.
##############################################################################

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn() {
    if [ "${INSIDE_EMACS:-}" ] ; then
        printf "WARNING: %s\n" "$*"
    else
        printf "%s\n" "$*" >&2
    fi
}

die() {
    warn "$*"
    exit 1
}

# OS specific support. $var _must_ be set to either true or false.
cygwin=false
msys=false
case "$(uname)" in
    CYGWIN* ) cygwin=true ;;
    MINGW* ) msys=true ;;
esac

# Attempt to set APP_HOME
SCRIPT="${0}"
while [ -h "$SCRIPT" ] ; do
    ls=$(ls -ld "$SCRIPT")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        SCRIPT="$link"
    else
        SCRIPT="$(dirname "$SCRIPT")/$link"
    fi
done

APP_HOME="$(dirname "$SCRIPT")"
cd "$APP_HOME" || die "Could not cd to $APP_HOME"
APP_HOME="$(pwd -P)"

# Determine Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="$(command -v java 2>/dev/null)"
fi

if [ -z "$JAVA_CMD" ] || [ ! -x "$JAVA_CMD" ] ; then
    die "Java is not installed or JAVA_HOME is not set correctly."
fi

# Setup the Gradle user home if not already set.
if [ -z "$GRADLE_USER_HOME" ] ; then
    GRADLE_USER_HOME="$HOME/.gradle"
fi

GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
GRADLE_WRAPPER_PROPERTIES="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$GRADLE_WRAPPER_JAR" ] ; then
    die "Gradle wrapper jar not found: $GRADLE_WRAPPER_JAR"
fi

# Build command
exec "$JAVA_CMD" -cp "$GRADLE_WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
