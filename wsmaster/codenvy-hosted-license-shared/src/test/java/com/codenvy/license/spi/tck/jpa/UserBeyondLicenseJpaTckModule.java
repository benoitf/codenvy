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
package com.codenvy.license.spi.tck.jpa;

import com.codenvy.license.spi.UserBeyondLicenseDao;
import com.codenvy.license.spi.impl.UserBeyondLicenseImpl;
import com.codenvy.license.spi.jpa.JpaUserBeyondLicenseDao;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.eclipse.che.api.core.jdbc.jpa.eclipselink.EntityListenerInjectionManagerInitializer;
import org.eclipse.che.api.core.jdbc.jpa.guice.JpaInitializer;
import org.eclipse.che.commons.test.tck.JpaCleaner;
import org.eclipse.che.commons.test.tck.TckModule;
import org.eclipse.che.commons.test.tck.TckResourcesCleaner;
import org.eclipse.che.commons.test.tck.repository.JpaTckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepository;

/**
 * @author Dmytro Nochevnov
 */
public class UserBeyondLicenseJpaTckModule extends TckModule {

    @Override
    protected void configure() {
        install(new JpaPersistModule("main"));
        bind(JpaInitializer.class).asEagerSingleton();
        bind(EntityListenerInjectionManagerInitializer.class).asEagerSingleton();
        bind(TckResourcesCleaner.class).to(JpaCleaner.class);
        bind(new TypeLiteral<TckRepository<UserBeyondLicenseImpl>>() {}).toInstance(new JpaTckRepository<>(UserBeyondLicenseImpl.class));

        bind(UserBeyondLicenseDao.class).to(JpaUserBeyondLicenseDao.class);
    }
}
