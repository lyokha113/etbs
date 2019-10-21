package fpt.capstone.etbs.util;

import java.util.ArrayList;
import java.util.List;

public class ExceptionHandler {

    public static List<String> getExceptionMessageChain(Throwable throwable) {
        List<String> result = new ArrayList<>();
        while (throwable != null) {
            result.add(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return result;
    }

}
