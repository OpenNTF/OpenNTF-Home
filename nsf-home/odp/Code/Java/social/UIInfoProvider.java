package social;

import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;

public class UIInfoProvider extends AbstractPeopleDataProvider {

	@Override
	public Object getValue(PersonImpl person, Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getType(PersonImpl person, Object key) {
		return Object.class;
	}

	@Override
	public void readValues(PersonImpl[] persons) {
	}

}
