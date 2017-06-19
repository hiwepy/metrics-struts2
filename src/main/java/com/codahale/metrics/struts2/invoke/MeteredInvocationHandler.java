/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
package com.codahale.metrics.struts2.invoke;

import java.lang.reflect.Method;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.struts2.utils.MetricUtils;

public class MeteredInvocationHandler extends AbstractInvocationHandler<Metered, Meter> {

	public static final Class<Metered> ANNOTATION = Metered.class;
	
	public MeteredInvocationHandler(MetricRegistry metricRegistry) {
		super(metricRegistry, ANNOTATION);
	}
	
	@Override
	protected Meter buildMetric(MetricRegistry metricRegistry, String metricName, Metered annotation) {
		return metricRegistry.meter(metricName);
	}

	@Override
	protected String buildMetricName(Class<?> targetClass, Method method, Metered annotation) {
		return MetricUtils.forMeteredMethod(targetClass, method, annotation);
	}

	@Override
	protected Object invoke(Object proxy, Method method, Object[] args, Meter meter, Metered annotation) throws Throwable {
		meter.mark();
		return method.invoke(proxy, args);
	}


}
