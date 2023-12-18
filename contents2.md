최신 자바활용

null 대신 Optional클래스

값이 없는 상황을 어떻게 처리할까?

예제) 자동차와 자동차 보험을 갖고 있는 사람 객체를 중첩 구조로 구현시
Person/Car/Insurance 데이터 모델

public class Person{
	private Car car;
	public Car getCar() { return car; }
}

public class Car {
	private Insurance insurance;
	public Insurance getInsurance() { return insurance; }
}

public class Insurance {
	private String name;
	public String getName() { return name; }
}

다음 코드에서는 어떤 문제가 발생할까?

public String getCarInsuranceName(Person person) {
	return person.getCar().getInsurance().getName();
}

-> 
null확인이 힘듬. 하나라도 null이면 nullpointException 발생


보수적인 자세로 NullPointerException(NPE) 줄이기

예기치 않은 NPE를 피하려면 어떻게 해야 할까? null 확인 코드를 추가해서 처리

예시) null 안전 시도 1: 깊은 의심

public String getCarInsuranceName(Person person) {
	if (person != null) {
		Car car = person.getCar();
		if (car != null) {
			Insurance insurance = car.getInsurance();
			if (insurance != null) {
				return insurance.getName();
			}
		}
	}
	return "Unknown";
}

위 코드는 중간에 하나라도 null이 있다면 if문을 빠져나와 "Unknown"을 반환한다.
모든 변수가 null인지 확인하기 때문에 변수에 접근할 때마다 중첩된 if가 추가되면서 들여쓰기 수준이 증가한다. 이러한 것을 '깊은 의심(deep doubt)'이라 부른다. 


예시) null 안전 시도 2: 너무 많은 출구
public String getCarInsuranceName(Person person) {
	if (person == null) {
		return "Unknown";
	}
	Car car = person.getCar();
	if (car == null) {
		return "Unknown";
	}
	Insurance insurance = car.getInsurance();
	if (insurance == null) {
		return "Unknown";
	}
	return insurance.getName();
}

위 코드는 중첩 if 블록을 없앴다. 즉, null 변수가 있으면 즉시 'Unknown'을 반환한다. 하지만 이 예제의 문제는 너무 많은 출구(return)가 생겼기 때문에 유지보수가 어려워진다. 게다가 동일한 출구문구가 반복되어 오타 등의 실수가 생길 수 있다. 

위의 코드들은 null의 여부를 깜빡하면 바로 에러가 나기 때문에 좋지 못하다.


null 때문에 발생하는 문제

- 에러의 근원: NPE 발생
- 코드를 어지럽힌다 : 중첩 null 확인 -> 코드 가독성 하락
- 아무 의미가 없다 : null은 아무 의미도 표현하지 않는다. 정적 형식 언어에서 값이 없음을 표현하는 방법으로 적절하지 않다. 
- 자바 철학에 위배된다 : 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 예외가 있는데 null포인터다.
- 형식 시스템에 구멍을 만든다 : null은 무형식이며 정보를 포함하지 않는다. 따라서 모든 참조 형식에 null을 할당할 수 있다. 이런 식으로 null이 할당되기 시작하면서 시스템의 다른 부분으로 null이 퍼졌을 때 애초에 null이 어떤 의미로 사용되었는지 알 수 없다.

 
다른 언어는 null을 어떻게 해결하나?

그루비는 안전 내비게이션 연산자(?.)를 도입해서 null을 해결했다. 
그루비에서 자동차에 적용한 보험회사의 이름을 가져오는 코드다.

def carInsuranceName = person?.car?.insurance?.name

호출 체인에 null인 참조가 있다면 null을 반환한다.(에러 발생 X)

자바7에도 비슷한 제안이 있었으나 채택되지 않았다.
모든 자바 개발자가 안전 네비게이션 연산자를 간절히 원하는 것은 아니다.
특히 NPE가 발생했다면 예외를 일으키는 메서드에 null을 확인하는 if문을 추가해서 문제를 쉽게 해결할 수 있기 때문.

하스켈, 스칼라 등의 함수형 언어는 아예 다른 관점에서 null 문제를 접근한다.
하스켈은 선택형값(optional value)을 저장할 수 있는 Maybe라는 형식을 제공한다.
Maybe는 주어진 형식의 값을 갖거나 아니면 아무 값도 갖지 않을 수 있다.
따라서 null 참조 개념은 자연스럽게 사라진다.
스칼라도 T 형식의 값을 갖거나 아무 값도 갖지 않을 수 있는 Option[T]라는 구조를 제공.
그리고 Option형식에서 제공하는 연산을 사용해서 값이 있는지 여부를 명시적으로 확인해야 한다.
(null 확인)
형식 시스템에서 이를 강제하므로 null과 관련한 문제가 일어날 가능성이 줄어든다.

자바8은 선택형값에 영향을 받아서 Optional<T>를 제공한다.


Optional클래스 소개

Optional은 선택형값을 캡슐화하는 클래스다. 
예를 들어 어떤 사람이 차를 소유하지 않는다면, car는 null이다. 하지만 새로운 Optional을 이용할 수 있으므로 null이 할당되는 것이 아니라 변수형을 Optional<Car>로 설정할 수 있다.

값이 있으면 Optional 클래스는 값(Car)을 감싼다.
값이 없으면 Optional.empty메서드로 Optional을 반환한다. (Optional이기 때문에 null이 아님. NPE 에러 발생 X)

Optional.empty는 Optional의 특별한 싱글턴 인스턴스를 반환하는 정적 팩토리 메서드다.

null대신 Optional을 사용하면서 Car형식이 Optional<Car>로 바뀌었다. 
이 시그니처변화만으로 car에 값이 없을 수 없음을 명시적으로 보여준다. 
반면 Car형식을 사용할 때엔 Car에 null 참조가 할당될 수 있는데 이것이 올바른 값인지 아닌지 판단할 수 없다.

