# Test map

Run `sh build.sh test` using JDK 17. The script discovers every root `*Test.java` class. The current suite contains 82 tests. Tests are deterministic where outcomes matter; random generation checks use fixed seeds. The test library is the course's committed `lib/student.jar`.

Methods without input are tested using valid stored values and invalid construction/state transitions, rather than inventing arguments for getters.

| Class / key methods | Normal case | Invalid / boundary case | Test files |
|---|---|---|---|
| Aircraft constructor and getters | Stored name, fuel and capacities | Null/blank name; negative, nonfinite, zero capacity, fuel over capacity | AircraftTest, IntegrationTest |
| hasEnoughFuel / canCarry | Below and exactly at limit | Insufficient fuel, excess passengers, negative or nonfinite request | AircraftTest, IntegrationTest |
| addFuel | Partial fill and exact capacity | Negative, zero, NaN, infinity, overfill; unchanged fuel | AircraftTest, IntegrationTest |
| Flight constructors and getters | Both constructor forms and all stored details | Null aircraft, invalid route/weather/number/time/passengers/fuel/status | FlightTest, IntegrationTest |
| getDispatchProblems / hasWeatherProblem / canDispatch | Clear flight and weather-only dispatch | Fuel/capacity blockers; all problem combinations; resolved flight | FlightTest, IntegrationTest, WeatherDispatchTest, StrategyTest |
| completeFlight | Safe departure, fuel consumption, stored lateness | Blocked, cancelled, delayed, already completed, negative arrival delay | FlightTest, IntegrationTest, FinalBuildTest |
| delayFlight / cancelFlight | Correct status | Already resolved flight cannot transition again | FlightTest, IntegrationTest |
| getFuelPrice / setFuelPrice | Default, $2.25 and $3 quotes | Zero, negative, nonfinite, above limit, resolved flight | FinalBuildTest |
| Dispatcher constructor and getters | Initial and updated state | Negative initial values | DispatcherTest |
| canAfford | Affordable, zero, exact balance | Too expensive, negative cost | DispatcherTest |
| applyOutcome | Cash, reputation and round effects; zero reputation floor | Null, insufficient cash, cash/reputation/round overflow; no partial mutation | DispatcherTest, IntegrationTest, FinalBuildTest |
| FlightOutcome constructor/getters | Stored changes, message and round flag | Null message | FlightOutcomeTest, FinalBuildTest |
| calculateOutcome | All four actions, both weather outcomes, fuel rounding/prices | Unknown/blank/null action, null flight/random, resolved or blocked flight, oversized fuel quote | FlightOutcomeTest, WeatherDispatchTest, FinalBuildTest |
| getDispatchNet / getLateCompensation | Exact manually checked examples and zero delay | Null flight, negative minutes | StrategyTest, FinalBuildTest |
| Options constructors | Standard menu, copied custom list | Null list, null/blank entry | OptionsTest, IntegrationTest, FinalBuildTest |
| getAvailableOptions | Normal, weather, fuel, capacity combinations | Null problem list | OptionsTest, StrategyTest, IntegrationTest |
| displayOptions | Numbered menu and empty list | Null list/entry | FinalBuildTest |
| isValidChoice / getChoice | Names, whitespace, numbers | Blank/null, negative, decimal, enormous number, unavailable name, null/empty list | OptionsTest, IntegrationTest, FinalBuildTest |
| FlightGame constructor | Fresh airline and input/random dependencies | Missing dependency or already-progressed airline | FinalBuildTest |
| generateFlight | 10,000 bounded scenarios, price diversity | Invalid candidate regeneration and bounded repeated failure | FlightGameTest, IntegrationTest, FinalBuildTest |
| processDecision | Refuel, dispatch, delay, cancel; exact accounting | Invalid input/flight, repeated action, fuel/capacity block, insufficient money, overflow | FlightGameTest, IntegrationTest, StrategyTest, FinalBuildTest |
| endGame | Ten resolved rounds and accurate final totals | Early call has no effect; repeated ending has no effect | FlightGameTest, IntegrationTest, FinalBuildTest |
| quitGame | Quit stops future changes; EOF exits cleanly | Invalid quit text does not stop game; repeat quit is harmless | FlightGameTest, IntegrationTest, FinalBuildTest |
| main / console loop | Startup, repeatable seeded play, full ten-round game | Invalid command-line arguments, invalid menu input, empty input stream | FlightGameTest, WeatherDispatchTest, FinalBuildTest |

## Live-demo smoke check

Run `sh build.sh demo`. Enter `wrong`, `Dispatch`, and `Quit` in that order. Confirm that invalid input keeps Flight 1 active, the dispatch produces an arrival result and updates cash/reputation, and Quit exits. A second run with the same seed and inputs should match. For a full ten-round completion check, enter Cancel ten times and verify that Flight 11 never appears.

GitHub Actions runs the full JUnit suite plus a console smoke test on every push and pull request. A green Actions result should be checked again after any final team changes.
