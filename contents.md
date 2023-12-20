# 스트림이란 무엇인가?
- 자바 8 API에 새로 추가된 기능  
- 스트림을 이용하면 선언형으로 컬렉션 데이터(List 등) 처리 가능
  - 선언적 이점이 먼데??  
	loop(for/while)와 if 를 구현하지 않고 바로 동작수행을 지정함.  
	for(int i = 0 ; 조건, i++){ ... } 같은 loop문  
	for(Object obj : List){ ... } 같은 누적자 요소 필터링
- 스트림 API 멀티코어 사용 가능  
  - 스트림의 병렬처리  
  - stream 자체는 멀티코어 병렬처리가 아니다.  
  - parallelStream() 이 멀티코어 병렬처리이다.  
- filter, sorted, map, collect 같은 여러 빌딩 블록 연산을 연결  
  	복잡한 데이터 처리 파이프라인을 만들 수 있다.  
	위 연산은 **고수준 빌딩 블록**으로 이루어져 있어 특정 스레딩 모델에 제한 없이 자유롭게 어떤 상황에서든 사용할 수 있다.  
	데이터 처리 과정을 병렬화하면서 스레드와 락을 걱정할 필요가 없다.

4,5,6장 학습하면 다음과 같은 코드 구현할 수 있다.
```java
Map<Dish.Type, List<Dish>> dishesByType =
menu.stream().collect(groupingBy(Dish::getType));
```

위를 일반 명령형으로 프로그래밍한다면?  
String[] types = 중복제거 타입 그룹핑  
for를 types만큼해서 각 type별 요리들을 ArrayList에 담아줌.  
map.put(types[type수], list[type수]) 타입수만큼 진행  

기타 라이브러리: 구아바, 아파치, 람다제이
구글에서 만든 구아바, 멀티맵, 멀티셋 등이 있다.

#### 자바8 스트림 API 이점
- 선언형 : 간결성, 가독성
- 조립할 수 있음 : 유연성
- 병렬화 : 성능이점

요리 리스트(메뉴)를 주요 예제로 사용
```java
List<Dish> menu = Arrays.asList(  
  new Dish("pork", false, 800, Dish.Type.MEAT),
  new Dish("beef", false, 700, Dish.Type.MEAT),
  new Dish("chicken", false, 400, Dish.Type.MEAT),
  new Dish("french fries", true, 530, Dish.Type.OTHER),
  new Dish("rice", true, 350, Dish.Type.OTHER),
  new Dish("season true", false, 120, Dish.Type.OTHER),
  new Dish("pizza", true, 550, Dish.Type.OTHER),
  new Dish("prawns", false, 300, Dish.Type.FISH),
  new Dish("salmon", false, 450, Dish.Type.FISH)
);
```
Dish는 다음과 같이 불변형 클래스다. 

```java
public class Dish{
   private final String name;
   private final boolean vegeterian;
   private final int calories;
   private final Type type;

   public Dish(String name, boolean vegetarian, int calories, Type type) {
      this.name = name;
      this.vegeterian = vegeterian;
      this.calories = calories;
      this.type = type;
   }

   public String getName(){
      return name;
   }

   public boolean isVegetarian(){
      return vegeterian;
   }   

   public int getCalories(){
      return calories;
   }

   public type getType(){
      return type;
   }

   @Override
   public String toString() {
      return name;
   }

   public enum Type { MEAT, FISH, OTHER }

}
```

#### 스트림이란 뭘까?  
> 데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소

- 연속된 요소 : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공. 컬렉션의 주제는 데이터고 스트림의 주제는 계산이다. 
 
- 소스 : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다.

- 데이터 처리 연산 : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.

- 파이프라이닝 : 스트림연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환함. 

- 내부 반복 : 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원.

```java
List<String> threeHighCaloriesDishNames = 
menu.stream()						//메뉴(요리 리스트)에서 스트림 얻기
	.filter( dish -> dish.getCalories() > 300 )	//파이프라인 연산만들기, 칼로리 필터링
	.map(Dish::getName)				//이름 추출
	.limit(3)					//선착순 3개 선택
	.collect(toList());				//결과 List로 반환
```

collect를 제외한 나머지 연산들은 서로 파이프라인을 형성할 수 있도록 스트림을 반환함. (return this = 체이닝)  
메서드 참조 방식 Dish::getName 는 람다식으로 d -> d.getName()과 같다.  
collect는 연산처리된 데이터소스를 다른 형식으로 변환한다.   

#### 컬렉션API vs 스트림API

컬렉션에 저장된 데이터는 **파일전체**와 같고 스트림은 **실시간 스트리밍**과 같아서 메모리에 전체 적재를 해두지 않고 계산할 때만 가져와서 계산을 진행함.  
탐색을 한 번 한 이후 다시 탐색하려면 새로운 스트림을 만들어야 함.  
(만약 데이터소스가 I/O 채널이라면 소스를 반복 사용할 수 없으므로 새로운 스트림을 만들 수 없다.) 이게 먼말이여?   
스트림은 단 한 번만 소비할 수 있다.  

```java
List<String> title = Arrays.asList("java8", "in", "action");
Stream<String>  s = title.stream();
s.forEach(System.out::println);  // -> java8, in, action이 출력
s.forEach(System.out::println);  // -> illegalStateException 발생 : 스트림이 이미 소비되었거나 닫힘.
```

외부 반복 vs 내부 반복  
(데이터 반복 처리 방법, 누가 데이터를 반복 처리하는가?)  
컬렉션은 사용자가 직접(데이터의 외부적으로) 요소를 반복, 스트림은 알아서 반복(라이브러리를 통해 데이터 내부적으로 반복)

- 컬렉션 반복(for-each사용)
```java
List<String> names = new ArrayList<>();
for(Dish dish : menu){
	names.add(dish.getName());
}
```

- 컬렉션 반복(반복자 사용)
```java
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while(iterator.hasNext()){
	Dish dish = iterator.next();
	names.add(dish.getName());
}
```

- 스트림 내부 반복
```java
List<String> names = menu.stream()
	.map(Dish::getName)
	.collect(toList());
```

내부 반복을 사용할 때 이점은??

스트림 라이브러리의 내부 반복은 데이터 표현과 하드웨어를 활용한 병렬성 구현을 자동으로 선택한다.  
반면 for-each를 이용하는 외부 반복시 병렬성을 스스로 관리해야 한다.  
(병렬성을 스스로 관리한다는 것은 병렬성을 포기하던지 synchronized로 시작하는 과정을 선택하는 것이다.)  

```java
List<String> highCaloricDishes = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while(iterator.hasNext()){
	Dish dish = iterator.next();
	if(dish.getCalories() > 300 ){
		highCaloricDishes.add(dish.getName());
	}
}
```

내부반복으로 리팩토링 ->

```java
List<String> highCaloricDishes = menu.stream()
	.filter(dish -> dish.getCalories() > 300 )
	.map(Dish::getName)
	.collect(toList());
```


스트림의 연산자

중간 연산 vs 최종 연산

중간 연산은 다른 스트림(또 다른 스트림, 스트림은 한 번만 실행)을 반환한다.
다른 중간 연산을 체이닝(연결)해서 질의를 만들 수 있다.
중간 연산의 중요한 특징 = 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것, 즉 게으르다는 것이다. 중간연산을 합친 다음에 합쳐진 중간연산을 최종연산으로 한 번에 처리하기 때문이다.

(limit연산)쇼트서킷 기법 덕분에 자료를 전부 확인하지 않는다. (최적화 가능, 중간탈출 용이)

filter와 map은 서로 다른 연산이지만 한 과정으로 병합(루프 퓨전)

최종연산은 스트림 파이프라인에서 결과를 도출. 보통 최종연산에 의해 List, Integer, void 등 스트림 이외의 결과가 반환됨. 
void예시) menu.stream().forEach(System.out::println);


스트림 이용 과정( ≒ 빌더패턴과 유사)
- 질의를 수행할 (컬렉션 같은) 데이터 소스
- 스트림 파이프라인을 구성할 중간 연산
- 스트림 파이프라인을 실행하고 결과를 만들 최종연산


스트림 활용

외부반복
List<Dish> vegeterianDishes = new ArrayList<>();
for(Dish d : menu) {
	if(d.isVegetarian()) {
		vegetarianDishes.add(d);
	}
}

내부반복
List<Dish> vegeterianDishes = menu.stream()
	.filter(Dish::isVegetarian)
	.collect(collectors.toList());


데이터를 어떻게 처리할지는 스트림 API가 관리하므로 편리하게 데이터 관련 작업을 할 수 있다. 

스트림API 연산
-필터링 - 프레디케이트(불리언을 반환하는 함수)로 필터링, 고유요소 필터링(hashcode, equals를 통해 고유여부 확인)
-슬라이싱 - 프레디케이트를 이용한 슬라이싱(takeWhile, dropWhile, limit, skip)
-매핑 - 특정 열을 선택하여 처리(map, flatMap = 여러 스트림을 하나로 평면화)
-검색
-매칭
-리듀싱
등

[1, 2, 3, 4, 5]	[]arrayList
[1, 4, 9, 16, 25] []arrayList

List<Integer> listOfIntSquareArrays = listOfIntArrays.stream()
	.map(n -> n * n)
	.collect(toList())


(중복 없나?)
두 개의 숫자 리스트가 있을 때, 모든 숫자 쌍의 리스트를 반환하시오.
두 개의 숫자 리스트를 

