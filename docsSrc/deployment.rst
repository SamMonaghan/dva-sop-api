#######################
Deployment Instructions
#######################

To deploy to the devtest and staging environments, deploy either via FTP or source control as described below.  

To deploy to production, swap the Azure deployment slots via the Azure management interface.

* Target: 'dvasopapi'
* Source: 'dvasopapi-staging'

**********
FTP Deploy
**********

1. Build .war using Gradle 'war' task.
1. Rename the .war to 'ROOT.war'.
1. FTP the war to '/site/wwwroot/webapps' folder for the relevant Azure Deployment Slot. 
    - Configure the FTP user name and password via the Azure management interface.
    - The user name is prefaced with the deployment slot name. For example, 'dvasopapi_devtest\yourusername'.  The password is set 'Deployment Credentials' in the Azure management interface.

*********************
Source Control Deploy
*********************

1. Build the .war using the Gradle 'copyWar' task.  This builds the war and copies it to the 'webapps' directory.
1. Git commit and push.


