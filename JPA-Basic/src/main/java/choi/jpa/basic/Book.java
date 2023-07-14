package choi.jpa.basic;

import javax.persistence.Entity;

@Entity
public class Book extends Item {

    private String author;

    private String isbn;

}
