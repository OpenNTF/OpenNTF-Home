/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bean;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.webinars.Presenter;

/**
 * Bean intended to be used in UI elements to translate presenter
 * IDs to {@link Presenter} objects.
 */
@RequestScoped
@Named("webinarPresentersBean")
public class WebinarPresentersBean {
	
	@Inject
	private Presenter.Repository presenterRepository;
	
	public Presenter getById(String presenterId) {
		if(StringUtil.isEmpty(presenterId)) {
			return null;
		}
		return presenterRepository.findByPresenterId(presenterId)
			.orElse(null);
	}
	
	public String getDisplayFormat(Collection<String> presenterIds) {
		if(presenterIds == null) {
			return null;
		}
		return presenterIds.stream()
			.map(this::getById)
			.filter(Objects::nonNull)
			.map(Presenter::getFullName)
			.collect(Collectors.joining(", "));
	}
}
