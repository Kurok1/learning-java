package indi.kurok1.starter.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 学校
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@ConfigurationProperties(prefix = "school")
public class School {

    private long id;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