합이 3으로 나누어떨어지는 쌍만 반환
a.stream.flatmap(i -> b.stream()
.filter(j -> ( i + j ) % 3 == 0)
.map(j -> new int[]{i, j})
.collect(toList());
//더 복잡한 느낌인디요???

list<int[]> resultList;

for(int i = 0 ; i < a.length ; i++) {
	for(int j = 0 ; b.length ; j++){
		resultList.add(new int[] {i, j});
	}
}


List<Integer> numbers1 = Arrays.asList(1,2,3);
List<Integer> numbers2 = Arrays.asList(3,4);

List<int[]> pairs = 

numbers1.stream()
	.flatMap(i -> 
		numbers2.stream()
			.filter(j -> (i + j) % 3 == 0)
			.map(j -> new int[] {i, j})
	.collect(toList());




검색과 매칭

allMatch, anyMatch, noneMatch, findFirst, findAny 등

anyMatch는 최종연산임 boolean 반환

noneMatch vs allMatch
- noneMatch는 스트림의 모든 요소가 false인지 확인
- allMatch는 스트림의 모든 요소가 true인지 확인

요소검색

Optional이란? 

Optional<T> 클래스는 값의 존재나 여부를 표현하는 컨테이너 클래스이다.
findAny는 아무요소도 반환하지 않을 수 있다. null은 nullPointException 같은 에러를 발생시키기 쉬우니까 조심해야한다. 따라서 Optional을 사용하는 게 좋다. 

- isPresent()는 Optional이 값을 포함하면 참(true)을 반환하고, 값을 포함하지 않으면, 거짓(false)를 반환한다.
- ifPresent(Consumer<T> block)은 값이 있으면 주어진 블록을 실행한다. Consumer함수형 인터페이스에는 T형식의 인수를 받고 void를 반환하는 람다를 전달할 수 있다.

- T get()은 값이 존재하면 값을 반환하고, 없으면 NoSuchElementExeption을 일으킨다.
- T orElse(T other)는 값이 있으면 값을 반환하고, 없으면 기본값을 반환한다.

Optional은 검색과 잘 쓸만 하겠네. 검색해보고 검색결과가 있다면 뭘한다. 리스트를 반환하는 거는 리스트가 없다고 반환하면 되지만 그 리스트를 가지고 뭔가 추가 작업을 한다면 Optional을 사용해야하고 검색함수는 Optional을 반환한다. (null을 반환하지 않는다. 따라서 null여부를 검사하지 않는다.)


첫 번째 요소 찾기
findFirst() 사용

그럼 왜 findAny가 존재하는가?
병렬성 때문이다. 병렬실행에서는 첫 번째 값을 찾기가 어렵다. 따라서 요소의 반환 순서가 상관없다면 병렬 스트림에서는 제약이 적은 findAny를 사용한다.

리듀싱(엑셀의 sumif와 비슷)(함수형프로그래밍에선 폴드라고 부름)
조건에 따른 누적값 계산

요소의 합 구하기(리듀스 사용 전)
int sum = 0;
for(int x : numbers){
	sum += x;
}

sum의 초기값은 0
리스트의 모든 요소를 조합하는 연산(+)

위 코드를 복붙하지않고 모든 숫자를 곱하는 연산을 구할 수 있다면 좋을 것이다.
int sum = numbers.stream().reduce(0, (acc, num) -> acc + num);

reduce의 첫 인자 0은 초깃값
두 요소를 조합해서 새로운 값을 만드는 BinaryOperator<T>

int sum = numbers.stream().reduce(0, Integer::sum);
자바8에서는 Integer클래스에서 두 숫자를 더하는 정적 sum 메서드를 제공한다.
따라서 직접 람다 코드를 구현할 필요없다.

초깃값없음
초깃값을 받지않도록 된 reduce도 있다. 그러나 이 reduce는 Optional을 반환한다.
Optional<Integer> sum = numbers.stream().reduce((a,b) -> a+b);
왜 Optional을 반환하는 걸까? 스트림에는 아무 요소도 없는 상황을 생각해보자.
이런 상황이라면 초깃값이 없으므로 reduce는 합계를 반환할 수 없다.(초깃값을 0으로 설정안하는 이유는 먼가?)
int max = numbers.stream().reduce(0, (a,b) -> (a > b) ? a : b);
위처럼 람다를 사용해도 되지만 아래의 메서드 참조표현이 더 직관적임(로직이 틀렸는지 확인하기 편함)

최댓값 = Optional<Integer> max = numbers.stream().reduce(Integer::max);
최솟값 = Optional<Integer> min = numbers.stream().reduce(Integer::min);



map과 reduce를 이용한 요리 개수 계산(맵과 리듀스를 연결하는 기법 = 맵리듀스패턴, 쉽게 병렬화)
int menuNum = menu.stream()
	.map(d -> 1)
	.reduce(0, (a,b) -> a + b);

개수계산으로 count함수도 있다.
long count = menu.stream().count();


< reduce메서드의 장법과 병렬화 >
왜 reduce를 통해서 합계를 구해야 하는 것인가?
내부 반복이 추상화돼서 병렬화가 가능. 반복적인 합계에서는 sum변수를 공유해야 하므로 쉽게 병렬화하기 어렵다. 강제적으로 동기화시키더라도 결국 병렬화로 얻어야 할 이득이 스레드 간의 소모적인 경쟁 때문에 상쇄된다. 
int sum = numbers.paralleStream().reduce(0, Integer::sum);
위 코드를 병렬로 실행하려면, reduce에 넘겨준 람다의 상태(인스턴스 변수 같은)가 바뀌지 말아야 하며, 연산이 어떤 순서로 실행되도 결과가 바뀌면 안된다.

< 스트림 연산: 상태없음과 상태있음 >
map, filter는 내부적인 상태를 갖지않는 연산이다.
하지만 reduce, sum, max 같은 연산은 결과를 누적할 내부상태가 필요.
sorted, distinct같은 모든 요소가 버퍼에 추가되어 있어야 하는 연산은 내부상태를 갖는 연산이다. (따라서 무한한 크기의 스트림은 불가하다.)


1. 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리
trans.stream().sorted().collect(Collectors.toList());

2. 거래자가 근무하는 모든 도시를 중복 없이 나열
trans.stream().map(Trans::getCity).distinct().collect(Collectors.toList());

3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
trans.stream().filter(trans -> trans.getCity == "케임브리지")
		.sorted(Comparator.comparing(Trans::getCity)
		.collect(Collectors.toList());

4. 모든 거래자의 이름을 알파벳순으로 정렬
trans.stream().map(Trans::getName).sorted(Comparator.comparing(Trans::getName).collect(Collectors.toList());

5. 밀라노에 거래자가 있는가?
trans.stream().anyMatch(trans -> trans.getCity == "밀라노");

6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력
trans.stream().filter(trans -> trans.getCity == "케임브리지")
.map(trans -> System.out.println(trans));

7. 전체 트랜잭션 중 최댓값은?
trans.stream().reduce(Integer::max);

8. 전체 트랜잭션 중 최솟값은?
trans.stream().reduce(Integer::min);


숫자 스트림

박싱비용이란? 

(Integer::sum)
Integer로 계산하게되면 기본형인 int로 언박싱해야 한다.
따라서 직접 sum()메서드를 호출하면 좋겠다.
하지만 그럴 수 없다. map메서드가 Stream<T>를 생성하기 때문이다.  먼 말이여?
스트림의 요소 형식은 Integer지만 인터페이스에는 sum 메서드가 없다. 왜 sum메서드가 없을까? menu처럼 Stream<Dish> 형식의 요소만 있다면, sum이라는 연산을 수행할 수가 없기 때문이다.
그래서 스트림 API 숫자 스트림을 효율적으로 처리할 수 있도록 기본형 특화 스트림을 제공한다.

기본형 특화 스트림

자바8에서는 세 가지 기본형 특화 스트림 제공
IntStream
DoubleStream
LongStream

각각의 인터페이스는 sum, max 등 자주 사용하는 숫자 관련 리듀싱 연산 수행 메서드를 제공
또한 필요시 다시 객체 스트림으로 복원 가능
특화 스트림은 오직 박싱 과정에서 효율과 관련 있다.

숫자 스트림으로 매핑

mapToInt, mapToDouble, mapToLong 메서드를 통해 스트림을 특화 스트림으로 변환

int calories = menu.stream()
		.mapToInt(Dish::getCalories)
		.sum(); -> int로 바로 계산 됨.

이제 reduce를 이용한 Integer를 언박싱하는 방식의 계산이 아니라 int를 바로 합계를 구하는데 사용할 수 있다.

스트림이 비어있다면 0을 반환
max, min, average 등 제공

객체 스트림으로 복원하기
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();

boxed메서드를 통해 일반 스트림으로 변환

기본값: OptionalInt

합계메서드 계산할 때는 0이라는 기본값이 있었고 문제가 없었다.
하지만 최소, 최대는 0이 문제가 될 수 있다.

OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();

OptionalInt를 이용해서 최댓값이 없는 상황에 사용할 기본값을 명시적으로 정의할 수 있다. int max = maxCalories.orElse(1); <- 값이 없을 경우 기본 최댓값을 명시적으로 설정

숫자 범위
IntStream과 LongStream은 range와 rangeClosed라는 정적 메서드를 제공
두 메서드 모두 첫 번째 인수로 시작값을, 두 번째 인수로 종료값을 갖는다.
range는 시작값과 종료값이 결과에 포함되지 않음.
rangeClosed는 결과에 포함.

IntStream evenNumbers = IntStream.rangeClosed(1, 100)	//1과 100 포함
			.filter(n -> n % 2 == 0);

System.out.println(evenNumbers.count()); //짝수 50개

IntStream evenNumbers = IntStream.range(1, 100)
			.filter(n -> n % 2 == 0);

System.out.println(evenNubmers.count()); //짝수 49개, 100을 포함하지 않음.


숫자 스트림 활용 : 피타고라스 수

필터를 이용해서 좋은 조합을 갖는 a, b를 선택할 수 있게 된다. 


IntStream.rangeClosed(1, 100)
	.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
	.boxed()
	.map(b -> new int[]{a, b, (int) Math.sqrt(a*a + b*b)});


map은 스트림의 각 요소를 int 배열로 변환하기 때문이다. 
IntStream의 map메서드는 스트림의 각 요소로 int가 반환된다.
개체값 스트림을 반환하는 IntStream의 mapToObj 메서드를 이용해서 이 코드를 재구현할 수 있다.


IntStream.rangeClosed(1, 100)
	.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
	.mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a*a + b*b)});

boxed를 통해 스트림 객체로 변환해도 되고, mapToObj를 통해 객체로 변환해도 됨.


Stream<int[]> pythagoreanTriples = 
	IntStream.rangeClosed(1, 100).boxed()
		.flatMap(a -> 
			IntStream.rangeClosed(a, 100)
				.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
				.mapToObj(b ->
				new int[] {a, b, (int)Math.sqrt(a*a+b*b)})
		);

flatMap을 굳이 사용하는 이유는??
flatMap은 병렬로 처리되는 각각의 스트림의 순서를 보장??(하나의 스트림으로 만듬)
1~100까지 범위를 flatMap으로 연산
이중 for문이라고 생각하면, 1~100을 다시 1~100으로 연산하면서 값들을 찾는다.
10,000번의 연산 작동

pythagoreanTriples.limit(5).forEach(System.out::println);



스트림 만들기

컬렉션, 범위의 숫자에서 스트림을 만들어봤다.
추가로 일련의 값, 배열, 파일, 함수를 이용한 무한 스트림만들기를 보자.

값으로 스트림 만들기

임의의 수를 인수로 받는 정적 메서드 Stream.of를 이용해서 스트림을 만들 수 있다.
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
stream.map(String::toUpperCase).forEach(System.out::println);

empty메서드를 통해 스트림 비우기
Stream<String> emptyStream = stream.empty();

null이 될 수 있는 객체로 스트림 만들기
자바9에서는 null이 될 수 있는 개체를 스트림으로 만들 수 있는 메소드가 추가됨.
개체가 null이라면 비어있는 스트림 반환.
System.getProperty는 제공된 키에 대응하는 속성이 없으면 null을 반환.

이런 메소드를 스트림에 활용하려면 null을 명시적으로 확인해야 함.

String homeValue = System.getProperty("home");  //null일 수도
Stream<String> homeValueStream = 
	(homeValue == null) ? Stream.empty() : Stream.of(value); //null확인


Stream.ofNullable을 이용하면,

Stream<String> homeValueStream = 
	Stream.ofNullable(System.getProperty("home")); 
		//null일 경우, 빈 스트림

null이 될 수 있는 객체를 포함하는 스트림값을 flatMap과 함께 사용하는 상황에서는 이 패턴을 더 유용하게 사용할 수 있다.

Stream<String> values = 
	Stream.of("config", "home", "user")
		.flatMap(key -> Stream.ofNullable(System.getProperty(key));

배열로 스트림 만들기
int[] numbers = {2, 3, 5, 7, 11, 13};
int sum = Arrays.stream(numbers).sum();	//IntStream인건가? 
//기본형 배열을 IntStream으로 변환

파일로 스트림 만들기
I/O연산에 사용하는 자바의 NIO API(논블록 I/O)도 스트림 API활용가능하도록 업데이트 됨.

java.nio.file.Files의 많은 정적 메서드가 스트림을 반환한다.

Files.lines는 주어진 파일의 행 스트림을 문자열로 반환한다.

(단어의 개수 확인하기)
long uniqueWords = 0;
try(Stream<String> lines = 
	Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){
	
	uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
			.distinct()
			.count();
}
	catch(IOException e){
//예외처리
}

try뒤에 나오는 () 괄호가 무슨 내용인지 몰랐는데 보통은 생략가능하여 사용하지 않고,
특정 리소스를 사용하고 AutoClosable하다면, 괄호에 사용하여 finally로 리소스를 닫아줄 필요가 없다. 따라서 
try(가용 리소스){
}catch(Exception e){
}
으로 끝난다.			

Stream 인터페이스는 AutoClosable인터페이스를 구현하기 때문에 try 블록 내의 자원은 자동 관리된다. 

함수로 무한 스트림 만들기
스트림 API는 함수에서 스트림을 만들 수 있는 두 정적 메서드 Stream.iterate와 Stream.generate를 제공. 두 연산을 이용해서 무한 스트림(크기가 고정되어 있지 않음)을 만들 수 있다.
iterate와 generate에서 만든 스트림은 요청할 때마다 주어진 함수를 이용해서 값을 만듬.
무한하게 Stream이 연결될 수 있으나, 보통 limit(n)과 함께 사용함.

무한스트림(=언바운드 스트림)

iterate메서드

Stream.iterate(0, n -> n + 2) 
	.limit(10)
	.forEach(System.out::println)


피보나치수열 20개 만들기
(예상답)
(0,1) (1,1) (1,2) (2,3) (3,5) (5,8) ... 
Stream.iterate(new int[]{0,1}, t -> new int[]{t[0], t[0]+t[1]})
	.limit(20)
	.forEach(System.out::println)

(예상답)
0, 1, 1, 2, 3, 5, 8 ....
Stream.iterate(new int[]{0,1}, t -> new int[]{t[0], t[0]+t[1]})
	.limit(20)
	.map(t -> t[0])
	.forEach(System.out::println)

자바9의 iterate메서드는 프레디케이트를 지원.

IntStream.iterate(0, n -> n < 100, n-> n + 4)
	.forEach(System.out::println);

iterate메서드는 두 번째 인수로 프레디케이트를 받아 언제까지 작업할지 기준을 정할 수 있다. 

아니면, 쇼트서킷을 지원하는 takeWhile을 이용하는 것도 있다.
IntStream.iterate(0, n -> n + 4)
	.takeWhile(n -> n < 100)
	.forEach(System.out::println)


generate 메서드

iterate와 달리 generate는 생산된 각 값을 연속적으로 계산하지 않는다. 
generate는 Supplier<T>를 인수로 받아서 새로운 값을 생산한다.

Stream.generate(Math::random)
	.limit(5)
	.forEach(System.out::println)

그럼 generate는 언제 사용하는가?
발행자가 상태가 없는 메서드

IntStream의 generate는 Supplier<T> 대신 IntSupplier를 인수로 받음.

무한스트림
IntStream ones = IntStream.generate(()-> 1);
(1을 계속해서 생성.)

IntStream twos = IntStream.generate(new IntSupplier(){
	public int getAsInt(){
		return 2;
	}
});


generate 메서드는 주어진 발행자(supplier)를 이용해서 2를 반환하는 getAsInt메서드를 반복적으로 호출한다. 익명 클래스와 람다는 비슷한 연산을 수행하지만 익명 클래스에서는 getAsInt메서드의 연산을 커스터마이징 할 수 있는 상태 필드를 정의할 수 있다는 점이 다르다. 부작용이 생길 수 있음을 보여준다. 지금까지의 람다는 부작용이 없었다. 상태를 바꾸지 않는다.

IntSupplier fib = new IntSupplier() {
	private int previous = 0;
	private int current = 1;
	public int getAsInt() {
		int oldPrevious = this.previous;
		int nextValue = this.previous + this.current;
		this.previous = this.current;
		this.current = nextValue;
		return oldPrevious;
	}
};
IntStream.generate(fib).limit(10).forEach(System.out::println)

getAsInt를 호출하면 객체 상태가 바뀌며 새로운 값을 생산한다. 
iterate를 사용했을 때는 각 과정에서 새로운 값을 생성하면서도 기존 상태를 바꾸지 않는 순수한 불편 상태를 유지했다. 스트림을 병렬로 처리하면서 올바른 결과를 얻으려면 불변 상태 기법을 고수해야 한다.



6장 스트림으로 데이터 수집

- Collectors 클래스로 컬렉션을 만들고 사용
- 하나의 값으로 데이터 스트림 리듀스하기
- 특별한 리듀싱 요약 연산
- 데이터 그룹화와 분할
- 자신만의 커스텀 컬렉션 개발

컬렉션, 컬렉터, collect를 헷갈리지 말자

collect와 컬렉터로 구현할 수 있는 질의 예제
- 통화별로 트랜잭션을 그룹화한 다음 해당 통화로 일어난 모든 트랜잭션 합계를 계산.
(Map<Currency, Integer> 반환). => 키, 값 저장 , 리스트가 아님.(not toList())

- 트랜잭션을 비싼 트랜잭션과 저렴한 트랜잭션 두 그룹으로 분류.
(Map<Boolean, List<Transaction>> 반환) 키로 true, false 두 분류 저장. 각 List 저장.

- 트랜잭션을 도시 등 다수준으로 그룹화. 각 트랜잭션이 비싼지 저렴한지 구분.
(Map<String, Map(Boolean, List<Transaction>>>)

Map<Currency, List<Transaction>> transactionsByCurrencies = 
	new HashMap<>();

for(Transaction ts : transactionList){
	Currency currency = ts.getCurrency();
	List<Transaction> tsForCurrency = tsByCurrencies.get(currency);
	if(tsForCurrency == null) {
		tsForCurrency = new ArrayList<>();
		tsByCurrencies.put(currency, tsForCurrency);
	}
	tsForCurrency.add(transaction);
}

(자바8문법으로 리팩토링)

Map<Currency, List<Transaction>> transactionByCurrencies = 
	transactions.stream().collect(groupingBy(Transaction::getCurrency));


컬렉터란 무엇인가?

함수형 프로그래밍에서는 '무엇'을 원하는지 직접 명시할 수 있어서 어떤 방법으로 이를 얻을지는 신경쓸 필요가 없다. 
이전에는 Collector 인터페이스의 toList를 사용해서 리스트로 반환했다.
이번에는 Collector인터페이스의 groupingBy를 사용해서 '각 키 버킷 그리고 각 키 버킷에 대응하는 요소 리스트를 값으로 포함하는 맵을 만들라'는 동작을 수행했다.

다수준으로 그룹화를 수행할 때, 명령형 프로그래밍과 함수형 프로그래밍의 차이점이 더욱 두드러진다. 명령형 코드에서는 문제를 해결하는 과정에서 다중루프와 조건문을 추가하며 가독성과 유지보수성이 크게 떨어진다. 반면 함수형 프로그래밍에선 필요한 컬렉터를 쉽게 추가할 수 있다.


고급 리듀싱 기능을 수행하는 컬렉터

훌륭하게 설계된 함수형API의 장점으로 높은 수준의 조합성과 재사용성을 꼽을 수 있다.
collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의가능.
collect를 호출하면 스트림의 요소에 리듀싱 연산이 수행된다.

Collector인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림에 어떤 리듀싱 연산을 수행할지 결정됨.

커스텀컬렉터를 구현할 수도 있다. Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다.


이미 정의된 컬렉터

groupingBy 같이 제공하는 메서드들. 
Collectors에서 제공하는 이런 메서드들은 세 가지로 분류가능.
- 스트림 요소를 하나의 값으로 리듀스하고 요약
- 요소 그룹화
- 요소 분할

?그룹화와 분할은 뭔 차이지? 그룹화의 특별한 연산인 분할.
분할은 한 개의 인수를 받아 불리언을 반환하는 함수. 즉 프레디케이트를 그룹화 함수로 사용.


리듀싱과 요약

counting -> 개수 반환
long howManyDishes = menu.stream().collect(Collectors.counting());

long howManyDishes = menu.stream().count(); (생략 후)
?어떻게 생략되는 거지? Collectors는 static으로 가져오는 건 알겠는데, collect는 어떻게 생략되는 건지?


스트림값에서 최댓값과 최솟값 검색

메뉴에서 칼로리가 가장 높은 요리 찾기
Collectors.maxBy, Collectors.minBy
스트림의 요소를 비교하는 데 사용할 Comparator를 인수로 받는다.
칼로리로 요리를 비교하는 Comparator를 구현한 다음에 Collectros.maxBy로 전달.

Comparator<Dish> dishCaloriesComparator =
	Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCaloriesDish =
	menu.stream().collect(maxBy(dishCaloriesComparator));

붙이면?

Optional<Dish> mostCaloriesDish =
	menu.stream()
	.collect(maxBy(Comparator.comparingInt(Dish::getCalories));

Comparator의 comparingInt메서드를 사용.

요약연산
Collectors클래스는 Collectors.summingInt라는 특별한 요약 팩토리 메서드를 제공.
summingInt는 객체를 int로 매핑하는 함수를 인수로 받음.
summingInt의 인수로 전달된 함수는 객체를 int로 매핑한 컬렉터를 반환.
summingInt가 collect 메서드로 전달되면 요약 작업을 수행한다.

메뉴리스트의 총 칼로리를 계산하는 코드

int totalCalory = menu.stream()
	.collect(summingInt(Menu::getCalory));


summingLong과 summingDouble 메서드는 summingInt와 같은 방식으로 동작한다.

단순 합계 외에 평균값 계산 등의 연산도 요약 기능으로 제공.
Collectors.averagingInt, averagingLong, averagingDouble 등으로 평균 계산.

double avgCalory = menu.stream()
	.collect(averagingDouble(Menu::getCalory));

개수 계산, 합계, 평균, 최댓값, 최솟값 계산이 두 개 이상 한 번에 수행돼야 할 수도 있다.
이런 상황에선 팩토리 메서드 summarizingInt가 반환하는 컬렉터를 사용할 수 있다.

IntSummaryStatistics menuStatistics = menu.stream()
	.collect(summarizingInt(Menu::getCalory));

위의 통계 정보 일체가 한 번에 수행되어서 IntSummaryStatistics 개체에 모두 저장.

출력하면 count, sum, min, average, max 값을 볼 수 있다.

int뿐 아니라 long, double도 LongStatistics, DoubleStatistics 객체를 반환하는
summarizingLong, summarizingDouble메서드가 있다.


문자열 연결(문자열을 가지고 놀 일이 많기 때문에 중요)

컬렉터에 joining팩토리 메서드를 이용하면 스트림의 각 객체에 toString메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.

메뉴의 모든 요리명을 연결하는 코드
String shortMenu = menu.stream().map(Menu::getName).collect(joining());

만약 Menu클래스가 요리명을 반환하는 toString 메서드를 포함하고 있다면, map으로 각 요리명을 추출하는 과정을 생략할 수 있다.

String shortMenu = menu.stream().collect(joining());

하지만 문자열에 구분자가 없기 때문에 구분자가 오버로드된 joining팩토리 메서드도 있다.
String shortMenu = menu.stream().map(Menu::getName).collect(joining(", "));

위와 같이 문자열을 합치지 않고 기존 방식으로 합친다면, for문이나 for-each를 통해서 좀 더 복잡하게 합칠 문자열의 길이까지 고려해야 하는 것에 비하면 훨씬 간단하다.


범용 리듀싱 요약 연산

reducing팩토리 메서드로도 지금까지의 컬렉터를 정의할 수 있다. 
범용 Collectors.reducing으로도 구현이 가능하다.
범용 팩토리 메서드 대신 특화된 컬렉터를 사용한 이유는 프로그래밍적 편의성 때문.
(하지만 프로그래머의 편의성 뿐만 아니라 가독성도 중요하다.)

reducing을 사용한 메뉴의 모든 칼로리 합계 계산
int totalCalories = menu.stream()
	.collect(reducing(0, Menu::getCalory, (i, j) -> i + j));


?그냥 stream().reduce쓰면 되지 왜 저걸 사용하는 거임??

reducing은 인수를 3개 사용.
첫 번째는 리듀싱 연산의 시작값이거나 스트림에 인수가 없을 때는 반환값이다.
두 번째는 변환함수다.
세 번째는 람다함수다.

Optional<Menu> mostCaloryMenu =
	menu.stream().collect(reducing(
		(d1, d2) -> d1.getCalory() > d2.getCalory() ? d1 : d2));

한 개의 인수를 가진 reducing 버전을 이용해서 가장 칼로리가 높은 요리를 찾을 수도 있다.
한 개의 인수를 받는 reducing은 스트림에 초깃값이 없어 못받을 수도 있기 때문에 Optional을 반환한다.


collect와 reduce

가변 컨테이너 관련 작업이면서 병렬성을 확보하려면 collect 메서드로 리듀싱 연산을 구현하는 것이 바람직.

컬렉션 프레임워크 유연성 : 같은 연산도 다양한 방식으로 수행할 수 있다.

int totalCalories = menu.stream().collect(reducing(0,
					Dish::getCalories,
					Integer::sum));


counting 컬렉터도 세 개의 인수를 갖는 reducing 팩토리 메서드를 이용하여 구현

public static <T> Collector<T, ?, Long> counting(){
	return reducing(0L, e -> 1L, Long::sum);  //1로 변환
}

제네릭 와일드카드 '?' 사용법

위 예제에서 사용된 '?'는 컬렉터의 누적자 형식이 알려지지 않았음을, 즉 누적자의 형식이 자유로움을 의미한다. 위 예제에서는 Collectors클래스에서 원래 정의된 메서드 시그니처를 그대로 사용했을 뿐이다.

컬렉터를 이용하지 않고 연산 수행
int totalCalories = menu.stream()
	.map(Dish::getCalories)
	.reduce(Integer::sum).get();
한 개의 인수를 갖는 reduce를 스트림에 적용한 다른 예제와 마찬가지로 reduce(Integer::sum) 도 Optional<Integer>를 반환한다. null문제 발생 가능하기 때문.

일반적으로 get보다는 orElse, orElseGet 등을 이용해서 Optional의 값을 얻어오는 것이 좋다.
스트림을 IntStream으로 매핑한 다음에 sum 메서드를 호출하는 방법으로도 결과를 얻을 수 있다.
int totalCalories = menu.stream().mapToInt(Dish::getCalories).sum();

자신의 상황에 맞는 최적의 해법 선택
하나의 연산을 다양한 방식으로 자바 스트림 API를 사용할 수 있다. 
문제를 해결할 수 있는 다양한 해결 방법을 확인한 다음에
가장 일반적으로 문제에 특화된 해결책을 고르는 것이 바람직하다. (문제를 여러 방식으로 접근해서 풀어보는 경험이 많아야 함.)
가독성과 성능 두 마리 토끼를 잡으려 노력.
IntStream을 사용한 방법으로 자동언박싱 연산을 수행하고 Integer를 int로 변환하는 과정이 없어서 성능까지 좋다.

퀴즈, 리듀싱으로 문자열 연결하기

joining컬렉터를 reducing컬렉터로 올바르게 바꾼 코드를 모두 선택하라.

String shortMenu = menu.stream().map(Dish::getName).collect(joining());

1. String shortMenu = menu.stream().map(Dish::getName)
	.collect(reducing((s1, s2) -> s1 + s2 )).get();
	//Dish리스트에서 이름을 추출하여 Stream<String>을 전달.
	//reducing의 인자를 하나만 사용(람다식)
	//인자가 하나인 reducing은 초깃값이 없기 때문에 Optional반환
	//따라서 get()으로 최종 값 추출.

2. String shortMenu = menu.stream()
	.collect(reducing((d1, d2) -> d1.getName() + d2.getName())).get();
	//Stream<Dish>에서 reducing을 통해 String을 반환
	//이게 자동으로 Optional<String>으로 되는 건가? 안됨.
	☆reducing은 BinaryOperator<T>, 즉 BiFunction<T,T,T>를 인수로 받음.
	즉, reducing은 두 인수를 받아 같은 형식을 반환하는 함수를 인수로 받음.
	따라서 요리를 인수로 받아서 문자열을 반환하면 컴파일 에러임.

3. String shortMenu = menu.stream()
	.collect(reducing( "", Dish::getName, (s1, s2) -> s1 + s2 ));
	//reducing컬렉터로 ""초기값에 연산에 사용할 String을 추출 후 람다식으로 연산 수행

정답 (1, 3)


그룹화

?이런 그룹화하는 연산을 DBMS서버에서 처리하는 게 더 좋은 걸까? 백엔드서버에서 처리하는 게 좋은 걸까?
자바8의 함수형을 사용하면 쉽게 그룹화 연산이 가능.
팩토리 메서드 Collectors.groupingBy를 이용해서 쉽게 메뉴 그룹화 가능.

Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
	.collect(groupingBy(Dish::getType));

위 연산으로 Map에 포함된 결과 : 
{FISH=[prawns, salmon], OTHER=[french fries, rice, season, fruit, pizza], MEAT=[pork, beef, chicken]}

스트림의 각 요리에서 Dish.Type과 일치하는 모든 요리를 추출하는 함수를 groupingBy함수로 전달.
이 함수를 기준으로 스트림이 그룹화됨. 이를 분류 함수라고 부름.
groupingBy(분류함수, 반환 값이 그룹화의 키로서 여기서는 getType으로 Type을 키로서 그룹화함)

단순한 속성 접근자 대신 더 복잡한 분류 기준이 필요한 경우 메서드 참조를 분류 함수로 사용할 수 없다.
이럴 경우엔 메서드 참조 대신 람다 표현식을 사용할 수 있다.

public enum CaloricLevel { DIET, NORMAL, FAT }

Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream()
	.collect(groupingBy(dish -> {
		if (dish.getCalories() <= 400) return CaloricLevel.DIET;
		else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
		else return CaloricLevel.FAT;
		}));


하나의 기준으로 그룹화를 했다.
이제 두 가지 이상의 기준으로 그룹화를 해보자.


그룹화된 요소 조작

만약 두 가지 기준이 필요해서 groupinBy 전에 filter를 통해 연산을 해준다면 어떤 문제가 생길까? 특정 조건을 만족하는 값들을 타입별로 그룹화한다고 했을 때, filter를 통해 조건을 필터링 해주고, Type을 그룹화할 수 있다. 하지만 이렇게 됐을 때 당초 자료에 있던 모든 Type의 종류가 맵에 저장되는 게 아니라 조건을 만족하는 Type만 저장되게 된다.

Map<Dish.Type, List<Dish>> dishesByTypeOver500Cal = menu.stream()
	.filter(d -> d.getCalories() > 500)
	.collect(groupingBy(Dish::getType));

Collectors 클래스는 일반적인 분류 함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy팩토리 메서드를 오버로드해 이 문제를 해결한다. 다음 코드에서 보여주는 것처럼 두 번째 Collector 안으로 필터 프레디케이트를 이동함으로 문제를 해결한다.

Map<Dish.Type, List<Dish>> dishesByTypeOver500Cal = menu.stream()
	.collect(groupingBy(Dish::getType,
		filtering(dish -> dish.getCalories() > 500, toList())));

(괄호 개많아지네 자바스크립트 처럼)

filtering 메소드는 Collectors 클래스의 또 다른 정적 팩토리 메서드로 프레디케이트를 인수로 받는다. 이 프레디케이트로 각 그룹의 요소와 필터링 된 요소를 재그룹화 한다.
이렇게 하면 목록이 빈 Type도 Map에 추가된다.

맵핑함수를 이용해 요소를 변환. map처럼 사용한 함수의 반환값을 모으기 위해 사용.
예를 들어 filtering은 반환값이 전달하는 Stream과 동일하지만, mapping은 다른 형식으로 변환 가능. (Dish --> String 또는 int 등)

Map<Dish.Type, List<String> dishNamesByType = menu.stream()
	.collect(groupingBy(Dish::getType,
		mapping(Dish::getName, toList())));

?mapping 후 자료반환을 toList가 아닌 다른 Collector의 메서드도 가능하겠네??

flatmapping 컬렉터를 이용하여 변환.
Map<String, List<String>> dishTags = new HashMap<>();
dishTags.put("pork", asList("greasy", "salty"));
dishTags.put("beef", asList("salty", "roasted"));
...

위 자료를 가지고 각 형식의 요리의 태그를 추출하기

Map<Dish.Type, Set<String>> dishNamesByType = menu.stream()
	.collect(groupingBy(Dish::getType, 
		flatMapping(dish -> dishTags.get( dish.getName() ).stream()
		,toSet())));

각 요리에서 태그 리스트를 얻어야 한다. 따라서 두 수준의 리스트를 한 수준으로 평면화하려
flatMap을 수행해야 한다. 리스트가 아닌 set을 통해 중복태그를 제거한다.


다수준 그룹화

두 인수를 받는 팩토리 메서드 Collectors.groupingBy를 이용해서 항목을 다수준으로 그룹화 할 수 있음. Collectors.groupingBy는 일반적인 분류 함수와 컬렉터를 인수로 받음.

예제
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
	menu.stream().collect(
		groupingBy(Dish::getType,	//첫 분류함수
			groupingBy( dish -> {    //둘째 분류함수
				if(dish.getCalories() <= 400)
					return CaloricLevel.DIET;
				else if(dish.getCalories() <= 700)
					return CaloricLevel.NORMAL;
				else return CaloricLevel.FAT;
				})
			)
		);
분류 함수 한 개의 인수를 갖는 groupingBy(f)는 사실 groupingBy(f, toList())의 축약형이다. 요리의 종류를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리를 찾는 프로그램도 다시 구현할 수 있다.

Map<Dish.Type, Optional<Dish>> mostCaloricByType =
	menu.stream()
		.collect(groupingBy(Dish::getType,
			maxBy(comparingInt(Dish::getCalories))));

maxBy + comparingInt

팩토리 메서드 maxBy가 생성하는 컬렉터의 결과 형식에 따라 맵의 값이 Optional 형식이 되었다. 실제로 메뉴의 요리 중 Optional.empty()를 값으로 갖는 요리는 없다. 처음부터 존재하지 않는 요리의 타입은 키로서 맵에 추가되지 않기 때문이다. groupingBy 컬렉터는 스트림의 첫 번째 요소를 찾은 이후에야 그룹화 맵에 새로운 키를 (게으르게) 추가함. 리듀싱 컬렉터가 반환하는 형식을 사용하는 상황이므로 굳이 Optional래퍼를 사용할 필요가 없다.
?이게 먼말임?


컬렉터 결과를 다른 형식에 적용하기

마지막 그룹화 연산에서 맵의 모든 값을 Optional로 감쌀 필요가 없으므로 Optional을 삭제할 수 있다. 팩토리 메서드 Collectors.collectingAndThen으로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.

각 서브그룹에서 가장 칼로리가 높은 요리 찾기
Map<Dish.Type, Dish> mostCaloricByType = 
	menu.stream()
		.collect(groupingBy(Dish::getType,	//분류함수
			maxBy(comparingInt(Dish::getCalories)),	//감싸인 컬렉터
		Optional::get))); //변환함수


팩토리 메서드 collectingAndThen은 적용할 컬렉터와 변환함수를 인수로 받아 다른 컬렉터를 반환함.
반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다.
리듀싱 컬렉터는 절대 Optional.empty()를 반환하지 않으므로 Optional::get은 안전하다.

GroupingBy와 함께 사용하는 다른 컬렉터 예제
일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때,
팩토리 메서드 groupingBy에 두 번째 인수로 전달한 컬렉터를 사용한다.

Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
	.collect(groupingBy(Dish::getType,
		summingInt(Dish::getCalories)));

이 외에도 mapping 메서드로 만들어진 컬렉터도 groupingBy와 자주 사용된다.
mapping메서드는 스트림의 인수를 변환하는 함수와 변환함수의 결과 객체를 누적하는 컬렉터를 인수로 받음.
mapping은 변환 역할이 있다.
예를 들어 각 요리 형식에 존재하는 모든 CaloricLevel값을 알고 싶다면,
Map<Dish.Type, ?> caloricLevelsByType = menu.stream()
	.collect(groupingBy(Dish::getType,
		mapping(dish -> {
		if(dish.getCalories() <= 400) return CaloricLevel.DIET;
		else if(dish.getCalories() <= 700) return CaloricLevel.NORMAL;
		else return CaloricLevel.FAT; },
		toSet() )));

Set의 형식을 정하고 싶다면 toCollection을 이용하면 된다.
HashSet::new를 toCollection에 전달할 수 있다.

Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = 
	.collect(groupingBy(Dish::getType,
		mapping(dish -> {
		if(dish.getCalories() <= 400) return CaloricLevel.DIET;
		else if(dish.getCalories() <= 700) return CaloricLevel.NORMAL;
		else return CaloricLevel.FAT; },
		toCollection(HashSet::new) )));


분할

분할은 분할 함수라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.
불리언을 반환하므로 맵의 키 형식은 Boolean이다. 참, 거짓의 그룹으로 분류된다.

채식요리와 채식아닌 요리로 분류
Map<Boolean, List<Dish>> partitionMenu = 
	menu.stream().collect(groupingBy(Dish::isVegetarian));

이제 참값의 키로 모든 채식요리를 얻을 수 있다.
List<Dish> vegetarianDishes = partitionMenu.get(true);

위를 한 번에 하면,

List<Dish> vegetarianDishes = menu.stream().filter(Dish::isVegetaian)
	.collect(toList());


분할의 장점
분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점이다.
partitioningBy메서드 사용
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType =
	menu.stream()
	collect(partitioningBy(Dish::isVegetarian,  //분할 함수
		groupingBy(Dish::getType)));

	
이전 코드를 활용하면 채식요리와 채식이 아닌 요리 각각 그룹에서 가장 칼로리가 높은 요리도 찾을 수 있다.

Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = 
	menu.stream().collect(
		partitioningBy(Dish::isVegetarian,
			collectAndThen(maxBy(comparingInt(Dish::getCalories)),
			Optional::get)));

사실 내부적으로 partitioningBy는 특수한 맵과 두 개의 필드로 구현되어 있다.

다음 코드의 다수준 분할 결과 예측

1. menu.stream().collect(partitioningBy(Dish::isVegetarian,
			partitioningBy(d -> d.getCalories() > 500)));

2. menu.stream().collect(partitioningBy(Dish::isVegetarian,
			partitioningBy(Dish::getType)));

3. menu.stream().collect(partitioningBy(Dish::isVegetarian,
			counting()));

답
1은 false=[false=[dishUnder500], true=[dishOver500]], true = [false=[dishUnder500], true=[dishOver500]]
으로 이중 분할 가능.

2는 grouping이 아닌 partitioning을 해서 안될 거 같은데?

3은 결과로 counting줘서 반환되는구나. 몰랐음.


숫자를 소수와 비소수로 분할하기

정수 n을 인수로 받아서 2에서 n까지의 자연수를 소수와 비소수로 나누는 프로그램 구현.
먼저 소수여부 판단하는 프레디케이트를 구현하자.

public boolean isPrime(int candidate) {
	return IntStream.range(2, candidate)	//2부터 candidate 미만 자연수
		.noneMatch(i -> candidate % i == 0); //나눠서 떨어지는 게 없다면 참 반환.
}

소수의 대상을 주어진 수의 제곱근 이하의 수로 제한할 수 있다.(성능 최적화)

public boolean isPrime(int candidate){
	int candidateRoot = (int) Math.sqrt((double)candidate);
	return IntStream.rangeClosed(2, candidateRoot)
				.noneMatch(i -> candidate % 1 == 0);
}

public Map<Boolean, List<Integer>> partitionPrimes(int n) {
	return IntStream.rangeClosed(2, n).boxed()			.collect(partitioningBy(candidate -> isPrime(candidate)));	
}

boxed하는 이유가 뭐더라? 기억이 안나네.
IntStream을 Stream<Integer>로 변환해줌.
IntStream을 왜 쓰더라? 그냥 기본형스트림을 제공하고 int 같은 기본형에 대한 메서드를 지원해서 쓰나?


Collectors 클래스의 정적 팩토리 메서드
- toList
- toSet
- toCollection
- counting
- summingInt
- averageInt
- summarizingInt
- joining
- maxBy
- minBy
- reducing
- collectingAndThen
- groupingBy
- partitioningBy


Collector 인터페이스

toList를 통해 Collector는 어떻게 정의되어 있고, 내부적으로 collect메서드는 toList가 반환하는 함수를 어떻게 활용했는지 이해해보자.

Collector 인터페이스의 시그니처와 다섯 개의 메서드 정의
public interface Collector<T, A, R> {
	Supplier<A> supplier();
	BiConsumer<A, T> accumulator();
	Function<A, R> finisher();
	BinaryOperator<A> combiner();
	Set<Characteristics> characteristics();
}

- T는 수집될 스트림 항목의 제네릭 형식
- A는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식
- R은 수집 연산 결과 객체의 형식(항상 그런 것은 아니지만 대개 컬렉션 형식)이다.

예를 들어 Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T>라는 클래스를 구현한다면,
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>

누적 과정에서 사용되는 객체가 수집 과정의 최종 결과로 사용된다.


Collector 인터페이스의 메서드 살펴보기

characteristics는 collect 메서드가 어떤 최적화(ex, 병렬화)를 이용해서 리듀싱 연산을 수행할 것인지 결정하도록 돕는 힌트 특성 집합을 제공한다.
나머지 네 메서드는 collect 메서드에서 실행하는 함수를 반환한다.

supplier 메서드 : 새로운 결과 컨테이너 만들기

supplier메서드는 빈 결과로 이루어진 Supplier를 반환해야 한다.
supplier는 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수이다.

ToListCollector에서 supplier는 빈 리스트를 반환함.

public Supplier<List<T>> supplier() {
	return () -> new ArrayList<T>();
}

public Supplier<List<T>> supplier() {
	return ArrayList::new;
}


accumulator 메서드 : 결과 컨테이너에 요소 추가하기
accumulator메서드는 리듀싱 연산을 수행하는 함수를 반환.
스트림에서 n번째 요소를 탐색할 때 두 인수, 누적자(스트림의 첫 n-1개 항목을 수집한 상태)와 n번째 요소를 함수에 적용.
함수의 반환 값은 void, 즉 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다. 
ToListCollector에서 accumulator가 반환하는 함수는 이미 탐색한 항목을 포함하는 리스트에 현재 항목을 추가하는 연산을 수행.

public BiConsumer<List<T>,T> accumulator() {
	return (list, item) -> list.add(item);
}

public BiConsumer<List<T>, T> accumulator() {
	return List::add;
}


finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기

finisher메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다. 
?약간 finally와 같은 건가?
public Function<List<T>, List<T>> finisher() {
	 return Function.identity();
}


combiner 메서드 : 두 결과 컨테이너 병합

combiner메서드는 스트림의 서로 다른 서브파티를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의.
toList의 combiner는 비교적 쉽게 구현 가능.

public BinaryOperator<List<T>> combiner() {
	return (list1, list2) -> {
		list1.addAll(list2);
		return list1;
	}
}

네 번째 메서드를 이용하면 스트림의 리듀싱을 병렬로 수행할 수 있다. 
병렬로 수행할 때 자바7의 포크/조인 프레임워크와 Spliterator를 사용함.
- 스트림을 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다.(보통 분산된 작업의 크기가 너무 작아지면 병렬 수행 속도는 순차 수행 속도보다 느려진다.)
- 이제 모든 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리 가능.
- 마지막엔 컬렉터의 combiner메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합침.


Characteristics 메서드
컬렉터의 연산을 정의하는 characteristics 형식의 불변 집합을 반환. 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지 힌트를 제공. 
Characteristics는 다음 세 항목을 포함하는 열거형이다.
- UNORDERED : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
- CONCURRENT : 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은 상황에서만 병렬 리듀싱을 수행할 수 있다.
- IDENTITY_FINISH : finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다. 또한 누적자 A를 결과R로 안전하게 형변환할 수 있다.

지금까지 개발한 ToListCollector에서 스트림의 요소를 누적하는 데 사용한 리스트가 최종결과 형식이므로 추가 변환이 필요 없다. 따라서 ToListCollector는 IDENTITY_FINISH다.
하지만 리스트의 순서는 상관이 없으므로 UNORDERED다. 마지막으로 ToListCollector는 CONCURRENT다. 하지만 이미 설명했듯이 요소의 순서가 무의미한 데이터 소스여야 병렬로 실행할 수 있다.


응용하기

지금까지 살펴본 다섯 가지 메서드를 이용해 자신만의 커스텀 ToListCollector를 구현할 수 있다.

public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
	@Override
	public Supplier<List<T>> supplier() {
		return ArrayList::new;	//수집 연산의 시발점
	}

	@Override
	public BiConsumer<List<T>, T> accumulator() {
		return List::add;  //탐색한 항목을 누적하고 바로 누적자를 고침. 
	}

	@Override
	public Function<List<T>, List<T>> finisher() {
		return Function.identity();  //항등함수
	}

	@Override
	public BinaryOperator<List<T>> combiner() {
		return (list1, list2) -> {
			list1.addAll(list2);	
			return list1;
		}
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(
			IDENTITY_FINISH, CONCURRENT));
	}
}

위 구현이 Collectors.toList 메서드가 반환하는 결과와 완전히 같은 것은 아니지만 사소한 최적화를 제외하면 대체로 비슷. 특히 자바API에서 제공하는 컬렉터는 싱글턴 Collections.emptyList()로 빈 리스트를 반환한다. 

List<Dish> dishes = menuStream.collect(new ToListCollector<Dish>());

다음은 기존의 코드다.

List<Dish> dishes = menuStream.collect(toList());

기존 코드의 toList는 팩토리지만 ToListCollector는 new로 인스턴스화한다는 점이 다르다.


컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기

IDENTITY_FINISH 수집 연산에서는 Collector 인터페이스를 완전히 새로 구현하지 않고도 같은 결과를 얻을 수 있다. Stream은 세 함수(발행, 누적, 합침)를 인수로 받는 collect 메서드를 오버로드하며 각각의 메서드는 Collector인터페이스의 메서드가 반환하는 함수와 같은 기능을 수행한다. 

예를 들어 스트림의 모든 항목을 리스트에 수집하는 방법도 있다.

List<Dish> dishes = menuStream.collect(
	ArrayList::new,  //발행
	List::add,	//누적
	List::addAll);	//합침
	
두 번째 코드가 이전 코드에 비해 좀 더 간결하고 축약되어 있지만, 가독성은 떨어진다.
커스텀 컬렉터를 구현하는 편이 중복을 피하고 재사용성을 높이는 데 도움이 된다.

두 번째 collect메서드로는 Characteristics를 전달할 수 없다. 
즉 IDENTITY_FINISH와 CONCURRENT지만 UNORDERED는 아닌 컬렉터로만 동작한다.


커스텀 컬렉터를 구현해서 성능 개선하기

n이하의 자연수를 소수와 비소수로 분류하기(과거 예제)
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
	return IntStream.rangeClosed(2, n).boxed()
		.collect(partitioningBy(candidate -> isPrime(candidate));
}

위 코드에서 isPrime메서드를 제곱근 이하로 대상의 숫자 범위를 제한해서 메서드를 개선함.

커스텀 컬렉터를 이용해서 성능을 더 개선해보자.


소수로만 나누기

우선 소수로 나누어떨어지는지 확인해서 대상의 범위를 좁힐 수 있다. 제수가(devisor) 소수가 아니면 소용없으므로 제수를 현재 숫자 이하에서 발견한 소수로 제한할 수 있다. 

중간결과 리스트가 있다면 isPrime 메서드로 중간 결과 리스트를 전달하도록 다음과 같이 코드를 구현할 수 있다.
public static boolean isPrime(List<Integer> primes, int candidate) {
	return primes.stream().noneMatch(i -> candidate % 1 == 0);
}

이번에도 대상 숫자의 제곱근보다 작은 소수만 사용하도록 코드를 최적화해야 한다.
현재 숫자 이하를 반환하는 takeWhile메서드를 구현한다.

public static boolean isPrime(List<Integer> primes, int candidate) {
	int candidateRoot = (int) Math.sqrt((double) candidate);	
	return primes.stream()
		.takeWhile(i -> i <= candidateRoot)
		.noneMatch(i -> candidate % 1 == 0);
}


takeWhile은 자바9의 기능으로 자바8에선 어떻게 해야 할까?
takeWhile을 직접 구현해 보자.

public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
	int i = 0;
	for (A item : list){
		if (!p.test(item)) {	
			//리스트의 현재 항목이 프레이케이트를 만족하는지 확인
			//만족하지 않으면 현재 검사한 항목의 이전 항목 하위 리스트
			//반환
			return list.subList(0, 1);
		}
		i++;
	}
	//리스트의 모든 항목이 프레디케이트를 만족하므로 리스트 자체 반환
	return list; 
}

