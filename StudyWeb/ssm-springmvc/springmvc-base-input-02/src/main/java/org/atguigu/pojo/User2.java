package org.atguigu.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/*
*   1.name不为空
*   字符串 @NotBlank   集合 @NotEmpty    包装 @NotNuLl
*  2.password长度在6-18位之间
* 3.age必须>=1
* 4.email必须符合邮箱格式
* 5.birthday必须为过去的时间
*
*
* */
@Data
public class User2 {

    @NotBlank
    private String name;

    @Length(min=6)
    private String password;

    @Min(1)
    private int age;

    @Email
    private String email;

    @Past
    private Date birthday;
}
