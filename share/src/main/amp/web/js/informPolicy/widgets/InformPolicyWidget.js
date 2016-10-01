define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/core/Core",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/InformPolicyWidget.html"
    ],
    function(declare, _Widget, Core, _Templated, template) {
        return declare([_Widget, Core, _Templated], {
            templateString: template,
            i18nRequirements: [ {i18nFile: "./i18n/InformPolicyWidget.properties"} ],
            cssRequirements: [{cssFile:"./css/InformPolicyWidget.css"}],
            
            buildRendering: function informPolicy_widgets_InformPolicyWidget__buildRendering() {
                this.greeting = this.message('hello-label');
                this.inherited(arguments);
            },

            postCreate: function informPolicy_widgets_InformPolicyWidget__postCreate() {
                this.alfPublish("INFORM_POLICY_WIDGET_ONLINE_TOPIC", true);
            }
        });
});