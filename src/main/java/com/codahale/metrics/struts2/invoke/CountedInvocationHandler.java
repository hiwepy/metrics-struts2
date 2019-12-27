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
package com.codahale.metrics.struts2.invoke;

import java.lang.reflect.Method;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.struts2.utils.MetricUtils;

public class CountedInvocationHandler extends AbstractInvocationHandler<Counted, Counter> {

	public static final Class<Counted> ANNOTATION = Counted.class;
	
	public CountedInvocationHandler(MetricRegistry metricRegistry) {
		super(metricRegistry, ANNOTATION);
	}
	
	@Override
	protected Counter buildMetric(MetricRegistry metricRegistry, String metricName, Counted annotation) {
		return metricRegistry.counter(metricName);
	}

	@Override
	protected String buildMetricName(Class<?> targetClass, Method method, Counted annotation) {
		return MetricUtils.forCountedMethod(targetClass, method, annotation);
	}

	@Override
	protected Object invoke(Object proxy, Method method, Object[] args, Counter counter, Counted annotation) throws Throwable {
		try {
			counter.inc();
			return method.invoke(proxy, args);
		}
		finally {
			if (!annotation.monotonic()) {
				counter.dec();
			}
		}
	}


}
