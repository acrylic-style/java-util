package util.base;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Threads {
    public static void dumpThread(@NotNull ThreadInfo thread) {
        dumpThread(thread, System.err);
    }

    public static void dumpThread(@NotNull ThreadInfo thread, @NotNull PrintStream log) {
        log.println("------------------------------");
        log.println("Current Thread: " + thread.getThreadName());
        log.println("\tPID: " + thread.getThreadId()
                + " | Suspended: " + thread.isSuspended()
                + " | Native: " + thread.isInNative()
                + " | State: " + thread.getThreadState());
        if (thread.getLockedMonitors().length != 0) {
            log.println("\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                log.println("\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }
        log.println("\tStack:");
        for (StackTraceElement stack : thread.getStackTrace()) {
            log.println("\t\t" + stack);
        }
    }

    public static void dumpThread(@NotNull ThreadInfo thread, @NotNull Logger log) {
        log.log(Level.SEVERE, "------------------------------");
        log.log(Level.SEVERE, "Current Thread: " + thread.getThreadName());
        log.log(Level.SEVERE, "\tPID: " + thread.getThreadId()
                + " | Suspended: " + thread.isSuspended()
                + " | Native: " + thread.isInNative()
                + " | State: " + thread.getThreadState());
        if (thread.getLockedMonitors().length != 0) {
            log.log(Level.SEVERE, "\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                log.log(Level.SEVERE, "\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }
        log.log(Level.SEVERE, "\tStack:");
        for (StackTraceElement stack : thread.getStackTrace()) {
            log.log(Level.SEVERE, "\t\t" + stack);
        }
    }
}
