package net.webpdf.wsclient.testsuite.integration.annotations;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ProxyTestConditionExt extends IntegrationTestConditionExt {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ConditionEvaluationResult result = super.evaluateExecutionCondition(context);
        if (result.isDisabled()) {
            return result;
        }

        return TestConfig.getInstance().getIntegrationTestConfig().isProxyTestsActive() ?
                ConditionEvaluationResult.enabled(
                        "Proxy tests are enabled via 'config/testConfig.json'.") :
                ConditionEvaluationResult.disabled(
                        "Proxy tests are disabled via 'config/testConfig.json'.");
    }

}
