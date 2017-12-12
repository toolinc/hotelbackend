package com.toolsoft.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A custom reentrant read/write lock that allows: 1) Multiple readers (when there is no writer).
 * Any thread can acquire multiple read locks (if nobody is writing). 2) One writer (when nobody
 * else is writing or reading). 3) A writer is allowed to acquire a read lock while holding the
 * write lock. 4) A writer is allowed to acquire another write lock while holding the write lock. 5)
 * A reader can not acquire a write lock while holding a read lock. <p> Use
 * ReentrantReadWriteLockTest to test this class. The code is modified from the code of Prof.
 * Rollins.
 */
public final class ReentrantReadWriteLock {

  private static final Logger log = Logger.getLogger(ReentrantReadWriteLock.class.getName());
  private final Map<Long, Integer> readLocks = new HashMap<>();
  private final Map<Long, Integer> writeLocks = new HashMap<>();

  /**
   * Return true if the current thread holds a read lock.
   */
  public synchronized boolean isReadLockHeldByCurrentThread() {
    return readLocks.containsKey(Thread.currentThread().getId()); // do not forget to change
  }

  /**
   * Return true if the current thread holds a write lock.
   */
  public synchronized boolean isWriteLockHeldByCurrentThread() {
    return writeLocks.containsKey(Thread.currentThread().getId());
  }

  /**
   * Non-blocking method that attempts to acquire the read lock. Returns true if successful. Checks
   * conditions (whether it can acquire the read lock), and if they are true, updates readers info.
   * <p> Note that if conditions are false (can not acquire the read lock at the moment), this
   * method does NOT wait, just returns false.
   */
  public synchronized boolean tryAcquiringReadLock() {
    if (writeLocks.isEmpty() || (writeLocks.size() == 1 && isWriteLockHeldByCurrentThread())) {
      if (isReadLockHeldByCurrentThread()) {
        readLocks
            .put(Thread.currentThread().getId(), readLocks.get(Thread.currentThread().getId()) + 1);
      } else {
        readLocks.put(Thread.currentThread().getId(), 1);
      }
      return true;
    }
    return false;
  }

  /**
   * Non-blocking method that attempts to acquire the write lock. Returns true if successful. Checks
   * conditions (whether it can acquire the write lock), and if they are true, updates writers info.
   * <p> Note that if conditions are false (can not acquire the write lock at the moment), this
   * method does NOT wait, just returns false.
   */
  public synchronized boolean tryAcquiringWriteLock() {
    if (((writeLocks.size() == 1 && isWriteLockHeldByCurrentThread()) || writeLocks.isEmpty())
        && readLocks.isEmpty()) {
      if (isWriteLockHeldByCurrentThread()) {
        writeLocks.put(Thread.currentThread().getId(),
            writeLocks.get(Thread.currentThread().getId()) + 1);
        return true;
      } else {
        writeLocks.put(Thread.currentThread().getId(), 1);
        return true;
      }
    }
    return false;
  }

  /**
   * Blocking method that will return only when the read lock has been acquired. Calls
   * tryAcquiringReadLock, and as long as it returns false, waits. Catches InterruptedException.
   */
  public synchronized void lockRead() {
    while (!tryAcquiringReadLock()) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        log.severe("Exception while running the LockRead: " + e);
      }
    }
  }

  /**
   * Releases the read lock held by the calling thread. Other threads might still be holding read
   * locks. If no more readers after unlocking, calls notifyAll().
   */
  public synchronized void unlockRead() {
    long currentThreadId = Thread.currentThread().getId();
    if (isReadLockHeldByCurrentThread()) {
      if (readLocks.get(currentThreadId) == 1) {
        readLocks.remove(currentThreadId);
      } else {
        readLocks.put(currentThreadId, readLocks.get(currentThreadId) - 1);
      }
    }
    if (readLocks.isEmpty()) {
      notifyAll();
    }
  }

  /**
   * Blocking method that will return only when the write lock has been acquired. Calls
   * tryAcquiringWriteLock, and as long as it returns false, waits. Catches InterruptedException.
   */
  public synchronized void lockWrite() {
    while (!tryAcquiringWriteLock()) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        log.severe("Exception while running the WriteLock: " + e);
      }
    }

  }

  /**
   * Releases the write lock held by the calling thread. The calling thread may continue to hold a
   * read lock. If the number of writers becomes 0, calls notifyAll.
   */
  public synchronized void unlockWrite() {
    long currentThreadId = Thread.currentThread().getId();
    if (isWriteLockHeldByCurrentThread()) {
      if (writeLocks.get(currentThreadId) == 1) {
        writeLocks.remove(currentThreadId);
      } else {
        writeLocks.put(currentThreadId, writeLocks.get(currentThreadId) - 1);
      }
    }
    if (writeLocks.isEmpty()) {
      notifyAll();
    }
  }
}
