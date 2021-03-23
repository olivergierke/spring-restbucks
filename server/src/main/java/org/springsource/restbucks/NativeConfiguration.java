/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.restbucks;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ProxyBits;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springsource.restbucks.drinks.Drink;

/**
 * Additional configuration needed to produce Graal metadata to let some application properly work on it.
 *
 * @author Oliver Drotbohm
 */
@Configuration
// Standard user component requiring proxying due to @Async
@AotProxyHint(targetClassName = "org.springsource.restbucks.engine.Engine", proxyFeatures = ProxyBits.IS_STATIC)

// Due to DrinksOptions.BY_NAME (i.e. the usage of a domain type with Spring Data's TypedSort)
@AotProxyHint(targetClass = Drink.class, proxyFeatures = ProxyBits.IS_STATIC)

// Referred to by a custom AttributeConverter
// https://github.com/spring-projects-experimental/spring-native/issues/829
@TypeHint(types = { org.javamoney.moneta.Money.class }, access = AccessBits.LOAD_AND_CONSTRUCT)

// Needed to get @TransactionalEventListener on Engine.process(…) working
// https://github.com/spring-projects-experimental/spring-native/issues/828
@TypeHint(types = { EventListener.class, TransactionalEventListener.class }, access = AccessBits.ANNOTATION)
// Additional wrapping needed due to https://github.com/spring-projects-experimental/spring-native/issues/830
@NativeHint( //
		jdkProxies = @JdkProxyHint(types = { TransactionalEventListener.class, SynthesizedAnnotation.class }) //
)
class NativeConfiguration {}
