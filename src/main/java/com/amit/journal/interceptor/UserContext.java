package com.amit.journal.interceptor;
import com.amit.journal.constants.Constants;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
public class UserContext {
    private static final Logger LOG = LogManager.getLogger(UserContext.class);
    private static final ThreadLocal<String> userInfo = new ThreadLocal<>();

    public static void setUserId(String userId) {

        if (CommonUtil.isNullOrEmpty(userId)) {
            LOG.warn("@ setUserId() userId  is null.");
        }
        userInfo.set(userId);
        ThreadContext.put(Constants.USERID, "[" + Constants.USERID+ ": " + userId+"]");
    }

    public static String getUserId() {
        String userId = userInfo.get();
        if (!CommonUtil.isNullOrEmpty(userId)) {
            return userId;
        }
        LOG.warn("@ getUserId() userId  is null. ");
        return "";
    }
    public static void clearUserId() {
        userInfo.remove();
    }
}
