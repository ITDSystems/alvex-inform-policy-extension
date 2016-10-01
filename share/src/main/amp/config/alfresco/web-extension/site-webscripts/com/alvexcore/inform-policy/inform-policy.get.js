function getUserPreferences() {
    var userPreferences = {};
    var prefs = JSON.parse(preferences.value);
    return prefs
};

var userPreferences = getUserPreferences();
has = function(obj, key) {
    return key.split(".").every(function(x) {
        if(typeof obj != "object" || obj === null || !(x in obj))
            return false;
        obj = obj[x];
        return true;
    });
}
var creator = true;
var lasteditor = true;
var associated = true;
var editor = true;
var infavorites = true;

if (has(userPreferences, 'com.alvexcore.documentchangeinform.creator')) {
    creator = userPreferences.com.alvexcore.documentchangeinform.creator;
}
if (has(userPreferences, 'com.alvexcore.documentchangeinform.lasteditor')) {
    lasteditor = userPreferences.com.alvexcore.documentchangeinform.lasteditor;
}
if (has(userPreferences, 'com.alvexcore.documentchangeinform.associated')) {
    associated = userPreferences.com.alvexcore.documentchangeinform.associated;
}
if (has(userPreferences, 'com.alvexcore.documentchangeinform.editor')) {
    editor = userPreferences.com.alvexcore.documentchangeinform.editor;
}
if (has(userPreferences, 'com.alvexcore.documentchangeinform.infavorites')) {
    infavorites = userPreferences.com.alvexcore.documentchangeinform.infavorites;
}

var form = {
    id : "TEST_FORM",
	name : "alfresco/forms/Form",
	config : {
        setValueTopic : "SET_INFORM_POLICY_EXISTENT_PREFERENCES_TOPIC",
        setValueTopicGlobalScope: true,
		showOkButton : true,
		okButtonLabel : "Save",
		okButtonPublishTopic : "SET_INFORM_POLICY_PREFERENCES_PUBLISH_TOPIC",
		okButtonPublishGlobal : true,

		showCancelButton : false,
		widgets : []
	}
};

var checkboxCreator = {
	name : "alfresco/forms/controls/DojoCheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_CREATOR",
		name : "creator",
		label : "Inform creator",
		description : "Check to be informed if creator.",
		value : creator
	}
};
form.config.widgets.push(checkboxCreator);

var checkboxLastEditor = {
	name : "alfresco/forms/controls/DojoCheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_LASTEDITOR",
		name : "lasteditor",
		label : "Inform last editor",
		description : "Check to be informed if last editor.",
		value : lasteditor
	}
};
form.config.widgets.push(checkboxLastEditor);

var checkboxAssociated = {
	name : "alfresco/forms/controls/DojoCheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_ASSOCIATED",
		name : "associated",
		label : "Inform associated",
		description : "Check to be informed if associated.",
		value : associated
	}
};
form.config.widgets.push(checkboxAssociated);

var checkboxEditor = {
	name : "alfresco/forms/controls/DojoCheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_EDITOR",
		name : "editor",
		label : "Inform editor",
		description : "Check to be informed if editor.",
		value : editor
	}
};
form.config.widgets.push(checkboxEditor);


var checkboxInfavorites = {
	name : "alfresco/forms/controls/DojoCheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_INFAVORITES",
		name : "infavorites",
		label : "Inform all infavorited",
		description : "Check to be informed if in favorites.",
		value : infavorites
	}
};
form.config.widgets.push(checkboxInfavorites);

model.jsonModel = {
    services: [
        "alfresco/services/CrudService",
        "alfresco/services/PreferenceService",
        "alfresco/services/UserService",
        "informPolicy/SetInformPolicyPreferencesService",
        {
          name: "alfresco/services/LoggingService",
          config: {
            loggingPreferences: {
              enabled: true,
              all: true
            }
          }
        }
    ],
    widgets: [{
        id: "SET_PAGE_TITLE",
        name: "alfresco/header/SetTitle",
        config: {
            title: "Inform policy configuration page."
        }
    },
    {
        id: "FORM_HORIZONTAL_WIDGET_LAYOUT",
        name: "alfresco/layout/HorizontalWidgets",
        config: {
            widgetWidth: 50,
            widgets: [
                    form
                ]
            }
    }]
};
