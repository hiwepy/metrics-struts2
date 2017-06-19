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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.struts2.utils.MetricUtils;

public class TimedInvocationHandler extends AbstractInvocationHandler<Timed, Timer> {

	public static final Class<Timed> ANNOTATION = Timed.class;
	
	public TimedInvocationHandler(MetricRegistry metricRegistry) {
		super(metricRegistry, ANNOTATION);
	}
	
	@Override
	protected Timer buildMetric(MetricRegistry metricRegistry, String metricName, Timed annotation) {
		return metricRegistry.timer(metricName);
	}

	@Override
	protected String buildMetricName(Class<?> targetClass, Method method, Timed annotation) {
		return MetricUtils.forTimedMethod(targetClass, method, annotation);
	}

	@Override
	protected Object invoke(Object proxy, Method method, Object[] args, Timer timer, Timed annotation) throws Throwable {
		final Context timerCtx = timer.time();
		try {
			 //执行目标对象方法
			return method.invoke(proxy, args);
		}
		finally {
			timerCtx.close();
		}
	}


}
