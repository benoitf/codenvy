/*
 *  [2012] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.license.server;

import com.codenvy.license.api.CodenvyLicense;
import com.codenvy.license.api.CodenvyLicenseFactory;
import com.codenvy.license.api.InvalidLicenseException;
import com.codenvy.license.api.LicenseException;
import com.codenvy.license.api.LicenseNotFoundException;
import com.codenvy.license.spi.UserBeyondLicenseDao;
import com.codenvy.license.spi.impl.UserBeyondLicenseImpl;
import com.codenvy.swarm.client.SwarmDockerConnector;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.UserManager;
import org.eclipse.che.commons.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Anatoliy Bazko
 * @author Dmytro Nochevnov
 * @author Alexander Andrienko
 */
@Singleton
public class CodenvyLicenseManager {

    private final CodenvyLicenseFactory licenseFactory;
    private UserBeyondLicenseDao userBeyondLicenseDao;
    private final Path                  licenseFile;
    private final UserManager           userManager;
    private final SwarmDockerConnector  dockerConnector;

    @Inject
    public CodenvyLicenseManager(@Named("license-manager.license-file") String licenseFile,
                                 CodenvyLicenseFactory licenseFactory,
                                 UserManager userManager,
                                 SwarmDockerConnector dockerConnector,
                                 UserBeyondLicenseDao userBeyondLicenseDao) {
        this.licenseFactory = licenseFactory;
        this.userBeyondLicenseDao = userBeyondLicenseDao;
        this.licenseFile = Paths.get(licenseFile);
        this.userManager = userManager;
        this.dockerConnector = dockerConnector;
    }

    /**
     * Stores valid Codenvy license into the storage.
     *
     * @throws NullPointerException
     *         if {@code licenseText} is null
     * @throws LicenseException
     *         if error occurred while storing
     */
    public void store(@NotNull String licenseText) throws LicenseException {
        Objects.requireNonNull(licenseText, "Codenvy license must not be null");
        CodenvyLicense codenvyLicense = licenseFactory.create(licenseText);

        try {
            Files.write(licenseFile, codenvyLicense.getLicenseText().getBytes());
        } catch (IOException e) {
            throw new LicenseException(e.getMessage(), e);
        }
    }

    /**
     * Loads Codenvy license out of underlying storage.
     *
     * @throws LicenseNotFoundException
     *         if license not found
     * @throws InvalidLicenseException
     *         if license not valid
     * @throws LicenseException
     *         if error occurred while loading license
     */
    @Nullable
    public CodenvyLicense load() throws LicenseException {
        String licenseText;
        try {
            licenseText = new String(Files.readAllBytes(licenseFile), UTF_8);
        } catch (NoSuchFileException e) {
            throw new LicenseNotFoundException("Codenvy license not found");
        } catch (IOException e) {
            throw new LicenseException(e.getMessage(), e);
        }

        if (isNullOrEmpty(licenseText)) {
            throw new LicenseNotFoundException("Codenvy license not found");
        }

        return licenseFactory.create(licenseText);
    }

    /**
     * Deletes Codenvy license from the storage.
     *
     * @throws LicenseException
     *         if error occurred while deleting license
     */
    public void delete() throws LicenseException {
        try {
            Files.delete(licenseFile);
        } catch (NoSuchFileException e) {
            throw new LicenseNotFoundException("Codenvy license not found");
        } catch (IOException e) {
            throw new LicenseException(e.getMessage(), e);
        }
    }

    /**
     * Return true if only Codenvy usage meets the constrains of license properties or free usage properties.
     **/
    public boolean isCodenvyUsageLegal() throws ServerException, IOException {
        long actualUsers = userManager.getTotalCount();
        int actualServers = dockerConnector.getAvailableNodes().size();

        try {
            CodenvyLicense codenvyLicense = load();
            return codenvyLicense.isLicenseUsageLegal(actualUsers, actualServers);
        } catch (LicenseException e) {
            return CodenvyLicense.isFreeUsageLegal(actualUsers, actualServers);
        }
    }

    /**
     * Return true if number users complies Codenvy license terms.
     */
    public boolean isCodenvyUsersNumberLegal() throws ServerException, IOException {
        long actualUsers = userManager.getTotalCount();

        try {
            CodenvyLicense codenvyLicense = load();
            return codenvyLicense.isLicenseUsageLegal(actualUsers, 0);
        } catch (LicenseException e) {
            return CodenvyLicense.isFreeUsageLegal(actualUsers, 0);
        }
    }

    /**
     * Return true if only node number meets the constrains of license properties or free usage properties.
     * If nodeNumber == null, uses actual number of machine nodes.
     *
     * @param nodeNumber
     *         number of machine nodes.
     */
    public boolean isCodenvyNodesUsageLegal(Integer nodeNumber) throws IOException {
        if (nodeNumber == null) {
            nodeNumber = dockerConnector.getAvailableNodes().size();
        }

        try {
            CodenvyLicense codenvyLicense = load();
            return codenvyLicense.isLicenseNodesUsageLegal(nodeNumber);
        } catch (LicenseException e) {
            return CodenvyLicense.isFreeUsageLegal(0, nodeNumber);  // user number doesn't matter
        }
    }

    /**
     * TODO
     * @throws ServerException
     * @throws ForbiddenException
     * @throws IOException
     */
    public void newUserVerification(String email) throws ServerException, ForbiddenException, IOException {
        if (!isCodenvyUsersNumberLegal()) {
            UserBeyondLicenseImpl user = new UserBeyondLicenseImpl(email);
            try {
                userBeyondLicenseDao.create(user);
            } catch (ConflictException ce) {
                // ignore error of duplication in DB
            }

            throw new ForbiddenException(
                String.format("The user cannot be added. You have %s users in Codenvy which is the maximum allowed by your current license.",
                              userManager.getTotalCount()));
        }
    }

    private String getEmail(String token) {
        // TODO
        return null;
    }
}
