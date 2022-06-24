package net.webpdf.wsclient.testsuite.integration.annotations;

import net.webpdf.wsclient.testsuite.config.IntegrationTestConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class OAuthTestConditionExt implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final var optional =
                findAnnotation(context.getElement(), OAuthTestCondition.class);
        if (optional.isPresent()) {
            OAuthTestCondition annotation = optional.get();
            switch (annotation.provider()) {
                case AZURE:
                    return IntegrationTestConfig.getInstance().getAzureConfig().isEnabled() ?
                            ConditionEvaluationResult.enabled(
                                    "Test is enabled via 'config/integrationTestConfig.json'.") :
                            ConditionEvaluationResult.disabled(
                                    "Test is disabled via 'config/integrationTestConfig.json'.");
                case AUTH_0:
                    return IntegrationTestConfig.getInstance().getAuth0Config().isEnabled() ?
                            ConditionEvaluationResult.enabled(
                                    "Test is enabled via 'config/integrationTestConfig.json'.") :
                            ConditionEvaluationResult.disabled(
                                    "Test is disabled via 'config/integrationTestConfig.json'.");
                default:
                    return ConditionEvaluationResult.disabled(
                            "Test is disabled, the selected OAuth provider is unknown.");
            }
        }
        return ConditionEvaluationResult.enabled(
                "Assumptions could not be checked - enable execution, provoking a failure.");
    }

}
