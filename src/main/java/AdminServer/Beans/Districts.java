package AdminServer.Beans;

import Utils.SharedBeans.Position;

import java.util.Random;

public class Districts {
    private final int[] districts;
    private final Random rnd = new Random();

    private final static Districts instance = new Districts();
    public static Districts getInstance() {
        return instance;
    }

    private Districts() {
        districts = new int[4];
    }

    public synchronized void decrement(int district){
        districts[district-1]--;
    }
    public synchronized void increment(int district){
        districts[district-1]++;
    }
    public synchronized int[] getDistricts(){
        return districts;
    }
    public synchronized int findBestDistrict(){
        int lowest = Integer.MAX_VALUE;
        int bestDistrict = 0;
        for (int i=0; i < districts.length; i++) {
            if (districts[i] < lowest) {
                lowest = districts[i];
                bestDistrict = i;
            }
        }
        return (bestDistrict + 1);
    }
    public synchronized Position findBestPosition(int district){
        int x = rnd.nextInt(5);
        int y = rnd.nextInt(5);
        switch (district) {
            case 2: x += 5; break;
            case 3: y += 5; break;
            case 4: x += 5; y += 5; break;
        }
        return new Position(x, y);
    }
}
