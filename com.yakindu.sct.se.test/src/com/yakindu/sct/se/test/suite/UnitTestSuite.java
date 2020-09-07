package com.yakindu.sct.se.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.yakindu.sct.se.test.integration.ITComprehensiveExpressionTest;
import com.yakindu.sct.se.test.integration.ITCycleEliminationTest;
import com.yakindu.sct.se.test.integration.ITDefaultValueTest;
import com.yakindu.sct.se.test.integration.ITDivisionRoundTest;
import com.yakindu.sct.se.test.integration.ITPriorityTransitionTest;
import com.yakindu.sct.se.test.integration.ITSelfTransitionCycleElimination;
import com.yakindu.sct.se.test.integration.ITZeroDivisionTest;
import com.yakindu.sct.se.test.unit.ExpressionServiceTest;
import com.yakindu.sct.se.test.unit.ImmutableListTest;
import com.yakindu.sct.se.test.unit.NodeServiceTest;
import com.yakindu.sct.se.test.unit.NodeTest;

@RunWith(Suite.class)				
@Suite.SuiteClasses({				
  ImmutableListTest.class,
  NodeTest.class,
  NodeServiceTest.class,
  ExpressionServiceTest.class
  
})		

public class UnitTestSuite {
}