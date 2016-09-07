[![Build Status](https://travis-ci.org/ITDSystems/alvex-inform-policy-extension.svg?branch=master)](https://travis-ci.org/ITDSystems/alvex-inform-policy-extension)

Alvex inform policy extension
========================

This extension informs users about changes in documents they are watching for.

### v1.1
* Moved to alvex component
* "In Favorite" group added

### v0.7
* Email metadata customising.
* Correct exceptions and checks

**Warning!** Extension would not work without configured OutboundSMTP!

### Known issues
Applying this extension to just installed Alfresco could broke sistem during bootstrap. Strongly recomended to install it only after the first start.

Use
-----
All preferences could be set in ***alfresco-global.properties***:

* Mail preferences
 * **documentchangeinform.mail.from** (String) - notification from address, not working if in your OutboundSMTP configuration mail.from.enabled false or not set!
 * **documentchangeinform.mail.subject** (String) - notification subject
* Group preferences
 * **documentchangeinform.creator** (booolean) - enable notifications for document creator
 * **documentchangeinform.lasteditor** (booolean) - same for last editor of document
 * **documentchangeinform.associated** (booolean) - same for everyone in target associations of document
 * **documentchangeinform.editors** (booolean) - and for version creators
 * **documentchangeinform.infavorites** (booolean) - for all who favorited this document (requires [alvex-infavorites-document-associtation](https://github.com/ITDSystems/alvex-infavorites-document-association) component)

After starting Alfresco with installed extension you can find email templates at **Data Dictionary/Email Templates/Document Change Notification/**. Customize them if you need.

Roadmap | TODO:
-----
* Investigate the possibility of repairing tests
* Article
* Inform about deletion
* Add user notifications preferences
* Add share extension for user preferences

# Downloads

Alvex component builds are automatically published to [nexus.itdhq.com](http://nexus.itdhq.com) by Travis CI.

# Build

To build Alvex follow [this guide](https://github.com/ITDSystems/alvex#build-component-from-source).
