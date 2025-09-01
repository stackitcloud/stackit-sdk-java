package cloud.stackit.sdk.core.wait;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("PMD.DoNotUseThreads")
public final class ScheduleExecutorSingleton {
	// Pool size for the thread pool
	private static final int POOL_SIZE = 1;
	private final ScheduledExecutorService scheduler;

	private ScheduleExecutorSingleton() {
		// Use Daemon threads to prevent that the user need to call shutdown
		// even if its program was already terminated
		ThreadFactory daemonThreadFactory =
				runnable -> {
					Thread thread = new Thread(runnable);
					thread.setDaemon(true);
					return thread;
				};
		scheduler = Executors.newScheduledThreadPool(POOL_SIZE, daemonThreadFactory);
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	// In order to make the Linter happy this is the only solution which is accepted.
	// Lock/ReentrantLock, synchronized and volatile are in general not accepted.
	private static final class SingletonHolder {
		public static final ScheduleExecutorSingleton INSTANCE = new ScheduleExecutorSingleton();
	}

	public static ScheduleExecutorSingleton getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
