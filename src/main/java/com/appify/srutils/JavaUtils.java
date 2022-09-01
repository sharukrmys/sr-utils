package com.appify.srutils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaUtils {
	
	public static final ZoneId DEFAULT_TZ = ZoneId.of("America/Los_Angeles");
	public static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	public static final Pattern PARENTHESES_PATTERN = Pattern.compile("\\(([^)]+)\\)");
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern NON_NUMBER_PATTERN = Pattern.compile("[^0-9]");
	public static final ObjectMapper OM = new ObjectMapper();
	public static final String UTF_8 = "UTF-8";

	public static boolean isNumeric(String str) {
		return str.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");
	}
	public static boolean isValidDate(String ip) {
		return ip.matches("((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])");
	}
	
	public static boolean isValidDateTime(String ip) {
		return ip.matches("\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2]\\d|3[0-1])T(?:[0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:\\.\\d+|)(?:Z|(?:\\+|\\-)(?:\\d{2}):?(?:\\d{2}))");
	}
	
	public static boolean isValidCurrency(String ip) {
		return ip.matches("^[+-]?(\\d*|\\d{1,3}(,\\d{3})*)(\\.\\d+)?\\b$");
	}
	
	public static boolean isValidTextLength(String ip, int length) {
		return ip.length() < length;
	}
	
	public static boolean isValidEmail(String ip) {
		return EMAIL_PATTERN.matcher(ip).matches();
	}
	
	public static boolean patternMatches(String emailAddress, String regexPattern) {
		return Pattern.compile(regexPattern).matcher(emailAddress).matches();
	}
	
	public static MultiValueMap<String, Object> convertJsonStringToFormData(String jsonInput) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(); 
		try {
			Map<String, Object> fields = doDeserialization(jsonInput);
			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				body.add(entry.getKey(), entry.getValue().toString());
			}
		} catch (IOException e) {
			System.err.println("Uanble to parse input string :"+jsonInput);
		}
		return body;
	}
	
	public static Map<String, String> convertJsonStringToMap(String jsonInput) {
		try {
			return OM.readValue(jsonInput, Map.class);
		} catch (IOException e) {
			System.err.println("Uanble to parse input string :"+jsonInput);
		}
		return new HashMap<>();
	}
	
	public static Map<String,Object> doDeserialization(String jsonStr) throws  IOException{
	    return OM.readValue(jsonStr, Map.class);
	}
	
	public static List<Map<String, String>> jsonArrayStrToList(String jsonStr) throws IOException {
		return OM.readValue(jsonStr, new TypeReference<List<Map<String, String>>>() {
		});
	}
	
	public static List<Map<String, Object>> jsonArrayStrToListObject(String jsonStr) throws IOException {
		return OM.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {
		});
	}
	
	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}
	
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !(collection == null || collection.isEmpty());
	}

	public static boolean isNotEmpty(Map collection) {
		return !(collection == null || collection.isEmpty());
	}
	
	public static boolean isNotEmpty(MultiValueMap<String, String> headers) {
		return !(headers == null || headers.isEmpty());
	}

	public static boolean isMapNotEmpty(MultiValueMap<String, Object> headers) {
		return !(headers == null || headers.isEmpty());
	}

