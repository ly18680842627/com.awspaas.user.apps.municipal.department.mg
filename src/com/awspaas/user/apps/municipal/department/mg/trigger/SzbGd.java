package com.awspaas.user.apps.municipal.department.mg.trigger;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SzbGd extends InterruptListener implements InterruptListenerInterface {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String data = SDK.getRepositoryAPI().getProcessExtendAttribute(ctx.getTaskInstance().getProcessDefId());
        String boname = "";
        JSONArray jsonArray = JSONArray.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = (JSONObject) jsonArray.get(i);
            if (jsonObj.get("key").equals("boName")) {
                boname = jsonObj.get("value").toString();
            }
        }
        if (boname.isEmpty() || boname.equals("")) {
            return true;
        }
        String id = ctx.getProcessInstance().getId();
        BO engineerBo = SDK.getBOAPI().query(boname).detailByBindId(id);
        if (ctx.isChoiceActionMenu("同意")) {
            engineerBo.set("ZT", 1);

        } else if (ctx.isChoiceActionMenu("不同意")) {
            engineerBo.set("ZT", 0);

        } else if (ctx.isChoiceActionMenu("退回")) {
            engineerBo.set("ZT", 2);
        } else {
            engineerBo.set("ZT", 3);
        }
        SDK.getBOAPI().update(boname, engineerBo);
        return true;
    }
}
