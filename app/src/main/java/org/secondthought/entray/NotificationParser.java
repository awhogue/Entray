package org.secondthought.entray;

/**
 * Parse times entered in natural language-ish format.
 *
 * Created by ahogue on 8/7/15.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

public class NotificationParser {
    private static Pattern timeRegex =
            Pattern.compile(
                    "^(.*?)" +           // Prefix (can be empty)
                    "(?:at +|@ *)?" +    // "at" time (non-capturing)
                    "(1?\\d)" +          // Hour
                    ":?" +               // Hour/minute separator
                    "(\\d\\d)?" +        // Minute
                    "(am|pm)?" +         // AM/PM
                    " ?(tomorrow)?" +    // Day
                    ":?" +               // Possible suffix intro (e.g. "8pm: buy milk")
                    "(.*)$",             // Suffix (can be empty)
                    Pattern.CASE_INSENSITIVE);

    /**
     * Attempts to parse a time out of the given input. If no time is found, returns the current time.
     *
     * @param input The input string to parse.
     * @return A datetime based on the parse, or the current time if no time is found.
     */
    public static ParsedNotification parse(String input) {
        return parse(input, new DateTime());
    }

    /**
     * Attempts to parse a time out of the given input. If no time is found, returns the "now" time.
     *
     * @param input The input string to parse.
     * @param now The reference time when this string was input.
     * @return A datetime based on the parse, or the current time if no time is found.
     */
    public static ParsedNotification parse(String input, DateTime now) {
        Matcher m = timeRegex.matcher(input);
        if (m.find()) {
            Integer hour = Integer.parseInt(m.group(2));
            Integer minute = 0;
            if (null != m.group(3)) {
                minute = Integer.parseInt(m.group(3));
            }

            // Parse out day.
            Integer addDays = 0;
            if (null != m.group(5)) {
                String dayMod = m.group(5).toLowerCase();
                if (dayMod.equals("tomorrow")) {
                    addDays = 1;
                }
            }

            if (null != m.group(4)) {
                String ampm = m.group(4).toLowerCase();
                if (ampm.equals("pm") && hour != 12) {
                    hour += 12;
                } else if (ampm.equals("am") && hour == 12) {
                    hour = 0;
                }
            } else if (0 == addDays && hour < 12) {
                // If am/pm isn't specified, and the hour is an hour still coming up today, treat it as such.
                // E.g. if it's 3pm and the input says "430", treat that as 4:30pm today rather than 4:30am tomorrow.
                // Put another way, always find the *next* available 4:30.
                Integer minOfDay = (hour * 60) + minute;
                if (minOfDay < now.getMinuteOfDay() && (minOfDay + (12 * 60)) > now.getMinuteOfDay()) {
                    hour += 12;
                }
            }

            // Notification text is what's left once we've pulled out the parsed date.
            String notificationText = (m.group(1) + " " + m.group(6)).trim();

            if (0 != addDays) {
                return new ParsedNotification(input, notificationText,
                        now.plusDays(addDays).withHourOfDay(hour).withMinuteOfHour(minute));
            } else {
                DateTime adjusted = now.withHourOfDay(hour).withMinuteOfHour(minute);
                if (adjusted.isBefore(now)) {
                    // If the new time is before now, then we looped over a midnight, so we increment the day.
                    return new ParsedNotification(input, notificationText, adjusted.plusDays(1));
                } else {
                    return new ParsedNotification(input, notificationText, adjusted);
                }
            }
        } else {
            return new ParsedNotification(input, input.trim(), null);
        }
    }
}
