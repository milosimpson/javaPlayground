Parity Testing
==============

Want the ability to a "suite" of tests against two different "backends".

Ideally
    
1. tests are written once
1. developer can run a single test from inside Intellij to debug
   
   1. this might require some additional setup beyond right-click run, as dev would minimally need to “pick” which backend should be hit.

1. Ideally we start a backend, run “all” the tests, and then shut it down.
1. Ideally, Ideally both backends can run at the same time, and tests run in “parallel”.
