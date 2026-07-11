package model;

import org.eclipse.jnosql.communication.ValueReader;
import org.eclipse.jnosql.communication.ValueWriter;

import jakarta.annotation.Priority;
import model.projects.FeatureRequest;
import model.projects.FeatureRequest.Status;

@Priority(1)
public class FeatureRequestStatusProcessor implements ValueReader, ValueWriter<FeatureRequest.Status, String> {

	@Override
	public boolean test(Class<?> t) {
		return FeatureRequest.Status.class.equals(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(Class<T> type, Object value) {
		if(value == null || "".equals(value)) { //$NON-NLS-1$
			return (T)FeatureRequest.Status.Submitted;
		} else if("Added to app".equals(value)) { //$NON-NLS-1$
			return (T)FeatureRequest.Status.Added;
		} else {
			return (T)FeatureRequest.Status.valueOf(value.toString());
		}
	}

	@Override
	public String write(Status object) {
		return object.getValue();
	}

}
