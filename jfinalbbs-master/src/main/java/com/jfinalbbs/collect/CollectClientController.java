package com.jfinalbbs.collect;

import com.jfinalbbs.common.BaseController;
import com.jfinalbbs.common.Constants;
import com.jfinalbbs.user.User;
import com.jfinalbbs.utils.StrUtil;

import java.util.Date;

/**
 * Created by Tomoya.
 * Copyright (c) 2016, All Rights Reserved.
 * http://jfinalbbs.com
 */
public class CollectClientController extends BaseController {

    public void index() {
        String token = getPara("token");
        if(StrUtil.isBlank(token)) {
            error("请先登录");
        } else {
            //根据token获取用户信息
            User user = User.me.findByToken(token);
            if (user == null) {
                error("用户不存在，请退出重新登录");
            } else {
                String tid = getPara("tid");
                Collect collect = Collect.me.findByTidAndAuthorId(tid, user.getStr("id"));
                if (collect != null) {
                    error("已经收藏过，无需再次收藏");
                } else {
                    collect = new Collect();
                    boolean b = collect.set("tid", tid)
                            .set("author_id", user.get("id"))
                            .set("in_time", new Date()).save();
                    if (!b) {
                        error("收藏失败");
                    }
                    success();
                }
            }
        }
    }

    public void delete() {
        String token = getPara("token");
        if(StrUtil.isBlank(token)) {
            error("请先登录");
        } else {
            //根据token获取用户信息
            User user = User.me.findByToken(token);
            if (user == null) {
                error("用户不存在，请退出重新登录");
            } else {
                String tid = getPara("tid");
                Collect collect = Collect.me.findByTidAndAuthorId(tid, user.getStr("id"));
                if (collect == null) {
                    renderText(Constants.OP_ERROR_MESSAGE);
                } else {
                    boolean b = Collect.me.deleteByTidAndAuthorId(tid, user.getStr("id"));
                    if (!b) {
                        error("取消收藏失败");
                    }
                    success();
                }
            }
        }
    }
}
