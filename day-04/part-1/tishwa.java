import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

class Solution {
    	
	public static Map<Integer, Guard> parseInput(String input) {
		/**
		 * parse the events to get, for each guard, all the naps they took
		 * @input a list of String that look like "YYYY-MM-DD HH:MM something happened"
		 * @return a map of guard ID to guard object, filled with naps
		 */
		String[] events = input.split("\n");
		Arrays.sort(events);

		Map<Integer, Guard> guards = new HashMap<Integer, Guard>();
		Guard currentGuard = null;
		for (String line: events) {
			// Get time informations
			int minute = Integer.parseInt(line.substring(15, 17));
			
			// Find out if its a starts shift / falls asleep / wakes up situation
			String situation = line.substring(19);
			if (situation.substring(0, 5).equals("Guard")) {
				// If it's a starts shift situation, get the guard's ID
				// and add it to the list of guards if they're not there
				int id = Integer.parseInt(situation.split("[# ]")[2]);
				currentGuard = guards.get(id);
				if (currentGuard == null) {
					currentGuard = new Guard(id);
					guards.put(id, currentGuard);
				}
			}
			else {
				currentGuard.addEvent(minute);
			}
		}
		return guards;
	}

    private static String solve(String input) {
		/**
		 * We want to find out from logs when the guards will most probably be asleep
		 * To do this, we need to find out which guard sleeps the most and when he sleeps
		 * @input a list of String that look like "YYYY-MM-DD HH:MM something happened"
		 * @return the id of the guard chosen time the minute when they will be asleep
		 */
		
		Map<Integer, Guard> guards = parseInput(input);
		
		// find out which guard has slept the most
		Guard guardMostAsleep = null;
		int minutesSlept = 0;
		for (int guardID: guards.keySet()) {
			Guard guard = guards.get(guardID);
            int timeAsleep = guard.getTotalNapDuration();
			if (timeAsleep > minutesSlept) {
				guardMostAsleep = guard;
				minutesSlept = timeAsleep;
			}
		}
		
		// find out when this guard was the most asleep
		int bestMinute = guardMostAsleep.getMinuteMostAsleep();
		
		// return ID of guard most asleep * minute when most asleep
		return Integer.toString(guardMostAsleep.id * bestMinute);
    };

    public static void main(String[] args) {
        String input = args[0];
        long startTime = System.currentTimeMillis();
        String result = solve(input);
        System.out.println("_duration: " + (System.currentTimeMillis() - startTime) + "\n" + result);
    }
}

class Guard {
	
	int id;
	List<Integer> naps = new ArrayList<Integer>();
	
	public Guard(int id) {
		this.id = id;
	}
	
	public void addEvent(int mn) {
		naps.add(mn);
	}
	
	public int getTotalNapDuration() {
		int totalNapDuration = 0;
		for (int i = 0; i < naps.size() - 1; i += 2) {
			totalNapDuration += naps.get(i+1) - naps.get(i);
		}
		return totalNapDuration;
	}
	
	public int getMinuteMostAsleep() {
		int[] minutes = new int[60];
		int start, end;
		for (int i = 0; i < naps.size(); i += 2) {
			start = naps.get(i);
			end = naps.get(i+1);
			for (int mn = start; mn < end; mn++) {
				minutes[mn] += 1;
			}
		}
		int minuteMostAsleep = 0, timeAsleepThisMinute = 0;
		for (int i = 0; i < minutes.length; i++) {
			if (minutes[i] > timeAsleepThisMinute) {
				minuteMostAsleep = i;
				timeAsleepThisMinute = minutes[i];
			}
		}
		return minuteMostAsleep;
	}
}