Optional을 사용하여 리팩토링
public class Person {
	private Optional<Car> car;        //<- 사람에게 차가 있을 수도 없을 수도 있다.
	public Optional<Car> getCar() {
		return car;
	}
}
public class Car {
	private Optional<Insurance> insurance;  //<-차에게 보험이 있을 수도 없을 수도 있다.
	public Optional<Insurance> getInsurance() {
		return insurance;
	}
}
public class Insurance {
	private String name;   //보험회사엔 반드시 이름이 있다.
	public String getName() {
		return name;
	}
}

Optional 클래스를 사용하면서 모델의 의미가 더 명확해졌다.
보험회사의 이름은 NullPointException이 발생할 수도 있다는 정보를 확인할 수 있다.
하지만 이것은 보험회사 이름이 null이면 안되기 때문에 왜 null인지 확인하는 게 필요하다.
null일 수 있는 것은 Optional을 사용한다.
Optional을 이용하면 값이 없는 상황이 데이터의 문제인지 알고리즘의 문제인지 명확하게 구분할 수 있다.
모든 null 참조를 Optional로 대치하는 것은 바람직하지 않다.
Optional의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것이다.


Optional 적용 패턴

Optional 객체 만들기

빈 Optional
정적 팩토리 메서드 Optional.empty로 빈 Optional객체를 얻을 수 있다.

Optional<Car> optCar = Optional.empty();

null이 아닌 값으로 Optional 만들기
정적 팩토리 메서드 Optional.of로 null이 아닌 값을 포함하는 Optional을 만들 수 있다.

Optional<Car> optCar = Optional.of(car);

이제 car가 null이라면 즉시 NullPointException이 발생한다.
(Optional을 사용하지 않았다면 car의 프로퍼티에 접근하려 할 때 에러가 발생했을 것이다)
?이걸 어따씀?

null값으로 Optional 만들기
정적 팩토리 메서드 Optional.ofNullable로 null값을 저장할 수 있는 Optional을 만들 수 있다.

Optional<Car> optCar = Optional.ofNullable(car);

car가 null이면 빈 Optional객체가 반환된다.

Optional에서 값을 가져오려면 get메서드를 사용하면 된다.
그런데 Optional이 비어 있으면 get을 호출했을 때 예외가 발생해서 null을 사용할 때와 같은 문제가 발생한다.
따라서 먼저 Optional로 명시적인 검사를 제거할 수 있는 방법을 살펴본다.

맵으로 Optional의 값을 추출하고 변환하기

이름을 얻기 전에 insurance가 null인지 확인할 때 아래와 같이 확인한다.
String name = null;
if (insurance != null) {
	name = insurance.getName();
}

위와 같은 패턴에 사용할 수 있도록 Optional은 map메서드를 지원한다.

Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
  Optional<String> name = optInsurance.map(Insurance::getName);

Optional의 map메서드는 스트림의 map메서드와 개념적으로 비슷하다.
Optional이 값을 포함하면 map의 인수로 제공된 함수가 값을 바꾼다.
Optional이 비어있으면, 아무일도 일어나지 않는다.

public String getCarInsuranceName(Person person) {
	return person.getCar().getInsurance().getName();
}

그러면 여러 메서드를 안전하게 호출하는데, 이 코드를 어떻게 활용할 수 있을까?


flatMap으로 Optional객체연결

Optional<Person> optPerson = Optional.of(person);
Optional<String> name = 
	optPerson.map(Person::getCar)	
		.map(Car::getInsurance)
		.map(Insurance::getName);


위 코드는 컴파일 되지 않는다. 왜?
변수 optPerson의 형식은 Optional<Person>이므로 map을 호출할 수 있다.
하지만 getCar는 Optional<Car>형식의 객체를 반환한다.
즉, map 연산의 결과는 Optional<Optional<Car>> 형식의 객체다.
getInsurance는 또 다른 Optional 객체를 반환하므로 getInsurance메서드를 지원하지 않는다.

이 문제를 해결하려면?
스트림의 flatMap은 함수를 인수로 받아서 다른 스트림을 반환하는 메서드다.
보통 인수로 받은 함수를 스트림의 각 요소에 적용하면 스트림의 스트림이 만들어진다.
하지만 flatMap은 인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 남긴다.
이차원 Optional을 일차원Optional로 평준화할 수 있다.


Optional로 자동차의 보험회사 이름 찾기

Optional의 map과 flatMap을 살펴봤으니 이제 이를 실제로 사용해보자.

public String getCarInsuranceName(Optional<Person> person) {
	return person.flatMap(Person::getCar)
			.flatMap(Car::getInsurance)
			.map(Insurance::getName)
			.orElse("Unknown");
}


Optional을 이용한 Person/Car/Insurance 참조 체인

flatMap을 통해 평준화를 진행하는데, 평준화 과정이란 이론적으로 두 Optional을 합치는 기능을 수행하면서 둘 중 하나라도 null이면 빈 Optional을 생성하는 연산이다.
flatMap을 빈 Optional에 호출하면 아무 일도 일어나지 않고 그대로 반환된다.
반면 Optional이 Person을 감싸고 있다면 flatMap에 전달된 Function이 Person에 적용된다.
Function을 적용한 결과가 이미 Optional이므로 flatMap 메서드는 결과를 그대로 반환할 수 있다.

호출 체인 중 어떤 메서드가 빈 Optional을 반환한다면 전체 결과로 빈 Optional을 반환하고 아니면 관련 보험회사의 이름을 포함하는 Optional을 반환한다.
호출 체인의 결과로 Optional<String>이 반환되는데 여기에 회사 이름이 저장되어 있을 수도 있고 없을 수도 있다. Optional이 비어있을 때 기본값을 제공하는 orElse메서드를 사용한다.