스트림API와 달리 직접 구현한 takeWhile메서드는 적극적으로 동작한다. 따라서 가능하면 noneMatch 동작과 조화를 이룰 수 있도록 자바9의 게으른 버전의 takeWhile을 사용하는 것이 좋다.

게으르다는 게 정확히 어떤 동작이더라? 계산을 미리 하지 않는다. 좀 더 구체적으로 알아봐야겠음.


새로운 isPrime메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자. 우선 Collector인터페이스를 구현하는 새로운 클래스를 선언한 다음에 Collector인터페이스에서 요구하는 메서드 다섯 개를 구현한다.

1단계 : Collector 클래스 시그니처 정의

다음의 Collector 인터페이스 정의를 참고해서 클래스 시그니처를 만들자.

public interface Collector<T, A, R>

위 코드에서 T는 스트림 요소의 형식, A는 중간 결과를 누적하는 객체의 형식, R은 collect 연산의 최종 결과 형식을 의미한다. 
우리는 정수로 이루어진 스트림에서 누적자와 최종 결과의 형식이 Map<Boolean, List<Integer>> 인 컬렉터를 구현해야 한다. 즉, Map<Boolean, List<Integer>>는 참과 거짓을 키로, 소수와 소수가 아닌 수를 값으로 갖는다.

