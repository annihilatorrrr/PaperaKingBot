package it.stredox02.duckbot.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserData {

    private String chatid;
    private long id;
    private String userName;
    private String firstName;
    private String lastName;
    private long time;

}