도메인 모델에 Optional을 사용했을 때 데이터를 직렬화할 수 없는 이유

Optional을 통해 값이 꼭 있어야 하는지 없을 수 있는지 구체적으로 표현할 수 있다.
Optional클래스의 설계자는 이와는 다른 용도로만 Optional 클래스를 사용할 것을 가정했다.
Optional의 용도가 선택형 반환값을 지원하는 것이라고 했다.
Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았으므로 Serializable 인터페이스를 구현하지 않는다.
따라서 우리 도메인 모델에 Optional을 사용한다면 직렬화 모델을 사용하는 도구나 프레임워크에 문제가 생길 수 있다.
?도메인 모델이란 예를 들면 뭐지?
이와 같은 단점에도 불구하고 Optional을 사용해서 도메인 모델을 구성하는 것이 바람직하다고 생각한다.
특히 객체 그래프에서 일부 또는 전체 객체가 null일 수 있는 상황이라면 더욱 그렇다.
?객체 그래프가 그래프 자료구조를 통해 만든 객체인가?
직렬화 모델이 필요하다면 다음 예제처럼 Optional로 값을 반환받을 수 있는 메서드를 추가하는 방식을 권장.
public class Person {
	private Car car;
	public Optional<Car> getCarAsOptional() {
		return Optional.ofNullable(car);
	}
}
?직렬화 모델에 대한 이해가 적어서 무슨 말인지 잘 모르겠음?


Optional 스트림 조작

자바9에선 Optional을 포함하는 스트림 처리를 위해 stream()메서드를 추가했다.


사람목록을 이용해 가입한 보험회사 이름 찾기
public Sec<String> getCarInsuranceNames(List<Person> persons) {
	return persons.stream()			//Stream<List<Person>>
		.map(Person::getCar)            //Stream<Optional<Car>
		.map(optCar -> optCar.flatMap(Car::getInsurance))
		.map(optIns -> optIns.map(Insurance::getName))
		.flatMap(Optional::stream)
		.collect(toSet());

디폴트 액션과 Optional 언랩

Optional인스턴스에 포함된 값을 읽는 다양한 방법들

- get() : 값을 읽는 가장 간단한 메서드, 안전하지 않음. get은 래핑된 값이 있다면 해당 값을 반환하고 없다면 NoSuchElementException을 발생시킨다. 따라서 값이 있다고 가정할 수 있는 상황이 아니면 get을 사용하지 않는 게 바람직하다. 결국 중첩된 null 확인 코드를 넣는 것과 같다.
- orElse() : 래핑된 값이 있다면 해당 값을 반환하고 없다면 기본 값을 제공할 수 있다.
- ofElseGet(Supplier<? extends T> other)는 orElse메서드에 대응하는 게으른 버전의 메서드다. Optional에 값이 없을 때만 Supplier가 실행되기 때문이다. 디폴트 메서드를 만드는 데 시간이 걸리거나(효율성 때문에) Optional이 비어있을 때만 기본값을 생성하고 싶다면 (기본값이 반드시 필요한 상황) orElseGet을 사용해야 한다.
- orElseThrow(Supplier<? extends X> exceptionSupplier)는 Optional이 비어있을 때 예외를 발생시킨다는 점에서 get과 비슷하다. 하지만 이 메서드는 발생시킬 예외의 종류를 선택할 수 있다.
- ifPresent(Consumer<? super T> consumer)를 이용하면 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있다. (Optional에 래핑된 값을 반환하는 게 아니라 동작만 실행시키는 듯)

자바9에 추가된 메서드
- ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) : Optional이 비었을 때 실행할 수 있는 Runnable을 인수로 받는다는 점만 ifPresent와 다르다. (비었다면 다른 행동을 해라가 추가됨)


두 Optional 합치기

Person과 Car정보를 이용해서 가장 저렴한 보험료를 제공하는 보험회사를 찾자.
public Insurance findCheapestInsurance(Person person, Car car) {
	//다양한 보험회사가 제공하는 서비스 조회
	//모든 결과 데이터 비교
	return cheapestCompany;
}

두 Optional을 인수로 받아서 Optional<Insurance>를 반환하는 null안전 버전의 메서드를 구현하자.
인수로 전달한 값 중 하나라도 비었다면, 빈 Optional<Insurance>를 반환한다.
Optional클래스는 Optional이 값을 포함하는지 여부를 알려주는 isPresent라는 메서드도 제공한다.

public Optional<Insurance> nullSafeFindCheapestInsurance(
	Optional<Person> person, Optional<Car> car) {
	if (person.isPresent() && car.isPresent()) {
	return Optional.of(findCheapestInsurance(person.get(), car.get()));
	} else {
		return Optional.empty();
	}
}


장점은 person과 car의 시그니처로 둘 다 아무값도 반환하지 않을 수 있다는 정보를 명시적으로 보여준다.
단점은 null확인 코드와 크게 다르지 않다.

Optional클래스와 Stream 인터페이스는 map과 flatMap 메서드 이외에도 다양한 비슷한 기능을 공유한다.
filter를 살펴보자.


Optional 언랩하지 않고 두 Optional 합치기


어떤 조건도 사용하지 않고 한 줄의 코드로 nullSafeFindCeapestInsurance()) 메서드 구현

public Optional<Insurance> nullSafeFindCheapestInsurance(
	Optional<Person> person, Optional<Car> car{
	return person.flatmap(p -> car.map(c->findCheapestInsurance(p,c)));
}


필터로 특정값 거르기

null여부 확인 후 프로퍼티 확인
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())){
	System.out.println("ok");
}

Optional에서 filter메서드 사용

Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
		.ifPresent(x -> System.out.println("ok"));

