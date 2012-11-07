package com.zuehlke.pgadmissions.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is a custom task executor implementation suitable for our needs. The specific feature are:<ol>
 * <li>Ensure that tasks execute sequentially (FIFO queue).</li>
 * <li>Each task has to be executed by a new thread, created just for executing this task (this ensure compatibility with the way we use Hibernate,
 * specifically - our session factory assigns one sessions per thread; and we want each task to be executed in a fresh session).</li>
 * <li>The executor has soft  pausing/resuming feature. "Soft" means that calling pause() stops just after finishing currently executing task and before taking the next one
 * (of course paused executor can still collect tasks to be done later).</li>
 * </ol>
 *
 * Implementation remark: please observe that althougth the executor runs tasks sequentially, we need fully concurrent-aware implementation because
 * the executor instance is being accessed concurrently by:<ul>
 *     <li>client threads that are submitting tasks for execution (i.e. calling execute() method)</li>
 *     <li>client threads that are controlling the executor (pause(), resume(), shutdown())</li>
 *     <li>worker threads that are executing tasks (actually we ensure that there is always at most one such thread)</li>
 *     <li>the Queue Consumer thread</li>
 * </ul>
 */
public class PausableHibernateCompatibleSequentialTaskExecutor implements Executor {
    private boolean pickingTasksIsPaused = false;
    private String name;
    private long lastTaskPickedUpSeqentialNumber = 0L;
    private long lastTaskFinished = 0L;
    private boolean someTaskIsJustExecuting = false;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private Thread queueConsumer = new QueueConsumerThread();
    private boolean isDead = false;

    public PausableHibernateCompatibleSequentialTaskExecutor() {
        this("no-name");
    }

    public PausableHibernateCompatibleSequentialTaskExecutor(String name) {
        queueConsumer.setDaemon(true);
        this.setName(name);
        queueConsumer.start();
    }

    public String getName() {
        return name;
    }

    /**
     * I return the number of last task from the queue that finished execution.
     * Tasks are identified with integer numbers, starting from 1.
     * Result 0 (zero) means that no task has been executed yet.
     *
     * @return number of last task that finished.
     */
    public long getLastTaskFinished() {
        return lastTaskFinished;
    }

    /**
     * I set my name. Setting the name is very useful for diagnostic purposes because basing on this name I generate
     * names for task executing threads.
     *
     * @param name my new name
     */
    public synchronized void setName(String name) {
        if (isDead)
            return;//dead executor is just dead

        this.name = name;
        queueConsumer.setName(name + "-queue-consumer");
    }

    /**
     * I am adding the given task to the queue of tasks waiting for execution.
     * The tasks are being executed sequentially (one at a time) and each of them is being executed in a new thread.
     * Tasks are executed in the order of adding.
     *
     * @param task task to be executed in the future
     */
    public synchronized void execute(Runnable task) {
        if (isDead)
            return;//dead executor is just dead

        queue.add(task);
    }

    /**
     * I pause picking tasks from the queue. This softly pauses execution.
     * Currently executing task (if any) is allowed to finish.
     * If I am already paused, I just ignore.
     */
    public synchronized void pause() {
        if (isDead)
            return;//dead executor is just dead

        pickingTasksIsPaused = true;
    }

    /**
     * I resume picking tasks from my task queue. This reverts to normal processing after pausing.
     * If not paused before, I just ignore.
     */
    public synchronized void resume() {
        if (isDead)
            return;//dead executor is just dead

        pickingTasksIsPaused = false;
        this.notify();
    }

    /**
     * I softly stop task execution (currently executing task will be allowed to finish).
     * Then I destroy the queue (tasks are abandoned) and queue consumer thread.  Finally I put myself in a "destroyed" state.
     */
    public synchronized void shutdown() {
        if (isDead)
            return;//dead executor is just dead

        this.isDead = true;
        queueConsumer.interrupt();
        queue = null;
    }

    //ooooooooooooooooooooooooooooooooooooooooooooooooooooo PRIVATE ooooooooooooooooooooooooooooooooooooooooooooooooooooooooo

    private synchronized void startTask(Runnable task) throws InterruptedException {
        while (someTaskIsJustExecuting || pickingTasksIsPaused)
            wait();
        lastTaskPickedUpSeqentialNumber++;
        String threadName = "executor-" + name + "-task-thread-" + lastTaskPickedUpSeqentialNumber;
        Thread taskExecutionThread = new TaskExecutionThread(task, lastTaskPickedUpSeqentialNumber, threadName);
        someTaskIsJustExecuting = true;
        taskExecutionThread.start();
    }

    /**
     * Queue consuming thread - runs in an infinite loop, taking subsequent elements from task queue.
     * Each task is being executed by a dedicated thread.
     */
    private class QueueConsumerThread extends Thread {

        @Override
        public void run() {
            try {
                while (! isDead) {
                    Runnable nextTask = queue.take();//we are waiting for next queue element OUTSIDE executor's monitor, thanks to this we avoid deadlock (!)
                    PausableHibernateCompatibleSequentialTaskExecutor.this.startTask(nextTask);
                }
            } catch (InterruptedException e) {
                //we are here because the executor is (apparently) shutting down
                //this is normal and the exception should be just ignored

                //*nothing to do here*
            }
        }
    }

    /**
     * This is definition of a thread dedicated for running just a single task.
     */
    private class TaskExecutionThread extends Thread {
        private Runnable clientTask;
        private Long id;

        private TaskExecutionThread(Runnable clientTask, long id, String name) {
            this.clientTask = clientTask;
            this.id = id;
            this.setName(name);
        }

        @Override
        public void run() {
            //first - do what the client wanted to be done
            clientTask.run();

            //then - inform my executor that I just finished my work.
            synchronized (PausableHibernateCompatibleSequentialTaskExecutor.this) {
                lastTaskFinished = id;
                someTaskIsJustExecuting = false;
                PausableHibernateCompatibleSequentialTaskExecutor.this.notify();
            }
        }
    }

}
