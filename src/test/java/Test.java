import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

  public static void main(String[] args) {
    Test test = new Test();
    test.abc();
  }

  public void abc() {
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();
    Map<String, String> map3 = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v1, HashMap::new));
    System.out.println(map3);
  }
}