filter메서드는 프레디케이트를 인수로 받음.
Optional 객체가 값을 가지며 프레디케이트와 일치하면 filter메서드는 그 값을 반환하고 그렇지 않으면 빈 Optional객체를 반환.
Optional은 최대 한 개의 요소를 포함할 수 있는 스트림과 같음.
filter가 0번 또는 1번 수행됨.

Optional 필터링 퀴즈

우리의 Person/Car/Insurance 모델을 구현하는 Person클래스에는 사람의 나이 정보를 가져오는 getAge라는 메서드도 있다. 다음 시그니처를 이용해서 예제 11-5의 getCarInsuranceName 메서드를 고치시오.

[예제 11-5 ]
public String getCarInsuranceName(Optional<Person> person) {
	return person.flatMap(Person::getCar)
			.flatMap(Car::getInsurance)
			.map(Insurance::getName)
			.orElse("Unknown");
}

public String getCarInsuranceName(Optional<Person> person, int minAge)
즉, 인수 person이 minAge 이상의 나이일 때만 보험회사 이름을 반환한다.


(예상답)
public String getCarInsuranceName(Optional<Person> person, int minAge) {
	return person.filter(p -> minAge <= p.getAge())   //filter로 나이 필터링
			.flatMap(Person::getCar)
			.flatMap(Car::getInsurance)
			.map(Insurance::getName)
			.orElse("Unknown");
}



Optional 클래스의 메서드
- empty : 빈 Optional 인스턴스 반환
- filter : 값이 존재하며 프레디케이트와 일치하면 값을 포함하는 Optional을 반환, 없다면 빈Optional
- flatMap : 값이 존재하면 인수로 제공된 함수를 적용한 결과 Optional반환, 없으면 빈 Optional
- get : 값이 존재하면 Optional이 감싸고 있는 값을 반환, 없으면 NoSuchElementException 발생
- ifPresent : 값이 존재하면 지정된 Consumer를 실행, 없으면 아무 작동안함
- ifPresentOrElse : 값이 존재하면 지정된 Consumer를 실행, 없으면 아무 작동안함
- isPresent : 값이 존재하면 true 반환, 값이 없으면 false 반홤
- map : 값이 존재하면 제공된 매핑 함수 적용
- of : 값이 존재하면 값을 감싸는 Optional을 반환, 값이 null이면 NullPointException 발생
- ofNullable : 값이 존재하면 값을 감싸는 Optional 반환, 값이 null이면 빈 Optional반환
- or : 값이 존재하면 같은 Optional 반환, 없으면 Supplier에서 만든 Optional 반환
?먼말임 용법 찾아봐야 할 듯?

- orElse : 값이 존재하면 값 반환, 없으면 기본값 반환
- orElseGet : 값이 존재하면 값 반환, 없으면 Supplier에서 제공하는 값 반환
- orElseThrow : 값이 존재하면 값 반환, 없으면 Supplier에서 생성한 예외 발생
- stream : 값이 존재하면 값만 포함하는 스트림 반환, 없으면 빈 스트림 반환


Optional 사용 실용 예제
새Optional 클래스를 효과적으로 이용하려면 잠재적으로 존재하지 않는 값의 처리 방법을 바꿔야 함.
즉, 코드 구현만 바꾸는 것이 아니라 네이티브 자바 API와 상호작용하는 방식도 바꿔야 함.

호환성 유지 때문에 기존 자바 API는 Optional을 적절하게 활용하지 못하고 있다. 그렇다고 기존 API에서 Optional을 사용할 수 없는 것은 아니다. Optional 기능을 활용할 수 있도록 우리 코드에 작은 유틸리티 메서드를 추가하는 방식으로 문제를 해결할 수 있다. 

잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기

기존 자바 API에서는 null을 반환하면서 요청한 값이 없거나 어떤 문제로 계산에 실패했음을 알린다.
예를 들어 Map의 get메서드로 요청한 키에 대응 값이 없다면 null을 반환함.
Optional을 반환하는 것이 더 바람직하다.
get메서드는 고칠 수 없지만 get메서드의 반환값을 Optional로 감쌀 수 있다.
Map<String, Object> 형식의 맵이 있는데, 다음처럼 key로 값에 접근한다고 가정.

Object value = map.get("key");

key에 해당하는 값이 map에 없다면 value는 null일 것이다.
map에서 반환하는 값을 Optional로 감싸서 개선할 수 있다.
Optional<Object> value = Optional.ofNullable(map.get("key"));


예외와 Optional 클래스

자바 API는 어떤 이유에서 값을 제공할 수 없을 때 null을 반환하는 대신 예외를 발생시킨다.
예를 들면 문자열을 정수로 변환하는 정적 메서드 Integer.parseInt(String)다.
문자열을 정수로 바꾸지 못할 때 NumberFormatException을 발생시킨다.
즉, 문자열이 숫자가 아니라는 것을 예외로 알리는 것.
이전에는 null값을 if로 처리했지만, 이런 경우엔 try/catch블록을 사용해야 한다.

정수로 변환할 수 없는 문자열 문제를 빈 Optional로 해결할 수 있다. 
parseInt가 Optional을 반환하도록 모델링 할 수 있다.
parseInt를 감싸는 작은 유틸리티 메서드를 구현해서 처리하자.

public static Optional<Integer> stringToInt(String s) {
	try {
		return Optional.of(Integer.parseInt(s));
	} catch (NumberFormatException e) {
		return Optional.empty();
	}
}

위와 같은 메서드를 포함하는 유틸리티 클래스(OptionalUtility)를 만들어서 필요할 때 이용하면 쉽게 사용할 수 있다.


기본형 Optional을 사용하지 말아야 하는 이유

스트림처럼 Optional도 기본형으로 특화된 OptionalInt, OptionalLong, OptionalDouble 등의 클래스를 제공한다. 
예를 들어 Optional<Integer> 대신 OptionalInt를 반환할 수 있다.
Stream은 기본형 특화(ex. StreamInt)로 성능향상을 했지만, Optional은 요소수가 0~1이기 때문에 기본 특화 클래스로 성능 개선할 수 없다.