public class PrimeNumbersCollector implements Collector
	<Integer,			//계산자
	Map<Boolean, List<Integer>>,	//누적자
	Map<Boolean, List<Integer>>>	//최종결과

2단계 : 리듀싱 연산 구현

이번에는 Collector 인터페이스에 선언된 다섯 메서드를 구현해야 한다. supplier메서드는 누적자를 만드는 함수를 반환해야 한다.

public Supplier<Map<Boolean, List<Integer>>> supplier() {
	return () -> new HashMap<Boolean, List<Integer>>() {{
		put(true, new ArrayList<Integer>());
		put(false, new ArrayList<Integer>());
	}};
}

(익명 클래스를 사용해서 Map에 초기값을 부여)

스트림의 요소를 어떻게 수집할지 결정하는 것은 accumulator 메서드이다. 가장 중요한 메서드이다. 최적화의 핵심이다. 

public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
	return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
		acc.get( isPrime(acc.get(true), candidate) )
		.add(candidate);
	};
}

위 코드에서 지금까지 발견한 소수리스트(누적 맵의 true키로 접근)와 소수 여부를 확인하는 candidate를 인수로 isPrime 메서드를 호출.


3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)

병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다. 

public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
	return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
		map1.get(true).addAll(map2.get(true));
		map1.get(false).addAll(map2.get(false));
		return map;
	};
}
(true면 true에 추가하고, false면 false에 추가)

