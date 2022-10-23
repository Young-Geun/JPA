package choi.jpa.basic1;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Member {

    @Id // PK
    private Long id;

    @Column(name = "name") // DB 컬럼명을 명시할 수 있다.(DB컬럼명과 변수명이 다를 경우 이와 같이 사용할 수 있다.)
    private String name;

    private Integer age;

    // @Enumerated(EnumType.ORDINAL) // 기본값. enum 순서를 데이터베이스에 저장 (enum의 순서로 저장할 경우, 순서가 바뀔경우 이전 데이터와 혼동될 수 있으므로 권장하지 않음.)
    @Enumerated(EnumType.STRING) // enum 이름을 데이터베이스에 저장
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    @Transient // DB컬럼에 매핑하지 않을 때 사용
    private String temp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }
}
