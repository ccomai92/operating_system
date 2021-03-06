import java.util.*;

public class Scheduler extends Thread {
	private Vector[] queues; // a list of all active threads (TCBs)
	private int timeSlice; // a time slice allocated to each user thread execution
	private static final int DEFAULT_TIME_SLICE = 1000; // 1000ms == 1 sec

	// New data added to p161
	private boolean[] tids; // Indicate which ids have been used
	private static final int DEFAULT_MAX_THREADS = 10000; // tids[] has 10000 elements

	// A new feature added to p161
	// Allocate an ID array, each element indicating if that id has been used
	private int nextId = 0;

	// pre: passed maxThreads number of element for tid
	// post: allocate tids array with the size given. initialize all entries to
	// false.
	// indicating that all ids have not been used.
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

	private void initQueues() {
		this.queues = new Vector[3];
		for (int i = 0; i < 3; i++) {
			this.queues[i] = new Vector();
		}
	}

	// A new feature added to p161
	// Retrieve the current thread's TCB from the queue
	public TCB getMyTcb() {
		Thread myThread = Thread.currentThread(); // Get my thread object
		synchronized (this.queues) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < this.queues[i].size(); j++) {
					TCB tcb = (TCB) this.queues[i].elementAt(j);
					Thread thread = tcb.getThread();
					if (thread == myThread) // if this is my TCB, return it
						return tcb;
				}
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
		this.timeSlice = DEFAULT_TIME_SLICE / 2;
		this.initQueues();
		this.initTid(DEFAULT_MAX_THREADS);
	}

	public Scheduler(int quantum) {
		this.timeSlice = quantum;
		this.initQueues();
		this.initTid(DEFAULT_MAX_THREADS);
	}

	// A new feature added to p161
	// A constructor to receive the max number of threads to be spawned
	public Scheduler(int quantum, int maxThreads) {
		this.timeSlice = quantum;
		this.initQueues();
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
		// t.setPriority(2); // ******************************************needs to be
		// removed for this ass
		TCB parentTcb = this.getMyTcb(); // get my TCB and find my TID
		int pid = (parentTcb != null) ? parentTcb.getTid() : -1;
		int tid = this.getNewTid(); // get a new TID
		if (tid == -1)
			return null;
		TCB tcb = new TCB(t, tid, pid); // create a new TCB
		this.queues[0].add(tcb); // add new thread (TCB) to queue[0] always
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
	// 1. Scheduler first executes all threads in queue 0. time quantum (timeSlice /
	// 2)
	// 2. If a thread in the queue 0 does not complete its execution in time slice,
	// move to queue 1
	// 3. if queue 0 is empty, execute threads in queue 1. time quantum (timeSlice)
	// in order to react to new threads in queue 0, scheduler check if queue 0 has
	// new TCB after timeSlice / 2
	// 4. Same for queue 2
	// 5. if a thread in queue 2 does not complete its execution in its time
	// quantum,
	// back to tail of queue 2 (different than text book).
	public void run() {
		Thread current = null;

		while (true) {
			try {

				// get the next TCB and its thread
				TCB currentTCB;
				int currentQ = -1;
				if (this.queues[0].size() != 0) {
					currentTCB = (TCB) this.queues[0].firstElement();
					currentQ = 0;
				} else if (this.queues[1].size() != 0) {
					currentTCB = (TCB) this.queues[1].firstElement();
					currentQ = 1;
				} else if (this.queues[2].size() != 0) {
					currentTCB = (TCB) this.queues[2].firstElement();
					currentQ = 2;
				} else {
					continue;
				}

				if (currentTCB.getTerminated() == true) {
					this.queues[currentQ].remove(currentTCB);
					this.returnTid(currentTCB.getTid());
					continue;
				}

				current = currentTCB.getThread();
				if (current != null) {
					if (current.isAlive()) {
						current.resume();
					} else {
						// Spawn must be controlled by Scheduler
						// Scheduler must start a new thread
						current.start();
						// current.setPriority(4); // *********remove this
					}
					this.schedulerSleep();

					if (currentQ == 0) {
						synchronized (this.queues) {
							if (current != null && current.isAlive())
								current.suspend();
							this.queues[0].remove(currentTCB); // rotate this TCB to the end
							this.queues[1].add(currentTCB);
						}
					} else if (currentQ == 1) {
						if (this.queues[0].size() != 0) {
							synchronized (this.queues) {
								if (current != null && current.isAlive())
									current.suspend();
								this.queues[1].remove(currentTCB); // rotate this TCB to the end
								this.queues[1].add(currentTCB);
							}
						} else if (currentTCB.getTerminated() == true) {
							this.queues[currentQ].remove(currentTCB);
							this.returnTid(currentTCB.getTid());
						} else {
							this.schedulerSleep();
							synchronized (this.queues) {
								if (current != null && current.isAlive())
									current.suspend();
								this.queues[1].remove(currentTCB); // rotate this TCB to the end
								this.queues[2].add(currentTCB);
							}
						}
					} else if (currentQ == 2) { // currentQ == 2
						for (int i = 0; i < 3; i++) {
							if (this.queues[0].size() != 0 || this.queues[1].size() != 0) {
								synchronized (this.queues) {
									if (current != null && current.isAlive())
										current.suspend();
									this.queues[2].remove(currentTCB); // rotate this TCB to the end
									this.queues[2].add(currentTCB);
								}
								break;
							} else if (i < 2) {
								if (currentTCB.getTerminated() == true) {
									this.queues[currentQ].remove(currentTCB);
									this.returnTid(currentTCB.getTid());
									break;
								}
								this.schedulerSleep();
							} else {
								this.schedulerSleep();
								synchronized (this.queues) {
									if (current != null && current.isAlive())
										current.suspend();
									this.queues[2].remove(currentTCB); // rotate this TCB to the end
									this.queues[2].add(currentTCB);
								}
							}
						}
					}
				}
				// this.schedulerSleep();
				// System.out.println("* * * Context Switch * * * ");

			} catch (NullPointerException e3) {
			}
			;
		}
	}
}