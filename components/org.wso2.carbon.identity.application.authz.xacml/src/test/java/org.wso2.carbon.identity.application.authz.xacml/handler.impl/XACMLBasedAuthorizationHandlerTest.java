package org.wso2.carbon.identity.application.authz.xacml.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@PrepareForTest(LogFactory.class)
public class XACMLBasedAuthorizationHandlerTest {

    private XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler;
    HttpServletRequest mockHttpServletRequest;
    HttpServletResponse mockHttpServletResponse;
    AuthenticationContext context;

    @BeforeClass
    public void init() {
        xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
    }

    @Test
    public void testGetInstance() {

        XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        Assert.assertNotNull(xacmlBasedAuthorizationHandler);
        Assert.assertEquals(XACMLBasedAuthorizationHandler.getInstance(), xacmlBasedAuthorizationHandler);
    }

    @Test
    public void testIsAuthorized() {

        //check when context is null
        Assert.assertTrue(!xacmlBasedAuthorizationHandler.isAuthorized(mockHttpServletRequest,
                                                                       mockHttpServletResponse,
                                                                       context));

        PowerMockito.mockStatic(LogFactory.class);
        Log log = PowerMockito.mock(Log.class);
        PowerMockito.when(log.isDebugEnabled()).thenReturn(true);

    }

}
