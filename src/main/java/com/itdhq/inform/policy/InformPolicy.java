package com.itdhq.inform.policy;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies.AfterCreateVersionPolicy;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Created by malchun on 11/13/15.
 */
public class InformPolicy
        implements AfterCreateVersionPolicy
{
    private Logger logger = Logger.getLogger(InformPolicy.class);
    private VersionService versionService;
    private PersonService personService;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    private ActionService actionService;

    private HashMap<String, String> templates;
    private String mailfrom;
    private String subject;
    private boolean creator;
    private boolean lasteditor;
    private boolean editors;
    private boolean associated;
    //private boolean infavorites;

    private Action mailAction;

    public void setVersionService(VersionService versionService) {this.versionService = versionService; }
    public void setNodeService(NodeService nodeService) {this.nodeService = nodeService; }
    public void setPersonService(PersonService personService) {this.personService = personService; }
    public void setActionService(ActionService actionService) {this.actionService = actionService; }
    public void setPolicyComponent(PolicyComponent policyComponent) {this.policyComponent = policyComponent; }
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {this.serviceRegistry = serviceRegistry; }


    public void setMailfrom(String mailfrom) {this.mailfrom = mailfrom; }
    public void setSubject(String subject) {this.subject = subject; }
    public void setCreator(boolean creator) {this.creator = creator; }
    public void setLasteditor(boolean lasteditor) {this.lasteditor = lasteditor; }
    public void setEditors(boolean editors) {this.editors = editors; }
    public void setAssociated(boolean associated) {this.associated = associated; }
    //public void setInfavorites(boolean infavorites) {this.infavorites = infavorites; }

    public void init()
    {
        logger.debug("Inform policy is online.");
        Behaviour afterCreateVersionBehaviour = new JavaBehaviour(this, "afterCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "afterCreateVersion"), InformPolicy.class, afterCreateVersionBehaviour);

        // Cause we already know where our templates are TODO change comment
        templates = new HashMap<>(5);
        templates.put("creator", "PATH:\"/app:company_home/app:dictionary/app:email_templates/cm:document_change_notification/cm:inform_mail_template_creator.html.ftl\"");
        templates.put("lasteditor", "PATH:\"/app:company_home/app:dictionary/app:email_templates/cm:document_change_notification/cm:inform_mail_template_lasteditor.html.ftl\"");
        templates.put("associated", "PATH:\"/app:company_home/app:dictionary/app:email_templates/cm:document_change_notification/cm:inform_mail_template_associated.html.ftl\"");
        templates.put("editors", "PATH:\"/app:company_home/app:dictionary/app:email_templates/cm:document_change_notification/cm:inform_mail_template_editors.html.ftl\"");
        //templates.put("infavorites", "PATH:\"/app:company_home/app:dictionary/app:email_templates/cm:document_change_notification/cm:inform_mail_template_infavorites.html.ftl\"");
    }

    @Override
    public void afterCreateVersion(NodeRef versionableNode, Version version) {
        HashSet<String> informedUsers = new HashSet<>();
        String lasteditorname = getLastEditor(version);
        String creatorname = getDocumentCreator(versionableNode);

        // Because I can!
        HashMap<String, Serializable> versioninfo = (HashMap<String, Serializable>) version.getVersionProperties();
        HashMap<String, Serializable> fortemplate = new HashMap<>();
        fortemplate.put("documentname", versioninfo.get("name").toString());
        fortemplate.put("updatedate", versioninfo.get("created").toString());
        fortemplate.put("versionlabel", versioninfo.get("versionLabel").toString());
        fortemplate.put("lasteditorname", lasteditorname);
        fortemplate.put("creatorname", creatorname);

        logger.debug("Notifying users about new version of the document :" + nodeService.getProperty(versionableNode, ContentModel.PROP_NAME));

        // Version creator
        if (creator) {
            logger.debug("Notifying creator of the document");
            NodeRef mailCreatorTemplate = getMailTemplate(templates.get("creator"));
            sendMail(creatorname, mailCreatorTemplate, fortemplate);
            informedUsers.add(creatorname);
        }

        // Last editor
        if (lasteditor) {
            logger.debug("Notifying last editor of the document");
            if (!informedUsers.contains(lasteditorname)) {
                NodeRef mailLastEditorTemplate = getMailTemplate(templates.get("lasteditor"));
                sendMail(lasteditorname, mailLastEditorTemplate, fortemplate);
                informedUsers.add(lasteditorname);
            }
        }

        // All associated
        if (associated) {
            logger.debug("Notifying users associated with the document");
            HashSet<String> associatedusernames = getAssociatedUsers(versionableNode);
            associatedusernames.removeAll(informedUsers);
            if (associatedusernames.size() > 0) {
                NodeRef mailAssociatedTemplate = getMailTemplate(templates.get("associated"));
                for (String user: associatedusernames)
                {
                    sendMail(user, mailAssociatedTemplate, fortemplate);
                }
                informedUsers.addAll(associatedusernames);
            }
        }

        // All editors
        if (editors) {
            logger.debug("Notifying all previous editors of the document");
            HashSet<String> editornames = getEditors(versionableNode);
            logger.debug("Editors :" + editornames.toString());
            editornames.removeAll(informedUsers);
            if (editornames.size() > 0) {
                NodeRef mailEditorsTemplate = getMailTemplate(templates.get("editors"));
                for (String user: editornames)
                {
                    sendMail(user, mailEditorsTemplate, fortemplate);
                }
                informedUsers.addAll(editornames);
            }
        }

        // TODO Favorites
        /*
        if (infavorites)
        {
            logger.debug("infavorites");
            informInFavoritesdUsers(versionableNode);
        }
        */
        logger.debug(informedUsers.toString());
    }

    private String getDocumentCreator(NodeRef document)
    {
        logger.debug("Getting creator for the document");
        String owner = (String) nodeService.getProperty(document, ContentModel.PROP_CREATOR);
        return owner;
    }

    private String getLastEditor(Version version)
    {
        logger.debug("Getting last editor for the document");
        String editor = (String) version.getVersionProperty("creator");
        return editor;
    }

    private HashSet<String> getEditors(NodeRef document)
    {
        logger.debug("Getting complete editors history for the document");
        VersionHistory versionHistory = versionService.getVersionHistory(document);
        ArrayList<Version> allVersions = new ArrayList(versionHistory.getAllVersions());

        HashSet<String> users = new HashSet<>();
        for(Version version: allVersions)
        {
            users.add((String) version.getVersionProperty("creator"));
        }
        return users;
    }

    private HashSet<String> getAssociatedUsers(NodeRef versionableNode)
    {
        logger.debug("Getting associated users for the document");
        HashSet <String> associatedUsers = new HashSet<>();
        ArrayList<AssociationRef> associationsTarget = (ArrayList<AssociationRef>) nodeService.getTargetAssocs(versionableNode, RegexQNamePattern.MATCH_ALL);
        for (AssociationRef assoc: associationsTarget)
        {
            NodeRef target = assoc.getTargetRef();
            if (nodeService.getType(target).equals(ContentModel.TYPE_PERSON)) {
                associatedUsers.add(personService.getPerson(target).getUserName());
            }
        }
        return associatedUsers;
    }

    private NodeRef getMailTemplate(String templatePATH) throws AlfrescoRuntimeException
    {
        logger.debug("Getting mail templates from repository");
        ResultSet resultSet = serviceRegistry.getSearchService().query(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"), SearchService.LANGUAGE_LUCENE, templatePATH);
        if (resultSet.length() == 0) {
            // Cause we have no better solution. Because policy works
            // during deployment of system exception causes crash.
            //throw new AlfrescoRuntimeException("Can't find email template!");
            logger.error("Template node not found!");
            return null;
        }
        return resultSet.getNodeRef(0);
    }

    public void setMailActionExecutor(Action mailAction)
    {
        if (null == this.mailAction) {

        } else {

        }
    }

    private void sendMail(String username, NodeRef emailTemplateNodeRef, HashMap<String, Serializable> fortemplate) throws AlfrescoRuntimeException
    {
        logger.debug("Sending notification to " + username);
        // Exit gracefully on missing template, since we can crash Alfresco startup otherwise.
        // It happens because startup procedure may bootstrap content, triggering our policy.
        if (null == emailTemplateNodeRef) {
            // Cause we have no better solution - 2.
            logger.error("Can't send email notification! Bad template node!");
            return;
        }
        try {
            Action mailAction = actionService.createAction(MailActionExecuter.NAME);
            mailAction.setParameterValue(MailActionExecuter.PARAM_TEMPLATE, emailTemplateNodeRef);
            mailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
            mailAction.setParameterValue(MailActionExecuter.PARAM_FROM, mailfrom);
            mailAction.setParameterValue(MailActionExecuter.PARAM_TO_MANY, username);

            // Here begins magic!
            Map<String, Serializable> templateArgs = (Map<String, Serializable>) fortemplate.clone();
            templateArgs.put("firstName", nodeService.getProperty(personService.getPerson(username), ContentModel.PROP_FIRSTNAME));
            templateArgs.put("lastName", nodeService.getProperty(personService.getPerson(username), ContentModel.PROP_LASTNAME));

            Map<String, Serializable> templateModel = new HashMap<>();
            templateModel.put("args", (Serializable) templateArgs);
            mailAction.setParameterValue(MailActionExecuter.PARAM_TEMPLATE_MODEL, (Serializable) templateModel);

            actionService.executeAction(mailAction, null);
        } catch (Exception e) {
            throw new AlfrescoRuntimeException("Can't send email!");
        }
    }
}
