package server.core;

import lombok.Data;

import java.util.Date;

/**
 * @Author yudm
 * @Date 2021/1/4 17:47
 * @Desc
 */
@Data
public class User {
    private String name;
    private Integer age;
    private Date time;
}
