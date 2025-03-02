//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.store.artifacts.services.entities;

import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntityArtifactsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;


@Singleton
public class VersionedEntityProvider extends EntityProvider implements VersionedEntityArtifactsProvider
{
    private static final String SEPARATOR = "-";

    @Inject
    public VersionedEntityProvider()
    {
        super();
    }

    @Override
    public ArtifactType getType()
    {
        return ArtifactType.VERSIONED_ENTITIES;
    }

    @Override
    public boolean matchesArtifactType(File file)
    {
        return file.getName().contains(SEPARATOR + getType().getModuleName() + SEPARATOR);
    }

}