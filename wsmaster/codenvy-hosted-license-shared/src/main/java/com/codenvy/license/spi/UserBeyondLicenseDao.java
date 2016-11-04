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
package com.codenvy.license.spi;

import com.codenvy.license.shared.model.UserBeyondLicense;
import com.codenvy.license.spi.impl.UserBeyondLicenseImpl;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ServerException;

/**
 * Defines data access object for {@link UserBeyondLicenseImpl}
 *
 * @author Dmytro Nochevnov
 */
public interface UserBeyondLicenseDao {
    /**
     * Creates record with email of user beyond the Codenvy license capacity.
     *
     * @param user
     *        user beyond the license to create
     * @throws ServerException
     *         when any error occurs
     * @throws ConflictException
     *        when user with such email already exists
     */
    void create(UserBeyondLicenseImpl user) throws ServerException, ConflictException;

    /**
     * Removes all users beyond the Codenvy license capacity.
     *
     * @throws ServerException
     *         when any other error occurs during users removing
     */
    void removeAll() throws ServerException;

    /**
     * Get count of all users beyond the Codenvy license capacity.
     *
     * @return user count
     * @throws ServerException
     *         when any error occurs
     */
    long getTotalCount() throws ServerException;
}
