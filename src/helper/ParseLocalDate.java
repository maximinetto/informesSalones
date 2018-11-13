package helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ParseLocalDate {
	
	public static String parseToLocalDate(LocalDate entrada) {
		return entrada.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	}

	public static Date convertToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return 	dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