//	public static boolean isNotEmpty(Map<String, Object> map) {
//		return !(map == null || map.isEmpty());
//	}
	
	public static <K, V> Map<K, V> sortByKeys(Map<K, V> unsortedMap) {
		return new TreeMap<>(unsortedMap);
	}

	
	public static Double sizeOfStringinKB(String input){
		try {
			return input.getBytes(UTF_8).length / Double.valueOf(1000);
		} catch (UnsupportedEncodingException e) {
			return 0.0;
		}
	}
	
	public static boolean isSpecialChar(String input) {
		Matcher m = SPECIAL_CHAR_PATTERN.matcher(input);
		return m.find();
	}
	
	public static List<Map<String, String>> getListFromJsonString(String jsonStr) throws JsonProcessingException {
		return OM.readValue(jsonStr, new TypeReference<List<Map<String, String>>>() {});
	}
	
	public static Map<String, String> getMapFromJsonString(String jsonStr) throws JsonProcessingException {
		return OM.readValue(jsonStr, new TypeReference<Map<String, String>>() {});
	}
	
	
	public static Map<String, String> getMapOfString(Map<String, Object> map) {
		Map<String, String> newMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof String) {
				newMap.put(entry.getKey(), (String) entry.getValue());
			} else if (!ObjectUtils.isEmpty(entry.getValue())) {
				if ((Boolean) entry.getValue()) {
					newMap.put(entry.getKey(), "true");
				} else {
					newMap.put(entry.getKey(), "false");
				}
			} else if (ObjectUtils.isEmpty(entry.getValue())) {
				newMap.put(entry.getKey(), null);
			} else {
				newMap.put(entry.getKey(), entry.getValue().toString());
			}
		}
		return newMap;
	}
	
	public static <T> T deepCopyByOM(Object ipData ,Class<T> requiredType) throws JsonProcessingException {
		return OM.readValue(OM.writeValueAsString(ipData), requiredType);	
	}
	
	public static String getFirstParenthesesContent(String inputStr) {
		Matcher m = PARENTHESES_PATTERN.matcher(inputStr);
		while (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	public static String getAllNumbers(String inputStr) {
		return NON_NUMBER_PATTERN.matcher(inputStr).replaceAll("");
	}
	
	public static List<String> getParenthesesContent(String inputStr) {
		List<String> content = new ArrayList<>();
		Matcher m = PARENTHESES_PATTERN.matcher(inputStr);
		while (m.find()) {
			content.add(m.group(1));
		}
		return content;
	}
	
	
	
	public static void sortRecords(List<Map<String, Object>> data, List<String> orderbyFields, boolean asc) {
		Comparator<Map<String, Object>> c = null;
		for (String orderby : orderbyFields) {
			Optional<Map<String, Object>> nonNullData = data.stream()
					.filter(a -> (a.containsKey(orderby.trim()) && a.get(orderby.trim()) != null)).findAny();
			if (nonNullData.isPresent() && nonNullData.get() != null && nonNullData.get().containsKey(orderby.trim())) {
				c = comparingWithDataType(asc, nonNullData.get().get(orderby.trim()), orderby.trim(), c);
			}
		}
		data.sort(c);
	}

	@SuppressWarnings("unchecked")
	private static Comparator<Map<String, Object>> comparingWithDataType(boolean asc, Object valueType, String value,
			Comparator<Map<String, Object>> c) {
		System.out.println(value);
		System.out.println(valueType.getClass().getSimpleName());
		if (valueType instanceof BigDecimal) {
			if (c != null) {
				return c.thenComparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : (BigDecimal) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			} else {
				return Comparator.comparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : (BigDecimal) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			}
		} else if (valueType instanceof Integer) {
			if (c != null) {
				return c.thenComparing((Map<String, Object> m) -> m.get(value) == null ? null : (Integer) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			} else {
				return Comparator.comparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : (Integer) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			}
		} else if (valueType instanceof Boolean) {
			if (c != null) {
				return c.thenComparing((Map<String, Object> m) -> m.get(value) == null ? null : (boolean) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			} else {
				return Comparator.comparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : (boolean) m.get(value),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			}
		} else {
			if (c != null) {
				return c.thenComparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : m.get(value).toString().toLowerCase(),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			} else {
				return Comparator.comparing(
						(Map<String, Object> m) -> m.get(value) == null ? null : m.get(value).toString().toLowerCase(),
						Comparator.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder()));
			}
		}
	}

	public static void sortList(List<Map<String, Object>> records, String orderField, boolean asc) {
		Object of = records.stream().filter(a -> a.get(orderField) != null).findAny().get().get(orderField);
		if (of != null) {
			log.info(of.getClass().getName() + " " + of);
			if (of instanceof BigDecimal) {
				records.sort(Comparator.comparing(
						map -> ((Map) map).get(orderField) == null ? null : (BigDecimal) ((Map) map).get(orderField),
						Comparator
								.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder())));
			} else if (of instanceof Boolean) {
				records.sort(Comparator.comparing(
						map -> ((Map) map).get(orderField) == null ? null : (boolean) ((Map) map).get(orderField),
						Comparator
								.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder())));
			} else {
				records.sort(Comparator.comparing(
						map -> ((Map) map).get(orderField) == null ? null
								: ((String) ((Map) map).get(orderField)).toLowerCase(),
						Comparator
								.nullsLast(asc ? (Comparator) Comparator.naturalOrder() : Comparator.reverseOrder())));
			}
		}
	}

	public static void sortMapByKey(List<Map<String, Object>> crList, final String sortKey, final boolean ascending) {
		Collections.sort(crList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Object obj1 = o1.get(sortKey);
				Object obj2 = o2.get(sortKey);
				if (obj1 != null && obj2 != null) {
					if (ascending)
						return obj1.toString().toLowerCase().compareTo(obj2.toString().toLowerCase());
					else
						return obj2.toString().toLowerCase().compareTo(obj1.toString().toLowerCase());
				} else
					return 0;
			}
		});
	}
	
	public static void sortMapByNumbericStringKey(List<Map<String, String>> crList, final String sortKey,
			final boolean ascending) {
		Collections.sort(crList, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				Integer obj1 = Integer.valueOf(o1.get(sortKey));
				Integer obj2 = Integer.valueOf(o2.get(sortKey));
				if (obj1 != null && obj2 != null) {
					if (ascending)
						return obj1.compareTo(obj2);
					else
						return obj2.compareTo(obj1);
				} else
					return 0;
			}
		});
	}
	
	
    public static void main(String[] args) throws UnsupportedEncodingException {
    	String inputVal = "shs@gmail.com";
    	System.out.println();
//    	System.out.println(getUserCurrentDateTime(null));
//    	System.out.println(getFileWithDate("input", "csv", "_", "dd MMM yyyy HH:mm:ss"));
//    	System.out.println(StringUtils.substringAfter("test.csv","."));
//    	System.out.println(getFileWithDate("test","csv", "_", "dd MMM yyyy HH:mm:ss"));
//    	System.out.println(isNumeric(" 1.11"));
//    	System.out.println(isNumeric("121.1 1"));
//    	System.out.println(isNumeric("121.1.1"));
//    	System.out.println(isNumeric("1.11"));
//
//    	System.out.println("Keysight Tech~001".matches("^.*Keysight.*$"));
//    	System.out.println("Keysight Tech~001".matches("^.*Keysight.*$"));
//
//    	System.out.println("Keysight Tech~001".matches("^.*Keysight.*$"));
//    	System.out.println("Keysight Tech~001".matches("^.*Keysight.*[~]001"));
//    	System.out.println("Keysight Tech~0012".matches("^.*Keysight.*[~]001*$"));
//    	System.out.println("Keysight Tech~001".matches("^.*Keysight.*$"));

	}
	@SuppressWarnings("unchecked")
	public static String extractJsonLevelData(String arr[] , int counter , Object response, int len) throws JsonMappingException, JsonProcessingException {
		if(counter >= len || response == null)
			return (String) response;
		Map<String , Object> responseMap = null;
		if(response instanceof String) {
			responseMap = new ObjectMapper().readValue(response.toString(), Map.class);
		}
		else if(response instanceof Map) {
			responseMap = (Map<String, Object>) response;
		}
		return extractJsonLevelData(arr , counter+1 , responseMap.get(arr[counter]) , len);
	}
	
}
