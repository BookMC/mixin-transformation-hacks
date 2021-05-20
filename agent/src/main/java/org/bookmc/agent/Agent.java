package org.bookmc.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    private static Instrumentation instrumentation;

    public static void agentmain(final String argument, final Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
    }
}