기본형 특화 Optional은 map, flatMap, filter 등을 지원하지 않으므로 기본형 특화 Optional을 사용할 것을 권장하지 않는다. 
게다가 스트림과 마찬가지로 기본형 특화 Optional로 생성한 결과는 다른 일반 Optional과 혼용할 수 없다.
예를 들어 OptionalInt를 반환한다면 이를 다른 Optional의 flatMap에 메서드 참조로 전달할 수 없다.
??그럼 굳이 왜 만들어 둠??


응용

Optional 클래스의 메서드를 실제 업무에서 어떻게 활용할 수 있을까
예를 들어 프로그램의 설정 인수로 Properties를 전달한다고 가정하자.
그리고 다음과 같은 Properties로 우리가 만든 코드를 테스트할 것이다.

Properties props = new Properties();
props.setProperty("a", "5");
props.setProperty("b", "true");
props.setProperty("c", "-3");

이제 프로그램에서는 Properties를 읽어서 값을 초 단위의 지속시간으로 해석한다.
다음과 같은 메서드 시그니처로 지속 시간을 읽을 것이다.

public int readDuration(Properties props, String name)

지속 시간은 양수여야 하므로 문자열이 양의 정수를 가리키면 해당 정수를 반환하지만 그 외에는 0을 반환한다.
이를 다음처럼 JUnit 어설션으로 구현할 수 있다.
assertEquals(5, readDuration(param, "a"));	//assertEquals(a, b) a랑b가 같은지 확인 
assertEquals(0, readDuration(param, "b"));
assertEquals(0, readDuration(param, "c"));
assertEquals(0, readDuration(param, "d"));

이들 어설션은 다음과 같은 의미를 갖는다.
프로퍼티 'a'는 양수로 변환할 수 있는 문자열을 포함하므로 readDuration메서드는 5를 반환한다.
'b'는 변환 불가라 0을 반환. 'c'는 음수 문자열이므로 0을 반환.
'd'는 해당 프로퍼티가 없으므로 0을 반환.


public int readDuration(Properties props, String name) {
	String value = props.getProperty(name);
	if (value != null) {
		try {
			int i = Integer.parseInt(value);
			if ( i > 0 ) {
			return i;
			}
		} catch (NumberFormatException nfe) { }
	}
	return 0;	//value가 null이거나 int로 변환한게 0보다 작거나 예외가 발생하면 0 반환
}

if랑 try/catch를 사용해서 구현 코드가 복잡해졌다.
위의 코드를 Optional을 사용해서 개선해보자


요청한 프로퍼티가 존재하지 않을 때 Properties.getProperty(String) 메서드는 null을 반환하므로 ofNullable 팩토리 메서드를 이용해 Optional을 반환하도록 바꿀 수 있다.
그리고 flatMap메서드에 앞서 만든 stringToInt메서드 참조를 전달해서 Optional<String>을 Optional<Integer>로 바꿀 수 있다. 마지막으로 음수를 필터링해서 제거한다.
이들 중 하나라도 빈 Optional을 반환하면 orElse에 의해 0을 반환한다.

Optional.ofNullable(props.getProperty(name))
	.flatMap(OptionalUtility::stringToInt)	//Optional<String> -> Optional<Integer>
	.filter(i -> i > 0)	//알아서 Integer를 비교하나? get없이?
	.orElse(0);


Optional과 스트림에서 사용한 방식은 여러 연산이 연결되는 데이터베이스 질의문과 비슷하다.



새로운 날짜와 시간 API

자바 API는 복잡한 어플리케이션을 만드는 데 필요한 여러 유용한 컴포넌트를 제공한다.
하지만 자바 API가 완벽한 것은 아니다.
대부분의 자바 개발자가 날짜와 시간 관련 기능에 만족하지 못했다.
자바8은 새로운 날짜와 시간 API를 제공한다.

자바 1.0에선 java.util.Date클래스 하나로 날짜와 시간 관련 기능을 제공했다.
날짜를 의미하는 Date라는 클래스의 이름과 달리 Date클래스는 특정 시점을 날짜가 아닌 밀리초 단위로 표현한다.
게다가 1900년을 기준으로 하는 오프셋, 0에서 시작하는 달 인덱스 등 모호한 설계로 유용성이 떨어졌다.

다음은 자바9의 릴리스 날짜인 2017년 9월 21일을  Date인스턴스를 만드는 코드다.
Date date = new Date(117, 8, 21);

다음은 날짜 출력 결과다.
Thu Sep 21 00:00:00 CET 2017

결과가 직관적이지 않다. 또한 Date클래스의 toString으로는 반환되는 문자열을 추가로 활용하기가 어렵다.
출력 결과에서 알 수 있듯이 Date는 JVM 기본시간대인 CET, 즉 중앙유럽시간대를 사용했다. 그렇다고 Date클래스가 자체적으로 시간대 정보를 알고 있는 것도 아니다.

자바 1.1에서는 Date 클래스의 여러 메서드를 사장 시키고 java.util.Calendar라는 클래스를 대안으로 제공했다. 1900년도에 시작하는 오프셋은 없앴지만, 여전히 달의 인덱스는 0부터 시작했다.
Date와 Calendar 두 가지 클래스가 생겨서 어느 클래스를 사용할지 혼란이 생겼다.
게다가 DateFormat 같은 일부 기능은 Date클래스에서만 작동했다.

DateFormat도 문제가 있다. 예를 들어 DateFormat은 스레드에 안전하지 않다. 즉, 두 스레드가 동시에 하나의 포매터로 날짜를 파싱할 때 예기치 못한 결과가 일어날 수 있다.

