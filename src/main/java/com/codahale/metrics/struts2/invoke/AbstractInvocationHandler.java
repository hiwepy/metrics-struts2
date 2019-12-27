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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

public abstract class AbstractInvocationHandler<A extends Annotation, M> implements InvocationHandler {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	protected final MetricRegistry metricRegistry;
	protected final Class<A> annotationClass;
	protected final ConcurrentMap<MethodKey, AnnotationMetricPair<A, M>> metrics;
	
	public AbstractInvocationHandler(final MetricRegistry metricRegistry, final Class<A> annotationClass) {
		this.metricRegistry = metricRegistry;
		this.annotationClass = annotationClass;
		this.metrics = new ConcurrentHashMap<MethodKey, AnnotationMetricPair<A, M>>();
		LOG.debug("Scanning for @{} annotated methods", annotationClass.getSimpleName());
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final AnnotationMetricPair<A, M> annotationMetricPair = metrics.get(MethodKey.forMethod(method));
		if (annotationMetricPair != null) {
			return invoke(proxy, method, args, annotationMetricPair.getMeter(), annotationMetricPair.getAnnotation());
		}
		else {
			return method.invoke(proxy, args);
		}
	}

	protected abstract String buildMetricName(Class<?> targetClass, Method method, A annotation);

	protected abstract M buildMetric(MetricRegistry metricRegistry, String metricName, A annotation);

	protected abstract Object invoke(Object proxy, Method method, Object[] args, M metric, A annotation) throws Throwable;

	public static final class AnnotationMetricPair<A extends Annotation, M> {
		
		private final A annotation;
		private final M meter;

		public AnnotationMetricPair(final A annotation, final M meter) {
			this.annotation = annotation;
			this.meter = meter;
		}

		public A getAnnotation() {
			return annotation;
		}

		public M getMeter() {
			return meter;
		}

	}

	
}
