# alfresco-inform-policy-extension-repo v0.6

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
* Check templates in init
* Check configured outbound mail from init
* Email metadata customising.
* "In Favorite" group.
* Correct exceptions and checks