참고로 알고리즘 자체가 순차적이어서 컬렉터를 실제 병렬로 사용할 순 없다. 따라서 combiner메서드는 호출될 일이 없으므로 빈 구현으로 남겨둘 수 있다.(또는 UnsupportedOperationException을 던지도록 구현하는 방법도 좋다.) 실제로 이 메서드는 사용할 일이 없지만 학습을 목적으로 구현한 것이다.
(이게 먼말이여??)

4단계 : finisher메서드와 컬렉터의 characteristics메서드
나머지 두 메서드는 쉽게 구현할 수 있다. 

accumulator의 형식은 컬렉터 결과 형식과 같으므로 변환과정이 필요 없다. 따라서 항등 함수 identity를 반환하도록 finisher메서드를 구현한다.

public Function<Map<Boolean, List<Integer>>,
		Map<Boolean, List<Integer>>> finisher() {
	return Function.identity();
}

커스텀 컬렉터는 CONCURRENT도 아니고 UNORDERED도 아니지만 IDENTITY_FINISH이므로 다음처럼 characteristics 메서드를 구현할 수 있다.

public Set<Characteristics> characteristics() {
	return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
}

PrimeNumberCollector의 최종 구현 코드
public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>,
Map<Boolean, List<Integer>>> {
	@Override
	public Supplier<Map<Boolean, List<Integer>>> supplier() {
		return () -> new HashMap<Boolean, List<Integer>> () {{
			put(true, new ArrayList<Integer>());
			put(false, new ArrayList<Integer>());
		}};
	}

	@Override
	public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
		return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
			acc.get( isPrime(acc.get(true), candidate)
			.add(candidate);
		};
	}
	
	@Override
	public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
		return (Map<Boolean, List<Integer>> map1,
			Map<Boolean, List<Integer>> map2) -> {
			map1.get(true).addAll(map2.get(true));
			map1.get(false).addAll(map2.get(false));
			return map1;
		};
	}

	@Override
	public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>>  finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
	}
}


