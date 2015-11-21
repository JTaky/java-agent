package com.jtaky.logger.agent;

import java.util.List;
import java.util.Stack;

public interface ITraceAppender {

    void output(List<MethodCall> methodCalls, IContext context);

}
