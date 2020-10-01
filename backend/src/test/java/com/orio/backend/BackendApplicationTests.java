package com.orio.backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {

		// work time
		String workTime = "12:20";

		// time format
		DateFormat dfOut = new SimpleDateFormat("kk:mm");
		DateFormat dfOut2 = new SimpleDateFormat("HH:mm");

		try {
			Date workDate = dfOut.parse(workTime);

			/* time sample */
			Date dDateA = dfOut.parse("00:45");
			Date dDateB = dfOut.parse("01:05");
			Date dDateC = dfOut.parse("01:25");
			Date dDate = dfOut.parse("00:00");

			/* define time */
			// min standard time a
			String mTimeA = "11:45";
			Date mDateA = dfOut.parse(mTimeA);
			// max a
			Date xDateA = new Date(mDateA.getTime() + (45 * 60000));

			// min standard time b
			String mTimeB = "17:20";
			Date mDateB = dfOut.parse(mTimeB);
			// max b
			Date xDateB = new Date(mDateB.getTime() + (20 * 60000));

			// min standard time c
			String mTimeC = "19:40";
			Date mDateC = dfOut.parse(mTimeC);
			// max c
			Date xDateC = new Date(mDateC.getTime() + (20 * 60000));

			/* calculate time */
			// time >= 20:00           => 01:25
			if (workDate.after(xDateC)){
				dDate = dDateC;
			}
			// 20:00 > time >= 19:40   => 01:05 + (time - 19:40)
			else if (workDate.compareTo(xDateC) < 0 && workDate.compareTo(mDateC) >= 0){
				long d = dDateB.getTime() + (workDate.getTime() - mDateC.getTime());
				dDate = new Date(d);
			}
			// 19:40 > time >= 17:40           => 01:05
			else if (workDate.compareTo(mDateC) < 0 && workDate.compareTo(xDateB) >= 0){
				dDate = dDateB;
			}
			// 17:40 > time >= 17:20   => 00:45 + (time - 17:20)
			else if (workDate.compareTo(xDateB) < 0 && workDate.compareTo(mDateB) >= 0){
				long d = dDateA.getTime() + (workDate.getTime() - mDateB.getTime());
				dDate = new Date(d);
			}
			// 17:20 > time >= 12:30            => 00:45
			else if (workDate.compareTo(mDateB) < 0 && workDate.compareTo(xDateA) >= 0){
				dDate = dDateA;
			}
			// 12:30 > time >= 11:45   => (time - 11:45)
			else if (workDate.compareTo(xDateA) < 0 && workDate.compareTo(mDateA) >= 0){
				long d = dDate.getTime() + (workDate.getTime() - mDateA.getTime());
				dDate = new Date(d);				
			}

			String outputDate = dfOut2.format(dDate);
			
			// return
			System.out.println("###" + outputDate);
			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

}