마치며
- collect는 스트림의 요소를 요약 결과로 누적하는 다양한 방법(컬렉터라 불리는)을 인수로 갖는 최종 연산.
- 스트림의 요소를 하나의 값으로 리듀스하고 요약하는 컬렉터뿐 아니라 최솟값, 최댓값, 평균값을 계산하는 컬렉터 등이 미리 정의되어 있다.
- 미리 정의된 컬렉터인 groupingBy로 스트림의 요소를 그룹화하거나, partioningBy로 스트림의 요소를 분할할 수 있다.
- 컬렉터는 다수준의 그룹화, 분할, 리듀싱 연산에 적합하게 설계되어 있다.
- Collector 인터페이스에 정의된 메서드를 구현해서 커스텀 컬렉터를 개발할 수 있다.



병렬 데이터 처리와 성능

자바 7 등장 전에는 데이터 컬렉션을 병렬로 처리하기가 어려웠다. 우선 데이터를 서브파트로 분할 -> 분할된 서브파트를 각각의 스레드로 할당 -> 의도치 않은 레이스 컨디션(경쟁상태)이 발생하지 않도록 적절한 동기화 추가 -> 부분 결과 합침.
자바7은 더 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있도록 포크/조인 프레임워크 기능을 제공한다.

스트림을 이용하면 순차 스트림을 병렬 스트림으로 자연스럽게 바꿀 수 있다.
병렬 스트림이 내부적으로 어떻게 처리되는지 알고 스트림을 잘못 사용하는 상황을 피하자.

여러 청크를 병렬로 처리하기 전에 병렬 스트림이 요소를 여러 청크로 분할하는 방법을 설명. 이 원리를 이해하지 못하면 의도치 않은, 설명하기 어려운 결과가 발생할 수 있다. 따라서 커스텀 Spliterator를 직접 구현하면서 분할 과정을 우리가 원하는 방식으로 제어하는 방법도 설명한다.


병렬 스트림

컬렉션에 parallelStream을 호출하면 병렬 스트림이 생성된다. 병렬 스트림이란 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다. 따라서 병렬 스트림을 이용하면 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있다.

숫자 n을 인수로 받아서 1부터 n까지 모든 숫자의 합계를 반환하는 메서드를 구현할 시.
//스트림 사용
public long sequentialSum(long n) {
	return Stream.iterate(1L, i -> i + 1)
			.limit(n)
			.reduce(0L, Long::sum);	//BinaryOperator로 리듀싱 작업 수행
}

//전통적인 자바
public long iterativeSum(long n) {
	long result = 0;
	for (long i = 1L; i <= n; i++) {
		result += i;
	}
	return result;
}

n이 커진다면 연산을 병렬로 처리하는 것이 좋을 것이다.
병렬 연산시 고려해야 할 것들은 무엇이 있을까?
1. 결과 변수는 어떻게 동기화해야 할까?
2. 몇 개의 스레드를 사용해야 할까?
3. 숫자는 어떻게 생성할까?
4. 생성된 숫자는 누가 더할까?

위의 고민을 병렬 스트림을 이용하면 쉽게 해결할 수 있다.


순차 스트림을 병렬 스트림으로 변환하기

public long parallelSum(long n) {
	return Stream.iterator(1L, i -> i + 1)
		.limit(n)
		.parallel()	//스트림을 병렬로 변환
		.reduce(0L, Long::sum);
}

리듀싱 연산을 여러 청크에 병렬로 수행하고 생성된 부분 결과를 다시 리듀싱 연산으로 합쳐서 전체 스트림의 리듀싱 결과를 도출한다.

순차스트림에 parallel을 호출해도 스트림 자체에는 아무 변화도 일어나지 않는다. 내부적으로는 parallel을 호출하면 이후 연산이 병렬로 수행되어야 함을 의미하는 불리언 플래그가 설정된다. 반대로 sequential은 병렬을 순차로 바꿀 수 있다.

stream.parallel()
	.filter(...)
	.sequential()
	.map(...)
	.parallel()
	.reduce();

parallel과 sequential 두 메서드 중 최종적으로 호출된 메서드가 전체 파이프라인에 영향을 미친다.
최종 호출이 parallel이므로 전체적으로 병렬로 실행된다.


병렬 스트림에서 사용하는 스레드 풀 설정

병렬 스트림은 내부적으로 ForkJoinPool을 사용한다. 기본적으로 프로세서 수, Runtime.getRuntime().availableProcessors()가 반환하는 값에 상응하는 스레드를 갖는다.


System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
전역 설정 코드이므로 이후의 모든 병렬 스트림 연산에 영향을 준다. 현재는 하나의 병렬 스트림에 사용할 수 있는 특정한 값을 지정할 수 없다. 일반적으로 기기의 프로세서 수와 같으므로 특별한 이유가 없다면 ForkJoinPool의 기본값을 그대로 사용할 것.

[ 반복형 / 순차 리듀싱 / 병렬 리듀싱 ] 중 어느 것이 가장 빠를지 확인해보자.


스트림 성능 측정

병렬화를 이용하면 순차나 반복 형식에 비해 성능이 더 좋아질 것이라 추측했다.
소프트웨어 공학에선 추측보단 측정을 해야 한다.
자바 마이크로벤치마크 하니스(JMH)라는 라이브러리를 통해 작은 벤치마크를 구현하자.
JMH는 어노테이션 기반 방식을 지원, 안정적으로 JVM기반 벤치마크를 구현 가능.
사실 JVM으로 실행되는 프로그램을 벤치마크하는 작업은 쉽지 않다.
핫스팟이 바이트코드를 최적화 하는데 필요한 준비시간, 가비지 컬렉터로 인한 오버헤드 등과 같은 여러 요소를 고려해야하기 때문이다.
메이븐 빌드 도구를 사용한다면 메이븐 빌드 과정을 정의하는 pom.xml 파일에 몇 가지 의존성을 추가해 프로젝트에서 JMH를 사용할 수 있다. (당연히 gradle도 가능)

<dependency>
	<groupId>org.openjdk.jmh</groupId>
	<artifactId>jmh-core</artifacId>
	<version>1.17.4</version>
</dependency>
<dependency>
	<groupId>org.openjdk.jmh</groupId>
	<artifactId>jmh-generator-annprocess</artifactId>
	<version>1.17.4</version>
</dependency>

첫 번째 라이브러리는 핵심 JMH 구현을 포함하고 두 번째 라이브러리는 자바 아카이브(JAR)파일을 만드는 데 도움을 주는 어노테이션 프로세서를 포함한다.
다음 플러그인도 추가한 다음 아카이브 파일을 이용해서 벤치마크를 편리하게 실행할 수 있다.

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <executions>
        <execution>
          <phase>package</phase>
          <goals><goal>shade</goal></goals>
          <configuration>
            <finalName>benchmarks</finalName>
            <transformers>
              <transformer implementation=
                "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>org.openjdk.jmh.Main</mainClass>
              </transformer>
            </transformers>
          </configuration>
	</execution>
      </executions>
    </plugin>
  </plugins>
</build>


n개의 숫자를 더하는 함수의 성능 측정

@BenchmarkMode(Mode.AverageTime)       //벤치마크 대상 메서드 실행시 걸린 평균시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) //벤치마크 결과 밀리초 단위 출력
@Fork(2, jvmArgs="-Xms4G", "-Xmx4G"})  //4Gb의 힙 공간을 제공한 환경에서 두 번 벤치마크 수행(결과 신뢰성 확보)
public class ParalleStreamBenchmark {
	private static final long N= 10_000_000L;
	
	@Benchmark   //벤치마크 대상 메서드
	public long sequentialSum(){
		return Stream.iterate(1L, i -> i + 1).limit(n).reduce( 0L, Long::sum);
	}

	@TearDown(Level.Invocation)  //매 번 벤치마크 실행한 다음에는 가비지 컬렉터 동작 시도
	public void tearDown(){
		System.gc();
	}
}

클래스를 컴파일하면 이전에 설정한 메이븐 플러그인이 benchmarks.jar라는 두 번째 파일을 만든다.
파일을 실행하자.

java -jar ./target/benchmarks.jar ParallelStreamBenchmark

벤치마크가 가능한 가비지 컬렉터의 영향을 받지 않도록 힙의 크기를 충분하게 설정했다.
또한 벤치마크 끝날 때마다 가비지 컬렉터를 실행하도록 강제했다.
이렇게 주의를 기울였지만 여전히 결과는 정확하지 않을 수 있다.
기계가 지원하는 코어의 갯수 등이 실행 시간에 영향을 미칠 수 있다.

JMH 명령은 핫스팟이 코드를 최적화 할 수 있도록 20번을 실행하며 벤치마크를 준비한 다음 20번을 더 실행해 최종결과를 계산한다. 
JMH는 기본적으로 20 + 20 회 반복 실행한다.
JMH의 특정 어노테이션이나 -w, -i플래그를 사용해서 횟수를 조절할 수 있다.

벤치마크를 실행하니 반복 > 순차 > 병렬 순으로 속도가 빨랐다.
왜 그럴까?

두 가지 문제가 있다.
- 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야 한다.
- 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.  (머여 어캄 그럼? 왜 만듬?)

 
iterate는 본질적으로 순차적이다. 병렬로 실행했어도 결국 순차처리 방식과 크게 다른 점이 없고 스레드를 할당하는 오버헤드만 증가했다.

병렬 프로그래밍은 병렬과 거리가 먼 반복 작업을 하면 오히려 성능이 더 나빠질 수 있다.
그렇다보니 parallel 메서드를 호출했을 때 내부처리과정을 잘 이해해야 한다.


더 특화된 메서드 사용
멀티코어 프로세서를 활용해서 효과적으로 합계 연산을 병렬로 실행하려면?
rangeClosed는 iterate에 비해 아래와 같은 장점이 있다.
- LongStream.rangeClosed는 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라짐.
- LongStream.rangeClosed는 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다.
  예를 들어, 1-20 범위의 숫자를 1-5, 6-10, 11-15, 16-20범위의 숫자로 분할할 수 있다.


@Benchmark
public long rangeSum(){
	return LongStream.rangeClosed(1,N)
		.reduce(0L, Long::sum);
}

iterate보다 처리속도가 더 빠르다. 오토박싱, 언박싱 등의 오버헤드가 사라졌다.
이걸 통해 상황에 따라서는 어떤 알고리즘을 병렬화하는 것보다 적절한 자료구조를 선택하는 것이 더 중요하다는 사실을 알 수 있다.
그렇다면 병렬처리를 하면 어떨까?

@Benchmark
public long parallelRangeSum() {
	return LongStream.rangeClosed(1, N)
		.parallel()
		.reduce(0L, Long::sum);
}

