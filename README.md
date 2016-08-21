# BRANCH IS UNDER HEAVY DEVELOPMENT! DO NOT USE!
[![Build Status](https://travis-ci.org/ITDSystems/alvex-meta.svg?branch=master)](https://travis-ci.org/ITDSystems/alvex-inform-policy-extension)
# Alvex inform policy extension v1.1

This extension allows Alfresco 5 to inform users about changes in related documents. Current branch created to up extension to alvex standarts.

## v0.7
### DONE
* Email metadata customising.
* Correct exceptions and checks
* ~~Check templates in init~~ (hard to make, dropped)

Now you can set prefered Subject and From in global properties of extension. Also, extension changed in way to make it safer for errors.

### Installation
For Linux:

1. Download .amp file from [release v0.7](https://github.com/ITDSystems/alvex-inform-policy-extension/releases/tag/v0.7)
2. Copy the .amp file into **{alfresco-directory}/amps/**
3. Run `./{alfresco-directory}/bin/apply_amps.sh`
4. Set your preferences in **alfresco-global.properties** (explained later)

**Warning!** Extension would not work without configured OutboundSMTP!

### Known issues
Applying this extension to just installed Alfresco could broke sistem during bootstrap. Strongly recomended to install it only after the first start.

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

### TODO:
* Article
* Inform about deletion
* "In Favorite" group.
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

After starting Alfresco with installed extension you can find email templates at **dictionary/Email Templates/Document Change Notification/** in repository and customize them if you need.
