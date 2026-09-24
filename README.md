# Hokie Air: Cleared for Departure

A Java console game about managing ten airline flights, cash, and reputation.
Requires JDK 17; the included `lib/student.jar` is needed for JUnit tests.

**Eclipse:** Import → General → Existing Projects into Workspace, select this folder, and use JavaSE-17.
Run `FlightGame.java` with Run As → Java Application; run the project with Run As → JUnit Test (JUnit 4).

**Terminal:** Run these commands from the repository folder:
```sh
mkdir -p bin
javac -cp lib/student.jar -d bin *.java
java -cp bin FlightGame
java -cp "bin:lib/student.jar" org.junit.runner.JUnitCore AircraftTest DispatcherTest FlightTest FlightOutcomeTest OptionsTest FlightGameTest IntegrationTest WeatherDispatchTest StrategyTest FinalBuildTest
```