순차 실행보다 빠른 병렬 리듀싱이다. 반복처리보다 빨랐다.
하지만 병렬화를 이용하려면 스트림을 재귀적으로 분할해야 하고, 각 서브스트림을 서로 다른 스레드의 리듀싱 연산으로 할당하고, 이들 결과를 하나의 값으로 합쳐야 한다. 멀티코어 간의 데이터 이동은 우리 생각보다 비싸다. 따라서 코어 간에 데이터 전송 시간보다 훨씬 오래 걸리는 작업만 병렬로 다른 코어에서 수행하는 것이 바람직하다.


병렬 스트림의 올바른 사용법

주요 문제는 공유된 상태를 바꾸는 알고리즘을 사용하는 것.
다음은 n까지의 자연수를 더하면서 공유된 누적자를 바꾸는 프로그램을 구현한 코드다.

public long sideEffectSum(long n) {
	Accumulator accumulator = new Accumulator();
	LongStream.rangeClosed(1, n).forEach(accumulator::add);
	return accumulator.total;
}

public class Accumulator{
	public long total = 0;
	public void add(long value) { total += value; }
}

위 코드의 문제는? 순차 실행시엔 문제가 없으나, 병렬 실행시 문제 발생
total에 접근할 때, 다수의 스레드에서 동시에 접근하면서 데이터 레이스 문제가 일어난다.
동기화로 문제를 해결하면 결국 병렬화의 특성이 없어져 버릴 것이다.

public long sideEffectParallelSum(long n) {
	Accumulator accumulator = new Accumulator();
	LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
	return accumulator.total;
}

위의 함수를 실행해보자.

System.out.println("SideEffect parallel sum done in: " +
	measurePerf(ParallelStream::sideEffectParallelSum, 10_000_000L) + " msecs" );

올바른 결과값이 나오지 않는다. 병렬 스트림을 사용했을 때 상태 공유에 따른 부작용을 피해야 한다.


병렬 스트림 효과적으로 사용하기

양을 기준으로 병렬 스트림 사용을 결정하는 것은 적절하지 않다. 기기나 환경이 바뀌면 양 역시도 기준이 바뀔 수 있다. 그래도 어떤 상황에서 병렬 스트림을 사용할 것인지 약간의 수량적 힌트를 정하는 것이 도움이 되기도 한다.

- 확신이 서지 않으면 직접 측정하라. 병렬 스트림의 수행과정은 투명하지 않으니 적절한 벤치마크로 직접 성능을 측정하자.
- 박싱을 주의하라. 자동 박싱과 언박싱은 성능을 크게 저하시킬 수 있는 요소이다. 기본형 특화 스트림을 사용하자.
- 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다. 특히 limit나 findFirst처럼 요소의 순서에 의존하는 연산이 그렇다.
- 스트림에서 수행하는 전체 파이프라인 연산비용을 고려하자. 처리해야할 요소수가 N이고 하나의 요소를 처리하는데 드는 비용이 Q라면 전체 스트림 파이프라인 처리 비용을 N*Q라고 예상할 수 있다. Q가 높아진다는 것은 병렬 스트림으로 성능을 개선할 가능성이 있음을 의미한다.
- 소량의 데이터에서는 병렬 스트림이 도움되지 않는다. 이점보다 오버헤드가 더 크다.
- 스트림을 구성하는 자료구조가 적절한지 확인하라. 예를 들어 ArrayList를 LinkedList보다 효율적으로 분할할 수 있다. LinkedList를 분할하려면 모든 요소를 탐색해야 한다.
- 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다. 
- 최종 연산의 병합 과정(예를 들면 Collector의 combiner 메서드) 비용을 살펴보라. 병합 과정의 비용이 비싸다면 병렬 스트림으로 얻은 성능의 이익이 서브스트림의 부분결과를 합치는 과정에서 상쇄될 수 있다.

스트림 소스의 분해성
소스
ArrayList - 훌륭함		//array구조라 좋음.
LinkedList - 나쁨			//객체가 연결된 구조라 나쁨.
IntStream.range - 훌륭함		//내부로직이 알아서 분배해주는 듯
Stream.iterate - 나쁨		//값을 하나씩 생성시키기 때문에 순차적이라 나쁨. 
HashSet - 좋음			//해쉬는 실질적으로 array를 사용하므로 좋을 듯.
TreeSet - 좋음			//왜 좋은지 모르겠음

마지막으로 병렬 스트림이 수행되는 내부 인프라구조도 살펴봐야 함. 
자바7에서 추가된 포크/조인 프레임워크로 병렬 스트림이 처리된다. 포크/조인 프레임워크를 살펴보자.


포크/조인 프레임워크

포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음에 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되어있다.
포크/조인 프레임워크에서는 서브태스크를 스레드 풀의 작업자 스레드에 분산할당하는 ExecutorService인터페이스를 구현한다.

RecursiveTask 활용
스레드풀을 이용하려면 RecursiveTask<R>의 서브클래스를 만들어야 한다. 
여기서 R은 병렬화된 태스크가 생성하는 결과 형식 또는 결과값이 없을 때(결과가 없더라도 다른 비지역 구조를 바꿀 수 있다)는 RecursiveAction 형식이다. 
RecursiveTask를 정의하려면 추상 메서드 compute를 구현해야 한다.

protected abstract R compute();

compute 메서드는 태스크를 서브태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다. 따라서 다음과 같은 의사코드 형식을 유지한다.

if (태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
	순차적으로 태스크 계산
} else {
	태스크를 두 서브태스크로 분할
	태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
	모든 서브태스크의 연산이 완료될 때까지 기다림
	각 서브태스크의 결과를 합침
}

태스크를 더 분할할 것인지 말 것인지 정해진 기준은 없지만 몇 가지 경험적으로 얻은 좋은 데이터가 있다.

분할 후 정복 알고리즘의 병렬화 버전이다.

포크/조인 프레임워크를 이용해서 병렬 합계 수행
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {
	private final long[] numbers;  //더할 숫자 배열
	private final int start;       //서브태스크에서 처리할 배열의 초기 위치와 최종위치
	private final int end;
	public static final long THRESHOLD = 10_000; //분할 임계값
	
	public ForkJoinCalculator(long[] numbers){  //메인 태스크 생성시 공개 생성자
		this(numbers, 0, numbers.length);
	}
	private ForkJoinCalculator(long[] numbers, int start, int end) { //비공개 생성자
		this.numbers = numbers;		//서브태스크를 재귀적으로 만들 때 사용
		this.start = start;
		this.end = end;
	}

	@Override
	protected Long compute() {
		int length = end - start;
		if (length <= THRESHOLD) {
			return computeSequentially();
		}
		ForkJoinSumCalculator leftTask = 
			new ForkJoinSumCalculator(numbers, start, start + length/2);
		leftTask.fork();
		ForkJoinSumCalculator rightTask = //다른 스레드로 생성한 태스크 비동기실행
			new ForkJoinSumCalculator(numbers, start + length/2, end);
		Long rightResult = rightTask.compute();	//두번째 서브태스크를 동기실행
		Long leftResult = leftTask.join();	//첫째 서브태스크결과 확인 또는 대기
		return leftResult + rightResult;	//분할된 서브태스크의 조합을 결과로
	}

	private long computeSequentially() {		//분할 불가능하면 계산하는 로직
		long sum = 0;
		for (int i = start; i < end; i++){
			sum += numbers[i];
		}
		return sum;
	}
}


ForkJoinSumCalculator의 생성자로 원하는 수의 배열을 넘겨줄 수 있다.

public static long forkJoinSum(long n) {
	long[] numbers = LongStream.rangeClosed(1, n).toArray();
	ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
	return new ForkJoinPool().invoke(task);
}

LongStream으로 n까지의 자연수를 포함하는 배열을 생성했다. 
생성된 배열을 ForkJoinSumCalculator의 생성자로 전달해서 ForkJoinTask를 만들었다.
마지막으로 생성한 태스크를 새로운 ForkJoinPool의 invoke메서드로 전달했다.

일반적으로 애플리케이션에서 둘 이상의 ForkJoinPool을 사용하지 않는다.
소프트웨어의 필요한 곳에서 언제든 가져다 쓸 수 있도록 ForkJoinPool을 한 번만 인스터스화해서 정적필드에 싱글턴으로 저장한다. ForkJoinPool을 만들면서 인수가 없는 디폴트 생성자를 이용했는데, 이는 JVM에서 이용할 수 있는 모든 프로세서가 자유롭게 풀에 접근할 수 있음을 의미한다.
더 정확힌 Runtime.availableProcessors의 반환값으로 풀에 사용할 스레드 수를 결정한다.
'availableProcessors' 사용할 수 있는 프로세서과 달리 실제 프로세서 외에 하이퍼스레딩과 관련된 가상 프로세서도 개수에 포함한다.

ForkJoinSumCalculator 실행

계산기객체를 ForkJoinPool로 전달하면 풀의 스레드가 compute매서드를 실행하면서 작업을 수행한다. 
compute 메서드는 병렬로 실행할 만큼 태스크가 작아졌는지 확인 후 배열을 추가로 분할하거나 실행한다.
이 과정이 재귀로 발생한다. 
각 서브태스크는 순차적으로 처리되며 포킹 프로세스로 만들어진 이진트리의 태스크를 루트에서 역순으로 방문한다.

성능확인
System.out.println("ForkJoin sum done in: " + measureSumPerf(
	ForkJoinSumCalculator::forkJoinSum, 10_000_000) + " msecs" );

병렬 스트림보다 성능이 나빠졌다. 하지만 이는 ForkJoinSumCalculator 태스크에서 사용할 수 있도록 전체 스트림을 long[] 으로 변환했기 때문이다.
?먼 말이여?


포크/조인 프레임워크를 제대로 사용하는 방법
쉽게 사용할 수 있지만 주의해서 사용하자.
- join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. 따라서 두 서브태스크가 모두 시작된 다음에 join을 호출해야 한다.
그렇지 않으면 각각의 서브태스크가 다른 태스크가 끝나길 기다리는 일이 발생한다.
- ResursiveTask내에서는 ForkJoinPool의 invoke메서드를 사용하지 말아야 한다.
대신 compute나 fork 메서드를 직접 호출할 수 있다. 순차코드에서 병렬 계산을 시작할 때만 invoke를 사용한다.
- 서브태스크에 fork 메서드를 호출해서 ForkJoinPool의 일정을 조절할 수 있다. 왼쪽 작업과 오른쪽 작업 모두에 fork 메서드를 호출하는 것이 자연스러울 것 같지만 한쪽 작업에는 fork를 호출하는 것보다는 compute를 호출하는 것이 효율적이다. 그러면 두 서브태스크의 한 태스크에는 같은 스레드를 재사용할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있다.
??먼말이지 ?위에서 left만 fork하길래 이상하다 생각했는데 이런 소리였군?
- 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅이 어렵다. 포크/조인 프레임워크에서는 fork라 불리는 다른 스레드에서 compute를 호출하므로 스택트레이스가 도움이 되지 않는다.
- 병렬 스트림에서 살펴본 것처럼 멀티코어에 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠른 것은 아니다. 다른 자바 코드와 마찬가지로 JIT 컴파일러에 의해 최적화되려면 몇 차례의 '준비과정' 또는 실행과정을 거쳐야 한다. 따라서 성능 측정시 하니스에서 그랬던 것처럼 여러 번 프로그램을 실행한 결과를 측정해야 한다. 또한 컴파일러 최적화는 병렬 버전보다는 순차버전에 집중될 수 있다는 사실도 기억하자.(예를 들어 순차버전에서는 죽은 코드를 분석해서 사용되지 않는 계산은 아예 삭제하는 등의 최적화를 달성하기 쉽다)

포크/조인 분할 전략에서는 주어진 서브태스크를 더 분할할 것인지 결정할 기준을 정해야 한다.
그 분할 조건을 살펴보자.


작업 훔치기
ForkJoinSumCalculator 예제에선 덧셈을 수행할 숫자가 만 개 이하면 서브태스크 분할을 중단했다.
기준값을 바꿔가면서 실험해보는 방법 외에는 좋은 기준을 찾을 뾰족한 방법이 없다.

