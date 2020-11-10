package com.gwm.clouduser.component;

import com.gw.bpm.flowable.model.RestVariable;
import com.gwm.clouduser.enums.ScopeEnum;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ProcessVariables
 * @Description: 流程变量
 * @Author: 99958168
 * @Date: 2020-06-11 16:49
 */
@Component
public class ProcessVariables {

    public RestVariable getRestVariable(String name, Object value) {
        RestVariable restVariable = new RestVariable();
        restVariable.setName(name);
        restVariable.setValue(value);
        return restVariable;
    }

    public RestVariable getRestVariableLocal(String name, Object value) {
        RestVariable restVariable = new RestVariable();
        restVariable.setName(name);
        restVariable.setValue(value);
        restVariable.setScope(ScopeEnum.local.name());
        return restVariable;
    }
}