마지막으로 Date와 Calendar는 모두 가변 클래스다.
2017년 9월 21일을 2017년 10월 25일로 바꾸면 어떤 문제가 생길까? 가변클래스 설계 때문에 유지보수가 어려워진다.

부실한 날짜와 시간 라이브러리 때문에 많은 개발자는 Joda-Time 같은 서드파티 날짜 시간 라이브러리를 사용했다.
결국 오라클은 자바8에서 Joda-Time의 많은 기능을 java.time 패키지로 추가했다.


java.time 패키지의 LocalDate, LocalTime, instant, Duration, Period클래스들

LocalDate와 LocalTime 사용

새로운 날짜와 시간 API를 사용할 때 처음 접하게 되는 것이 LocalDate다. LocalDate인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체다.
특히 LocalDate 객체는 어떤 시간대 정보도 포함하지 않는다.(오로지 날짜)
정적 팩토리 메서드 of로 인스턴스를 만든다.

LocalDate date = LocalDate.of(2017, 9, 21) //2017-09-21
int year = date.getYear();		//2017
Month month = date.getMonth();		//SEPTEMBER
int day = date.getDayOfMonth();         //21
DayOfWeek dow = date.getDayOfWeek();    //THURSDAY
int len = date.lengthOfMonth();         //월의 일수
boolean leap = date.isLeapYear();       //false(윤년 여부)

팩토리 메서드 now는 시스템 시계의 정보를 이용해서 현재 날짜 정보를 얻는다.
LocalDate today = LocalDate.now();

get메서드에서 TemporalField를 전달해서 정보를 얻는 방법도 있다.
TemporalField는 시간 관련 객체에서 어떤 필드의 값에 접근할지 정의하는 인터페이스다.
열거나 ChronoField는 TemporalField 인터페이스를 정의하므로 ChronoField의 열거자 요소를 이용해서 원하는 정보를 쉽게 얻을 수 있다.

int year = date.get(ChronoField.YEAR);	//2023
int month = date.get(ChronoField.MONTH_OF_YEAR);
int day = date.get(ChronoField.DAY_OF_MONTH);

가독성을 높이기 위해 내장메서드를 사요할 수도 있다.

int year = date.getYear();
int month = date.getMonthValue();
int day = date.getDayOfMonth();


00:00:00 같은 시간은 LocalTime으로 표현할 수 있다.

정적 메서드 of는 오버로드 된 두 메서드가 있다. 시, 분을 받는 메서드랑 시, 분, 초를 받는 메서드가 있다.

LocalTime time = LocalTime.of(13,45,20); //13:45:20

int hour = time.getHour();               //13
int minute = time.getMinute();		 //45
int second = time.getSecond();           //20


날짜, 시간 문자열을 LocalDate와 LocalTime의 인스턴스를 만드는 방법도 있다.
LocalDate ld = LocalDate.parse("2017-05-25");
LocalTime lt= LocalTime.parse("13:05:22");

parse메서드에 DateTimeFormatter를 전달할 수도 있다. DateTimeFormatter의 인스턴스는 날짜, 시간 객체의 형식을 지정한다.
DateTimeFormatter는 이전에 설명했던 java.util.DateFormat클래스를 대체하는 클래스다.
문자열을 LocalDate나 LocalTime으로 파싱할 수 없을 때, parse 메서드는 DateTimeParseException을 일으킨다.


날짜와 시간 조합
LocalDateTime은 LocalDate와 LocalTime을 쌍으로 갖는 복합클래스다.

//2017-09-21T13:45:20
LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
LocalDateTime dt2 = LocalDateTime.of(date, time);
LocalDateTime dt3 = date.atTime(13, 45, 20);
LocalDateTime dt4 = date.atTime(time);
LocalDateTime dt5 = time.atDate(date);

dt.toLocalDate();
dt.toLocalTime();


Instant 클래스 : 기계와 날짜의 시간

사람 시간 = 주, 날짜, 시간, 분 ...
기계시간 = 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 것

Instant클래스는 유닉스 에포크 시간(1970년 1월 1일 0시 0분 0초 UTC)을 기준으로 특정 지점까지의 시간을 초로 표현)

팩토리 메서드 ofEpochSecond에 초를 넘겨줘서 Instant 클래스 인스턴스를 만듬.
Instant 클래스는 나노초(10억분의 1초)의 정밀도 제공.
또한 오버로드된 ofEpochSecond메서드에선 두 번째 인수를 이용해서 나노초 단위로 시간을 보정.
0에서 999,999,999 사이의 값을 지정할 수 있다.
따라서 다음 네 가지 코드는 같은 Instant를 반환.

Instant.ofEpochSecond(3);
Instant.ofEpochSecond(3, 0);
Instant.ofEpochSecond(2, 1_000_000_000);  //2초 이후의 1억나노초
Instant.ofEpochSecond(4, -1_000_000_000); //4초 이전의 1억나노초

LocalDate 등을 포함하여 사람이 읽을 수 있는 날짜 시간 클래스에서 그랬던 것처럼 Instant클래스도 사람이 확인할 수 있도록 시간을 표시해주는 정적 팩토리 메서드 now를 제공.

Instant는 기계 전용의 유틸리티라서 사람이 읽을 수 있는 시간 정보를 제공하지 않음.

int day = Instant.now().get(ChronoField.DAY_OF_MONTH);

위 코드는 UnsupportedTemporalTypeException을 발생.


Duration과 Period 정의

위 클래스들은 Temporal 인터페이스를 구현.
Temporal인터페이스는 특정 시간을 모델링하는 객체의 값을 어떻게 읽고 조작할지 정의.
이번에는 두 시간 객체 사이의 지속시간 duration을 만들어볼 차례다.
Duration 클래스의 정적 팩토리 메서드 between으로 두 시간 객체 사이의 지속시간을 만들 수 있다.
두 개의 LocalTime, LocalDateTime, Instant를 사용.

