package com.jtaky.logger.agent;

public interface IContext {

    class ExceptionContext implements IContext {

        private final Exception e;

        public ExceptionContext(Exception e){
            this.e = e;
        }

        @Override
        public String toString(){
            return e.getMessage();
        }
    }

    class MethodContext implements IContext {

        public final MethodCall methodCall;

        public MethodContext(MethodCall methodCall){
            this.methodCall = methodCall;
        }

        @Override
        public String toString(){
            return methodCall.toString();
        }

    }

}
