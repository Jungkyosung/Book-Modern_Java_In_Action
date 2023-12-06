스트림 API 멀티코어 사용 가능
스트림의 병렬처리
stream 자체는 멀티코어 병렬처리가 아니다 
parallelStream() 이 멀티코어 병렬처리이다

stream을 통해 선언적으로 코딩할 수 있다.
선언적 이점이 먼데??
loop와 if 를 구현하지 않고 바로 동작수행을 지정함.
for(int i = 0 ; 조건, i++){ ... } 같은 loop문
for(Object obj : List){ ... } 같은 누적자 요소 필터링

filter, sorted, map, collect 같은 여러 빌딩 블록 연산을 연결
복잡한 데이터 처리 파이프라인을 만들 수 있다. 
위 연산은 고수준 빌딩 블록으로 이루어져 있어 특정 스레딩 모델에 제한 없이
자유롭게 어떤 상황에서든 사용할 수 있다. 
데이터 처리 과정을 병렬화하면서 스레드와 락을 걱정할 필요가 없다.

스트림API는 왜 매우 비싼 연산인가?
4,5,6장 학습하면 다음과 같은 코드 구현할 수 있다.
Map<Dish.Type, List<Dish>> dishesByType =
menu.stream().collect(groupingBy(Dish::getType));

위를 일반 명령형으로 프로그래밍한다면?
String[] types = 중복제거 타입 그룹핑
for를 types만큼해서 각 type별 요리들을 ArrayList에 담아줌.
map.put(types[type수], list[type수]) 타입수만큼 진행

기타 라이브러리: 구아바, 아파치, 람다제이
구글에서 만든 구아바, 멀티맵, 멀티셋 등이 있다.

자바8 스트림 API 이점
- 선언형 : 간결성, 가독성
- 조립할 수 있음 : 유연성
- 병렬화 : 성능이점

new Dish("pork", false, 800, Dish.Type.MEAT)
Dish는 다음과 같이 불변형 클래스다. 

public class Dish{
	private final String name;
	private final boolean isVegeterian;
	private final int calories;
	private final Type type;
}

스트림과 컬렉션 차이

필터링, 슬라이싱, 검색, 매칭, 매핑, 리듀싱

스트림이란 뭘까?
데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소

- 연속된 요소 : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공. 컬렉션의 주제는 데이터고 스트림의 주제는 계산이다. 
 
- 소스 : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다.

- 데이터 처리 연산 : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.

- 파이프라이닝 : 스트림연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환함. 

- 내부 반복 : 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원.

List<String> threeHighCaloriesDishNames = 
menu.stream()
	.filter( dish -> dish.getCalories() > 300 )
	.map(Dish::getName)
	.limit(3)
	.collect(toList());

collect를 제외한 나머지 연산들은 서로 파이프라인을 형성할 수 있도록 스트림을 반환함.
(return this = 체이닝)
메서드 참조 방식 Dish::getName 는 람다식으로 d -> d.getName()과 같다.
collect는 연산처리된 데이터소스를 다른 형식으로 변환한다. 

컬렉션API vs 스트림API

컬렉션에 저장된 데이터는 파일전체와 같고
스트림은 실시간 스트리밍과 같아서 메모리에 전체 적재를 해두지 않고 계산할 때만 가져와서 계산을 진행함. 탐색을 한 번 한 이후 다시 탐색하려면 새로운 스트림을 만들어야 함.
(만약 데이터소스가 I/O 채널이라면 소스를 반복 사용할 수 없으므로 새로운 스트림을 만들 수 없다.) 이게 먼말이여? 
스트림은 단 한 번만 소비할 수 있다.

List<String> title = Arrays.asList("java8", "in", "action");
Stream<String>  s = title.stream();
s.forEach(System.out::println);  -> java8, in, action이 출력
s.forEach(System.out::println);  -> illegalStateException 발생 : 스트림이 이미 소비되었거나 닫힘.

외부 반복 vs 내부 반복 (데이터 반복 처리 방법, 누가 데이터를 반복 처리하는가?)
컬렉션은 사용자가 직접(데이터의 외부적으로) 요소를 반복, 스트림은 알아서 반복(라이브러리를 통해 데이터 내부적으로 반복)

- 컬렉션 반복(for-each사용)
List<String> names = new ArrayList<>();
for(Dish dish : menu){
	names.add(dish.getName());
}

- 컬렉션 반복(반복자 사용)
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while(iterator.hasNext()){
	Dish dish = iterator.next();
	names.add(dish.getName());
}

- 스트림 내부 반복
List<String> names = menu.stream()
	.map(Dish::getName)
	.collect(toList());

내부 반복을 사용할 때 이점은??

스트림 라이브러리의 내부 반복은 데이터 표현과 하드웨어를 활용한 병렬성 구현을 자동으로 선택한다. 반면 for-each를 이용하는 외부 반복시 병렬성을 스스로 관리해야 한다.
(병렬성을 스스로 관리한다는 것은 병렬성을 포기하던지 synchronized로 시작하는 과정을 선택하는 것이다.)


List<String> highCaloricDishes = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while(iterator.hasNext()){
	Dish dish = iterator.next();
	if(dish.getCalories() > 300 ){
		highCaloricDishes.add(dish.getName());
	}
}

내부반복으로 리팩토링 ->

List<String> highCaloricDishes = menu.stream()
	.filter(dish -> dish.getCalories() > 300 )
	.map(Dish::getName)
	.collect(toList());



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
Stream.iterate(0, n -> n + 1) //0, 1, 1, 2, 3, 5 ...
	.
	.forEach(System.out::println)













