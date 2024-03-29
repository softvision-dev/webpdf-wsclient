package net.webpdf.wsclient.testsuite.integration.annotations;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class OAuthTestConditionExt extends IntegrationTestConditionExt {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ConditionEvaluationResult result = super.evaluateExecutionCondition(context);
        if (result.isDisabled()) {
            return result;
        }

        final var optional =
                findAnnotation(context.getElement(), OAuthTest.class);
        if (optional.isPresent()) {
            OAuthTest annotation = optional.get();
            if (!TestConfig.getInstance().getIntegrationTestConfig().isIntegrationTestsActive()) {
                return ConditionEvaluationResult.disabled(
                        "Integration Tests are disabled via 'config/testConfig.json'.");
            }
            switch (annotation.provider()) {
                case AZURE:
                    return TestConfig.getInstance().getIntegrationTestConfig().getAzureConfig().isEnabled() ?
                            ConditionEvaluationResult.enabled(
                                    "OAuth2 (Azure) tests are enabled via 'config/testConfig.json'.") :
                            ConditionEvaluationResult.disabled(
                                    "OAuth2 (Azure) tests are disabled via 'config/testConfig.json'.");
                case AUTH_0:
                    return TestConfig.getInstance().getIntegrationTestConfig().getAuth0Config().isEnabled() ?
                            ConditionEvaluationResult.enabled(
                                    "OAuth2 (Auth0) tests are enabled via 'config/testConfig.json'.") :
                            ConditionEvaluationResult.disabled(
                                    "OAuth2 (Auth0) tests are disabled via 'config/testConfig.json'.");
                default:
                    return ConditionEvaluationResult.disabled(
                            "Test is disabled, the selected OAuth provider is unknown.");
            }
        }
        return ConditionEvaluationResult.enabled(
                "Assumptions could not be checked - enable execution, provoking a failure.");
    }

}
