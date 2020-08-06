package org.kikkoman.parity.service;

/*
 * Interface to have two backing "implementations", that we want to have the same set of tests
 *  run against to ensure parity in our backend implementations.
 */
public interface Backend {

  BackendResult callOne();

  BackendResult callTwo();
}
