/*
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