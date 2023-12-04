
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import static java.util.stream.Collectors.*;

public class Main {
    public static void main(String[] args) {

        Main.basicStream();

    }

    private static void basicStream(){
        //예시 데이터 : 음식의 이름, 채식여부, 칼로리, 종류
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );

        //A.칼로리가 500보다 높은 음식 3가지 (스트림API 사용, 내부 반복)
        long startTime = System.currentTimeMillis();
        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(dish -> {
                            //System.out.println("filter : " + dish);
                            return dish.getCalories() > 500;
                        })
                        .map(dish -> {
                            //System.out.println("map : " + dish);
                            return dish.getName();
                        })
                        .limit(3)
                        .collect(toList());
        long endTime = System.currentTimeMillis();
        System.out.println(threeHighCaloricDishNames);
        System.out.println("작업시간 = " + (endTime - startTime));

        //B.칼로리가 500보다 높은 음식 3가지 (반복자 사용, 외부 반복)
        List<String> threeHighCaloricDishNames2 = new ArrayList<>();
        Iterator<Dish> iterator = menu.iterator();
        int count = 0;
        startTime = System.currentTimeMillis();
        while(iterator.hasNext() && count < 3){
            Dish dish = iterator.next();
            if(dish.getCalories() > 500){
                threeHighCaloricDishNames2.add(dish.getName());
                count++;
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println(threeHighCaloricDishNames2);
        System.out.println("작업시간 = " + (endTime - startTime));

        //C.칼로리가 500보다 높은 음식 3가지 (for-each 사용, 외부 반복)
        List<String> threeHighCaloricDishNames3 = new ArrayList<>();
        int count2 = 0;
        startTime = System.currentTimeMillis();
        for(Dish dish : menu){
            if(dish.getCalories() > 500 && count2 < 3){
                threeHighCaloricDishNames3.add(dish.getName());
                count2++;
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println(threeHighCaloricDishNames3);
        System.out.println("작업시간 = " + (endTime - startTime));


        //검토를 위해 첫째 실행의 순서를 뒤로 옮겨봤음. 유의미한 속도차이가 발생.
        startTime = System.currentTimeMillis();
        List<String> threeHighCaloricDishNames4 =
                menu.stream()
                        .filter(dish -> dish.getCalories() > 500)
                        .map(dish -> dish.getName())
                        .limit(3)
                        .collect(toList());
        endTime = System.currentTimeMillis();
        System.out.println(threeHighCaloricDishNames4);
        System.out.println("작업시간 = " + (endTime - startTime));

    }
    private static void usingStream(){
        
    }
}