Duration d1 = Duration.between(time1, time2);
Duration d2 = Duration.between(dateTime1, dateTime2);
Duration d3 = Duration.between(instant1, instant2);

LocalDateTime은 사람전용, Instant는 기계전용이라 혼합하여 사용 불가하다.

또한 Duration은 초와 나노초로 시간단위를 표현하므로 between 메서드에 LocalDate를 전달할 수 없다.
년, 월, 일로 시간을 표현할 때는 Period 클래스를 사용한다.
즉, Period 클래스의 팩토리 메서드 between을 이용하면 두 LocalDate의 차이를 확인할 수 있다.

Period tenDays = period.between(LocalDate.of(2017, 9, 11), LocalDate(2017, 9, 21));

Duration과 Period 클래스는 자신의 인스턴스를 만들 수 있도록 다양한 팩토리 메서드를 제공.
즉, 두 시간 객체를 사용하지 않고도 Duration과 Period 클래스를 만들 수 있다.

Duration threeMinutes = Duration.ofMinutes(3);
Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);

Period tenDays = Period.ofDays(10);
Period threeWeeks = Period.ofWeeks(3);
Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);

Duration과 Period클래스가 공통으로 제공하는 메서드를 보여준다.

간격을 표현하는 날짜와 시간 클래스의 공통 메서드
정적메서드
between : 두 시간 사이의 간격을 생성
from : 시간 단위로 간격을 생성
of : 주어진 구성 요소에서 간격 인스턴스를 생성
parse : 문자열을 파싱해서 간격 인스턴를 생성

일반메서드
addTo : 현재 값의 복사본을 생성한 다음 지정된 Temporal객체에 추가
get : 현재 간격 정보값을 읽음
isNegative : 간격이 음수인지 확인
isZero : 간격이 0인지 확인
minus : 현재 값에서 주어진 시간을 뺀 복사본을 생성
multipliedBy : 현재값에 주어진 값을 곱한 복사본을 생성
negated : 주어진 값의 부호를 반전한 복사본을 생성
plus : 현재 값에 주어진 시간을 더한 복사본을 생성
subtractFrom : 지정된 Temporal 객체에서 간격을 뺌.

지금까지 살펴본 모든 클래스는 불변이다. 불변 클래스는 함수형 프로그래밍 그리고 스레드 안전성과 도메인 모델의 일관성을 유지하는 데 좋은 특징이다.
하지만 새로운 날짜와 시간 API에선 변경된 객체 버전을 만들 수 있는 메서드를 제공.
예를 들어 기존 LocalDate인스턴스에서 3일을 더해야 하는 경우 등.


날짜 조정, 파싱, 포매팅

withAttribute 메서드로 기존의 LocalDate를 바꾼 버전을 직접 간단하게 만들 수 있다. 

절대적인 방식으로 LocalDate의 속성 바꾸기

LocalDate date1 = LocalDate.of(2017, 9, 21); 
LocalDate date2 = date1.withYear(2011);  	//2011-09-21
LocalDate date3 = date2.withDayOfMonth(25);	//2011-09-25
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);  //2011-02-25

위 예제의 마지막 행에서 보여주는 것처럼 첫 번째 인수로 TemporalField를 갖는 메서드를 사용하면 좀 더 범용적으로 메서드를 활용할 수 있다. 
마지막 with 메서드는 예제 12-2의 get메서드와 쌍을 이룬다.
이들 두 메서드는 날짜와 시간 API의 모든 클래스가 구현하는 Temporal 인터페이스에 정의되어 있다.
Temporal 인터페이스는 LocalDate, LocalTime, LocalDateTime, Instant처럼 특정 시간을 정의한다.
정확히 표현하자면 get과 with메서드로 Temporal객체의 필드값을 읽거나 고칠 수 있다.
어떤 Temporal객체가 지정된 필드를 지원하지 않으면 UnsupportedTemporalTypeException이 발생한다.

*with메서드는 기존의 Temporal객체를 바꾸는 것이 아니라 필드를 갱신한 복사본을 만든다.
이런 과정을 함수형 갱신이라고 부른다.

선언형으로 LocalDate를 사용하는 방법도 있다. 예를 들어 다음 예제처럼 지정된 시간을 추가하거나 뺄수 있다.

LocalDate date1 = LocalDate.of(2017, 9, 21);
LocalDate date2 = date1.plusWeeks(1);		//2017-09-28
LocalDate date3 = date2.minusYears(6);		//2011-09-28
LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS); //2012-03-28

plus, minus도 Temporal 인터페이스에 정의되어 있다.
메어드의 인수에 숫자와 TemporalUnit을 활용할 수 있다.
ChronoUnit 열거형은 TemporalUnit 인터페이스를 쉽게 활용할 수 있는 구현을 제공.

LocalDate, LocalTime, LocalDateTime, Instant 등 날짜와 시간을 표현하는 모든 클래스는 서로 비슷한 메서드를 제공.

특정 시점을 표현하는 날짜 시간 클래스의 공통 메서드
정적 메서드
from : 주어진 Temporal 객체를 이용해서 클래스의 인스턴스를 생성
now : 시스템 시계로 Temporal 객체를 생성
of : 주어진 구성요소에서 Temporal 객체의 인스턴스 생성
parse : 문자열을 파싱해서 Temporal 객체 생성

일반메서드
atOffset : 시간대 오프셋과 Temporal 객체를 합침
atZone : 시간대 오프셋과 Temporal 객체를 합침
format : 지정된 포매터를 이용해서 Temporal 객체를 문자열로 변환함(Instant는 지원안함)
get : Temporal 객체의 상태를 읽음
minus : 특정시간을 뺀 Temporal객체의 복사본을 생성
plus : 특정 시간을 더한 Temporal 객체의 복사본을 생성
with : 일부 상태를 바꾼 Temporal 객체의 복사본을 생성


