package model.blog;

import jakarta.nosql.mapping.AttributeConverter;

public class BooleanYNConveter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		return attribute == null ? "n" : attribute ? "y" : "n";
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData) {
		return "y".equals(dbData);
	}

}
