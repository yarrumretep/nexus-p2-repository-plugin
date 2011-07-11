/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.internal.tasks;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.plugins.p2.repository.P2RepositoryAggregator;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesTask;
import org.sonatype.scheduling.SchedulerTask;

@Named( P2RepositoryAggregatorTaskDescriptor.ID )
public class P2RepositoryAggregatorTask
    extends AbstractNexusRepositoriesTask<Object>
    implements SchedulerTask<Object>
{

    private final P2RepositoryAggregator p2RepositoryAggregator;

    @Inject
    P2RepositoryAggregatorTask( final P2RepositoryAggregator p2RepositoryAggregator )
    {
        this.p2RepositoryAggregator = p2RepositoryAggregator;
    }

    @Override
    protected String getRepositoryFieldId()
    {
        return P2RepositoryAggregatorTaskDescriptor.REPO_OR_GROUP_FIELD_ID;
    }

    @Override
    protected String getAction()
    {
        return "REBUILD";
    }

    @Override
    protected String getMessage()
    {
        if ( getRepositoryId() != null )
        {
            return String.format( "Rebuild p2 repository on repository [%s] from root path and bellow",
                getRepositoryId() );
        }
        else
        {
            return "Rebuild p2 repository for all repositories (with a P2 Repository Generator Capability enabled)";
        }
    }

    @Override
    protected Object doRun()
        throws Exception
    {
        final String repositoryId = getRepositoryId();
        if ( repositoryId != null )
        {
            p2RepositoryAggregator.scanAndRebuild( repositoryId );
        }
        else
        {
            p2RepositoryAggregator.scanAndRebuild();
        }

        return null;
    }

}