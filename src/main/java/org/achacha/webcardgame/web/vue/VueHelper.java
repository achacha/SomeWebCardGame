package org.achacha.webcardgame.web.vue;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.i18n.UIMessageHelper;

public class VueHelper {
    public static JsonObject buildData() {
        CallContext ctx = CallContextTls.get();
        Preconditions.checkNotNull(ctx);

        JsonObject jobj = new JsonObject();
        if (ctx.getLogin() != null) {
            jobj.addProperty(
                    "welcomeMessage",
                    UIMessageHelper.getInstance().getLocalizedMsg(
                            "home.welcome",
                            ctx.getLogin().getFname()
                    )
            );
            jobj.add("login", ctx.getLogin().toJsonObject());
        }
        return jobj;
    }
}
