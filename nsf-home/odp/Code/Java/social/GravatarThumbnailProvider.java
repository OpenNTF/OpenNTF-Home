package social;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;

import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.xml.bind.DatatypeConverter;
import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class GravatarThumbnailProvider extends AbstractPeopleDataProvider {
	
	@Override
	public int getWeight() {
		return WEIGHT_LOW;
	}

	@Override
	public Object getValue(PersonImpl person, Object key) {
		switch(StringUtil.toString(key)) {
		case "thumbnailUrl":
			String id = person.getId();
			if(StringUtil.isNotEmpty(id) && !"anonymous".equalsIgnoreCase(id)) {
				// Try to find their email address
				try {
					Session session = CDI.current().select(Session.class, NamedLiteral.of("dominoSession")).get();
					Directory dir = session.getDirectory();
					DirectoryNavigator nav = dir.lookupNames("($Users)", id, "InternetAddress");
					if(nav.findFirstMatch()) {
						List<?> vals = nav.getFirstItemValue();
						if(vals != null && !vals.isEmpty()) {
							String email = StringUtil.toString(vals.get(0));
							if(StringUtil.isNotEmpty(email)) {
								// If found, send them to Gravatar
								MessageDigest md = MessageDigest.getInstance("MD5");
							    md.update(email.getBytes());
							    byte[] digest = md.digest();
							    String md5 = DatatypeConverter.printHexBinary(digest).toLowerCase();
								return "http://www.gravatar.com/avatar/" + md5 + "?d=wavatar";
							}
						}
					}
				} catch(NotesException | NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				}
			}
			return null;
		default:
			return null;
		}
	}

	@Override
	public Class<?> getType(PersonImpl person, Object key) {
		switch(StringUtil.toString(key)) {
		case "thumbnailUrl":
			return String.class;
		default:
			return Object.class;
		}
	}

	@Override
	public void readValues(PersonImpl[] persons) {
		
	}


}
