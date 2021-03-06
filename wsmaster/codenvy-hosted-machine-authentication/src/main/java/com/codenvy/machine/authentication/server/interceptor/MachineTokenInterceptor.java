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
package com.codenvy.machine.authentication.server.interceptor;

import com.codenvy.machine.authentication.server.MachineTokenRegistry;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.che.api.core.model.workspace.Workspace;
import org.eclipse.che.commons.env.EnvironmentContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Intercepts calls to workspace start/stop methods and
 * creates machine authorization token in the registry.
 *
 * @author Max Shaposhnik (mshaposhnik@codenvy.com)
 */
@Singleton
public class MachineTokenInterceptor implements MethodInterceptor {

    @Inject
    MachineTokenRegistry tokenRegistry;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object result = invocation.proceed();

        if (result instanceof Workspace) {
            final Workspace workspace = ((Workspace)result);
            // generate machine token for user who start the workspace
            tokenRegistry.generateToken(EnvironmentContext.getCurrent().getSubject().getUserId(), workspace.getId());
        }
        return result;
    }
}
