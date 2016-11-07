package com.jfinalbbs.interceptor;

import com.jfinalbbs.common.Constants;
import com.jfinalbbs.user.AdminUser;
import com.jfinalbbs.utils.StrUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * Created by Tomoya.
 * Copyright (c) 2016, All Rights Reserved.
 * http://jfinalbbs.com
 */
public class AdminUserInterceptor implements Interceptor {
    public void intercept(Invocation ai) {
        HttpServletRequest request = ai.getController().getRequest();
        HttpSession session = ai.getController().getSession();

        String namePwd = ai.getController().getCookie(Constants.COOKIE_ADMIN_TOKEN);
        AdminUser adminUser = (AdminUser) session.getAttribute(Constants.SESSION_ADMIN_USER);

        if(StrUtil.isBlank(namePwd) && adminUser != null) {
            ai.getController().setCookie(Constants.COOKIE_ADMIN_TOKEN, StrUtil.getEncryptionToken(adminUser.get("username") + "@&@" + adminUser.get("password")), 30 * 60 * 60 * 24);
        } else if(!StrUtil.isBlank(namePwd) && adminUser == null) {
            String[] strings = StrUtil.getDecryptToken(namePwd).split("@&@");
            adminUser = AdminUser.me.login(strings[0], strings[1]);
            session.setAttribute(Constants.SESSION_ADMIN_USER, adminUser);
        }

        if(StrUtil.isBlank(namePwd) && adminUser == null) {
            String uri = request.getRequestURI();
            String param = "";
            if (request.getQueryString() != null) {
                try {
                    param = new String(request.getQueryString().getBytes("ISO8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (!param.equals("")) uri += "?" + param;
            session.setAttribute(Constants.ADMIN_BEFORE_URL, uri);
            ai.getController().redirect(Constants.getBaseUrl() + "/adminlogin");
        } else {
            ai.invoke();
        }
    }
}
