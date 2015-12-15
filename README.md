# alfresco-inform-policy-extension-repo

An Alfresco 5.0 extension. Informs interested users about document updates. Current stable version is in **v0.7** branch.

### Installation
Check [**v0.7** branch][1].

### How it works
Extension contains AfterCreateVersionPolicy implementation, that collect all users in groups "Creator", "Last edtor", "Associated", "Editors" and informs them with emails if flag for group is "true".

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
##### git
* Releases
* Migrate in ITD repo


## [v0.7][1]
### DONE
* Email metadata customising.
* Correct exceptions and checks
* ~~Check templates in init~~ (hard to make, dropped)

Now you can set prefered Subject and From in global properties of extension. Also, extension changed in way to make it safer for errors.

[1]: https://github.com/malchun/alfresco-inform-policy-extension-repo/tree/v0.7