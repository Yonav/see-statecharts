package com.yakindu.sct.se.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.yakindu.sct.se.test.integration.ITComprehensiveExpressionTest;
import com.yakindu.sct.se.test.integration.ITCycleEliminationTest;
import com.yakindu.sct.se.test.integration.ITDefaultValueTest;
import com.yakindu.sct.se.test.integration.ITDivisionRoundTest;
import com.yakindu.sct.se.test.integration.ITPriorityTransitionTest;
import com.yakindu.sct.se.test.integration.ITSelfTransitionCycleElimination;
import com.yakindu.sct.se.test.integration.ITThoroughStatechartTest;
import com.yakindu.sct.se.test.integration.ITZeroDivisionTest;

@RunWith(Suite.class)				
@Suite.SuiteClasses({	
	  ITComprehensiveExpressionTest.class,
	  ITCycleEliminationTest.class,
	  ITDefaultValueTest.class,			
	  ITDivisionRoundTest.class,
	  ITPriorityTransitionTest.class,
	  ITSelfTransitionCycleElimination.class,
	  ITZeroDivisionTest.class,
	  ITThoroughStatechartTest.class
  
})		

public class IntegrationTestSuite {
}