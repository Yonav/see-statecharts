package com.yakindu.sct.se.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)				
@Suite.SuiteClasses({	
	  IntegrationTestSuite.class,
	  UnitTestSuite.class
  
})		

public class OverallTestSuite {
}