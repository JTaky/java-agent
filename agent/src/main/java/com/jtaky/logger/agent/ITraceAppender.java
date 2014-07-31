package com.jtaky.logger.agent;

import java.util.List;
import java.util.Stack;

public interface ITraceAppender {

    public void output(List<MethodCall> methodCalls);

}
