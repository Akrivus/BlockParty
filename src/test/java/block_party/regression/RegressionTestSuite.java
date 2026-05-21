package block_party.regression;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import java.util.Arrays;
import java.util.List;

/**
 * Small no-framework runner for lightweight regression contracts.
 */
public final class RegressionTestSuite {
    private RegressionTestSuite() {
    }

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        List<RegressionTest> tests = Arrays.asList(
                new HideRegressionTest(),
                new SceneContractRegressionTest(),
                new TraitRegressionTest(),
                new ViewModelRegressionTest(),
                new NetworkRegressionTest(),
                new RegistryContractRegressionTest(),
                new PersistenceRegressionTest(),
                new MoeTexturesRegressionTest()
        );

        for (RegressionTest test : tests) {
            test.run();
        }

        System.out.printf("Lightweight regression tests passed: %d suites.%n", tests.size());
    }
}
