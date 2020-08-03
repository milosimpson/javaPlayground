package org.kikkoman.parity;

import org.testng.IConfigurationListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ConsoleListener implements ITestListener, ISuiteListener,
      IConfigurationListener {

  // TEST Suite
  @Override
  public void onStart(final ISuite suite) {
    System.out.println("[TEST SUITE STARTING] : " + suite.getName()
          + " with parallelism: " + suite.getParallel());
  }

  // TEST Run (suite can can multiple runs)
  @Override
  public void onStart(final ITestContext context) {
    System.out.println("[TEST RUN STARTING] : " + context.getName());
  }

  @Override
  public void onFinish(final ISuite suite) {
    System.out.println("[TEST SUITE FINISHED] : " + suite.getName());
  }

  @Override
  public void onFinish(final ITestContext context) {
    System.out.println("[TEST RUN FINISHED] : " + context.getName());
  }


  // TEST Cases
  @Override
  public void onTestStart(final ITestResult result) {
    System.out.println("[TEST CASE STARTING] : " + result.getTestClass().getName()
          + "." + result.getName());
  }

  @Override
  public void onTestSuccess(final ITestResult result) {
    long runtime = result.getEndMillis() - result.getStartMillis();

    System.out.println("[TEST CASE FINISHED] : " + result.getTestClass().getName()
          + "." + result.getName() + " took " + runtime + "ms");
  }

  @Override
  public void onTestFailure(final ITestResult result) {
    System.out.println("[TEST CASE FAILED] : " + result.getTestClass().getName()
          + "." + result.getName());
  }

  @Override
  public void onTestSkipped(final ITestResult result) {
    System.out.println("[TEST CASE SKIPPED] : " + result.getTestClass().getName()
          + "." + result.getName());
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    System.out.println("[TEST CASE FAILED] but within success percentage : "
          + result.getTestClass().getName() + "." + result.getName());
  }

  @Override
  public void onConfigurationSuccess(final ITestResult itr) {
    long runtime = itr.getEndMillis() - itr.getStartMillis();

    System.out.println("[TEST CASE CONFIG] : "
          + itr.getTestClass().getName() + "." + itr.getName() + " took " + runtime + "ms");
  }

  @Override
  public void onConfigurationFailure(final ITestResult itr) {
    System.out.println("[TEST CASE CONFIG FAIL] : "
          + itr.getTestClass().getName() + "." + itr.getName());
  }

  @Override
  public void onConfigurationSkip(final ITestResult itr) {
    System.out.println("[TEST CASE CONFIG SKIPPED] : "
          + itr.getTestClass().getName() + "." + itr.getName());
  }
}