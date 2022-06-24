package net.webpdf.wsclient.testsuite.integration.annotations;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class IntegrationTestConditionExt implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return TestConfig.getInstance().isIntegrationTestsActive() ?
                ConditionEvaluationResult.enabled(
                        "Integration Tests are enabled via 'config/testConfig.json'.") :
                ConditionEvaluationResult.disabled(
                        "Integration Tests are disabled via 'config/testConfig.json'.");
    }

}
