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

public class ParamHooksTest {

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

    static class F {
        int a;
        public F(int a, String someStr, Runnable callback){
            try {
                this.a = a;
            } finally {
                callback.run();
            }
        }
    }
	
	@Test
	public void testPrimitiveTypes(){
        f(12, 0.33, () -> {
            List<MethodCall> methodCallList = ParameterStorage.findMethodCall(getClass().getName(), "f");
            Assert.assertFalse("Did not find method call in stack", methodCallList.isEmpty());

            MethodCall fCall = methodCallList.get(0);
            Assert.assertEquals("com.jtaky.demo.logger.agent.ParamHooksTest",  fCall.className());
            Assert.assertEquals("f",  fCall.methodName());
            Assert.assertEquals(3,  fCall.args.size());
            Assert.assertEquals(12,  fCall.args.get(0));
            Assert.assertEquals(0.33,  (Double)fCall.args.get(1), 0.01);
        });
	}

    @Test
    public void testConstructorParameters(){
        new F(12, "some string", () -> {
            List<MethodCall> methodCallList = ParameterStorage.findMethodCall(getClass().getName() + "$F", ParameterStorage.CONSTRUCTOR_NAME);
            Assert.assertFalse("Did not find method call in stack", methodCallList.isEmpty());

            MethodCall fCall = methodCallList.get(0);
            Assert.assertEquals("com.jtaky.demo.logger.agent.ParamHooksTest$F",  fCall.className());
            Assert.assertEquals("<init>",  fCall.methodName());
            Assert.assertEquals(3,  fCall.args.size());
            Assert.assertEquals(12,  fCall.args.get(0));
            Assert.assertEquals("some string",  fCall.args.get(1));
        });
    }

    @Test
    public void testRecursionTypes(){
        f(12, 0.33, () ->
            f(11, 0.23, () -> {
                List<MethodCall> methodCallList = ParameterStorage.findMethodCall(getClass().getName(), "f");
                Assert.assertEquals("Expected recursion is tracked", 2, methodCallList.size());
            })
        );
    }

	@Test
	public void testClasses(){
		Assert.assertNotNull(f(new A(), new B()));
	}

}
