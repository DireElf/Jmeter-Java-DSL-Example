import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

public class GooglePerfTest {
    @Test
    public void googlePerformanceTest() throws IOException {
        TestPlanStats testPlanStats = testPlan(
                threadGroup(2, 10, httpSampler("https://www.google.com/")),
                jtlWriter("target", "report.csv"))
                .run();
        assertThat(testPlanStats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
    }
}
