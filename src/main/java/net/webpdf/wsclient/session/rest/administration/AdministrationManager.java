package net.webpdf.wsclient.session.rest.administration;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A class implementing {@link AdministrationManager} administrates and monitors the webPDF server configurations.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} used by the currently active {@link RestSession}.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface AdministrationManager<T_REST_DOCUMENT extends RestDocument> {
    /**
     * Returns the {@link RestSession} used by this {@link AdministrationManager}.
     *
     * @return The {@link RestSession} used by this {@link AdministrationManager}.
     */
    @NotNull RestSession<T_REST_DOCUMENT> getSession();

    /**
     * Returns the byte size of the current log or a specific log file of the server. If the date query parameter
     * is specified, an explicitly selected log will be read.
     *
     * @param date A specific {@link Date} to extract from the server log.
     * @return number the length of the requested log
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull Integer fetchLogLength(@Nullable Date date) throws ResultException;

    /**
     * Returns the byte size of the current log file of the server.
     *
     * @return number the length of the requested log
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull Integer fetchLogLength() throws ResultException;

    /**
     * Returns the contents of the current log or a specific log file of the server. If the date query parameter
     * is specified, an explicitly selected log will be read.
     *
     * @param range A specific byte range (e.g. 0-1024) to extract from the server log
     * @param date  A specific optional {@link Date} to extract from the server log. By default, today's date will be used.
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull String fetchLog(@NotNull String range, @Nullable Date date) throws ResultException;

    /**
     * Returns the contents of the current log file of the server.
     *
     * @param range A specific byte range (e.g. 0-1024) to extract from the server log
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull String fetchLog(@NotNull String range) throws ResultException;

    /**
     * Returns the contents of the current log file of the server.
     *
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull String fetchLog() throws ResultException;

    /**
     * Provides status information about the server, the JVM and the Web services.
     *
     * @return {@link AdminServerStatus} The status information about the server.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminServerStatus fetchServerStatus() throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param outputStream The target {@link OutputStream} the support information shall be written to.
     * @param group        List of components to be included in the support information.
     * @param start        Start {@link Date} from when the logs will be included (if logs is included in group).
     *                     If empty, then current date.
     * @param end          End {@link Date} until when the logs will be included (if logs is included in group).
     *                     If empty, then current date.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(
            @NotNull OutputStream outputStream, @Nullable AdminSupportEntryGroup[] group, @Nullable Date start, @Nullable Date end
    ) throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param outputStream The target {@link OutputStream} the support information shall be written to.
     * @param group        List of components to be included in the support information.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(@NotNull OutputStream outputStream, @Nullable AdminSupportEntryGroup[] group) throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param outputStream The target {@link OutputStream} the support information shall be written to.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(@NotNull OutputStream outputStream) throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param target The target {@link File} the support information shall be written to.
     * @param group  List of components to be included in the support information.
     * @param start  Start {@link Date} from when the logs will be included (if logs is included in group).
     *               If empty, then current date.
     * @param end    End {@link Date} until when the logs will be included (if logs is included in group).
     *               If empty, then current date.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(
            @NotNull File target, @Nullable AdminSupportEntryGroup[] group, @Nullable Date start, @Nullable Date end
    ) throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param target The target {@link File} the support information shall be written to.
     * @param group  List of components to be included in the support information.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(@NotNull File target, @Nullable AdminSupportEntryGroup[] group) throws ResultException;

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param target The target {@link File} the support information shall be written to.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void buildSupportPackage(@NotNull File target) throws ResultException;

    /**
     * Restarts the server.
     *
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void restartServer() throws ResultException;

    /**
     * Gets the {@link ApplicationConfigApplication} configuration from the server updating the cached configuration.
     *
     * @return {@link ApplicationConfigApplication} the requested configuration.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull ApplicationConfigApplication fetchApplicationConfiguration() throws ResultException;

    /**
     * Gets the cached {@link ApplicationConfigApplication} configuration of the server or fetches via
     * {@link AdministrationManager#fetchApplicationConfiguration} if cache is empty
     *
     * @return {@link ApplicationConfigApplication} the requested configuration.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull ApplicationConfigApplication getApplicationConfiguration() throws ResultException;

    /**
     * <p>
     * Updates the {@link ApplicationConfigApplication} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * Optionally also validates the {@link ApplicationConfigApplication} configuration with additional {@link AdminApplicationCheck}s.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link ApplicationConfigApplication} configuration defines settings for the web services and
     *                      the portal page.
     * @param checks        A list of {@link AdminApplicationCheck}s to validate the configuration.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ApplicationConfigApplication} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminApplicationCheck> checks
    ) throws ResultException;

    /**
     * <p>
     * Updates the {@link ApplicationConfigApplication} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link ApplicationConfigApplication} configuration defines settings for the web services and
     *                      the portal page.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ApplicationConfigApplication} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration
    ) throws ResultException;

    /**
     * <p>
     * Validates the {@link ApplicationConfigApplication} configuration with the given {@link AdminApplicationCheck}s.
     * </p>
     *
     * @param configuration The {@link ApplicationConfigApplication} configuration defines settings for the web services and
     *                      the portal page.
     * @param checks        The list of {@link AdminApplicationCheck}s to validate the configuration with.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ApplicationConfigApplication} configuration is validated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult validateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminApplicationCheck> checks
    ) throws ResultException;

    /**
     * Gets the {@link ServerConfigServer} configuration from the server updating the cached configuration.
     *
     * @return {@link ServerConfigServer} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull ServerConfigServer fetchServerConfiguration() throws ResultException;

    /**
     * Gets the cached {@link ServerConfigServer} configuration of the server or fetches via
     * {@link AdministrationManager#fetchServerConfiguration} if cache is empty
     *
     * @return {@link ServerConfigServer} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull ServerConfigServer getServerConfiguration() throws ResultException;

    /**
     * <p>
     * Updates the {@link ServerConfigServer} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * Optionally also validates the {@link ServerConfigServer} configuration with additional {@link AdminServerCheck}s.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link ServerConfigServer} configuration defines settings for the server such
     *                      as the ports, user sources or authorization settings.
     * @param checks        A list of {@link AdminServerCheck}s to validate the configuration
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ServerConfigServer} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateServerConfiguration(
            @NotNull ServerConfigServer configuration, @NotNull List<AdminServerCheck> checks
    ) throws ResultException;

    /**
     * <p>
     * Updates the {@link ServerConfigServer} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link ServerConfigServer} configuration defines settings for the server such
     *                      as the ports, user sources or authorization settings.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ServerConfigServer} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateServerConfiguration(
            @NotNull ServerConfigServer configuration
    ) throws ResultException;

    /**
     * <p>
     * Validates the {@link ServerConfigServer} configuration with the given {@link AdminServerCheck}s.
     * </p>
     *
     * @param configuration The {@link ServerConfigServer} configuration defines settings for the web services and
     *                      the portal page.
     * @param checks        The list of {@link AdminServerCheck}s to validate the configuration with.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ServerConfigServer} configuration is validated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult validateServerConfiguration(
            @NotNull ServerConfigServer configuration, @NotNull List<AdminServerCheck> checks
    ) throws ResultException;

    /**
     * Gets the {@link UserConfigUsers} configuration of the server updating the cached configuration.
     *
     * @return {@link UserConfigUsers} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull UserConfigUsers fetchUserConfiguration() throws ResultException;

    /**
     * Gets the cached {@link UserConfigUsers} configuration of the server or fetches via
     * {@link AdministrationManager#fetchUserConfiguration} if cache is empty
     *
     * @return {@link UserConfigUsers} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull UserConfigUsers getUserConfiguration() throws ResultException;

    /**
     * <p>
     * Updates the {@link UserConfigUsers} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * Optionally also validates the {@link UserConfigUsers} configuration with additional {@link AdminUserCheck}s.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link UserConfigUsers} configuration lists users of the webPDF server.
     * @param checks        A list of {@link AdminUserCheck}s to validate the configuration.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link UserConfigUsers} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateUserConfiguration(
            @NotNull UserConfigUsers configuration, @NotNull List<AdminUserCheck> checks
    ) throws ResultException;

    /**
     * <p>
     * Updates the {@link UserConfigUsers} configuration if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link UserConfigUsers} configuration lists users of the webPDF server.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link UserConfigUsers} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateUserConfiguration(
            @NotNull UserConfigUsers configuration
    ) throws ResultException;

    /**
     * <p>
     * Validates the {@link UserConfigUsers} configuration with the given {@link AdminUserCheck}s.
     * </p>
     *
     * @param configuration The {@link UserConfigUsers} configuration defines settings for the web services and
     *                      the portal page.
     * @param checks        The list of {@link AdminUserCheck}s to validate the configuration with.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link UserConfigUsers} configuration is validated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult validateUserConfiguration(
            @NotNull UserConfigUsers configuration, @NotNull List<AdminUserCheck> checks
    ) throws ResultException;

    /**
     * Gets the {@link AdminLogFileConfiguration} from the server updating the cached configuration.
     *
     * @return {@link AdminLogFileConfiguration} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminLogFileConfiguration fetchLogConfiguration() throws ResultException;

    /**
     * Gets the cached {@link AdminLogFileConfiguration} configuration of the server or fetches via
     * {@link AdministrationManager#fetchLogConfiguration} if cache is empty
     *
     * @return {@link AdminLogFileConfiguration} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminLogFileConfiguration getLogConfiguration() throws ResultException;

    /**
     * <p>
     * Updates the {@link AdminLogFileConfiguration} if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * Optionally also validates the {@link AdminLogFileConfiguration} with additional {@link AdminLogCheck}s.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link AdminLogFileConfiguration} provides information and settings about
     *                      the configured server logging.
     * @param checks        A list of {@link AdminLogCheck}s to validate the configuration
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link AdminLogFileConfiguration} is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration, @NotNull List<AdminLogCheck> checks
    ) throws ResultException;

    /**
     * <p>
     * Updates the {@link AdminLogFileConfiguration} if no {@link AdminConfigurationResult#getError()} occurred.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link AdminLogFileConfiguration} provides information and settings about
     *                      the configured server logging.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link AdminLogFileConfiguration} is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult updateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration
    ) throws ResultException;

    /**
     * <p>
     * Validates the {@link AdminLogFileConfiguration} configuration with the given {@link AdminLogCheck}s.
     * </p>
     *
     * @param configuration The {@link AdminLogFileConfiguration} configuration defines settings for the web services and
     *                      the portal page.
     * @param checks        The list of {@link AdminLogCheck}s to validate the configuration with.
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link AdminLogFileConfiguration} configuration is validated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult validateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration, @NotNull List<AdminLogCheck> checks
    ) throws ResultException;

    /**
     * Gets the {@link AdminGlobalKeyStore} of the server updating the cached keystore.
     *
     * @return {@link AdminGlobalKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminGlobalKeyStore fetchGlobalKeyStore() throws ResultException;

    /**
     * Gets the cached {@link AdminGlobalKeyStore} of the server or fetches via
     * {@link AdministrationManager#fetchGlobalKeyStore} if cache is empty
     *
     * @return {@link AdminGlobalKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminGlobalKeyStore getGlobalKeyStore() throws ResultException;

    /**
     * <p>
     * Sets the {@link AdminGlobalKeyStore} of the server.
     * </p>
     * <p>
     * <b>Be Aware:</b> This needs to be updated via {@link AdministrationManager#updateApplicationConfiguration}
     * to take effekt on the server.
     * </p>
     *
     * @param keystore the {@link AdminGlobalKeyStore} to set
     */
    void setGlobalKeyStore(@NotNull AdminGlobalKeyStore keystore);

    /**
     * Gets the {@link AdminTrustStoreKeyStore} from the server updating the cached keystore.
     *
     * @return {@link AdminTrustStoreKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminTrustStoreKeyStore fetchTrustStoreKeyStore() throws ResultException;

    /**
     * Gets the cached {@link AdminTrustStoreKeyStore} configuration of the server or fetches via
     * {@link AdministrationManager#fetchTrustStoreKeyStore} if cache is empty
     *
     * @return {@link AdminTrustStoreKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminTrustStoreKeyStore getTrustStoreKeyStore() throws ResultException;

    /**
     * <p>
     * Sets the {@link AdminTrustStoreKeyStore} of the server.
     * </p>
     * <p>
     * <b>Be Aware:</b> This needs to be updated via {@link AdministrationManager#updateServerConfiguration}
     * to take effekt on the server.
     * </p>
     *
     * @param keystore the {@link AdminTrustStoreKeyStore} to set
     */
    void setTrustStoreKeyStore(@NotNull AdminTrustStoreKeyStore keystore);

    /**
     * Gets the {@link AdminConnectorKeyStore} from the server updating the cached keystore.
     *
     * @return {@link AdminConnectorKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull Map<String, AdminConnectorKeyStore> fetchConnectorKeyStore() throws ResultException;

    /**
     * Gets the cached {@link AdminConnectorKeyStore} of the server or fetches via
     * {@link AdministrationManager#fetchConnectorKeyStore} if cache is empty
     *
     * @return {@link AdminConnectorKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull Map<String, AdminConnectorKeyStore> getConnectorKeyStore() throws ResultException;

    /**
     * <p>
     * Sets the {@link AdminConnectorKeyStore} of the server.
     * </p>
     * <p>
     * <b>Be Aware:</b> This needs to be updated via {@link AdministrationManager#updateServerConfiguration}
     * to take effekt on the server.
     * </p>
     *
     * @param keystore the {@link AdminConnectorKeyStore} to set
     */
    void setConnectorKeyStore(@NotNull Map<String, AdminConnectorKeyStore> keystore);

    /**
     * This is a shortcut function to validate {@link ApplicationConfigApplication} configuration executables
     *
     * @param configuration The {@link ApplicationConfigApplication} configuration defines settings for the web services and
     *                      the portal page.
     * @param executables   A list of {@link AdminExecutableName}s to validate
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link ApplicationConfigApplication} configuration validated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminConfigurationResult testExecutables(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminExecutableName> executables
    ) throws ResultException;

    /**
     * Retrieves a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     * If {@link AdminFileGroupDataStore#GENERIC} is set, the file to get is referenced by the optional
     * filename parameter.
     *
     * @param group    The group of datastore files to search for the file.
     * @param filename A filename to specify the file if {@link AdminFileGroupDataStore#GENERIC} is set
     * @return The requested {@link AdminFileDataStore}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    AdminFileDataStore fetchDatastore(@NotNull AdminFileGroupDataStore group, @Nullable String filename) throws ResultException;

    /**
     * Retrieves a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     * If {@link AdminFileGroupDataStore#GENERIC} is set, the file to get is referenced by the optional
     * filename parameter.
     *
     * @param group The group of datastore files to search for the file.
     * @return The requested {@link AdminFileDataStore}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    AdminFileDataStore fetchDatastore(@NotNull AdminFileGroupDataStore group) throws ResultException;

    /**
     * Updates a file in the server's data store.
     *
     * @param fileDataStore Bundles the file, the file group and the settings to be updated as {@link AdminFileDataStore}
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void updateDatastore(@NotNull AdminFileDataStore fileDataStore) throws ResultException;

    /**
     * Deletes a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     * If {@link AdminFileGroupDataStore#GENERIC} is set, the file to delete is referenced by the optional
     * filename parameter.
     *
     * @param group    The group of datastore files to search for the file.
     * @param filename A filename to specify the file if {@link AdminFileGroupDataStore#GENERIC} is set
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void deleteDatastore(@NotNull AdminFileGroupDataStore group, @Nullable String filename) throws ResultException;

    /**
     * Deletes a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     *
     * @param group The group of datastore files to search for the file.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void deleteDatastore(@NotNull AdminFileGroupDataStore group) throws ResultException;

    /**
     * <p>
     * <b>(Experimental Web service)</b>
     * </p>
     * <p>
     * Reads statistic information from the server for Web services and file formats.
     * </p>
     *
     * @param dataSource  Data source from which the data is read.
     * @param aggregation Aggregation mode for the retrieved data.
     * @param webservices List of webservice names from which the data should be retrieved.
     * @param start       Start {@link Date} for the data, formatted as ISO-8601 extended offset (zoned based) date-time
     *                    format.
     * @param end         End {@link Date} for the data, formatted as ISO-8601 extended offset (zoned based) date-time
     *                    format.
     * @return The requested {@link AdminStatistic}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull AdminStatistic fetchServerStatistic(
            @NotNull AdminDataSourceServerState dataSource, @NotNull AdminAggregationServerState aggregation,
            @NotNull List<Webservice> webservices, @NotNull Date start, @NotNull Date end
    ) throws ResultException;

    /**
     * Returns the session table from server with detailed status information about each session.
     *
     * @return The requested {@link SessionTable}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @NotNull SessionTable fetchSessionTable() throws ResultException;

    /**
     * Closes the session with the specified ID, by activating the session expiration. After the call, any access
     * to the session results in an error.
     *
     * @param sessionId ID of the session to be closed.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    void closeSession(@NotNull String sessionId) throws ResultException;
}
