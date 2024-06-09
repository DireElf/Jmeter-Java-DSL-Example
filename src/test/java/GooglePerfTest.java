import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

public class GooglePerfTest {
    public static final int THREADS_NUMBER = 2;
    public static final int ITERATIONS_NUMBER = 10;
    public static final String LINK = "https://www.google.com/";


    @Test
    public void googlePerformanceTest() throws IOException {
        TestPlanStats testPlanStats = testPlan(
                threadGroup(THREADS_NUMBER, ITERATIONS_NUMBER, httpSampler(LINK)),
                jtlWriter("target", "report.csv"))
                .run();
        assertThat(testPlanStats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
    }
}
