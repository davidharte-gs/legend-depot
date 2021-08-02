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

package org.finos.legend.depot.store.artifacts.services.file;

import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.store.artifacts.ArtifactLoadingException;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsProvider;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationLoader;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class FileGenerationsProviderImpl implements FileGenerationsProvider
{
    public static final ArtifactType FILE_GENERATION = ArtifactType.FILE_GENERATIONS;

    @Inject
    public FileGenerationsProviderImpl()
    {
        super();
    }

    @Override
    public List<FileGeneration> loadArtifacts(List<File> files)
    {
        List<FileGeneration> generations = new ArrayList<>();
        files.stream().filter(this::isGenerationFile).forEach(f ->
        {
            try (FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(f))
            {
                List<FileGeneration> fileGenerations = loader.getAllFileGenerations().collect(Collectors.toList());
                generations.addAll(fileGenerations);
            }
            catch (Exception e)
            {
                throw new ArtifactLoadingException(e.getMessage());
            }
        });
        return generations;
    }

    private boolean isGenerationFile(File file)
    {
        return file.getName().contains(ArtifactType.FILE_GENERATIONS.getModuleName());
    }

    @Override
    public ArtifactType getType()
    {
        return FILE_GENERATION;
    }

}
