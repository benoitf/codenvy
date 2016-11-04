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
package com.codenvy.license.spi.impl;

import com.codenvy.license.shared.model.UserBeyondLicense;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Data object for {@link UserBeyondLicense}.
 *
 * @author Dmytro Nochevnov
 */
@Entity(name = "user_beyondlicense")
@Table(indexes = {@Index(columnList = "email")})
public class UserBeyondLicenseImpl implements UserBeyondLicense {

    @Id
    private String email;

    public UserBeyondLicenseImpl(String email) {
        this.email = email;
    }

    public UserBeyondLicenseImpl() {}

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserBeyondLicenseImpl))
            return false;
        UserBeyondLicenseImpl that = (UserBeyondLicenseImpl)o;
        return Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return "UserBeyondLicenseImpl{" +
               "email='" + email + '\'' +
               '}';
    }

    @Override public String getEmail() {
        return this.email;
    }
}
