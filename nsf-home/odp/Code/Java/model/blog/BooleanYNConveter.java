/**
 * Copyright (c) 2022-2023 Contributors to the OpenNTF Home App Project
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
package model.blog;

import jakarta.nosql.mapping.AttributeConverter;

public class BooleanYNConveter implements AttributeConverter<Boolean, Object> {

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		return attribute == null ? "n" : attribute ? "y" : "n";
	}

	@Override
	public Boolean convertToEntityAttribute(Object dbData) {
		return "y".equals(dbData) || Boolean.TRUE.equals(dbData);
	}

}
