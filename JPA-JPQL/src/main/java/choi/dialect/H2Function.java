package choi.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class H2Function extends H2Dialect {

    public H2Function() {
        /*
            DB에 만들어진 함수를 가져다 쓸 때 아래와 같이 등록할 수 있음.
                * group_concat는 기존에 H2에서 제공하고 있는 함수인데, 사용자가 만들었다고 가정하고 진행
         */

        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }

}
