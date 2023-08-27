package cn.indix.hfx.demo.util;

import lombok.Data;
import lombok.SneakyThrows;
import redis.clients.jedis.params.SetParams;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author hfx
 * @version 1.0.0
 * @Description: 基于Redis的分布式锁
 * @Date: 2023/08/11 14:46
 */
@Data
public class RedisLock implements Lock {

    /**
     * 分布式锁key
     */
    private String key;

    /**
     * 过期时间
     */
    private long expire;

    /**
     * 锁拥有者标识
     */
    private String exclusiveOwner;

    private Thread daemon;

    private final long THREAD_SLEEP_MILLS_AFTER_ACQUIRE_FAIL = 30;


    public RedisLock(String key, long expire) {
        this.key = key;
        this.expire = expire;
        this.exclusiveOwner = UUID.randomUUID().toString().replace("-", "");
    }

    @SneakyThrows
    @Override
    public void lock() {
        while (true) {
            if (tryAcquire()) {
                afterLocked();
                return;
            }
            Thread.sleep(THREAD_SLEEP_MILLS_AFTER_ACQUIRE_FAIL);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (true) {
            if (tryAcquire()) {
                afterLocked();
                return;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Thread.sleep(THREAD_SLEEP_MILLS_AFTER_ACQUIRE_FAIL);
        }
    }

    @Override
    public boolean tryLock() {
        if (tryAcquire()) {
            afterLocked();
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long startTime = System.nanoTime();
        long timeoutTime = startTime + unit.toNanos(time);
        while (System.nanoTime() < timeoutTime) {
            if (tryAcquire()) {
                afterLocked();
                return true;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(THREAD_SLEEP_MILLS_AFTER_ACQUIRE_FAIL) < timeoutTime) {
                Thread.sleep(THREAD_SLEEP_MILLS_AFTER_ACQUIRE_FAIL);
            }
        }
        return false;
    }

    @Override
    public void unlock() {
        if (!RedisUtil.exists(key)) {
            return;
        }
        String exclusiveOwner = RedisUtil.get(key);
        if (Objects.equals(exclusiveOwner, this.exclusiveOwner)) {
            // 只能当锁是被当前线程持有时，才可以释放
            if (!RedisUtil.del(key)) {
                throw new RuntimeException("释放锁失败");
            } else {
                daemon.interrupt();
            }
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    private boolean tryAcquire() {
        // SET key 1 NX EX expire
        SetParams setParams = SetParams.setParams();
        setParams.nx();
        setParams.ex(expire);
        return RedisUtil.set(key, exclusiveOwner, setParams);
    }

    private void afterLocked() {
        // 每个分布式锁都需要起一个守护进程 浪费资源
        RedisLockDaemonRunner runner = new RedisLockDaemonRunner(this);
        daemon = new Thread(runner, String.format("RedisLock-%s-daemon", this.getExclusiveOwner()));
        daemon.setDaemon(true);
        daemon.start();
    }

}
