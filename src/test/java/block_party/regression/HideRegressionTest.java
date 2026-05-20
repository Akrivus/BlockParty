package block_party.regression;

import block_party.entities.goals.HideUntil;

import static block_party.regression.TestSupport.assertEquals;

final class HideRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testHideUntilParsing();
    }

    private void testHideUntilParsing() {
        assertEquals(HideUntil.EXPOSED, HideUntil.EXPOSED.fromValue("exposed"), "HideUntil parses lowercase exposed");
        assertEquals(HideUntil.ONE_SECOND_PASSES, HideUntil.EXPOSED.fromValue("one_second_passes"), "HideUntil parses timed hide");
        assertEquals(HideUntil.ONE_SECOND_PASSES, HideUntil.ONE_SECOND_PASSES.fromValue("missing"), "HideUntil falls back to receiver");
        assertEquals("ONE_SECOND_PASSES", HideUntil.ONE_SECOND_PASSES.getValue(), "HideUntil value stays stable");
    }
}
