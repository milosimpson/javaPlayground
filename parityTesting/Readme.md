## Purpose of this module

Figure out how to build a single "set" of tests that can run against different Api backends.

Ideally
    
1. tests are written once
1. developer can run a single test from inside Intellij to debug
   
   1. this might require some additional setup beyond right-click run, as dev would minimally 
   need to “pick” which backend should be hit.

1. Ideally we start a backend, run “all” the tests, and then shut it down.
1. Ideally-Ideally both backends can run at the same time, and tests run in “parallel”.

### Goal 1 : Independent Reusable Tests

This is where each test class starts an Api service, tests it, and then shuts it down. 
Difference is that the "backend" can be driven by a config param in the testNg suite config file.

This is possible (and hinted at in TestNg docs) using "Parameters" in the TestNg suite
configuration.

https://testng.org/doc/documentation-main.html#parameters

In a single suite.xml you can define two <test> entries that run across the same set of 
test classes, but have different Parameters.

See [testNg suite config that does this](testSuites/backend-unit-testSuite.xml)

This doesn't actually fire up an Api, but it is still indicative of the approach.


#### Developer Usability

Tests can be annotated with "default" values for the Parameters, so that developers 
can still right-click run the tests directly from Intellij.

Dev can temporarily edit the "default" value and run to test non-default Api backends.

See [SimpleBackendUnitTest](src/test/java/org/kikkoman/parity/unit/SimpleBackendUnitTest.java)

### Goal 2 : Api setup/teardown separate from Tests

Api setup and teardown happens once, and a set of tests run against that Api.

Tests have to be written to be able to coexist, either they work on unique data or clean up after 
themselves.

This requires two testNg suite config xml files, due to the way that testNg properties work for 
Suite level annotations @BeforeSuite and @AfterSuite.

See [suite A](testSuites/backend-A-integration-testSuite.xml) and [suite B](testSuites/backend-B-integration-testSuite.xml)

If you try to have a two <test> entries with properties defined inside them in a single suite 
config file, it seems like "suite level properties" / what @BeforeSuite annotated methods see
is are just the **first** <test> config.

See [example of busted/non-functional setup](testSuites/non-functional-integration-testSuite.xml)

#### Developer Usability

Because Tests no longer start stop tests, this gets tricky for when a developer wants to run
a single test from the IDE.

Two ways to tackle this
1) Edit suite config file to run Suite and test class, right-click run suite config file from Intellij.
   
   See the commented out <classes> definition in [suite A](testSuites/backend-A-integration-testSuite.xml) 
   
2) do something fancy in the tests, such that they can tell they are not being run as part of a Suite.

   