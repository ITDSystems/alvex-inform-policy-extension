package com.itdhq.inform.policy.test;

/**
 * Created by malchun on 11/24/15.
 */

import com.itdhq.inform.policy.InformPolicy;
import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;
import junit.framework.TestCase;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.management.subsystems.ApplicationContextFactory;
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
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.bsf.util.IOUtils;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.repository.Authentication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

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
    private static final String DOCUMENT_NAME = "ChildDocumentWithVersionLabel.txt";
    private static final String PARENT_FOLDER_NAME = "ParentFolder-" + System.currentTimeMillis();

    private NodeRef document;
    private NodeRef parentFolder;
    private ArrayList<NodeRef> users;

    private MailActionExecuter ACTION_EXECUTER;
    private Boolean WAS_IN_TEST_MODE;

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
    
    @Autowired
    private ApplicationContext ctx;

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

        ACTION_EXECUTER = ctx.getBean("OutboundSMTP", ApplicationContextFactory.class)
                .getApplicationContext().getBean("mail", MailActionExecuter.class);
        WAS_IN_TEST_MODE = ACTION_EXECUTER.isTestMode();
        ACTION_EXECUTER.setTestMode(true);
        ACTION_EXECUTER.resetTestSentCount();

        /*
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("alfresco/application-context.xml");
        String encoding  = "UTF-8";
        try {
            log.debug(org.apache.commons.io.IOUtils.toString(in, encoding));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        users = new ArrayList<NodeRef>(4);

        authenticationService.authenticate(ADMIN_CREDENTIAL, ADMIN_CREDENTIAL.toCharArray());

        // Generating additional users
        users.add(createUser("user0", "User0", "Creator", "user0_creator@test.com", "password"));
        users.add(createUser("user1", "User1", "LastEditor", "user1_lasteditor@test.com", "password"));
        users.add(createUser("user2", "User2", "Associated", "user2_associated@test.com", "password"));
        users.add(createUser("user3", "User3", "Editor", "user3_editor@test.com", "password"));
        //users.add(createUser("user4", "User4", "InFavorites", "user4_infavorits@test.com", "password"));

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
                properties.put(ContentModel.PROP_MODIFIER, nodeService.getProperty(users.get(0), ContentModel.PROP_USERNAME));

                document = nodeService.createNode(parentFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(ContentModel.USER_MODEL_URI, DOCUMENT_NAME),
                        //document = nodeService.createNode(parentFolder, QName.createQName("http://www.mycompany.com/model/content/1.0", "assocs"), QName.createQName(ContentModel.USER_MODEL_URI, DOCUMENT_NAME),
                        ContentModel.TYPE_CONTENT, properties).getChildRef();
                contentService.getWriter(document, ContentModel.PROP_CONTENT, true).putContent("I'm a test document.");

                if (!nodeService.hasAspect(document, ContentModel.ASPECT_VERSIONABLE))
                {
                    Map<QName, Serializable> versionProperties = new HashMap<>();
                    versionProperties.put(ContentModel.PROP_VERSION_LABEL, "1.0");
                    versionProperties.put(ContentModel.PROP_INITIAL_VERSION, true);
                    versionProperties.put(ContentModel.PROP_VERSION_TYPE, VersionType.MAJOR);
                    versionProperties.put(ContentModel.PROP_CREATOR, nodeService.getProperty(users.get(0), ContentModel.PROP_USERNAME));
                    versionProperties.put(ContentModel.PROP_MODIFIER, nodeService.getProperty(users.get(0), ContentModel.PROP_USERNAME));
                    nodeService.addAspect(document, ContentModel.ASPECT_VERSIONABLE, versionProperties);
                }
                return null;
            }
        });
    }


    @Test
    public void mainTestCase()
    {
        log.debug("mainTestCase");
        log.debug(versionService.getCurrentVersion(document).getVersionProperties().toString());

        Map<String, Serializable> versionProperties = new HashMap<>();
        versionProperties.put("initialVersion", false);
        versionProperties.put("versionType", VersionType.MINOR);
        versionProperties.put("creator", nodeService.getProperty(users.get(3), ContentModel.PROP_USERNAME));
        versionProperties.put("modifier", nodeService.getProperty(users.get(3), ContentModel.PROP_USERNAME));
        versionService.createVersion(document, versionProperties);
        log.debug(versionService.getCurrentVersion(document).getVersionProperties().toString());

        versionProperties.clear();
        versionProperties = new HashMap<>();
        versionProperties.put("initialVersion", false);
        versionProperties.put("versionType", VersionType.MINOR);
        versionProperties.put("creator", nodeService.getProperty(users.get(1), ContentModel.PROP_USERNAME));
        versionProperties.put("modifier", nodeService.getProperty(users.get(1), ContentModel.PROP_USERNAME));
        versionService.createVersion(document, versionProperties);
        log.debug(versionService.getCurrentVersion(document).getVersionProperties().toString());


        versionProperties.clear();
        versionProperties = new HashMap<>();
        versionProperties.put("initialVersion", false);
        versionProperties.put("versionType", VersionType.MAJOR);
        versionProperties.put("creator", nodeService.getProperty(users.get(1), ContentModel.PROP_USERNAME));
        versionProperties.put("modifier", nodeService.getProperty(users.get(1), ContentModel.PROP_USERNAME));
        versionService.createVersion(document, versionProperties);
        log.debug(versionService.getCurrentVersion(document).getVersionProperties().toString());

        VersionHistory versionHistory = versionService.getVersionHistory(document);
        ArrayList<Version> allVersions = new ArrayList(versionHistory.getAllVersions());

        HashSet<String> users = new HashSet<>();
        for(Version version: allVersions)
        {
            log.debug("Versions : " + (String) version.getVersionProperty("creator"));
        }

        int numberOfMessages = ACTION_EXECUTER.getTestSentCount();
        Assert.assertEquals("Correct number of messages during the test", 8, numberOfMessages);
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
        if (users != null) {
            users.clear();
        }
        ACTION_EXECUTER.setTestMode(WAS_IN_TEST_MODE);
    }

    private NodeRef createUser(String username, String firstName, String lastName, String email, String passwd) {

        if (!authenticationService.authenticationExists(username)) {
            HashMap<QName, Serializable> properties = new HashMap<>();
            properties.put(ContentModel.PROP_USERNAME, username);
            properties.put(ContentModel.PROP_FIRSTNAME, firstName);
            properties.put(ContentModel.PROP_LASTNAME, lastName);
            properties.put(ContentModel.PROP_EMAIL, email);
            properties.put(ContentModel.PROP_PASSWORD, passwd);
            properties.put(ContentModel.PROP_ENABLED, true);

            return personService.createPerson(properties);
        } else {
            return personService.getPerson(username);
        }
    }
}
