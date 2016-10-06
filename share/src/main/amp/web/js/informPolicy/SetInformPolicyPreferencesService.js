define(["dojo/_base/declare",
        "alfresco/core/Core",
        "service/constants/Default",
        "alfresco/core/topics",
        "dojo/_base/lang"],
        function(declare, Core, AlfConstants, topics, lang) {

  return declare([Core], {

    constructor: function informPolicy_SetInformPolicyPreferencesService__constructor(args) {
      lang.mixin(this, args);
      this.alfSubscribe("SET_INFORM_POLICY_PREFERENCES_PUBLISH_TOPIC", lang.hitch(this, this.savePreferences));
    },

    savePreferences: function informPolicy_SetInformPolicyPreferencesService__savePreferences(payload) {
        /*
        my_payload = {preference:'com.itdhq.documentchangeinform.creator', value: true, alfTopic: 'SET_PREFERENCE'};
        var url = AlfConstants.PROXY_URI + "api/people/" + encodeURIComponent(AlfConstants.USERNAME) + "/preferences";

            // Set the remote preference...
            var preferenceObj = {};
            lang.setObject(my_payload.preference, my_payload.value, preferenceObj);
            var responseTopic = payload.alfTopic;
            if (payload.alfResponseTopic)
            {
               responseTopic = payload.alfResponseTopic;
            }
            this.serviceXhr({url : url,
                             preference: my_payload.preference,
                             value: my_payload.value,
                             data: preferenceObj,
                             method: "POST"});

        */
        this.alfPublish(topics.SET_PREFERENCE, {preference:'com.alvexcore.documentchangeinform.creator', value: payload.creator});
        this.alfPublish(topics.SET_PREFERENCE, {preference:'com.alvexcore.documentchangeinform.lasteditor', value: payload.lasteditor});
        this.alfPublish(topics.SET_PREFERENCE, {preference:'com.alvexcore.documentchangeinform.associated', value: payload.associated});
        this.alfPublish(topics.SET_PREFERENCE, {preference:'com.alvexcore.documentchangeinform.editor', value: payload.editor});
        this.alfPublish(topics.SET_PREFERENCE, {preference:'com.alvexcore.documentchangeinform.infavorites', value: payload.infavorites});
    }
  });
});
