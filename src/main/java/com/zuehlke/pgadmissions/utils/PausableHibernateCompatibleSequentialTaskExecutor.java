package com.zuehlke.pgadmissions.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This is a custom task executor implementation suitable for our needs. The specific feature are:<ol>
 * <li>Tasks execute sequentially (FIFO queue).</li>
 * <li>Each task is executed by a new thread, created just for executing this task.
 * <li>The executor has soft  pausing/resuming feature. "Soft" means that calling pause() stops just after finishing currently executing task and before taking the next one
 * (of course paused executor can still collect tasks to be done later).</li>
 * </ol>
 * <p/>
 *
 * Above semantics allows executing tasks that access database via Spring/Hibernate cooperation and sessionFactory.getCurrentSession() pattern.
 * This is because hibernate sessions are being coupled with java threads.
 *
 * <b>Implementation remarks:</b><p/>
 * 1. Please observe that although the executor runs tasks sequentially, we need fully concurrent-aware implementation because
 * the executor instance is being accessed concurrently by:<ul>
 *     <li>client threads that are submitting tasks for execution (i.e. calling execute() method)</li>
 *     <li>client threads that are controlling the executor (pause(), resume(), shutdown())</li>
 *     <li>worker threads that are executing tasks (actually we ensure that there is always at most one such thread)</li>
 *     <li>the Queue Consumer thread</li>
 * </ul>
 * <p/>
 * 2. Both "queue consumer thread" and "queueWe run all threads issued inside implementation with background priority (Thread.MIN_PRIORITY).
 */
public class PausableHibernateCompatibleSequentialTaskExecutor implements Executor {
    
    private final Logger log = LoggerFactory.getLogger(PausableHibernateCompatibleSequentialTaskExecutor.class);
            
    private boolean pickingTasksIsPaused = false;
    
    private String name;
    
    private long lastTaskPickedUpSeqentialNumber = 0L;
    
    private long lastTaskFinished = 0L;
    
    private boolean someTaskIsJustExecuting = false;
    
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    
    private Thread queueConsumer = new QueueConsumerThread();
    
    private boolean isDead = false;

    private TransactionTemplate transactionTemplate;

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public PausableHibernateCompatibleSequentialTaskExecutor() {
        this("no-name");
    }

    public PausableHibernateCompatibleSequentialTaskExecutor(String name) {
        this.name = name;
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
        if (isDead) {
            return;//dead executor is just dead
        }
        
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
        if (isDead) {
            return;//dead executor is just dead
        }
        queue.add(task);
    }

    /**
     * I pause picking tasks from the queue. This softly pauses execution.
     * Currently executing task (if any) is allowed to finish.
     * If I am already paused, I just ignore.
     */
    public synchronized void pause() {
        if (isDead){
            return;//dead executor is just dead
        }
        pickingTasksIsPaused = true;
    }

    /**
     * I resume picking tasks from my task queue. This reverts to normal processing after pausing.
     * If not paused before, I just ignore.
     */
    public synchronized void resume() {
        if (isDead) {
            return;//dead executor is just dead
        }
        pickingTasksIsPaused = false;
        this.notify();
    }

    /**
     * I softly stop task execution (currently executing task will be allowed to finish).
     * Then I destroy the queue (tasks are abandoned) and queue consumer thread.  Finally I put myself in a "destroyed" state.
     */
    public synchronized void shutdown() {
        if (isDead) {
            return;//dead executor is just dead
        }
        this.isDead = true;
        queueConsumer.interrupt();
        queue = null;
    }

    //ooooooooooooooooooooooooooooooooooooooooooooooooooooo PRIVATE ooooooooooooooooooooooooooooooooooooooooooooooooooooooooo

    private synchronized void startTask(Runnable task) throws InterruptedException {
        while (someTaskIsJustExecuting || pickingTasksIsPaused) {
            wait();
        }
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
    private final class QueueConsumerThread extends Thread {

        private QueueConsumerThread() {
            this.setDaemon(true);
            this.setPriority(Thread.MIN_PRIORITY);//this executor is designed to run tasks in background
        }

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
    private final class TaskExecutionThread extends Thread {
        private Runnable clientTask;
        private Long id;

        private TaskExecutionThread(Runnable clientTask, long id, String name) {
            this.clientTask = clientTask;
            this.id = id;
            this.setName(name);
            queueConsumer.setPriority(Thread.MIN_PRIORITY);//this executor is designed to run tasks in background
        }

        @Override
        public void run() {
            //step 1 - do what the client wanted to be done
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                        clientTask.run();
                    }
                });

            } catch (Exception e) {
                //in general tasks should handle all exceptions, in case of some exception propagating from run() we could:
                //1. log the error
                //2. invoke some (pluggable) master error handler
                //Currently only stratego (1) is implemented.
                //todo: consider implementing pluggable "master exception handler queue" feature
                log.error("Exeption propagation from task submitted to task executor. Exception was ignored." +
                    "\n    task info: " + this.getName() +
                    "\n" + StacktraceDump.forException(e), e);
            }

            //step 2 - inform my executor that I have just finished my work.
            synchronized (PausableHibernateCompatibleSequentialTaskExecutor.this) {
                lastTaskFinished = id;
                someTaskIsJustExecuting = false;
                PausableHibernateCompatibleSequentialTaskExecutor.this.notify();
            }
        }
    }
    
    public boolean isPickingTasksIsPaused() {
        return pickingTasksIsPaused;
    }
}