LocalDate 조정
다음 코드를 실행했을 때 date의 변숫값은?

LocalDate date = LocalDate.of(2014, 3, 18);
date = date.with(ChronoField.MONTH_OF_YEAR, 9);	//2014-9-18
date = date.plusYears(2).minusDays(10);		//2016-9-8
date.withYear(2011);				//변수에 할당하지 않아서 반응 없음.


TemporalAdjusters 사용하기
지금까지 살펴본 날짜 조정 기능은 비교적 간단하다.
때로는 다음 주 일요일, 돌아오는 평일, 어떤 달의 마지막 날 등 좀 더 복잡한 날짜 조정 기능이 필요할 것이다.
날짜와 시간 API는 다양한 상황에서 사용할 수 있도록 다양한 TemporalAdjuster를 제공.

import static java.time.temporal.TemporalAdjusters.*;
LocalDate date1 = LocalDate.of(2014, 3, 18);
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));	//2014-3-23
LocalDate date3 = date2.with(lastDayOfMonth());			//2014-3-31

TemporalAdjusters는 인터페이스며, TemporalAdjuster를 반환하는 정적 팩토리 메서드를 포함하는 클래스이다.

TemporalAdjusters 클래스의 팩토리 메서드
dayOfWeekInMonth : 서수 요일에 해당하는 날짜를 반환하는 TemporalAdjuster를 반환함(음수를 사용시 월의 끝에서 거꾸로 계산)
firstDayOfMonth : 현재 달의 첫 번째 날짜를 반환하는 TemporalAdjuster를 반환함
firstDayOfNextMonth : 다음 달의 첫 번째 날짜를 반환하는 TemporalAdjuster를 반환
firstDayOfNextYear : 내년의 첫날
firstDayOfYear : 올해의 첫날
firstInMonth : 현재 달의 첫 요일
lastDayOfMonth : 현재달의 마지막날
lastDayOfNextMonth : 다음달의 마지막날
lastDayOfNextYear : 다음 년의 마지막 날
lastDayOfYear : 올해의 마지막날
lastInMonth : 현재달의 마지막 요일에 해당하는 날짜를 반환하는 TemporalAdjuster 반환
next previous : 현재 달에서 현재 날짜 이후로 지정한 요일이 처음으로 나타나는 날짜를 반환하는 TemporalAdjuster를 반환
nextOrSame/previousOrSame : 현재 날짜 이후로 지정한 요일이 처음/이전으로 나타나는 날짜를 반환하는 TemporalAdjuster반환(현재날짜포함)

필요한 기능이 정의되어 있지 않을 때는 비교적 쉽게 커스텀 TemporalAdjuster 구현을 만들 수 있다.
실제로 TemporalAdjuster 인터페이스는 다음처럼 하나의 메서드만 정의한다.(하나의 메서드만 정의하므로 함수형 인터페이스다.)

@FunctionalInterface
public interface TemporalAdjuster {
	Temporal adjustInto(Temporal temporal);
}

TemporalAdjuster인터페이스 구현은 Temporal 객체를 어떻게 다른 Temporal 객체로 변환할지 정의한다.
결국 TemporalAdjuster 인터페이스를 UnaryOperator<Temporal>과 같은 형식으로 간주할 수 있다.

커스텀 TemporalAdjuster 구현하기
TemporalAdjuster 인터페이스를 구현하는 NextWorkingDay 클래스를 구현하시오.
이 클래스는 날짜를 하루씩 다음날로 바꾸는데 이때 토요일과 일요일은 건너뛴다. 
date = date.with(new NextWorkingDay());

public class NextWorkingDay implements TemporalAdjuster {
	
	@OVerride
	public Temporal adjustInto(Temporal temporal) {
		DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
		int dayToAdd = 1;
		if( dow == DayOfWeek.FRIDAY) dayToAdd = 3;
		else if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
		return temporal.plus(dayToAdd, ChronoUnit.DAYS);
	}
}	

TemporalAdjuster는 함수형 인터페이스이므로 람다 표현식이 가능하다.

date = date.with(temporal -> {
	DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
	int dayToAdd = 1;
	if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
	else if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
	return temporal.plus(dayToAdd, ChronoUnit.DAYS);
});

(굳이 람다로 하기엔 별 차이가 없음)


자주 사용하는 다른 모든 기능도 캡슐화해서 팀 전체가 재사용할 수 있는 날짜를 조정하는 작은 라이브러리를 만들 수 있다.


날짜와 시간 객체 출력과 파싱

포매팅과 파싱 전용 패키지인 java.time.format이 새로 추가되었다. 
이 패키지에서 가장 중요한 클래스는 DateTimeFormatter다.
정적 팩토리 메서드와 상수를 이용해서 손쉽게 포매터를 만들 수 있다.
DateTimeFormatter를 이용해서 날짜나 시간을 특정 형식의 문자열로 만들 수 있다.

LocalDate date = LocalDate.of(2014, 3, 18);
String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);	//20140318
String s1 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);	//2014-03-18

반대로 날짜나 시간을 표현하는 문자열을 파싱해서 날짜 객체를 다시 만들 수 있다.
날짜와 시간 API에서 특정 시점이나 간격을 표현하는 모든 클래스의 팩토리 메서드 parse를 이용해서 문자열을 날짜 객체로 만들 수 있다.

LocalDate date1 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
LocalDate date2 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);

기존의 java.util.DateFormat 클래스와 달리 스레드에서 안전하게 사용할 수 있는 클래스다.

패턴으로 DateTimeFormatter 만들기

DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formatterDate = date1.format(formatter);
LocalDate date2 = LocalDate.parse(formatterDate, formatter);




















