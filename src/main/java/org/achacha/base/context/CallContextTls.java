package org.achacha.base.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallContextTls {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallContextTls.class);

    protected static final ThreadLocal<CallContext> threadLocal = new ThreadLocal<>();

    public static final String BEAN_CONTEXTLOG = "context.log";

    // Spring initialized logging object
    protected static CallContextLog contextLog = new CallContextLog();

    /**
     * @return ContextLog for this instance
     */
    public static CallContextLog getContextLog() {
        return contextLog;
    }

    /**
     * Set Context on the current thread
     * @param context Context
     * @return Same CallContext passed back for convenience
     */
    public static CallContext set(CallContext context) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("+++set+++ Context="+System.identityHashCode(context)+"  URI="+context.getRequest().getRequestURI());
        }
        threadLocal.set(context);
        return context;
    }

    /**
     * Unset Context from the current call/thread
     */
    public static void unset() {
        CallContext context = get();
        if (LOGGER.isDebugEnabled()) {
            if (null != context)
                LOGGER.debug("---unset--- Context="+System.identityHashCode(context)+"  URI="+context.getRequest().getRequestURI()+"  execution_time="+(System.currentTimeMillis()-context.getCreatedTimeMillis())+"ms");
            else
                LOGGER.debug("---unset--- Context=null");
        }
        if (null != context)
            contextLog.process(context);

        threadLocal.remove();
    }

    /**
     * @return Context associated with this call/thread
     */
    public static CallContext get() {
        return threadLocal.get();
    }
}
