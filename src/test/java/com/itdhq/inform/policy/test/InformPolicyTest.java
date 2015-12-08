package com.itdhq.inform.policy.test;

/**
 * Created by malchun on 11/24/15.
 */

import com.itdhq.inform.policy.InformPolicy;
import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;
import junit.framework.TestCase;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.repository.Authentication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertNotNull;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class InformPolicyTest extends TestCase
{
    static Logger log = Logger.getLogger(InformPolicyTest.class);

    @Autowired
    protected InformPolicy informPolicy;

    private static final String ADMIN_CREDENTIAL = "admin";
    private static final String ROOT_NODE_TERM = "PATH:\"/app\\:company_home\"";
    private static final String DOCUMENT_NAME = "ChildDocumentWithVersionLabel-.txt";
    private static final String PARENT_FOLDER_NAME = "ParentFolder-" + System.currentTimeMillis();

    private NodeRef document;
    private NodeRef parentFolder;
    private ArrayList<NodeRef> users;

    @Autowired
    @Qualifier("TransactionService")
    private TransactionService transactionService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    @Qualifier("AuthenticationService")
    private AuthenticationService authenticationService;

    @Autowired
    @Qualifier("PersonService")
    private PersonService personService;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("SearchService")
    private SearchService searchService;

    @Autowired
    @Qualifier("ContentService")
    private ContentService contentService;

    @Test
    public void testWiring()
    {
        log.debug("testWiring");
        assertNotNull(informPolicy);
    }

    @Before
    public void before()
    {
        log.debug("before");
        users = new ArrayList<NodeRef>(5);

        authenticationService.authenticate(ADMIN_CREDENTIAL, ADMIN_CREDENTIAL.toCharArray());

        // Generating additional users
        HashMap<QName, Serializable> properties = new HashMap<>();
        properties.put(ContentModel.PROP_USERNAME, "user1");
        properties.put(ContentModel.PROP_FIRSTNAME, "User1");
        properties.put(ContentModel.PROP_LASTNAME, "Creator");
        properties.put(ContentModel.PROP_EMAIL, "user1@test.com");
        properties.put(ContentModel.PROP_PASSWORD, "password");
        properties.put(ContentModel.PROP_ENABLED, true);

        NodeRef person1 = personService.createPerson(properties);
        users.add(person1);

        properties.clear();
        properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, "user2");
        properties.put(ContentModel.PROP_FIRSTNAME, "User2");
        properties.put(ContentModel.PROP_LASTNAME, "LastEditor");
        properties.put(ContentModel.PROP_EMAIL, "user2@test.com");
        properties.put(ContentModel.PROP_PASSWORD, "password");
        properties.put(ContentModel.PROP_ENABLED, true);

        NodeRef person2 = personService.createPerson(properties);
        users.add(person2);

        properties.clear();
        properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, "user3");
        properties.put(ContentModel.PROP_FIRSTNAME, "User3");
        properties.put(ContentModel.PROP_LASTNAME, "Associated");
        properties.put(ContentModel.PROP_EMAIL, "user3@test.com");
        properties.put(ContentModel.PROP_PASSWORD, "password");
        properties.put(ContentModel.PROP_ENABLED, true);

        NodeRef person3 = personService.createPerson(properties);
        users.add(person3);

        properties.clear();
        properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, "user4");
        properties.put(ContentModel.PROP_FIRSTNAME, "User4");
        properties.put(ContentModel.PROP_LASTNAME, "Editor");
        properties.put(ContentModel.PROP_EMAIL, "user4@test.com");
        properties.put(ContentModel.PROP_PASSWORD, "password");
        properties.put(ContentModel.PROP_ENABLED, true);

        NodeRef person4 = personService.createPerson(properties);
        users.add(person4);

        log.debug("Size: " + users.size());
        // Generating document
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                ResultSet query = null;
                NodeRef rootNode = null;
                try
                {
                    query = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, ROOT_NODE_TERM);
                    rootNode = query.getNodeRef(0);
                }
                finally
                {
                    if (null != query)
                    {
                        query.close();
                    }
                }

                Map<QName, Serializable> properties = new HashMap<>();
                properties.put(ContentModel.PROP_NAME, PARENT_FOLDER_NAME);
                parentFolder = nodeService.createNode(rootNode, ContentModel.ASSOC_CONTAINS, QName.createQName(ContentModel.USER_MODEL_URI, PARENT_FOLDER_NAME),
                        ContentModel.TYPE_FOLDER, properties).getChildRef();

                properties.clear();


                properties.put(ContentModel.PROP_NAME, DOCUMENT_NAME);
                properties.put(ContentModel.PROP_CREATOR, nodeService.getProperty(users.get(0), ContentModel.PROP_USERNAME));

                document = nodeService.createNode(parentFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(ContentModel.USER_MODEL_URI, DOCUMENT_NAME),
                        //document = nodeService.createNode(parentFolder, QName.createQName("http://www.mycompany.com/model/content/1.0", "assocs"), QName.createQName(ContentModel.USER_MODEL_URI, DOCUMENT_NAME),
                        ContentModel.TYPE_CONTENT, properties).getChildRef();
                contentService.getWriter(document, ContentModel.PROP_CONTENT, true).putContent("I'm a test document.");

                if (!nodeService.hasAspect(document, ContentModel.ASPECT_VERSIONABLE))
                {
                    Map<QName, Serializable> versionProperties = new HashMap<>();
                    versionProperties.put(ContentModel.PROP_VERSION_LABEL, "0.1");
                    versionProperties.put(ContentModel.PROP_INITIAL_VERSION, true);
                    versionProperties.put(ContentModel.PROP_VERSION_TYPE, VersionType.MINOR);
                    versionProperties.put(ContentModel.PROP_CREATOR, nodeService.getProperty(users.get(0), ContentModel.PROP_USERNAME));
                    nodeService.addAspect(document, ContentModel.ASPECT_VERSIONABLE, versionProperties);
                }
                log.debug(nodeService.getProperties(document).toString());
                log.debug(versionService.getCurrentVersion(document).getVersionProperties().toString());
                return null;
            }
        });
    }


    @Test
    public void mainTestCase()
    {
        log.debug("mainTestCase");
    }

    @After
    public void after()
    {
        log.debug("after");
        for (NodeRef user: users)
        {
            personService.deletePerson(user);
        }
        fileFolderService.delete(document);
        fileFolderService.delete(parentFolder);
        users.clear();
    }
}