코어 개수와 관계없이 적절한 크기로 분할된 많은 태스크를 포킹하는 것이 바람직하다.
이론적으로는 코어 개수만큼 병렬화된 태스크로 작업부하를 분할하면 모든 CPU코어에서 태스크를 실행할 것이고 크기가 같은 각각의 태스크는 같은 시간에 종료될 것이라 생각할 수 있다.
하지만 복잡한 시나리오의 경우 각각 서브태스크의 작업완료 시간이 크게 달라질 수 있다.
포크/조인 프레임워크에서는 작업 훔치기라는 기법으로 이 문제를 해결한다.
작업 훔치기 기법에서는 ForkJoinPool의 모든 스레드를 거의 공정하게 분할한다. 
각각의 스레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다. 
이때 한 스레드는 다른 스레드보다 자신에게 할당된 태스크를 더 빨리 처리할 수 있다.
이때 할일이 없어진 스레드는 유휴상태로 바뀌는 것이 아니라 다른 스레드 큐의 꼬리에서 작업을 훔쳐온다.
??어떻게 훔쳐오는 거지? 알고리즘 내용이 없네, 작업훔치기 알고리즘을 확인해 볼 것.
모든 태스크가 작업을 끝낼 때까지, 즉 모든 큐가 빌때까지 이 과정을 반복한다. 따라서 태스크의 크기를 작게 나누어야 작업자 스레드 간의 작업부하를 비슷한 수준으로 유지할 수 있다.

분할로직을 개발하지 않고도 병렬 스트림을 이용할 수 있다. 스트림을 자동으로 분할해주는 기능이 있다.


Spliterator 인터페이스
자바8의 새로운 인터페이스, '분할할 수 있는 반복자'라는 의미
Iterator처럼 Spliterator는 소스의 요소 탐색 기능을 제공한다는 점은 같지만, Spliterator는 병렬 작업에 특화되어 있다.
커스텀 Spliterator를 직접 구현해서 어떻게 동작하는지 이해해보자.
자바8은 컬렉션 프레임워크에 포함된 모든 자료구조에 사용할 수 있는 디폴트 Spliterator 구현을 제공.
컬렉션은 spliterator라는 메서드를 제공하는 Spliterator인터페이스를 구현한다.

public interface Spliterator<T> {
	boolean tryAdvance(Consumer<? super T> action);
	Spliterator<T> trySplit();
	long estimateSize();
	int characteristics();
}

T는 spliterator에서 탐색하는 요소의 형식을 가리킨다. 
tryAdvance메서드는 Spliterator의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 참을 반환한다.(즉, 일반적인 Iterator 동작과 같다) 반면 trySplit 메서드는 Splitertor의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 Spliterator를 생성하는 메서드다.
Spliterator에서는 estimateSize 메서드로 탐색해야 할 요소 수 정보를 제공할 수 있다. 특히 탐색해야 할 요소 수가 정확하진 않더라도 제공된 값을 이용해서 더 쉽고 공평하게 SPliterator를 분할할 수 있다.

분할과정

1단계에서 첫 번째 Spliterator에 trySplit을 호출하면 두 번째 Spliterator가 생성된다.
2단계에서 두 개의 Spliterator에서 trySplit을 다시 호출하면 네 개의 Spliterator가 생성된다.
이처럼 trySPlit의 결과가 null이 될 때까지 반복한다.
4단계에서 Spliterator에 호출한 모든 trySPlit의 결과가 null이면 재귀 분할 과정이 종료된다.
이 분할 과정은 characteristics 메서드로 정의하는 SPliterator의 특성에 영향을 받는다.


Spliterator 특성

characteristics 메서드는 Spliterator 자체의 특성 집합을 포함하는 int를 반환.
Spliterator를 이용하는 프로그램은 이들 특성을 참고해서 SPliterator를 더 잘 제어하고 최적화할 수 있다.(안타깝게도 일부 특성은 컬렉터와 개념상 비슷함에도 다른 방식으로 정의되었다.)

ORDERED - 리스트처럼 요소에 정해진 순서가 있으므로 Spliterator는 요소를 탐색하고 분할할 때 이 순서에 유의해야 한다.
DISTINCT - x, y두 요소를 방문했을 때, x.equals(y)는 항상 false를 반환.
SORTED - 탐색된 요소는 미리 정의된 정렬 순서를 따름.
SIZED - 크기가 알려진 소스(예를 들어 Set)로 Spliterator를 생성했으므로 estimatedSize()는 정확한 값을 반환.
NON-NULL - 탐색하는 모든 요소는 null이 아님.
IMMUTABLE - 이 Spliterator의 소스는 불변이다. 즉, 요소를 탐색하는 동안 요소를 추가하거나, 삭제하거나, 고칠 수 없다.
CONCURRENT - 동기화 없이 Spliterator의 소스를 여러 스레드에서 동시에 고칠 수 있다.
SUBSIZED - 이 Spliterator 그리고 분할되는 모든 Spliterator는 SIZED 특성을 갖는다.


커스텀 Spliterator 구현하기

문자열의 단어 수를 계산하는 메서드 구현
- 반복 버전
public int countWordsIteratively(String s) {
	int counter = 0;
	boolean lastSpace = true;
	for (char c : s.toCharArray()) {	//문자열의 모든 문자 탐색
		if(Character.isWhitespace(c)) {	//공백이면 lastSpace = true, 연속공백 무효
			lastSpace = true;	
		} else {
			if (lastSpace) counter++;  //공백이 아니고 lastSpace가 true면 +1
			lastSpace = false;	   //공백이 아니면 일단 lastSpace false
		}
	}
	return counter;		//공백 개수(단어 수) 반환
}


반복형 대신 함수형을 이용하면 직접 스레드를 동기화하지 않고도 병렬 스트림으로 작업을 병렬화할 수 있다.


함수형으로 단어 수 세는 메서드 재구현

우선 String을 스트림으로 변환.
안타깝게도 스트림은 int, long, double 기본형만 제공하므로 Stream<Character>를 사용해야 한다.ㅈㄷ

Stream<Character> stream = IntStream.range(0, SENTENCE.length())
					.mapToObj(SENTENCE::charAt);

스트림에 리듀싱 연산을 실행하면서 단어 수를 계산할 수 있다.
이때 지금까지 발견한 단어 수를 계산하는 int변수, 마지막 문자가 공백인지 확인하는 Boolean 변수 두 가지가 필요하다.
자바에는 튜플(래퍼 객체 없이 다형 요소의 정렬 리스트를 표현할 수 있는 구조체)이 없음.
따라서 이들 변수 상태를 캡슐화하는 새로운 클래스 WordCounter를 만들어야 함.

class WordCounter {
	private final int counter;
	private final boolean lastSpace;
	public WordCounter(int counter, boolean lastSpace) {
		this.counter = counter;
		this.lastSpace = space;
	}
	public WordCounter accumulate(Character c) {
		if(Character.isWhitespace(c)){
			return lastSpace? this : new WordCounter(counter, true);
		} else {
			return lastSpace ? new WordCounter(counter + 1, false), this;
		}
	}
	public WordCounter combine(WordCounter wordCounter) {
		return new WordCounter(counter + wordCounter.counter,
						 wordCounter.lastSpace);
	}
	public int getCounter() {
		return counter;
	}
}


accumulate 메서드는 WordCOunter의 상태를 어떻게 바꿀 것인지, 또는 엄밀히 WordCounter는 (속성을 바꿀 수 없는) 불변 클래스이므로 새로운 WordCounter클래스를 어떤 상태로 생성할 것인지 정의한다.
스트림을 탐색하면서 새로운 문자를 찾을 때 마다 accumulate메서드를 호출.
두 번째 메서드 combine은 문자열 서브 스트림을 처리한 WordCounter의 결과를 합침.
즉, combine은 WordCounter의 내부 counter값을 서로 합침.

이제 다음 코드처럼 문자 스트림의 리듀싱 연산을 직관적으로 구현할 수 있다.

private int countWords(Stream<Character> stream) {
	WordCounter wordCounter = stream.reduce(new WordCounter(0, true), //초기값
						WordCounter::accumulate,  //누적
						WordCounter::combine);    //결합
	return wordCounter.getCounter();
}

이것으로 순차스트림 문자 수 찾기가 완성됐다.
이것을 병렬 수행으로 바꿔보자.

WordCounter 병렬로 수행하기

System.out.println("Found " + countWords(stream.parallel()) + " words");

아마 원하는 결과가 나오지 않을 것이다.

무엇이 잘못됐을까? 원래 문자열을 임의의 위치에서 둘로 나누다보니 예상치 못하게 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다.
어떻게 해결할 수 있을까? 문자열을 임의의 위치가 아닌 단어가 끝나는 위치에서만 분할하자.
그러려면 단어 끝에서 문자열을 분할하는 문자 Spliterator가 필요하다.

문자 Spliterator를 구현한 다음에 병렬 스트림으로 전달하는 코드

class WordCounterSpliterator implements Spliterator<Character> { //공백기준 분리자
	private final String string;		//분할할 문자
	private int currentChar = 0;		//현재 위치
	public WordCounterSpliterator(String string) {
		this.string = string;
	}
	@Override
	public boolean tryAdvance(Consumer<? super Character> action) {  //
		action.accept(string.charAt(currentChar++));
		return currentChar < string.length();
	}
	@Override
	public Spliterator<Character> trySplit() {	//분리
		int currentSize = string.length() - currentChar; //size는 문자길이 - 위치
		if (currentSize < 10 ) {			 //size가 10보다 작으면
			return null;				 //분할 안함. null
		}
		for (int splitPos = currentSize / 2 + currentChar;
			splitPos < string.length(); splitPos++) {
			if (Character.isWhitespace(string.charAt(splitPos))) {
				Spliterator<Character> spliterator = 
					new WordCounterSpliterator(string.substring 							(currentChar, splitPos));
					currentChar = splitPos;
					return spliterator;
			}
		}
		return null;
	}
	@Override
	public long estimateSize() {
		return string.length() - currentChar;
	}
	@Override
	public int charateristics() {
		return ORDERED + SIZED + SUBSIZED + NON-NULL + IMMUTABLE;
	}
}

분석 대상 문자열로 Spliterator를 생성 -> 현재 탐색 중인 문자를 가리키는 인덱스를 이용해서 모든 문자 반복 탐색.
- tryAdvance메서드는 문자열에서 현재 인덱스에 해당하는 문자를 Consumer에 제공한 다음 인덱스 증가.
인수로 전달된 Consumer는 스트림을 탐색하면서 적용해야 하는 함수 집합이 작업을 처리할 수 있도록 소비한 문자를 전달하는 자바 내부 클래스다.
예제에선 스트림을 탐색하면서 하나의 리듀싱 함수, 즉 WordCounter의 accumulate메서드만 적용한다.
tryAdvance메서드는 새로운 커서 위치가 전체 문자열 길이보다 작으면 참을 반환. 이는 반복 탐색해야 할 문자가 남았음을 의미.
- trySplit은 반복될 자료구조를 분할하는 로직을 포함. Spliterator에서 가장 중요한 메서드.
분할 동작을 중단할 임계점을 설정해야 한다. 여기선 아주 작은 한계값(10개문자)을 사용했지만 실제는 너무 많은 분할을 하지않도록 더 높게 설정해야 함. 분할 과정에서 남은 문자 수가 한계값 이하면 null을 반환. 즉, 분할 중지. 
반대로 분할이 필요한 상황에서는 파싱해야할 문자열 청크의 중간 위치를 기준으로 분할. 이때 단어 중간을 분할하지 않도록 빈 문자가 나올때까지 분할 위치를 이동. 새로 만든 Spliterator는 현재 위치(currentChar)부터 분할된 위치까지의 문자를 탐색.
- 탐색해야 할 요소의 개수(estimatedSize)는 Spliterator가 파싱할 문자열 전체 길이(string.length())와 현재 반복중인 위치(currentChar)의 차다.
- 마지막으로 characteristic메서드는 프레임워크에 SPliterator가 ORDERED(문자열), NONNULL(문자열에는 null 문자가 존재하지 않음), IMMUTABLE(문자열 자체가 불편 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음) 등의 특성임을 알려준다.


WordCounterSpliterator 활용

WorkCounterSpliterator를 병렬 스트림에 사용하자.

Spliterator<Character> spliterator = new WordCounterSPliterator(SENTENCE);
Stream<Character> stream = StreamSupport.steam(splitarator, ture):

StreamSupport.stream 팰토리 메서드로 전달한 두 번째 불리언 인수는 병렬 스트림 생성여부를 지시.
System.out.println("Found " + countWords(stream) + "words");

이제 정확한 결과가 출력된다.

Spliterator는 첫 번째 탐색 시점, 첫 번째 분할 시점, 또는 첫 번째 예상 크기 요청 시점에 요소의 소스를 바인딩 할 수 있다.(바인딩?) 이와 같은 동작을 늦은 바인딩 Spliterator라고 부른다.





