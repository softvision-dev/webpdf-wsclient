package net.webpdf.wsclient.session.rest.administration;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("EmptyTryBlock")
public class AbstractAdministrationManager<T_REST_DOCUMENT extends RestDocument>
        implements AdministrationManager<T_REST_DOCUMENT> {
    private final @NotNull RestSession<T_REST_DOCUMENT> session;
    private @Nullable ApplicationConfigApplication applicationConfiguration;
    private @Nullable ServerConfigServer serverConfiguration;
    private @Nullable UserConfigUsers userConfiguration;
    private @Nullable AdminLogFileConfiguration logConfiguration;
    private @Nullable AdminGlobalKeyStore globalKeyStore;
    private @Nullable Map<String, AdminConnectorKeyStore> connectorKeyStore;
    private @Nullable AdminTrustStoreKeyStore trustStoreKeyStore;

    /**
     * Initializes an {@link AdministrationManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link AdministrationManager} shall be created for.
     */
    AbstractAdministrationManager(@NotNull RestSession<T_REST_DOCUMENT> session) {
        this.session = session;
    }

    /**
     * Returns the {@link RestSession} used by this {@link AdministrationManager}.
     *
     * @return The {@link RestSession} used by this {@link AdministrationManager}.
     */
    @Override
    public @NotNull RestSession<T_REST_DOCUMENT> getSession() {
        return session;
    }

    /**
     * Validates if the current user has access rights and throws {@link ClientResultException} otherwise
     *
     * @throws ResultException Shall be thrown upon invalid user access.
     */
    protected void validateUser() throws ResultException {
        AuthUserCredentials user = this.session.getUser();

        if (user == null || !user.getIsAdmin()) {
            throw new ClientResultException(Error.ADMIN_PERMISSION_ERROR);
        }
    }

    /**
     * Returns the byte size of the current log or a specific log file of the server. If the date query parameter
     * is specified, an explicitly selected log will be read.
     *
     * @param date A specific {@link Date} to extract from the server log.
     * @return number the length of the requested log
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull Integer fetchLogLength(@Nullable Date date) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();
        if (date != null) {
            searchParams.add(new BasicNameValuePair("date", DateFormatUtils.format(date, "yyyy-MM-dd")));
        }

        try( ClassicHttpResponse response = HttpRestRequest.createRequest(this.session)
                    .setAcceptHeader(DataFormat.ANY.getMimeType())
                    .buildRequest(HttpMethod.HEAD, this.session.getURI("admin/server/log", searchParams))
                    .executeRequest()){

            int contentLength = 0;

            Header contentLengthHeader = response.getHeader(HttpHeaders.CONTENT_LENGTH.toLowerCase());
            if (contentLengthHeader != null) {
                contentLength = Integer.parseInt(contentLengthHeader.getValue());
            }

            return contentLength;
        } catch (IOException | ProtocolException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * Returns the byte size of the current log file of the server.
     *
     * @return number the length of the requested log
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public @NotNull Integer fetchLogLength() throws ResultException {
        return this.fetchLogLength(null);
    }

    /**
     * Returns the contents of the current log or a specific log file of the server. If the date query parameter
     * is specified, an explicitly selected log will be read.
     *
     * @param range A specific byte range (e.g. 0-1024) to extract from the server log
     * @param date  A specific optional {@link Date} to extract from the server log. By default, today's date will be used.
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull String fetchLog(@NotNull String range, @Nullable Date date) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();
        if (date != null) {
            searchParams.add(new BasicNameValuePair("date", DateFormatUtils.format(date, "yyyy-MM-dd")));
        }

        String logContent = HttpRestRequest.createRequest(this.session)
                .setAcceptHeader(DataFormat.PLAIN.getMimeType())
                .setAdditionalHeader(HttpHeaders.RANGE, "bytes=" + range)
                .buildRequest(HttpMethod.GET, this.session.getURI("admin/server/log", searchParams))
                .executeRequest(String.class);

        if (logContent == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return logContent;
    }

    /**
     * Returns the contents of the current log file of the server.
     *
     * @param range A specific byte range (e.g. 0-1024) to extract from the server log
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public @NotNull String fetchLog(@NotNull String range) throws ResultException {
        return this.fetchLog(range, null);
    }

    /**
     * Returns the contents of the current log file of the server.
     *
     * @return number The contents of the current log or a specific log file of the server
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public @NotNull String fetchLog() throws ResultException {
        return this.fetchLog("0-");
    }

    /**
     * Provides status information about the server, the JVM and the Web services.
     *
     * @return {@link AdminServerStatus} The status information about the server.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminServerStatus fetchServerStatus() throws ResultException {
        this.validateUser();

        AdminServerStatus status = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/server/status")
                .executeRequest(AdminServerStatus.class);

        if (status == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return status;
    }

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
    @Override
    public void buildSupportPackage(
            @NotNull OutputStream outputStream, @Nullable AdminSupportEntryGroup[] group, @Nullable Date start, @Nullable Date end
    ) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();

        if (group != null) {
            for (AdminSupportEntryGroup entryGroup : group) {
                if (entryGroup != null && entryGroup.getValue() != null) {
                    searchParams.add(new BasicNameValuePair("group", entryGroup.getValue()));
                }
            }
        }

        if (start != null) {
            searchParams.add(new BasicNameValuePair("start", DateFormatUtils.format(start, "yyyy-MM-dd")));
        }

        if (end != null) {
            searchParams.add(new BasicNameValuePair("end", DateFormatUtils.format(end, "yyyy-MM-dd")));
        }

        HttpRestRequest.createRequest(this.session)
                .setAcceptHeader(DataFormat.OCTET_STREAM.getMimeType())
                .buildRequest(HttpMethod.GET, this.session.getURI("admin/server/support", searchParams))
                .executeRequest(outputStream);
    }

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param outputStream The target {@link OutputStream} the support information shall be written to.
     * @param group        List of components to be included in the support information.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public void buildSupportPackage(@NotNull OutputStream outputStream, @Nullable AdminSupportEntryGroup[] group) throws ResultException {
        this.buildSupportPackage(outputStream, group, null, null);
    }

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param outputStream The target {@link OutputStream} the support information shall be written to.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public void buildSupportPackage(@NotNull OutputStream outputStream) throws ResultException {
        this.buildSupportPackage(outputStream, null, null, null);
    }

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
    @Override
    public void buildSupportPackage(
            @NotNull File target, @Nullable AdminSupportEntryGroup[] group, @Nullable Date start, @Nullable Date end
    ) throws ResultException {
        try (OutputStream outputStream = new FileOutputStream(target)) {
            this.buildSupportPackage(outputStream, group, start, end);
        } catch (IOException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param target The target {@link File} the support information shall be written to.
     * @param group  List of components to be included in the support information.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public void buildSupportPackage(@NotNull File target, @Nullable AdminSupportEntryGroup[] group) throws ResultException {
        this.buildSupportPackage(target, group, null, null);
    }

    /**
     * Collects a set of support information, that may simplify finding the cause and solution of issues.
     *
     * @param target The target {@link File} the support information shall be written to.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public void buildSupportPackage(@NotNull File target) throws ResultException {
        this.buildSupportPackage(target, null, null, null);
    }

    /**
     * Restarts the server.
     *
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public void restartServer() throws ResultException {
        this.validateUser();

        try (ClassicHttpResponse ignored = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/server/restart")
                .executeRequest()) {
        } catch (IOException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * Gets the {@link ApplicationConfigApplication} configuration from the server updating the cached configuration.
     *
     * @return {@link ApplicationConfigApplication} the requested configuration.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull ApplicationConfigApplication fetchApplicationConfiguration() throws ResultException {
        this.validateUser();

        AdminApplicationConfiguration adminApplicationConfiguration = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/configuration/application")
                .executeRequest(AdminApplicationConfiguration.class);

        if (adminApplicationConfiguration == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        this.applicationConfiguration = adminApplicationConfiguration.getConfiguration();
        this.globalKeyStore = adminApplicationConfiguration.getGlobalKeyStore();

        return this.applicationConfiguration;
    }

    /**
     * Gets the cached {@link ApplicationConfigApplication} configuration of the server or fetches via
     * {@link AdministrationManager#fetchApplicationConfiguration} if cache is empty
     *
     * @return {@link ApplicationConfigApplication} the requested configuration.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull ApplicationConfigApplication getApplicationConfiguration() throws ResultException {
        this.validateUser();

        return this.applicationConfiguration == null ? this.fetchApplicationConfiguration() : this.applicationConfiguration;
    }

    /**
     * <p>
     * Updates the {@link ApplicationConfigApplication} configuration if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminApplicationCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminApplicationConfiguration applicationConfiguration = new AdminApplicationConfiguration();
        applicationConfiguration.setConfiguration(configuration);
        applicationConfiguration.setConfigurationChecks(checks);
        applicationConfiguration.setConfigurationMode(AdminConfigurationMode.WRITE);
        applicationConfiguration.setConfigurationType(AdminConfigurationType.APPLICATION);
        applicationConfiguration.setGlobalKeyStore(this.getGlobalKeyStore());

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(applicationConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        if (configurationResult.getError() != null && configurationResult.getError().getCode() != null &&
                configurationResult.getError().getCode() == 0) {
            this.applicationConfiguration = configuration;
        }

        return configurationResult;
    }

    /**
     * <p>
     * Updates the {@link ApplicationConfigApplication} configuration if no {@link AdminConfigurationResult#getError()} occured.
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
    public @NotNull AdminConfigurationResult updateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration
    ) throws ResultException {
        return this.updateApplicationConfiguration(configuration, new ArrayList<>());
    }

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
    @Override
    public @NotNull AdminConfigurationResult validateApplicationConfiguration(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminApplicationCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminApplicationConfiguration applicationConfiguration = new AdminApplicationConfiguration();
        applicationConfiguration.setConfiguration(configuration);
        applicationConfiguration.setConfigurationChecks(checks);
        applicationConfiguration.setConfigurationMode(AdminConfigurationMode.VALIDATE);
        applicationConfiguration.setConfigurationType(AdminConfigurationType.APPLICATION);
        applicationConfiguration.setGlobalKeyStore(this.getGlobalKeyStore());

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(applicationConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return configurationResult;
    }

    /**
     * Gets the {@link ServerConfigServer} configuration from the server updating the cached configuration.
     *
     * @return {@link ServerConfigServer} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull ServerConfigServer fetchServerConfiguration() throws ResultException {
        this.validateUser();

        AdminServerConfiguration adminServerConfiguration = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/configuration/server")
                .executeRequest(AdminServerConfiguration.class);

        if (adminServerConfiguration == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        this.serverConfiguration = adminServerConfiguration.getConfiguration();
        this.connectorKeyStore = adminServerConfiguration.getConnectorKeyStore();
        this.trustStoreKeyStore = adminServerConfiguration.getTrustStoreKeyStore();

        return this.serverConfiguration;
    }

    /**
     * Gets the cached {@link ServerConfigServer} configuration of the server or fetches via
     * {@link AdministrationManager#fetchServerConfiguration} if cache is empty
     *
     * @return {@link ServerConfigServer} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull ServerConfigServer getServerConfiguration() throws ResultException {
        this.validateUser();

        return this.serverConfiguration == null ? this.fetchServerConfiguration() : this.serverConfiguration;
    }

    /**
     * <p>
     * Updates the {@link ServerConfigServer} configuration if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateServerConfiguration(
            @NotNull ServerConfigServer configuration, @NotNull List<AdminServerCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminServerConfiguration serverConfiguration = new AdminServerConfiguration();
        serverConfiguration.setConfiguration(configuration);
        serverConfiguration.setConfigurationChecks(checks);
        serverConfiguration.setConfigurationMode(AdminConfigurationMode.WRITE);
        serverConfiguration.setConfigurationType(AdminConfigurationType.SERVER);
        serverConfiguration.setTrustStoreKeyStore(this.getTrustStoreKeyStore());
        serverConfiguration.setConnectorKeyStore(this.getConnectorKeyStore());

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(serverConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        if (configurationResult.getError() != null && configurationResult.getError().getCode() != null &&
                configurationResult.getError().getCode() == 0) {
            this.serverConfiguration = configuration;
        }

        return configurationResult;
    }

    /**
     * <p>
     * Updates the {@link ServerConfigServer} configuration if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateServerConfiguration(
            @NotNull ServerConfigServer configuration
    ) throws ResultException {
        return this.updateServerConfiguration(configuration, new ArrayList<>());
    }

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
    @Override
    public @NotNull AdminConfigurationResult validateServerConfiguration(
            @NotNull ServerConfigServer configuration, @NotNull List<AdminServerCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminServerConfiguration serverConfiguration = new AdminServerConfiguration();
        serverConfiguration.setConfiguration(configuration);
        serverConfiguration.setConfigurationChecks(checks);
        serverConfiguration.setConfigurationMode(AdminConfigurationMode.VALIDATE);
        serverConfiguration.setConfigurationType(AdminConfigurationType.SERVER);
        serverConfiguration.setTrustStoreKeyStore(this.getTrustStoreKeyStore());
        serverConfiguration.setConnectorKeyStore(this.getConnectorKeyStore());

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(serverConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return configurationResult;
    }

    /**
     * Gets the {@link UserConfigUsers} configuration of the server updating the cached configuration.
     *
     * @return {@link UserConfigUsers} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull UserConfigUsers fetchUserConfiguration() throws ResultException {
        this.validateUser();

        AdminUserConfiguration adminUserConfiguration = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/configuration/user")
                .executeRequest(AdminUserConfiguration.class);

        if (adminUserConfiguration == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        this.userConfiguration = adminUserConfiguration.getConfiguration();

        return this.userConfiguration;
    }

    /**
     * Gets the cached {@link UserConfigUsers} configuration of the server or fetches via
     * {@link AdministrationManager#fetchUserConfiguration} if cache is empty
     *
     * @return {@link UserConfigUsers} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull UserConfigUsers getUserConfiguration() throws ResultException {
        this.validateUser();

        return this.userConfiguration == null ? this.fetchUserConfiguration() : this.userConfiguration;
    }

    /**
     * <p>
     * Updates the {@link UserConfigUsers} configuration if no {@link AdminConfigurationResult#getError()} occured.
     * </p>
     * <p>
     * Optionally also validates the {@link UserConfigUsers} configuration with additional {@link AdminUserCheck}s.
     * </p>
     * <p>
     * <b>Be Aware:</b> Some of these changes might require a server restart to take effect.
     * </p>
     *
     * @param configuration The {@link UserConfigUsers} configuration lists users of the webPDF server.
     * @param checks        A list of {@link AdminUserCheck}s to validate the configuration
     * @return defines an extended {@link AdminConfigurationResult} for administrative configuration operations
     * when the {@link UserConfigUsers} configuration is updated.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminConfigurationResult updateUserConfiguration(
            @NotNull UserConfigUsers configuration, @NotNull List<AdminUserCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminUserConfiguration userConfiguration = new AdminUserConfiguration();
        userConfiguration.setConfiguration(configuration);
        userConfiguration.setConfigurationChecks(checks);
        userConfiguration.setConfigurationMode(AdminConfigurationMode.WRITE);
        userConfiguration.setConfigurationType(AdminConfigurationType.USER);

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(userConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        if (configurationResult.getError() != null && configurationResult.getError().getCode() != null &&
                configurationResult.getError().getCode() == 0) {
            this.userConfiguration = configuration;
        }

        return configurationResult;
    }

    /**
     * <p>
     * Updates the {@link UserConfigUsers} configuration if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateUserConfiguration(
            @NotNull UserConfigUsers configuration
    ) throws ResultException {
        return this.updateUserConfiguration(configuration, new ArrayList<>());
    }

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
    @Override
    public @NotNull AdminConfigurationResult validateUserConfiguration(
            @NotNull UserConfigUsers configuration, @NotNull List<AdminUserCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminUserConfiguration userConfiguration = new AdminUserConfiguration();
        userConfiguration.setConfiguration(configuration);
        userConfiguration.setConfigurationChecks(checks);
        userConfiguration.setConfigurationMode(AdminConfigurationMode.VALIDATE);
        userConfiguration.setConfigurationType(AdminConfigurationType.USER);

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(userConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return configurationResult;
    }

    /**
     * Gets the {@link AdminLogFileConfiguration} from the server updating the cached configuration.
     *
     * @return {@link AdminLogFileConfiguration} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminLogFileConfiguration fetchLogConfiguration() throws ResultException {
        this.validateUser();

        AdminLogConfiguration adminLogConfiguration = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/configuration/log")
                .executeRequest(AdminLogConfiguration.class);

        if (adminLogConfiguration == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        this.logConfiguration = adminLogConfiguration.getConfiguration();

        return this.logConfiguration;
    }

    /**
     * Gets the cached {@link AdminLogFileConfiguration} configuration of the server or fetches via
     * {@link AdministrationManager#fetchLogConfiguration} if cache is empty
     *
     * @return {@link AdminLogFileConfiguration} the requested configuration
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminLogFileConfiguration getLogConfiguration() throws ResultException {
        this.validateUser();

        return this.logConfiguration == null ? this.fetchLogConfiguration() : this.logConfiguration;
    }

    /**
     * <p>
     * Updates the {@link AdminLogFileConfiguration} if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration, @NotNull List<AdminLogCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminLogConfiguration logConfiguration = new AdminLogConfiguration();
        logConfiguration.setConfiguration(configuration);
        logConfiguration.setConfigurationChecks(checks);
        logConfiguration.setConfigurationMode(AdminConfigurationMode.WRITE);
        logConfiguration.setConfigurationType(AdminConfigurationType.LOG);

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(logConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        if (configurationResult.getError() != null && configurationResult.getError().getCode() != null &&
                configurationResult.getError().getCode() == 0) {
            this.logConfiguration = configuration;
        }

        return configurationResult;
    }

    /**
     * <p>
     * Updates the {@link AdminLogFileConfiguration} if no {@link AdminConfigurationResult#getError()} occured.
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
    @Override
    public @NotNull AdminConfigurationResult updateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration
    ) throws ResultException {
        return this.updateLogConfiguration(configuration, new ArrayList<>());
    }

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
    @Override
    public @NotNull AdminConfigurationResult validateLogConfiguration(
            @NotNull AdminLogFileConfiguration configuration, @NotNull List<AdminLogCheck> checks
    ) throws ResultException {
        this.validateUser();

        AdminLogConfiguration logConfiguration = new AdminLogConfiguration();
        logConfiguration.setConfiguration(configuration);
        logConfiguration.setConfigurationChecks(checks);
        logConfiguration.setConfigurationMode(AdminConfigurationMode.VALIDATE);
        logConfiguration.setConfigurationType(AdminConfigurationType.LOG);

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(logConfiguration))
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return configurationResult;
    }

    /**
     * Gets the {@link AdminGlobalKeyStore} of the server updating the cached keystore.
     *
     * @return {@link AdminGlobalKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminGlobalKeyStore fetchGlobalKeyStore() throws ResultException {
        this.validateUser();

        this.fetchApplicationConfiguration();

        if (this.globalKeyStore == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return this.globalKeyStore;
    }

    /**
     * Gets the cached {@link AdminGlobalKeyStore} of the server or fetches via
     * {@link AdministrationManager#fetchGlobalKeyStore} if cache is empty
     *
     * @return {@link AdminGlobalKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminGlobalKeyStore getGlobalKeyStore() throws ResultException {
        this.validateUser();

        return this.globalKeyStore == null ? this.fetchGlobalKeyStore() : this.globalKeyStore;
    }

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
    @Override
    public void setGlobalKeyStore(@NotNull AdminGlobalKeyStore keystore) {
        this.globalKeyStore = keystore;
    }

    /**
     * Gets the {@link AdminTrustStoreKeyStore} from the server updating the cached keystore.
     *
     * @return {@link AdminTrustStoreKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminTrustStoreKeyStore fetchTrustStoreKeyStore() throws ResultException {
        this.validateUser();

        this.fetchServerConfiguration();

        if (this.trustStoreKeyStore == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return this.trustStoreKeyStore;
    }

    /**
     * Gets the cached {@link AdminTrustStoreKeyStore} configuration of the server or fetches via
     * {@link AdministrationManager#fetchTrustStoreKeyStore} if cache is empty
     *
     * @return {@link AdminTrustStoreKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull AdminTrustStoreKeyStore getTrustStoreKeyStore() throws ResultException {
        this.validateUser();

        return this.trustStoreKeyStore == null ? this.fetchTrustStoreKeyStore() : this.trustStoreKeyStore;
    }

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
    @Override
    public void setTrustStoreKeyStore(@NotNull AdminTrustStoreKeyStore keystore) {
        this.trustStoreKeyStore = keystore;
    }

    /**
     * Gets the {@link List <AdminConnectorKeyStore>} from the server updating the cached keystore.
     *
     * @return {@link AdminConnectorKeyStore} the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull Map<String, AdminConnectorKeyStore> fetchConnectorKeyStore() throws ResultException {
        this.validateUser();

        this.fetchServerConfiguration();

        if (this.connectorKeyStore == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return this.connectorKeyStore;
    }

    /**
     * Gets the cached {@link AdminConnectorKeyStore}s of the server or fetches via
     * {@link AdministrationManager#fetchConnectorKeyStore} if cache is empty
     *
     * @return {@link AdminConnectorKeyStore}s the requested keystore
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull Map<String, AdminConnectorKeyStore> getConnectorKeyStore() throws ResultException {
        this.validateUser();

        return this.connectorKeyStore == null ? this.fetchConnectorKeyStore() : this.connectorKeyStore;
    }

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
    @Override
    public void setConnectorKeyStore(@NotNull Map<String, AdminConnectorKeyStore> keystore) {
        this.connectorKeyStore = keystore;
    }

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
    @Override
    public @NotNull AdminConfigurationResult testExecutables(
            @NotNull ApplicationConfigApplication configuration, @NotNull List<AdminExecutableName> executables
    ) throws ResultException {
        this.validateUser();

        AdminApplicationConfiguration applicationConfiguration = new AdminApplicationConfiguration();
        applicationConfiguration.setConfiguration(configuration);

        AdminExecutableApplicationCheck executableCheck = new AdminExecutableApplicationCheck();
        executableCheck.setCheckType(AdminApplicationCheckMode.EXECUTABLE);
        executableCheck.setExecutables(executables);

        List<AdminApplicationCheck> checks = new ArrayList<>();
        checks.add(executableCheck);

        applicationConfiguration.setConfigurationChecks(checks);
        applicationConfiguration.setConfigurationMode(AdminConfigurationMode.VALIDATE);
        applicationConfiguration.setConfigurationType(AdminConfigurationType.APPLICATION);
        applicationConfiguration.setGlobalKeyStore(this.getGlobalKeyStore());

        AdminConfigurationResult configurationResult = HttpRestRequest.createRequest(this.session)
                .buildRequest(
                        HttpMethod.POST, "admin/configuration/", this.prepareHttpEntity(applicationConfiguration)
                )
                .executeRequest(AdminConfigurationResult.class);

        if (configurationResult == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return configurationResult;
    }

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
    @Override
    public AdminFileDataStore fetchDatastore(
            @NotNull AdminFileGroupDataStore group, @Nullable String filename
    ) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();
        if (filename != null) {
            searchParams.add(new BasicNameValuePair("name", filename));
        }

        AdminFileDataStore fileDataStore = HttpRestRequest.createRequest(this.session)
                .buildRequest(
                        HttpMethod.GET, this.session.getURI("admin/datastore/" + group, searchParams), null
                )
                .executeRequest(AdminFileDataStore.class);

        if (fileDataStore == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return fileDataStore;
    }

    /**
     * Retrieves a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     * If {@link AdminFileGroupDataStore#GENERIC} is set, the file to get is referenced by the optional
     * filename parameter.
     *
     * @param group The group of datastore files to search for the file.
     * @return The requested {@link AdminFileDataStore}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public AdminFileDataStore fetchDatastore(@NotNull AdminFileGroupDataStore group) throws ResultException {
        return this.fetchDatastore(group, null);
    }

    /**
     * Updates a file in the server's data store.
     *
     * @param fileDataStore Bundles the file, the file group and the settings to be updated as {@link AdminFileDataStore}
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public void updateDatastore(@NotNull AdminFileDataStore fileDataStore) throws ResultException {
        this.validateUser();

        HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/datastore/", this.prepareHttpEntity(fileDataStore))
                .executeRequest(AdminFileDataStore.class);
    }

    /**
     * Deletes a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     * If {@link AdminFileGroupDataStore#GENERIC} is set, the file to delete is referenced by the optional
     * filename parameter.
     *
     * @param group    The group of datastore files to search for the file.
     * @param filename A filename to specify the file if {@link AdminFileGroupDataStore#GENERIC} is set
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public void deleteDatastore(@NotNull AdminFileGroupDataStore group, @Nullable String filename) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();
        if (filename != null) {
            searchParams.add(new BasicNameValuePair("name", filename));
        }

        try (ClassicHttpResponse ignored = HttpRestRequest.createRequest(this.session)
                .buildRequest(
                        HttpMethod.DELETE, this.session.getURI("admin/datastore/" + group, searchParams), null
                )
                .executeRequest()) {
        } catch (IOException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * Deletes a file, depending on the selected {@link AdminFileGroupDataStore}, from the server's data store.
     *
     * @param group The group of datastore files to search for the file.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    public void deleteDatastore(@NotNull AdminFileGroupDataStore group) throws ResultException {
        this.deleteDatastore(group, null);
    }

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
    @Override
    public @NotNull AdminStatistic fetchServerStatistic(
            @NotNull AdminDataSourceServerState dataSource, @NotNull AdminAggregationServerState aggregation,
            @NotNull List<Webservice> webservices, @NotNull Date start, @NotNull Date end
    ) throws ResultException {
        this.validateUser();

        List<NameValuePair> searchParams = new ArrayList<>();
        searchParams.add(new BasicNameValuePair("start", DateFormatUtils.format(
                start, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()
        )));
        searchParams.add(new BasicNameValuePair("end", DateFormatUtils.format(
                end, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()
        )));

        for (Webservice webservice : webservices) {
            searchParams.add(new BasicNameValuePair("webservice", webservice.getValue()));
        }

        AdminStatistic statistic = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, this.session.getURI(
                        "admin/statistic/" + dataSource.getValue() + "/" + aggregation.getValue(), searchParams
                ))
                .executeRequest(AdminStatistic.class);

        if (statistic == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return statistic;
    }

    /**
     * Returns the session table from server with detailed status information about each session.
     *
     * @return The requested {@link SessionTable}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @NotNull SessionTable fetchSessionTable() throws ResultException {
        this.validateUser();

        SessionTable sessionTable = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "admin/session/table")
                .executeRequest(SessionTable.class);

        if (sessionTable == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return sessionTable;
    }

    /**
     * Closes the session with the specified ID, by activating the session expiration. After the call, any access
     * to the session results in an error.
     *
     * @param sessionId ID of the session to be closed.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public void closeSession(@NotNull String sessionId) throws ResultException {
        this.validateUser();

        try (ClassicHttpResponse ignored = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, "admin/session/" + sessionId + "/close")
                .executeRequest()) {
        } catch (IOException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * Prepares a {@link HttpEntity} for internal requests to the webPDF server.
     *
     * @param parameter The parameters, that shall be used for the request.
     * @param <T>       The parameter type (data transfer object/bean) that shall be used.
     * @return The resulting state of the data transfer object.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private <T> @NotNull HttpEntity prepareHttpEntity(@NotNull T parameter) throws ResultException {
        try {
            return new StringEntity(SerializeHelper.toJSON(parameter),
                    ContentType.create(DataFormat.JSON.getMimeType(), StandardCharsets.UTF_8));
        } catch (UnsupportedCharsetException ex) {
            throw new ClientResultException(Error.XML_OR_JSON_CONVERSION_FAILURE, ex);
        }
    }
}
