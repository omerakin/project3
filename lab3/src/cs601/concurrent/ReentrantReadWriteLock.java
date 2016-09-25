package cs601.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * A reentrant read/write lock that allows: 
 * 1) Multiple readers (when there is no writer).
 * 2) One writer (when nobody else is writing or reading). 
 * 3) A writer is allowed to acquire a read lock while holding the write lock. 
 * The assignment is based on the assignment of Prof. Rollins (original author).
 */
public class ReentrantReadWriteLock {

	// TODO: Add instance variables : you need to keep track of the read lock holders and the write lock holders.
	// We should be able to find the number of read locks and the number of write locks 
	// a thread with the given threadId is holding 
	
	private final static Object countLock = new Object();
	private Map<Thread,Integer> readingThreads;
	private Map<Thread,Integer> writingThread;
	private volatile int numberOfReaders;
	private volatile int numberOfWriters;
	private volatile int numberOfReadersRequested;
	private volatile int numberOfWritersRequested;
	
	/**
	 * Constructor for ReentrantReadWriteLock
	 */
	public ReentrantReadWriteLock() {
		// FILL IN CODE
		readingThreads = new HashMap<Thread,Integer>();
		writingThread = new HashMap<Thread,Integer>();
		numberOfReaders = 0;
		numberOfWriters = 0;
		numberOfReadersRequested = 0;
		numberOfWritersRequested = 0;
	}

	/**
	 * Returns true if the current thread holds a read lock.
	 * 
	 * @return
	 */
	public synchronized boolean isReadLockHeldByCurrentThread() {
		// FILL IN CODE
		Thread currentthread = Thread.currentThread();
		return readingThreads.containsKey(currentthread); // don't forget to change it
	}

	/**
	 * Returns true if the current thread holds a write lock.
	 * 
	 * @return
	 */
	public synchronized boolean isWriteLockHeldByCurrentThread() {
		// FILL IN CODE
		Thread currentthread = Thread.currentThread();
		return writingThread.containsKey(currentthread); // don't forget to change it
	}

	/**
	 * Non-blocking method that tries to acquire the read lock. Returns true
	 * if successful.
	 * 
	 * @return
	 */
	public synchronized boolean tryAcquiringReadLock() {
		// FILL IN CODE
		if (writingThread.containsKey(Thread.currentThread())) {
			incrementNumberOfReadersRequested();
			return true;
		} else if(numberOfWriters > 0 || numberOfWritersRequested > 0) {
			return false;
		} else {
			incrementNumberOfReadersRequested();
			return true;
		}
		// don't forget to change it
	}

	/**
	 * Non-blocking method that tries to acquire the write lock. Returns true
	 * if successful.
	 * 
	 * @return
	 */
	public synchronized boolean tryAcquiringWriteLock() {
		// FILL IN CODE
		if (writingThread.containsKey(Thread.currentThread())) {
			incrementNumberOfWritersRequested();
			return true;
		} else if (numberOfWriters > 0 || numberOfReaders > 0 || 
				numberOfWritersRequested > 0 || numberOfReadersRequested > 0) {
			return false;
		} else {
			incrementNumberOfWritersRequested();
			return true;
		}
		// don't forget to change it
	}

	/**
	 * Blocking method - calls tryAcquiringReadLock and returns only when the read lock has been
	 * acquired, otherwise waits.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockRead() {
		// FILL IN CODE
		while(!tryAcquiringReadLock()){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Thread currentthread =Thread.currentThread(); 
		getSetReadingCount(currentthread);
		decrementNumberOfReadersRequested();
		incrementNumberOfReadLocks();
	}

	private Integer getSetReadingCount(Thread currentthread) {
		// TODO Auto-generated method stub
		int count;
		if(readingThreads.containsKey(currentthread)){
			count = readingThreads.get(currentthread) + 1;
			readingThreads.remove(currentthread);
		} else {
			count = 1;
		}
		readingThreads.put(currentthread, count);
		return null;
	}

	/**
	 * Releases the read lock held by the current thread. 
	 */
	public synchronized void unlockRead() {
		// FILL IN CODE
		Thread currentthread = Thread.currentThread();
		if(readingThreads.get(currentthread) == 1){
			readingThreads.remove(currentthread);
		} else {
			readingThreads.put(currentthread, readingThreads.get(currentthread) - 1);
		}		
		decrementNumberOfReadLocks();
		notifyAll();
	}

	/**
	 * Blocking method that calls tryAcquiringWriteLock and returns only when the write lock has been
	 * acquired, otherwise waits.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockWrite() {
		// FILL IN CODE
		while(!tryAcquiringWriteLock()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		Thread currentthread = Thread.currentThread();
		getWritingCount(currentthread);
		decrementNumberOfWritersRequested();
		incrementNumberOfWriteLocks();
	}

	private Integer getWritingCount(Thread currentthread) {
		// TODO Auto-generated method stub
		int count;
		if(writingThread.containsKey(currentthread)){
			count = writingThread.get(currentthread) + 1;
			writingThread.remove(currentthread);
		} else {
			count = 1;
		}
		writingThread.put(currentthread, count);
		return null;
	}

	/**
	 * Releases the write lock held by the current thread. 
	 */
	public synchronized void unlockWrite() {
		// FILL IN CODE
		Thread currentthread = Thread.currentThread();
		if(writingThread.get(currentthread) == 1){
			writingThread.remove(currentthread);
		} else {
			writingThread.put(currentthread, writingThread.get(currentthread) - 1);
		}
		decrementNumberOfWriteLocks();
		notifyAll();
	}
	
	public void incrementNumberOfReadLocks() {
		synchronized(countLock) {
			numberOfReaders++;
		}
	}
	public void decrementNumberOfReadLocks() {
		synchronized(countLock) {
			numberOfReaders--;
		}
	}
	
	public void incrementNumberOfWriteLocks() {
		synchronized(countLock) {
			numberOfWriters++;
		}
	}
	public void decrementNumberOfWriteLocks() {
		synchronized(countLock) {
			numberOfWriters--;
		}
	}
	
	public void incrementNumberOfReadersRequested() {
		synchronized(countLock) {
			numberOfReadersRequested++;
		}
	}
	public void decrementNumberOfReadersRequested() {
		synchronized(countLock) {
			numberOfReadersRequested--;
		}
	}
	
	public void incrementNumberOfWritersRequested() {
		synchronized(countLock) {
			numberOfWritersRequested++;
		}
	}
	public void decrementNumberOfWritersRequested() {
		synchronized(countLock) {
			numberOfWritersRequested--;
		}
	}
}
