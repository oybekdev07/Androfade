#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

WHERE=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
WHERE="$( echo "$BASH_SOURCE" | sed 's|/[^/]*$||;s|^\./||' )"

if [ -z "$BASH_SOURCE" ]; then
    if [ -L "$0" ]; then
        WHERE="$( cd -P "$( dirname \"$0\" )" && pwd )"
    else
        WHERE="$( cd -P \"$(dirname \"$0\")\" && pwd )"
    fi
fi

DEFAULT_JVM_OPTS=''-Xmx64m'' -Xms64m''
JAVA_OPTS="$DEFAULT_JVM_OPTS"

if command -v java >/dev/null 2>&1 ; then
    if ! command -v >/dev/null 2>&1 ; then
        javaExecutable="$(which java)"
    else
        javaExecutable="$(readlink -f "$(which java)")"
    fi
    javaHome="$(dirname "$javaExecutable")"
    javaHome=$(expr "$javaHome" : '\(.*\)/bin$') 2>/dev/null || javaHome="$javaHome"
    JAVA_HOME="$javaHome"
    export JAVA_HOME
fi

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        exec "$JAVA_HOME/jre/sh/java" $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
    else
        exec "$JAVA_HOME/sh/java" $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
    fi
else
    exec java $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
fi
