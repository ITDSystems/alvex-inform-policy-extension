# alfresco-inform-policy-extension-repo v0.8

This extension allows Alfresco 5 to inform users about changes in related documents.

## v0.8
### DONE
* "In Favorite" group.

With installed [In Favorites Association](https://github.com/malchun/alfresco-infavorites-document-association-repo) extension you could inform users about changes of their favorited documents

### Installation
For Linux:

1. Clone repo `git clone https://github.com/malchun/alfresco-inform-policy-extension-repo`
2. In repo folder checkout v0.7 branch `git checkout v0.8-dev`
3. Run `mvn install` (make sure that you have maven and jdk8 installed)
4. Copy the .amp file from **{repo-directory}/target/** into **{alfresco-directory}/amps/**
5. Run `./{alfresco-directory}/bin/apply_amps.sh`
6. Set your preferences in **alfresco-global.properties** (explained later)

Or instead you could just load .amp file from https://github.com/ITDSystems/alfresco-inform-policy-extension-repo/releases/tag/v0.7 and begin from step 4

**Warning!** Extension would not work without configured OutboundSMTP!

### Development build
You'll need test models with cm:person assocs for developemnt and debug. Sample model and context for it are located under **src/test/resources/alfresco/extension/**.
* Running embedded Tomcat with `./run.sh` activates it automatically.
* If you need a debug amp to run on external Alfresco, build it with `mvn -Pdevelopment clean package`
* On Share side edit **share-config-custom.xml** to include into **<types/>** section something like this:
```xml
         <type name="cm:content">
               <subtype name="myc:assocs" />
         </type>
```
* After that you can "Change type" of any file into custom **myc:assocs** to get cm:person assocs for debug purposes.

## Current
### DONE
* Some test
* ~~Check configured outbound mail from init~~ (pointless, dropped)
* "In Favorite" group.

### TODO:
* Article
* Inform about deletion
* Add user notifications preferences
* Add share extension for user preferences

### Usage
All preferences could be set in ***alfresco-global.properties***. This version has next preferences:
* Mail preferences
 * **documentchangeinform.mail.from** (String) - notification from address, not working if in your OutboundSMTP configuration mail.from.enabled false or not set!
 * **documentchangeinform.mail.subject** (String) - notification subject
* Group preferences
 * **documentchangeinform.creator** (booolean) - enable notifications for document creator
 * **documentchangeinform.lasteditor** (booolean) - same for last editor of document
 * **documentchangeinform.associated** (booolean) - same for everyone in target associations of document
 * **documentchangeinform.editors** (booolean) - and for version creators
 * **documentchangeinform.infavorites** (booolean) - also for favorited

After starting Alfresco with installed extension you can find email templates at **dictionary/Email Templates/Document Change Notification/** in repository and customize them if you need.

