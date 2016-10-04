var headerMenu = widgetUtils.findObject(model.jsonModel, "id", "HEADER_USER_MENU");
if (headerMenu != null) {
    var userMenuOthers = widgetUtils.findObject(model.jsonModel, "id", "HEADER_USER_MENU_OTHER_GROUP");
    if ( userMenuOthers != null ) {
        var OthersMenu = JSON.parse(JSON.stringify(userMenuOthers));
        widgetUtils.deleteObjectFromArray(model.jsonModel, "id", "HEADER_USER_MENU_OTHER_GROUP");
    }

    headerMenu.config.widgets.push({
       id: "HEADER_USER_MENU_INFORM_POLICY_GROUP",
           name: "alfresco/menus/AlfMenuGroup",
           config:
           {
               label: "inform_policy.group.label",
               widgets:
               [
                   {
                       id: "HEADER_USER_MENU_INFORM_POLICY_SETTINGS",
                       name: "alfresco/header/AlfMenuItem",
                       config:
                       {
                            label: "inform_policy.link.label",
                            targetUrl: "hdp/ws/inform_policy_config"
                       }
                   }
               ]
           }
    });
    headerMenu.config.widgets.push(OthersMenu);
}