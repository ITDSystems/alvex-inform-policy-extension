var creator = true;
var lasteditor = true;
var associated = true;
var editor = true;
var infavorites = false;

// ... finally from user preferences
// Loading user preferences
function getUserPreferences() {
  var userPreferences = {};
  var prefs = JSON.parse(preferences.value);
  return prefs;
};

var userPreferences = getUserPreferences();

// 'in' analog with support of path through object.
function has(obj, key) {
  return key.split(".").every(function(x) {
      if(typeof obj != "object" || obj === null || !(x in obj))
          return false;
      obj = obj[x];
      return true;
  });
}

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
		okButtonLabel : "inform.form.button.save.label",
		okButtonPublishTopic : "SET_INFORM_POLICY_PREFERENCES_PUBLISH_TOPIC",//"ALF_SET_PREFERENCE",
		okButtonPublishGlobal : true,

		showCancelButton : true,
		cancelButtonLabel : "inform.form.button.cancel.label",
		cancelButtonPublishTopic : "ALF_RELOAD_PAGE",
		cancelButtonPublishGlobal : true,
		widgets : [],
	}
};

var checkboxCreator = {
	name : "alfresco/forms/controls/CheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_CREATOR",
		name : "creator",
		label : "inform.creator.checkbox.label",
		value : creator
	}
};
form.config.widgets.push(checkboxCreator);

var checkboxLastEditor = {
	name : "alfresco/forms/controls/CheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_LASTEDITOR",
		name : "lasteditor",
		label : "inform.lasteditor.checkbox.label",
		value : lasteditor
	}
};
form.config.widgets.push(checkboxLastEditor);

var checkboxAssociated = {
	name : "alfresco/forms/controls/CheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_ASSOCIATED",
		name : "associated",
		label : "inform.associated.checkbox.label",
		value : associated
	}
};
form.config.widgets.push(checkboxAssociated);

var checkboxEditor = {
	name : "alfresco/forms/controls/CheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_EDITOR",
		name : "editor",
		label : "inform.editor.checkbox.label",
		value : editor
	}
};
form.config.widgets.push(checkboxEditor);


var checkboxInfavorites = {
	name : "alfresco/forms/controls/CheckBox",
	config : {
		fieldId : "DOCUMENTCHANGEINFORM_INFAVORITES",
		name : "infavorites",
		label : "inform.infavorites.checkbox.label",
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
    ],
    widgets: [{
        id: "SET_PAGE_TITLE",
        name: "alfresco/header/SetTitle",
        config: {
            widgetWidth: 100,
            title: "Inform policy configuration page."
        }
    },
    {
        id: "FORM_HORIZONTAL_WIDGET_LAYOUT",
        name: "alfresco/layout/HorizontalWidgets",
        config: {
              widgetMarginLeft: "20",
              widgetMarginRight: "20",
              widgets: [
                  {
                      name: "alfresco/layout/TitleDescriptionAndContent",
                      config: {
                        title: "inform.page.title",
                        description: "inform.page.description",
                        widgets:[
                            form
                        ]
                      }
                  }
              ]
           }
    }]
};
