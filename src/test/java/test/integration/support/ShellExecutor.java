package test.integration.support;

import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ShellExecutor {

    private final ShellTest shellTest;
    private final int timeOut;
    private final TimeUnit unit;

    public ShellExecutor(ShellTest shellTest, int timeOut, TimeUnit unit) {
        this.shellTest = shellTest;
        this.timeOut = timeOut;
        this.unit = unit;
    }

    public ShellExecutor execute(final String command) throws TimeoutException {
        return execute(command, 15);
    }

    public ShellExecutor execute(String command, int timeout) throws TimeoutException {
        final Result result = shellTest.execute(command, timeout, TimeUnit.SECONDS);
        if (result instanceof Failed) {
            Assertions.fail(result.getMessage(), ((Failed) result).getException());
        }
        return this;
    }
}
