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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.factory.DefaultInterceptorFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class InstrumentInterceptorFactory extends DefaultInterceptorFactory {

	protected String metricRegistry;
	protected MetricRegistry registry = null;
	protected InvocationHandler invocationHandler = null;
	
	public InstrumentInterceptorFactory() {
		super();
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
	public Interceptor buildInterceptor(final InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
		
		final Interceptor target = super.buildInterceptor(interceptorConfig, interceptorRefParams);
		if( getInvocationHandler() != null ){
    		//返回Action的代理对象
    		return (Interceptor) Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    getInvocationHandler()
            );
    	}
		return target;
		
	}
	
}
