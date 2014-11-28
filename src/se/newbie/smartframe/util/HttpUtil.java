package se.newbie.smartframe.util;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

		public static Map<String, String> parseQueryString(String aQueryString) {
			if (aQueryString.indexOf("?") > -1) {
				aQueryString = aQueryString.split("?")[1];
			}
			if (aQueryString.indexOf("#") > -1) {
				aQueryString = aQueryString.split("#")[1];
			}			
			Map<String, String> parameters = new HashMap<String, String>();
			String[] querySegments = aQueryString.split("&");
			for (int i = 0; i < querySegments.length; i++) {
				String[] param = querySegments[i].split("=");
				parameters.put(param[0], param[1]);
			}
			return parameters;
		}
}
