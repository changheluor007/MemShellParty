package com.reajason.javaweb.integration.payara;

import com.reajason.javaweb.memshell.config.Constants;
import com.reajason.javaweb.memshell.config.Server;
import com.reajason.javaweb.memshell.config.ShellTool;
import com.reajason.javaweb.memshell.packer.Packers;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.jar.asm.Opcodes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static com.reajason.javaweb.integration.ContainerTool.getUrl;
import static com.reajason.javaweb.integration.ContainerTool.warJakartaFile;
import static com.reajason.javaweb.integration.DoesNotContainExceptionMatcher.doesNotContainException;
import static com.reajason.javaweb.integration.ShellAssertionTool.testShellInjectAssertOk;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author ReaJason
 * @since 2024/12/12
 */
@Slf4j
@Testcontainers
public class Payara620222ContainerTest {
    public static final String imageName = "reajason/payara:6.2022.2-jdk11";

    @Container
    public static final GenericContainer<?> container = new GenericContainer<>(imageName)
            .withCopyToContainer(warJakartaFile, "/usr/local/payara6/glassfish/domains/domain1/autodeploy/app.war")
            .waitingFor(Wait.forLogMessage(".*JMXService.*", 1))
            .withExposedPorts(8080);

    static Stream<Arguments> casesProvider() {
        return Stream.of(
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Behinder, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Behinder, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Godzilla, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Godzilla, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Command, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Command, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Suo5, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_FILTER, ShellTool.Suo5, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Behinder, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Behinder, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Godzilla, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Godzilla, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Command, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Command, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Suo5, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_LISTENER, ShellTool.Suo5, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Behinder, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Behinder, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Godzilla, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Godzilla, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Command, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Command, Packers.Deserialize),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Suo5, Packers.JSP),
                arguments(imageName, Constants.JAKARTA_VALVE, ShellTool.Suo5, Packers.Deserialize)
        );
    }

    @AfterAll
    static void tearDown() {
        String logs = container.getLogs();
        assertThat("Logs should not contain any exceptions", logs, doesNotContainException());
    }

    @ParameterizedTest(name = "{0}|{1}{2}|{3}")
    @MethodSource("casesProvider")
    void test(String imageName, String shellType, ShellTool shellTool, Packers packer) {
        testShellInjectAssertOk(getUrl(container), Server.Payara, shellType, shellTool, Opcodes.V1_6, packer);
    }
}
