# alfresco-inform-policy-extension-repo

An Alfresco 5.0 extension. Informs interested users about document updates. 

### Installation
For Linux
* Clone the repo
* Run $mvn install from the root (make sure that you have maven and jdk8 installed)
* Copy the .amp file from target/ into {alfresco-directory}/amps/
* Run $./{alfresco-directory}/bin/apply_amps.sh

### How it works
Extension contains AfterCreateVersionPolicy implementation, that collect all users in groups "Creator", "Last edtor", "Associated", "Editors" and informs them with emails if flag for group is "true".

### TODO:
* Test.
* Check configured outbound mail from init
* Inform about deletion
* "In Favorite" group.
* Add user notifications preferences

## v0.7
### DONE
* Email metadata customising.
* Correct exceptions and checks
* Check templates in init (hard to make, dropped)

Now you can set prefered Subject and From in global properties of extension. Also, extension changed in way to make it more safer for errors.