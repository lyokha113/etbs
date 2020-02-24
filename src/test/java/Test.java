import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {


  public static void main(String[] args) {
    double [] array = { -9, -10, -32, 5, 1, 0, 16};

    double rs = 0;
    for (double v : array)
    {
      double lvShortRisk = -v * 10;
      if (rs < lvShortRisk)
      {
        rs = lvShortRisk;
      }
    }

    double max = Arrays.stream(array).max().orElse(0);
    double rs2 = max * 10;

    System.out.println(rs);
    System.out.println(rs2);
  }

}
