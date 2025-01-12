package com.ctrip.xpipe.redis.checker.healthcheck.stability;

import com.ctrip.xpipe.redis.checker.config.CheckerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lishanglin
 * date 2022/8/8
 */
@Component
public class DefaultStabilityHolder implements StabilityHolder {

    private StabilityInspector inspector;

    private CheckerConfig config;

    private boolean stable;

    private long expiredAtMill;

    @Autowired
    public DefaultStabilityHolder(StabilityInspector inspector, CheckerConfig config) {
        this.inspector = inspector;
        this.config = config;
        this.stable = true;
        this.expiredAtMill = 0L;
    }

    @Override
    public boolean isSiteStable() {
        if (expiredAtMill > System.currentTimeMillis()) {
            return stable;
        }
        Boolean configStable = config.getSiteStable();
        if (null != configStable) return configStable;

        return inspector.isSiteStable();
    }

    @Override
    public Desc getDebugDesc() {
        Desc desc = new Desc();
        desc.siteStable = inspector.isSiteStable();
        desc.configStable = config.getSiteStable();
        desc.staticStable = stable;
        desc.staticExpiredAt = expiredAtMill;
        return desc;
    }

    @Override
    public void setStaticStable(boolean stable, int ttl) {
        this.stable = stable;
        this.expiredAtMill = System.currentTimeMillis() + (ttl * 1000);
    }

    @Override
    public void useDynamicStable() {
        this.expiredAtMill = 0;
    }

    public static class Desc {
        public Boolean siteStable;
        public Boolean configStable;
        public Boolean staticStable;
        public Long staticExpiredAt;
    }
}
