package bean;

import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
public class TranslationBean {
	@Inject
	HttpServletRequest request;

	@Produces @Named("translation")
	public ResourceBundle getTranslation() {
		return ResourceBundle.getBundle("translation", request.getLocale()); //$NON-NLS-1$
	}

	@RequestScoped
	@Named("messages")
	public static class Messages {
		@Inject
		HttpServletRequest request;

		public String format(final String key, final Object... params) {
			ResourceBundle translation = CDI.current().select(ResourceBundle.class, NamedLiteral.of("translation")).get(); //$NON-NLS-1$
			String message = translation.getString(key);
			return MessageFormat.format(message, params);
		}

		public String getMonth(final int index) {
			return DateFormatSymbols.getInstance(request.getLocale()).getMonths()[index];
		}
	}
}