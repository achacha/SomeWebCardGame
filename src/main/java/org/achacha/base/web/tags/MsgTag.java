package org.achacha.base.web.tags;


import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import org.achacha.base.i18n.UIMessageHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringWriter;

/**
 * JSP tag for converting resource IDs into their locale specific value
 * Data that starts with [ is parsed as CSV and [ is dropped
 * You cannot have both data and JSP body, body may override the attribute depending on implementation
 *
 * Usages:
 * <code>
 *     <i18n:msg key="some.resource.key"/>
 *     <i18n:msg key="some.resource.key.with.data"/>
 *     <i18n:msg key="some.resource.key.with.data">data to be substituted</i18n:msg>
 *     <i18n:msg key="some.resource.key.with.multiple.data" data="[data to be substituted 0,data 1,data 2"/>
 *     <i18n:msg key="some.resource.key.with.multiple.data">[data to be substituted 0,data 1,data 2</i18n:msg>
 * </code>
 */
public class MsgTag extends SimpleTagSupport {
    private static final Logger LOGGER = LogManager.getLogger(MsgTag.class);

    private String key;
    private String[] data;
    private boolean quote;

    public void setKey(String key) {
        this.key = key;
    }
    public void setData(String data) {
        if (data.length() > 0 && data.charAt(0) == '[') {
            this.data = data.substring(1).split(",");
        }
        else {
            this.data = new String[] { data };
        }
    }
    public void setQuote(boolean quote) {
        this.quote = quote;
    }

    @Override
    public void setJspBody(JspFragment jspBody) {
        StringWriter sw = new StringWriter();
        try {
            jspBody.invoke(sw);
            setData(sw.toString());
        } catch (Exception e) {
            LOGGER.error("Failed to invoke body for i18n JSP tag for key="+key, e);
        }
    }

    @Override
    public void doTag() {
        try {
            JspWriter writer = getJspContext().getOut();
            if (quote)
                writer.print("\"");

            writer.print(UIMessageHelper.getInstance().getLocalizedMsg(key, (Object[]) data));

            if (quote)
                writer.print("\"");
        } catch (Exception e) {
            LOGGER.error("Failed to render i18n JSP tag for key="+key, e);
        }
    }
}