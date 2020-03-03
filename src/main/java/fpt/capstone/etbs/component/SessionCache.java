package fpt.capstone.etbs.component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Component;

@Component
public class SessionCache {

  private static SessionCache instance;
  private static Map<String, String> sessions = new ConcurrentHashMap<>();
  private static Set<String> writeLockingKeys = ConcurrentHashMap.newKeySet();
  private final ReadWriteLock LOCK = new ReentrantReadWriteLock();
  private final Lock WRITE_LOCK = LOCK.writeLock();
  private final Condition WRITE_LOCK_CONDITION = WRITE_LOCK.newCondition();

  private SessionCache() {
  }

  public static SessionCache getInstance() {
    if (instance == null) {
      synchronized (SessionCache.class) {
        if (instance == null) {
          instance = new SessionCache();
          return instance;
        }
      }
    }
    return instance;
  }


  public String get(String key) {
    if (writeLockingKeys.contains(key)) {
      WRITE_LOCK.lock();
      try {
        if (writeLockingKeys.contains(key)) {
          WRITE_LOCK_CONDITION.await();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        WRITE_LOCK.unlock();
      }
    }
    return sessions.computeIfAbsent(key, k -> null);
  }

  public void set(String key, String value) {
    WRITE_LOCK.lock();
    try {
      writeLockingKeys.add(key);
      sessions.compute(key, (k, v) -> value);
      writeLockingKeys.remove(key);
    } finally {
      WRITE_LOCK_CONDITION.signalAll();
      WRITE_LOCK.unlock();
    }
  }

  public void remove(String key) {
    WRITE_LOCK.lock();
    try {
      writeLockingKeys.add(key);
      sessions.remove(key);
      writeLockingKeys.remove(key);
    } finally {
      WRITE_LOCK_CONDITION.signalAll();
      WRITE_LOCK.unlock();
    }
  }

  public void cleanup() {
    WRITE_LOCK.lock();
    try {
      sessions.clear();
    } finally {
      WRITE_LOCK.unlock();
    }
  }


}
