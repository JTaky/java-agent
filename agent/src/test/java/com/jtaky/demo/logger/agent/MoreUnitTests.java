package com.jtaky.demo.logger.agent;

import com.jtaky.logger.agent.MethodCall;
import com.jtaky.logger.agent.ParameterStorage;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

public class MoreUnitTests {

	class A { public String toString(){ return "A"; } }
	class B { public String toString(){ return "B"; } }
	class C { public String toString(){ return "C"; } }

	private double f(int a, double b, Runnable callback){
        try {
            return a + b;
        } finally {
            callback.run();
        }
    }

	private Object f(A a, B b){
		return new C();
	}
	
	@Test
	public void testPrimitiveTypes(){
        f(12, 0.33, () -> {
            List<MethodCall> methodCallList = ParameterStorage.findMethodCall("MoreUnitTests", "f");
            Assert.assertFalse("Did not find method call in stack", methodCallList.isEmpty());
            MethodCall fCall = methodCallList.get(0);

            Assert.assertEquals("com.jtaky.demo.logger.agent.MoreUnitTests",  fCall.className);
            Assert.assertEquals("f",  fCall.methodName);
            Assert.assertEquals(3,  fCall.args.size());
            Assert.assertEquals(12,  fCall.args.get(0));
            Assert.assertEquals(0.33,  (Double)fCall.args.get(1), 0.01);
        });
	}

    @Test
    public void testRecursionTypes(){
        f(12, 0.33, () ->
            f(11, 0.23, () -> {
                List<MethodCall> methodCallList = ParameterStorage.findMethodCall("MoreUnitTests", "f");
                Assert.assertEquals("Expected recursion is tracked", 2, methodCallList.size());
            })
        );
    }

	@Test
	public void testClasses(){
		Assert.assertNotNull(f(new A(), new B()));
	}

}
