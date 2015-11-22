package com.github.jtaky.logger.agent;

import java.util.List;

public interface ITraceAppender {

    void output(List<MethodCall> methodCalls, IContext context);

}
