import java.util.*;

public class Scheduler extends Thread {
	private Vector queue;	// a list of all active threads (TCBs)
	private int timeSlice;	// a time slice allocated to each user thread execution 
	private static final int DEFAULT_TIME_SLICE = 1000; // 1000ms == 1 sec

	// New data added to p161
	private boolean[] tids; // Indicate which ids have been used
	private static final int DEFAULT_MAX_THREADS = 10000; // tids[] has 10000 elements

	// A new feature added to p161
	// Allocate an ID array, each element indicating if that id has been used
	private int nextId = 0;

	// pre: passed maxThreads number of element for tid 
	// post: allocate tids array with the size given. initialize all entries to false. 
	// 		 indicating that all ids have not been used. 
	private void initTid(int maxThreads) {
		tids = new boolean[maxThreads];
		for (int i = 0; i < maxThreads; i++)
			tids[i] = false;
	}

	// A new feature added to p161
	// Search an available thread ID and provide a new thread with this ID
	private int getNewTid() {
		for (int i = 0; i < this.tids.length; i++) {
			int tentative = (this.nextId + i) % this.tids.length;
			if (this.tids[tentative] == false) {
				this.tids[tentative] = true;
				this.nextId = (tentative + 1) % this.tids.length;
				return tentative;
			}
		}
		return -1;
	}

	// A new feature added to p161
	// Return the thread ID and set the corresponding tids element to be unused
	// return false if it was already unused, otherwise return true; 
	private boolean returnTid(int tid) {
		if (tid >= 0 && tid < tids.length && tids[tid] == true) {
			tids[tid] = false;
			return true;
		}
		return false;
	}

	// A new feature added to p161
	// Retrieve the current thread's TCB from the queue
	public TCB getMyTcb() {
		Thread myThread = Thread.currentThread(); // Get my thread object
		synchronized (queue) {
			for (int i = 0; i < queue.size(); i++) {
				TCB tcb = (TCB) queue.elementAt(i);
				Thread thread = tcb.getThread();
				if (thread == myThread) // if this is my TCB, return it
					return tcb;
			}
		}
		return null;
	}

	// A new feature added to p161
	// Return the maximal number of threads to be spawned in the system
	// available number of threads 
	public int getMaxThreads() {
		return this.tids.length;
	}

	public Scheduler() {
		this.timeSlice = DEFAULT_TIME_SLICE;
		this.queue = new Vector();
		this.initTid(DEFAULT_MAX_THREADS);
	}

	public Scheduler(int quantum) {
		this.timeSlice = quantum;
		this.queue = new Vector();
		this.initTid(DEFAULT_MAX_THREADS);
	}

	// A new feature added to p161
	// A constructor to receive the max number of threads to be spawned
	public Scheduler(int quantum, int maxThreads) {
		this.timeSlice = quantum;
		this.queue = new Vector();
		this.initTid(maxThreads);
	}

	// puts the Scheduler to sleep for a given time quantum 
	private void schedulerSleep() {
		try {
			Thread.sleep(this.timeSlice);
		} catch (InterruptedException e) {
		}
	}

	// A modified addThread of p161 example
	// allocates a new TCB to this thread t and adds the TCB to the active 
	// thread queue. This new TCB receives the calling thread's id as its\
	// parent id 
	public TCB addThread(Thread t) {
		//t.setPriority(2); // ******************************************needs to be removed for this ass
		TCB parentTcb = this.getMyTcb(); // get my TCB and find my TID
		int pid = (parentTcb != null) ? parentTcb.getTid() : -1;
		int tid = this.getNewTid(); // get a new TID
		if (tid == -1)
			return null;
		TCB tcb = new TCB(t, tid, pid); // create a new TCB
		this.queue.add(tcb);
		return tcb;
	}

	// A new feature added to p161
	// Removing the TCB of a terminating thread
	// actual deletion of a terminated TCB is performed inside the run() 
	public boolean deleteThread() {
		TCB tcb = getMyTcb();
		if (tcb != null)
			return tcb.setTerminated();
		else
			return false;
	}

	// puts the calling thread to sleep for a given time quantum 
	public void sleepThread(int milliseconds) {
		try {
			sleep(milliseconds);
		} catch (InterruptedException e) {
		}
	}

	// A modified run of p161
	// this is heart of Scheduler. 
	// difference from the lecture slide 
	// 	1. retrieving a next available TCB rather than a thread from the active thread list
	// 	2. deleting it if it has been marked as terminated 
	// 	3. starting the thread if it has not yet been started. 
	// Other than these difference, the Scheduler repeats retrieving a next available TCB 
	// from the list, raising up the corresponding thread's priority, yielding CPU
	// to this thread with sleep(), and lowering the thread's priority  
	public void run() {
		Thread current = null;

		//this.setPriority(6); // *****************************************neeeds to be removed 

		while (true) {
			try {
				// get the next TCB and its thrad
				if (this.queue.size() == 0)
					continue;
				TCB currentTCB = (TCB) this.queue.firstElement();
				if (currentTCB.getTerminated() == true) {
					this.queue.remove(currentTCB);
					this.returnTid(currentTCB.getTid());
					continue;
				}
				current = currentTCB.getThread();
				if (current != null) {
					if (current.isAlive())
						//current.setPriority(4); // ********replace this with current.resume()
						current.resume();
					else {
						// Spawn must be controlled by Scheduler
						// Scheduler must start a new thread
						current.start();
						//current.setPriority(4); // *********remove this 
					}
				}

				this.schedulerSleep();
				// System.out.println("* * * Context Switch * * * ");

				synchronized (queue) {
					if (current != null && current.isAlive())
						//current.setPriority(2); //************remplace with current.suspend()
						current.suspend(); 
					queue.remove(currentTCB); // rotate this TCB to the end
					queue.add(currentTCB);
				}
			} catch (NullPointerException e3) {
			}
			;
		}
	}
}
