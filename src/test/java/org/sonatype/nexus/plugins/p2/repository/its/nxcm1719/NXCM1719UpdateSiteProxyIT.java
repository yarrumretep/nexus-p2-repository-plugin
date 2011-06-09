/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.its.nxcm1719;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.plugins.p2.repository.its.AbstractNexusProxyP2IntegrationIT;
import org.sonatype.nexus.rest.model.RepositoryProxyResource;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.sonatype.nexus.test.utils.TestProperties;

public class NXCM1719UpdateSiteProxyIT
    extends AbstractNexusProxyP2IntegrationIT
{

    public NXCM1719UpdateSiteProxyIT()
    {
        super( "nxcm1719" );
    }

    @Test
    public void test()
        throws Exception
    {
        final String nexusTestRepoUrl = getNexusTestRepoUrl();

        final File installDir = new File( "target/eclipse/nxcm1719" );

        final String correctURL = TestProperties.getString( "proxy-repo-base-url" ) + "updatesite/";
        TaskScheduleUtil.waitForAllTasksToStop();

        // try
        // {
        Response response = RequestFacade.doGetRequest( "content/repositories/" + getTestRepositoryId() + "/features/" );
        final String responseText = response.getEntity().getText();
        Assert.assertFalse( "response: " + response.getStatus() + "\n" + responseText, response.getStatus().isSuccess() );

        // installUsingP2(
        // nexusTestRepoUrl,
        // "com.sonatype.nexus.p2.its.feature.feature.group",
        // installDir.getCanonicalPath() );
        // Assert.fail( "Expected failer, because the remote URL is wrong" );
        // }
        // catch( Exception e )
        // {
        // // expected
        // }

        final RepositoryMessageUtil repoUtil =
            new RepositoryMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
        final RepositoryProxyResource repo = (RepositoryProxyResource) repoUtil.getRepository( getTestRepositoryId() );

        repo.getRemoteStorage().setRemoteStorageUrl( correctURL );
        repoUtil.updateRepo( repo );

        // wait for the tasks
        TaskScheduleUtil.waitForAllTasksToStop();

        response = RequestFacade.doGetRequest( "content/repositories/" + getTestRepositoryId() + "/features/" );
        Assert.assertTrue( "expected success: " + response.getStatus(), response.getStatus().isSuccess() );

        installUsingP2( nexusTestRepoUrl, "com.sonatype.nexus.p2.its.feature.feature.group",
            installDir.getCanonicalPath() );

        final File feature = new File( installDir, "features/com.sonatype.nexus.p2.its.feature_1.0.0" );
        Assert.assertTrue( feature.exists() && feature.isDirectory() );

        final File bundle = new File( installDir, "plugins/com.sonatype.nexus.p2.its.bundle_1.0.0.jar" );
        Assert.assertTrue( bundle.canRead() );
    }
}