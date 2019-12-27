/*
 * Copyright (c) 2018 (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.codahale.metrics.struts2;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.inject.Inject;

public class InstrumentActionFactory implements ActionFactory {

	protected ObjectFactory objectFactory;
	protected String metricRegistry;
	protected MetricRegistry registry = null;
	protected InvocationHandler invocationHandler = null;
	
    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.registry = SharedMetricRegistries.getOrCreate(getMetricRegistry());
    }
    
    public String getMetricRegistry() {
		return metricRegistry;
	}

	@Inject("metric-registry-name")
	public void setMetricRegistry(String metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
    
	public InvocationHandler getInvocationHandler() {
		return invocationHandler;
	}

	@Inject
	public void setInvocationHandler(InvocationHandler invocationHandler) {
		this.invocationHandler = invocationHandler;
	}

	@Override
    public Object buildAction(final String actionName, final String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
        //构建Action对象
    	final Object target = objectFactory.buildBean(config.getClassName(), extraContext);
    	if( getInvocationHandler() != null ){
    		//返回Action的代理对象
    		return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    getInvocationHandler()
            );
    	}
		return target;
    }
	
}
