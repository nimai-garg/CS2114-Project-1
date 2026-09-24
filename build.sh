#!/bin/sh
set -eu
cd "$(dirname "$0")"
mode=${1:-test}
if [ "$#" -gt 0 ]; then shift; fi
case "$mode" in
    compile|run|test|demo) ;;
    *) echo 'Usage: sh build.sh [compile|run|test|demo] [game arguments]' >&2; exit 2 ;;
esac
java_bin=
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    java_bin="$JAVA_HOME/bin"
else
    for candidate in /Library/Java/JavaVirtualMachines/*/Contents/Home/bin /Applications/Eclipse.app/Contents/Eclipse/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.macosx.*_17.*/jre/bin; do
        if [ -x "$candidate/java" ] && "$candidate/java" -version 2>&1 | head -1 | grep -q '"17\.'; then
            java_bin=$candidate
            break
        fi
    done
fi
if [ -n "$java_bin" ]; then
    java_cmd="$java_bin/java"
    javac_cmd="$java_bin/javac"
else
    java_cmd=java
    javac_cmd=javac
fi
if ! "$java_cmd" -version 2>&1 | head -1 | grep -q '"17\.'; then
    echo 'Use JDK 17. Set JAVA_HOME to its installation folder and try again.' >&2
    exit 1
fi
if [ ! -f lib/student.jar ]; then
    echo 'Missing lib/student.jar. Restore the course library included in the repository.' >&2
    exit 1
fi
mkdir -p bin
"$javac_cmd" -Xlint:all -cp lib/student.jar -d bin ./*.java
case "$mode" in
    compile) echo 'Compilation passed.' ;;
    run) exec "$java_cmd" -cp bin FlightGame "$@" ;;
    demo) exec "$java_cmd" -cp bin FlightGame --seed 2114 ;;
    test)
        test_classes=
        for test_file in ./*Test.java; do
            test_name=${test_file#./}
            test_classes="$test_classes ${test_name%.java}"
        done
        exec "$java_cmd" -cp bin:lib/student.jar org.junit.runner.JUnitCore $test_classes
        ;;
esac
