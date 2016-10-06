package com.alvexcore.repo.informPolicy.webscript;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * Created by azverev on 10/1/16.
 */
public class DocumentChangeInformGet
        extends AbstractWebScript
{
    private boolean creator;
    private boolean lasteditor;
    private boolean editors;
    private boolean associated;
    private boolean infavorites;

    public void setCreator(boolean creator) {this.creator = creator; }
    public void setLasteditor(boolean lasteditor) {this.lasteditor = lasteditor; }
    public void setEditors(boolean editors) {this.editors = editors; }
    public void setAssociated(boolean associated) {this.associated = associated; }
    public void setInfavorites(boolean infavorites) {this.infavorites = infavorites; }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        try
        {
            // build a json object
            JSONObject obj = new JSONObject();

            // put some data on it
            obj.put("creator", creator);
            obj.put("lasteditor", lasteditor);
            obj.put("editors", editors);
            obj.put("associated", associated);
            obj.put("infavorites", infavorites);
            // build a JSON string and send it back
            String jsonString = obj.toString();
            res.getWriter().write(jsonString);
        }
        catch(JSONException e)
        {
            throw new WebScriptException("Unable to serialize JSON");
        }
    }
}

