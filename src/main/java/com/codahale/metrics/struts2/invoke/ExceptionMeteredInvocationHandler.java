/*
 * Copyright (c) 2018 (https://github.com/vindell).
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
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.struts2.utils.MetricUtils;

public class ExceptionMeteredInvocationHandler extends AbstractInvocationHandler<ExceptionMetered, Meter> {

	public static final Class<ExceptionMetered> ANNOTATION = ExceptionMetered.class;
	
	public ExceptionMeteredInvocationHandler(MetricRegistry metricRegistry) {
		super(metricRegistry, ANNOTATION);
	}
	
	@Override
	protected Meter buildMetric(MetricRegistry metricRegistry, String metricName, ExceptionMetered annotation) {
		return metricRegistry.meter(metricName);
	}

	@Override
	protected String buildMetricName(Class<?> targetClass, Method method, ExceptionMetered annotation) {
		return MetricUtils.forExceptionMeteredMethod(targetClass, method, annotation);
	}

	@Override
	protected Object invoke(Object proxy, Method method, Object[] args, Meter meter, ExceptionMetered annotation) throws Throwable {
		try {
			return method.invoke(proxy, args);
		}
		catch (Throwable t) {
			if (annotation.cause().isAssignableFrom(t.getClass())) {
				meter.mark();
			}
			throw t;
		}
	}


}
