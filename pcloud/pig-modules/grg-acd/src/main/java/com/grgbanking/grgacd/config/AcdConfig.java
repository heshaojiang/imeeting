package com.grgbanking.grgacd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wjqiu
 * @since 2019-04-21
 */

@Component
public class AcdConfig {
    @Value("${acd.caller.timeout.makecall:180}")
    private long caller_makecall_timeout;

    @Value("${acd.caller.timeout.pending:60}")
    private long caller_pending_timeout;

    @Value("${acd.caller.timeout.ringing:60}")
    private long caller_ringing_timeout;

    public long getCaller_makecall_timeout() {
        return caller_makecall_timeout;
    }
    public long getCaller_pending_timeout() {
        return caller_pending_timeout;
    }
    public long getCaller_ringing_timeout() {
        return caller_ringing_timeout;
    }

}
