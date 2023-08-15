package cn.indix.hfx.demo.util;

import org.springframework.util.StringUtils;

/**
 * @author hfx
 * @version 1.0.0
 * @Description: redis锁守护进程
 * @Date: 2023/08/15 16:20
 */
public class RedisLockDaemonRunner implements Runnable {

    private RedisLock redisLock;

    private final long EXPIRE_MILLS = 1000;

    public RedisLockDaemonRunner(RedisLock redisLock) {
        this.redisLock = redisLock;
    }

    @Override
    public void run() {
        if (redisLock == null || StringUtils.isEmpty(redisLock.getKey())) {
            return;
        }
        while (true) {
            if (Thread.interrupted()) {
                return;
            }
            if (RedisUtil.expireTime(redisLock.getKey()) <= EXPIRE_MILLS) {
                RedisUtil.set(redisLock.getKey(), EXPIRE_MILLS);
                try {
                    Thread.sleep(EXPIRE_MILLS - 10);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

}
