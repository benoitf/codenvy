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
package com.codenvy.license.spi.jpa;

import com.codenvy.license.spi.UserBeyondLicenseDao;
import com.codenvy.license.spi.impl.UserBeyondLicenseImpl;
import com.google.inject.persist.Transactional;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.jdbc.jpa.DuplicateKeyException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * JPA based implementation of {@link UserBeyondLicenseDao}.
 *
 * @author Sergii Leschenko
 */
@Singleton
public class JpaUserBeyondLicenseDao implements UserBeyondLicenseDao {

    private final Provider<EntityManager> managerProvider;

    @Inject
    public JpaUserBeyondLicenseDao(Provider<EntityManager> managerProvider) {
        this.managerProvider = managerProvider;
    }

    @Override
    public void create(UserBeyondLicenseImpl user) throws ServerException, ConflictException {
        requireNonNull(user, "Required non-null user");
        try {
            doCreate(user);
        } catch (DuplicateKeyException e) {
            throw new ConflictException(format("User with email %s already exists", user.getEmail()));
        } catch (RuntimeException x) {
            throw new ServerException(x.getLocalizedMessage(), x);
        }
    }

    @Transactional
    protected void doCreate(UserBeyondLicenseImpl user) {
        managerProvider.get().persist(user);
    }

    @Override
    @Transactional
    public void removeAll() throws ServerException {
        final EntityManager manager = managerProvider.get();
        manager.createQuery("DELETE FROM user_beyondlicense")
               .executeUpdate();
    }

    @Override
    @Transactional
    public long getTotalCount() throws ServerException {
        final EntityManager manager = managerProvider.get();
        return manager.createQuery("SELECT COUNT(u) FROM user_beyondlicense u" , Long.class)
                      .getSingleResult();
    }
}
