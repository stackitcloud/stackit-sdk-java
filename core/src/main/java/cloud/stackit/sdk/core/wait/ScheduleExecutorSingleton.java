package cloud.stackit.sdk.core.wait;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({
	"PMD.DoNotUseThreads",
	"PMD.SingleMethodSingleton",
	"PMD.NonThreadSafeSingleton"
})
public final class ScheduleExecutorSingleton {
	// Default pool size for the thread pool
	private static final int DEFAULT_POOL_SIZE = 1;
	private static ScheduleExecutorSingleton instance;
	private static final Lock LOCK = new ReentrantLock();
	private final ScheduledExecutorService scheduler;

	/** Default constructor which takes the default pool size */
	private ScheduleExecutorSingleton() {
		this(DEFAULT_POOL_SIZE);
	}

	/**
	 * Constructor to set a different pool size
	 *
	 * @param poolSize
	 */
	private ScheduleExecutorSingleton(int poolSize) {
		// Use Daemon threads to prevent that the user need to call shutdown
		// even if its program was already terminated
		ThreadFactory daemonThreadFactory =
				runnable -> {
					Thread thread = new Thread(runnable);
					thread.setDaemon(true);
					return thread;
				};
		this.scheduler = Executors.newScheduledThreadPool(poolSize, daemonThreadFactory);
	}

	public static ScheduleExecutorSingleton getInstance() {
		return getInstance(DEFAULT_POOL_SIZE);
	}

	public static ScheduleExecutorSingleton getInstance(int poolSize) {
		if (instance == null) {
			LOCK.lock();
			try {
				if (instance == null) {
					instance = new ScheduleExecutorSingleton(poolSize);
				}
			} finally {
				LOCK.unlock();
			}
		}
		return instance;
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}
}
