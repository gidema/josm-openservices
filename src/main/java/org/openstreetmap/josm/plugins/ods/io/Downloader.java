package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;

public interface Downloader {
    static final int NTHREADS = 10;
 
    public void setup(OdsContext context);

    
    public FutureTask<TaskStatus> getPrepareTask();

    public FutureTask<TaskStatus> getFetchTask();

    public FutureTask<TaskStatus> getProcessTask();

    public void cancel();
    
    public static List<FutureTask<TaskStatus>> getPrepareTasks(List<? extends Downloader> downloaders) {
        return downloaders.stream()
                .map(Downloader::getPrepareTask)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public static List<FutureTask<TaskStatus>> getFetchTasks(List<? extends Downloader> downloaders) {
        return downloaders.stream()
                .map(Downloader::getFetchTask)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public static List<FutureTask<TaskStatus>> getProcessTasks(List<? extends Downloader> downloaders) {
        return downloaders.stream()
                .map(Downloader::getProcessTask)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public static TaskStatus runTasks(List<FutureTask<TaskStatus>> tasks) {
        // Currently, tasks are allowed to be null. Make sure we handle only non-null tasks
        List<FutureTask<TaskStatus>> realTasks = tasks.stream().filter(Objects::nonNull).collect(Collectors.toList());
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        realTasks.forEach(executor::submit);
        try {
            while (true) {
                boolean allDone = true;
                for (FutureTask<TaskStatus> task : realTasks) {
                    allDone &= task.isDone();
                }
                if (allDone) {
                    executor.shutdown();
                    List<TaskStatus> taskStatuses = new ArrayList<>(realTasks.size());
                    for (FutureTask<TaskStatus> task : realTasks) {
                        TaskStatus taskStatus;
                        try {
                            taskStatus = task.get();
                        }
                        catch (ExecutionException e) {
                            taskStatus = new TaskStatus(null, null, e);
                        }
                        taskStatuses.add(taskStatus);
                    }
                    return new TaskStatus(taskStatuses);
                }
                Thread.sleep(500L, 0);
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            realTasks.forEach(task -> task.cancel(true));
            return new TaskStatus(true);
        }
        catch (Exception e) {
            return new TaskStatus(null, e.getMessage(), e);
        }
    }
    
    public enum Modus {
        FetchModifications, // Only fetch features where something has changed (Created, Updated or Deleted) 
        FetchActual, // Only fetch actual features from the open data source
        FetchAll;
    }

    public static boolean checkErrors(TaskStatus status, ProgressMonitor pm) {
        boolean errors = false;
        if (status.hasErrors()) {
            pm.indeterminateSubTask(status.getErrorString());
            errors = true;
        }
        if (status.hasExceptions()) {
            pm.indeterminateSubTask(status.getExceptionString());
            errors = true;
        }
        return errors;
    }

}
