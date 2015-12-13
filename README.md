# alfresco-inform-policy-extension-repo v0.7

An Alfresco 5.0 extension. Informs interested users about document updates. 

### Installation
For Linux
* Clone the repo
* Run `mvn install`
from repo root (make sure that you have maven and jdk8 installed)
* Copy the .amp file from ***target/*** into ***{alfresco-directory}/amps/***
* Run `./{alfresco-directory}/bin/apply_amps.sh`

Now it's ready to work!

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

## v0.7
### DONE
* Email metadata customising.
* Correct exceptions and checks
* ~~Check templates in init~~ (hard to make, dropped)

Now you can set prefered Subject and From in global properties of extension. Also, extension changed in way to make it safer for errors.
