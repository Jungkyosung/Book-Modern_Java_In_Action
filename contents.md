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















