class TimeAgo extends HTMLElement {
	static units = {
		year: 24 * 60 * 60 * 1000 * 365,
		month: 24 * 60 * 60 * 1000 * 365 / 12,
		day: 24 * 60 * 60 * 1000,
		hour: 60 * 60 * 1000,
		minute: 60 * 1000,
		second: 1000
	};
	static relativeFormat = new Intl.RelativeTimeFormat(document.documentElement.lang);
	static dateTimeFormat = new Intl.DateTimeFormat(document.documentElement.lang, { dateStyle: 'short', timeStyle: 'short' });
	static dateFormat = new Intl.DateTimeFormat(document.documentElement.lang, { dateStyle: 'medium' });

	constructor() {
		super();
	}

	connectedCallback() {
		let date = new Date(this.getAttribute("value"));

		this.innerText = this._relativize(date);
		if (this.getAttribute("value").indexOf("T") > -1) {
			this.title = TimeAgo.dateTimeFormat.format(date);
		} else {
			this.title = TimeAgo.dateFormat.format(date)
		}
	}

	_relativize(d1) {
		var elapsed = d1 - new Date();

		for (var u in TimeAgo.units) {
			if (Math.abs(elapsed) > TimeAgo.units[u] || u == 'second') {
				return TimeAgo.relativeFormat.format(Math.round(elapsed / TimeAgo.units[u]), u)
			}
		}
		return TimeAgo.dateTimeFormat.format(d1);
	}
}

customElements.define("time-ago", TimeAgo);