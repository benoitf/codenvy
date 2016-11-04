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
package com.codenvy.license.spi.tck;

import com.codenvy.license.spi.UserBeyondLicenseDao;
import com.codenvy.license.spi.impl.UserBeyondLicenseImpl;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.commons.test.tck.TckListener;
import org.eclipse.che.commons.test.tck.repository.TckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepositoryException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@link UserBeyondLicenseDao} contract.
 *
 * @author Dmytro Nochevnov
 */
@Listeners(TckListener.class)
public class UserBeyondLicenseDaoTest {

    private UserBeyondLicenseImpl[] users;

    @Inject
    private UserBeyondLicenseDao userDao;

    @Inject
    private TckRepository<UserBeyondLicenseImpl> tckRepository;

    @AfterMethod
    private void cleanup() throws TckRepositoryException {
        tckRepository.removeAll();
    }

    @Test
    public void shouldCreateUser() throws Exception {
        // given
        final UserBeyondLicenseImpl user = new UserBeyondLicenseImpl("email1");

        // when
        userDao.create(user);

        // then
        assertEquals(userDao.getTotalCount(), 1);
    }


    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "User with email email1 already exists")
    public void shouldThrowConflictExceptionWhenCreateUser() throws Exception {
        // given
        final UserBeyondLicenseImpl user = new UserBeyondLicenseImpl("email1");
        userDao.create(user);

        // when
        userDao.create(user);
    }

    @Test
    public void shouldRemoveAllUsers() throws Exception {
        // given
        userDao.create(new UserBeyondLicenseImpl("email1"));
        userDao.create(new UserBeyondLicenseImpl("email2"));

        // when
        userDao.removeAll();

        // then
        assertEquals(userDao.getTotalCount(), 0);
    }
}
