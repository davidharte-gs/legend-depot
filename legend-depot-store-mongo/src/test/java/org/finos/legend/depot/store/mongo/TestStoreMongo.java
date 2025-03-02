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

package org.finos.legend.depot.store.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bson.Document;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.junit.After;
import org.junit.Assert;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public abstract class TestStoreMongo
{
    private MongoServer server = new MongoServer(new MemoryBackend());
    private MongoClient mongoClient = new MongoClient(new ServerAddress(server.bind()));
    protected MongoDatabase mongoProvider = mongoClient.getDatabase("test-db");

    protected  MongoClient getMongoClient()
    {
        return mongoClient;
    }

    public static List<ProjectData> readProjectConfigsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<ProjectData> projects = new ObjectMapper().readValue(jsonInput, new TypeReference<List<ProjectData>>()
            {
            });
            Assert.assertNotNull("testing file" + fileName.getFile(), projects);
            return projects;
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
        return null;
    }

    @After
    public void tearDownData()
    {
        this.mongoProvider.drop();
    }

    protected MongoDatabase getMongoDatabase()
    {
        return mongoProvider;
    }

    private MongoCollection getMongoProjects()
    {
        return getMongoDatabase().getCollection(ProjectsMongo.MONGO_PROJECTS);
    }

    private MongoCollection getMongoEntities()
    {
        return getMongoDatabase().getCollection(EntitiesMongo.ENTITIES_VERSIONS);
    }

    protected List<StoredEntity> readEntitiesFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoredEntity> entities = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoredEntity>>()
            {
            });
            Assert.assertNotNull("testing file" + fileName.getFile(), entities);
            return entities;
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test versioned entity metadata" + e.getMessage());
        }
        return null;
    }

    protected void setUpProjectsFromFile(URL projectConfigFile)
    {
        try
        {
            readProjectConfigsFile(projectConfigFile).forEach(project ->
            {
                try
                {
                    getMongoProjects().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test project " + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoProjects());
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    protected void setUpEntitiesDataFromFile(URL versionedEntities)
    {
        try
        {
            readEntitiesFile(versionedEntities).forEach(entity ->
            {
                try
                {
                    getMongoEntities().insertOne(Document.parse(new ObjectMapper().writeValueAsString(entity)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test entity" + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoEntities());
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test entity" + e.getMessage());
        }
    }

    protected Date toDate(LocalDateTime date)
    {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }
}
