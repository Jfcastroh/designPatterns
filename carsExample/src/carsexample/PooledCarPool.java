package carsexample;

import java.util.HashMap;
import java.util.Map;

public class PooledCarPool {
	private static long expTime = 6000;//6 seconds
	public static HashMap<PooledCar, Long> available = new HashMap<PooledCar, Long>();
	public static HashMap<PooledCar, Long> inUse = new HashMap<PooledCar, Long>();
	
	
	public synchronized static PooledCar getObject() {
		long now = System.currentTimeMillis();
		if (!available.isEmpty()) {
			for (Map.Entry<PooledCar, Long> entry : available.entrySet()) {
				if (now - entry.getValue() > expTime) { //object has expired
					popElement(available);
				} else {
					PooledCar po = popElement(available, entry.getKey());
					push(inUse, po, now); 
					return po;
				}
			}
		}

		// either no PooledCar is available or each has expired, so return a new one
		return createPooledCar(now);
	}	
	
	private synchronized static PooledCar createPooledCar(long now) {
		PooledCar po = new PooledCar();
		push(inUse, po, now);
		return po;
        }

	private synchronized static void push(HashMap<PooledCar, Long> map,
			PooledCar po, long now) {
		map.put(po, now);
	}

	public static void releaseObject(PooledCar po) {
		cleanUp(po);
		available.put(po, System.currentTimeMillis());
		inUse.remove(po);
	}
	
	private static PooledCar popElement(HashMap<PooledCar, Long> map) {
		 Map.Entry<PooledCar, Long> entry = map.entrySet().iterator().next();
		 PooledCar key= entry.getKey();
		 //Long value=entry.getValue();
		 map.remove(entry.getKey());
		 return key;
	}
	
	private static PooledCar popElement(HashMap<PooledCar, Long> map, PooledCar key) {
		map.remove(key);
		return key;
	}
	
	public static void cleanUp(PooledCar po) {
		po.setCar1(null);
		po.setCar2(null);
		po.setCar3(null);
	}
}