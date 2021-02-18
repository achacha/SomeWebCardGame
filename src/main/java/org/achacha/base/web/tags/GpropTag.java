package org.achacha.base.web.tags;

import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GpropTag extends SimpleTagSupport {
    private static final Logger LOGGER = LogManager.getLogger(GpropTag.class);

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void doTag() {
        try {
            getJspContext().getOut().print(Global.getInstance().getProperties().getProperty(key));
        } catch (Exception e) {
            LOGGER.error("Failed to render gprop JSP tag for key="+key, e);
        }
    }
}