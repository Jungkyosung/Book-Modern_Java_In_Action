
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

    public static void 실전연습5_6(){

        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );


        //1. 2011년 발생한 모든 트랜잭션을 찾아 값을 오름차순 정렬
        //(예상답) 300, 400
        List<Transaction> result1 = transactions.stream()
                .filter(trans -> trans.getYear() == 2011)
                .sorted(comparing(Transaction::getAmount))
                .collect(Collectors.toList());
                //.map(Transaction::getAmount)
                //.sorted().toList();

        System.out.println(result1);

        //2. 거래자가 근무하는 모든 도시를 중복없이 나열
        //(예상답) Cambridge, Milan
        List<String> cities = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                //.map(Transaction::getTrader)
                //.map(Trader::getCity)
                .distinct().toList();

        System.out.println(cities);

        //3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
        //(예상답) Alan, Brian, Raoul
        List<Trader> tradersNameOfCambride = transactions.stream()
                .map(Transaction::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                //.map(Trader::getName)
                //.distinct()
                //.sorted().toList();
                .distinct() //거래자가 중복 제거되나?
                .sorted(comparing(Trader::getName))
                .collect(Collectors.toList());

        System.out.println(tradersNameOfCambride);

        //4. 모든 거래자의 이름을 알파벳순으로 정렬(중복제외)
        //(예상답) Alan, Brian, Mario, Raoul
        String tradersNameOfTrading = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                //.map(Trader::getName)
                .distinct()
                .sorted()
                .reduce("", (a,b) -> a + b);

        System.out.println(tradersNameOfTrading);

        //5. 밀라노에 거래자가 있는가?
        //(예상답) true
        boolean isTraderOfMilan = transactions.stream()
                //.map(Transaction::getTrader)
                .anyMatch(transactiontrader -> transactiontrader.getTrader().getCity().equals("Milan"));

        System.out.println(isTraderOfMilan);

        //6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력
        //(예상답) 300, 1000, 400, 950
        transactions.stream()
                .filter(transaction -> transaction.getTrader().getCity().equals("Cambridge"))
                //.map(transaction -> transaction.getAmount()).toList();
                .map(Transaction::getAmount)
                .forEach(System.out::println);

        //System.out.println(amountOfCambridgeTraders);

        //7. 전체 트랜잭션 중 최댓값은?
        //(예상답) 1000
        Optional<Integer> max = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(Integer::max);

        System.out.println(max.get());

        //8. 전체 트랜잭션 중 최솟값은?
        //(예상답) 300
        Optional<Transaction> min = transactions.stream()
                //.map(Transaction::getAmount)
                //.reduce(Integer::min);
                .min(comparing(Transaction::getAmount));

        System.out.println(min.get().getAmount());
    }

    static class Trader{
        String name;
        String city;

        Trader(String name, String city){
            this.name = name;
            this.city = city;
        }

        String getName(){
            return this.name;
        }

        String getCity(){
            return this.city;
        }
    }

    static class Transaction{
        Trader trader;
        int year;
        int amount;

        Transaction(Trader trader, int year, int amount){
            this.trader = trader;
            this.year = year;
            this.amount = amount;
        }

        Trader getTrader(){
            return this.trader;
        }

        int getYear(){
            return this.year;
        }

        int getAmount(){
            return this.amount;
        }
    }
}
