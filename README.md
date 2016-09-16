[![Build Status](https://travis-ci.org/ITDSystems/alvex-inform-policy-extension.svg?branch=master)](https://travis-ci.org/ITDSystems/alvex-inform-policy-extension)

Alvex inform policy extension
========================

This extension informs users about changes in documents they are watching for. If someone updates a document, the following users receive email:
* creator of the document
* last editor of the document
* all editors of the document
* all users mentioned in associations of the document
* users favourited the document (requires [alvex-infavorites-document-associtation](https://github.com/ITDSystems/alvex-infavorites-document-association) component)

Compatible with Alfresco 5.1.

This component depends on:
* [Alvex Utils](https://github.com/ITDSystems/alvex-utils)

# Downloads

Download ready-to-use Alvex components via [Alvex](https://github.com/ITDSystems/alvex#downloads).

# Build from source

To build Alvex follow [this guide](https://github.com/ITDSystems/alvex#build-component-from-source).

# Use

All preferences could be set in **alfresco-global.properties**:

* Mail preferences
 * **documentchangeinform.mail.from** (String) - notification from address, not working if in your OutboundSMTP configuration mail.from.enabled false or not set!
 * **documentchangeinform.mail.subject** (String) - notification subject
* Group preferences
 * **documentchangeinform.creator** (booolean) - enable notifications for document creator
 * **documentchangeinform.lasteditor** (booolean) - enable notifications for last editor of document
 * **documentchangeinform.associated** (booolean) - enable notification for everyone in target associations of document
 * **documentchangeinform.editors** (booolean) - enable notification for all editors of document
 * **documentchangeinform.infavorites** (booolean) - enable notification for all who favorited this document (requires [alvex-infavorites-document-associtation](https://github.com/ITDSystems/alvex-infavorites-document-association) component)

Sample config:

```
documentchangeinform.mail.from=Alfresco instance <alvex@itdhq.com>
documentchangeinform.mail.subject=Notification
documentchangeinform.creator=true
documentchangeinform.lasteditor=true
documentchangeinform.associated=true
documentchangeinform.editors=true
documentchangeinform.infavorites=true
```

After starting Alfresco with installed extension you can find email templates at **Data Dictionary/Email Templates/Document Change Notification/**. Customize them if you need.

**Warning!** Extension would not work without configured OutboundSMTP!
